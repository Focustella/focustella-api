package com.example.focustella.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple")
public class AppleApnsProperties {

    private String teamId;
    private String keyId;
    private String privateKeyP8;
    private String liveActivityTopic;
    private Environment apnsEnv = Environment.SANDBOX;

    public enum Environment {
        SANDBOX,
        PRODUCTION
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPrivateKeyP8() {
        return privateKeyP8;
    }

    public void setPrivateKeyP8(String privateKeyP8) {
        this.privateKeyP8 = privateKeyP8;
    }

    public String getLiveActivityTopic() {
        return liveActivityTopic;
    }

    public void setLiveActivityTopic(String liveActivityTopic) {
        this.liveActivityTopic = liveActivityTopic;
    }

    public Environment getApnsEnv() {
        return apnsEnv;
    }

    public void setApnsEnv(Environment apnsEnv) {
        this.apnsEnv = apnsEnv;
    }
}
