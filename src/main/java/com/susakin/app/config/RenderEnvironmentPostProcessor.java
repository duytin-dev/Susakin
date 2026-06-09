package com.susakin.app.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Render thường inject DATABASE_URL (postgresql://...).
 * Chuyển sang spring.datasource.* nếu chưa cấu hình JDBC thủ công.
 */
public class RenderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        String springUrl = environment.getProperty("SPRING_DATASOURCE_URL", "");
        if (!springUrl.isBlank() && !isLocalUrl(springUrl)) {
            return;
        }

        Map<String, Object> props = parseDatabaseUrl(databaseUrl);
        environment.getPropertySources().addFirst(new MapPropertySource("renderDatabase", props));
    }

    private boolean isLocalUrl(String url) {
        return url.contains("localhost") || url.contains("127.0.0.1");
    }

    private Map<String, Object> parseDatabaseUrl(String databaseUrl) {
        String normalized = databaseUrl.replace("postgres://", "postgresql://");
        URI uri = URI.create(normalized);

        String username = "";
        String password = "";
        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isBlank()) {
            String[] parts = userInfo.split(":", 2);
            username = decode(parts[0]);
            if (parts.length > 1) {
                password = decode(parts[1]);
            }
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(uri.getHost());
        if (uri.getPort() > 0) {
            jdbcUrl.append(':').append(uri.getPort());
        }
        jdbcUrl.append(uri.getPath());

        String query = uri.getQuery();
        if (query == null || query.isBlank()) {
            jdbcUrl.append("?sslmode=require");
        } else if (!query.contains("sslmode")) {
            jdbcUrl.append('?').append(query).append("&sslmode=require");
        } else {
            jdbcUrl.append('?').append(query);
        }

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl.toString());
        props.put("spring.datasource.username", username);
        props.put("spring.datasource.password", password);
        return props;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
