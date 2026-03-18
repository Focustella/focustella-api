package com.example.focustella.infrastructure.persistence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppUserSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public AppUserSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS app_user (
                    id VARCHAR(36) PRIMARY KEY,
                    seed BIGINT NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL
                )
                """);

        jdbcTemplate.execute("ALTER TABLE app_user ADD COLUMN IF NOT EXISTS type VARCHAR(20)");
        jdbcTemplate.update("UPDATE app_user SET type = 'ANONYMOUS' WHERE type IS NULL");
        jdbcTemplate.execute("ALTER TABLE app_user ALTER COLUMN type SET NOT NULL");
    }
}
