package com.example.focustella.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.focus.constellation")
public class FocusConstellationProperties {

    private List<Rule> rules = new ArrayList<>(List.of(
            new Rule(1, 25, 3, 4),
            new Rule(26, 50, 4, 6),
            new Rule(51, 90, 6, 8),
            new Rule(91, 180, 8, 12)
    ));

    @Setter
    @Getter
    public static class Rule {
        private int minMinutes;
        private int maxMinutes;
        private int minStarCount;
        private int maxStarCount;

        public Rule() {
        }

        public Rule(int minMinutes, int maxMinutes, int minStarCount, int maxStarCount) {
            this.minMinutes = minMinutes;
            this.maxMinutes = maxMinutes;
            this.minStarCount = minStarCount;
            this.maxStarCount = maxStarCount;
        }

    }
}
