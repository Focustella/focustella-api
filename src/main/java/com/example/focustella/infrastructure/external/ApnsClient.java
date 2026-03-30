package com.example.focustella.infrastructure.external;

import com.example.focustella.infrastructure.config.AppleApnsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApnsClient {

    private static final Duration JWT_TTL = Duration.ofMinutes(50);
    private static final Set<Integer> RETRYABLE_STATUS_CODES = Set.of(429, 500, 503);
    private static final Set<String> TERMINAL_REASONS = Set.of("BadDeviceToken", "Unregistered");

    private final AppleApnsProperties appleApnsProperties;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final HttpClient httpClient;

    private volatile CachedJwt cachedJwt;

    public ApnsClient(
            AppleApnsProperties appleApnsProperties,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        this.appleApnsProperties = appleApnsProperties;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public ApnsPushResult send(
            String pushToken,
            String event,
            ContentState contentState,
            Instant timestamp
    ) {
        ensureConfigured();

        String payload = serializePayload(event, contentState, timestamp);
        log.info(
                "Prepared APNs live activity request: event={}, pushToken={}, topic={}, host={}, payloadSummary={}",
                event,
                maskPushToken(pushToken),
                appleApnsProperties.getLiveActivityTopic(),
                resolveBaseUrl(),
                summarizeContentState(contentState)
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resolveBaseUrl() + "/3/device/" + pushToken))
                .header("authorization", "bearer " + resolveProviderToken())
                .header("apns-push-type", "liveactivity")
                .header("apns-topic", appleApnsProperties.getLiveActivityTopic())
                .header("apns-priority", "10")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        return executeWithRetry(request);
    }

    private ApnsPushResult executeWithRetry(HttpRequest request) {
        IOException lastIoException = null;
        InterruptedException lastInterruptedException = null;

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                log.info("Sending APNs request attempt: attempt={}, uri={}", attempt + 1, request.uri());
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                ApnsPushResult result = toResult(response);

                if (!result.retryableFailure() || attempt == 2) {
                    return result;
                }

                log.warn(
                        "Retrying APNs request after retryable response: attempt={}, statusCode={}, reason={}",
                        attempt + 1,
                        result.statusCode(),
                        result.reason()
                );
                sleepBackoff(attempt);
            } catch (IOException exception) {
                lastIoException = exception;
                log.warn("APNs request I/O failure on attempt {}: {}", attempt + 1, exception.getMessage());
                if (attempt == 2) {
                    break;
                }
                sleepBackoff(attempt);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                lastInterruptedException = exception;
                log.warn("APNs request interrupted on attempt {}: {}", attempt + 1, exception.getMessage());
                break;
            }
        }

        String reason = "APNs request interrupted";
        if (lastIoException != null && lastIoException.getMessage() != null) {
            reason = lastIoException.getMessage();
        } else if (lastInterruptedException != null && lastInterruptedException.getMessage() != null) {
            reason = lastInterruptedException.getMessage();
        }
        return new ApnsPushResult(false, 0, null, reason, false, true);
    }

    private void sleepBackoff(int attempt) {
        try {
            Thread.sleep((long) Math.pow(2, attempt) * 1_000L);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private ApnsPushResult toResult(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String apnsId = response.headers().firstValue("apns-id").orElse(null);
        String reason = extractReason(response.body());
        boolean accepted = statusCode >= 200 && statusCode < 300;
        boolean terminalTokenFailure = statusCode == 410 || TERMINAL_REASONS.contains(reason);
        boolean retryableFailure = RETRYABLE_STATUS_CODES.contains(statusCode);

        return new ApnsPushResult(accepted, statusCode, apnsId, reason, terminalTokenFailure, retryableFailure);
    }

    private String extractReason(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.hasNonNull("reason")) {
                return node.get("reason").asText();
            }
        } catch (IOException exception) {
            log.debug("Failed to parse APNs response body: {}", body, exception);
        }
        return body;
    }

    private String serializePayload(String event, ContentState contentState, Instant timestamp) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "aps", Map.of(
                            "timestamp", timestamp.getEpochSecond(),
                            "event", event,
                            "content-state", contentState
                    )
            ));
        } catch (IOException exception) {
            throw new IllegalStateException("APNs payload serialization failed", exception);
        }
    }

    private String resolveProviderToken() {
        CachedJwt current = cachedJwt;
        Instant now = Instant.now(clock);

        if (current != null && now.isBefore(current.expiresAt())) {
            return current.token();
        }

        synchronized (this) {
            current = cachedJwt;
            if (current != null && now.isBefore(current.expiresAt())) {
                return current.token();
            }

            String token = Jwts.builder()
                    .header()
                    .keyId(appleApnsProperties.getKeyId())
                    .and()
                    .issuer(appleApnsProperties.getTeamId())
                    .issuedAt(java.util.Date.from(now))
                    .signWith(loadPrivateKey(), Jwts.SIG.ES256)
                    .compact();

            cachedJwt = new CachedJwt(token, now.plus(JWT_TTL));
            return token;
        }
    }

    private PrivateKey loadPrivateKey() {
        try {
            String normalized = Objects.requireNonNull(appleApnsProperties.getPrivateKeyP8(), "APPLE_PRIVATE_KEY_P8 is required")
                    .replace("\\n", "\n")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(normalized);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("EC").generatePrivate(keySpec);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to parse APNs private key", exception);
        }
    }

    private String resolveBaseUrl() {
        return appleApnsProperties.getApnsEnv() == AppleApnsProperties.Environment.PRODUCTION
                ? "https://api.push.apple.com"
                : "https://api.sandbox.push.apple.com";
    }

    private void ensureConfigured() {
        List<String> missing = List.of(
                        valueOrNull(appleApnsProperties.getTeamId(), "APPLE_TEAM_ID"),
                        valueOrNull(appleApnsProperties.getKeyId(), "APPLE_KEY_ID"),
                        valueOrNull(appleApnsProperties.getPrivateKeyP8(), "APPLE_PRIVATE_KEY_P8"),
                        valueOrNull(appleApnsProperties.getLiveActivityTopic(), "APPLE_LIVE_ACTIVITY_TOPIC")
                ).stream()
                .filter(Objects::nonNull)
                .toList();

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing APNs configuration: " + String.join(", ", missing));
        }
    }

    private String valueOrNull(String value, String name) {
        return value == null || value.isBlank() ? name : null;
    }

    private String summarizeContentState(ContentState contentState) {
        return "status=" + contentState.status()
                + ",remainingSeconds=" + contentState.remainingSeconds()
                + ",totalSeconds=" + contentState.totalSeconds()
                + ",discoveredStarCount=" + contentState.discoveredStarCount()
                + ",totalStarCount=" + contentState.totalStarCount();
    }

    private String maskPushToken(String pushToken) {
        if (pushToken == null || pushToken.isBlank()) {
            return "<empty>";
        }
        if (pushToken.length() <= 12) {
            return pushToken;
        }
        return pushToken.substring(0, 6) + "..." + pushToken.substring(pushToken.length() - 6);
    }

    private record CachedJwt(String token, Instant expiresAt) {
    }

    public record ApnsPushResult(
            boolean accepted,
            int statusCode,
            String apnsId,
            String reason,
            boolean terminalTokenFailure,
            boolean retryableFailure
    ) {
    }

    public record ContentState(
            String status,
            int remainingSeconds,
            int totalSeconds,
            long referenceDate,
            double activeElapsedSeconds,
            int discoveredStarCount,
            int totalStarCount,
            ConstellationPreview constellationPreview
    ) {
    }

    public record ConstellationPreview(
            List<PreviewStar> stars,
            List<PreviewEdge> edges,
            double rotationRadians
    ) {
    }

    public record PreviewStar(double vectorX, double vectorY) {
    }

    public record PreviewEdge(int fromIndex, int toIndex) {
    }
}
