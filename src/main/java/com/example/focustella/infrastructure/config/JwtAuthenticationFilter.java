package com.example.focustella.infrastructure.config;

import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.common.exception.code.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            writeUnauthorizedResponse(response, request);
            return;
        }

        try {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            String userId = jwtTokenProvider.parse(token).getSubject();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(response, request);
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setStatus(CommonErrorCode.UNAUTHORIZED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.failure(CommonErrorCode.UNAUTHORIZED.toApiError(request.getRequestURI()))
                )
        );
    }
}
