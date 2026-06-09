package com.susakin.app.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public record DatabaseSettings(String jdbcUrl, String username, String password) {

    public static DatabaseSettings resolve() {
        String databaseUrl = getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            return fromDatabaseUrl(databaseUrl);
        }

        String jdbcUrl = firstNonBlank(
                getenv("SPRING_DATASOURCE_URL"),
                System.getProperty("spring.datasource.url")
        );
        String username = firstNonBlank(
                getenv("SPRING_DATASOURCE_USERNAME"),
                System.getProperty("spring.datasource.username"),
                "postgres"
        );
        String password = firstNonBlank(
                getenv("SPRING_DATASOURCE_PASSWORD"),
                System.getProperty("spring.datasource.password"),
                "123456"
        );

        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            jdbcUrl = "jdbc:postgresql://localhost:5432/sasukin";
        }

        return new DatabaseSettings(normalizeJdbcUrl(jdbcUrl), username, password);
    }

    private static DatabaseSettings fromDatabaseUrl(String databaseUrl) {
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

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://").append(uri.getHost());
        if (uri.getPort() > 0) {
            jdbcUrl.append(':').append(uri.getPort());
        }
        jdbcUrl.append(uri.getPath());

        return new DatabaseSettings(normalizeJdbcUrl(jdbcUrl.toString()), username, password);
    }

    static String normalizeJdbcUrl(String jdbcUrl) {
        boolean internalRenderHost = jdbcUrl.contains("dpg-")
                && jdbcUrl.contains("-a")
                && !jdbcUrl.contains(".render.com");

        if (internalRenderHost) {
            return jdbcUrl
                    .replace("?sslmode=require", "")
                    .replace("&sslmode=require", "")
                    .replace("?sslmode=prefer", "")
                    .replace("&sslmode=prefer", "");
        }

        if (!jdbcUrl.contains("sslmode=")) {
            return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
        }
        return jdbcUrl;
    }

    public void validateForRender() {
        if (!isRender()) {
            return;
        }
        if (jdbcUrl.contains("localhost") || jdbcUrl.contains("127.0.0.1")) {
            throw new IllegalStateException("""
                    [Susakin] Database URL trỏ về localhost trên Render.
                    Thêm DATABASE_URL (Add from Database) hoặc SPRING_DATASOURCE_URL.
                    """);
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("""
                    [Susakin] Thiếu SPRING_DATASOURCE_PASSWORD trên Render.
                    Vào Web Service > Environment > dán password từ Database > Credentials.
                    """);
        }
    }

    private static boolean isRender() {
        return getenv("RENDER") != null || getenv("RENDER_SERVICE_ID") != null;
    }

    private static String getenv(String key) {
        return System.getenv(key);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
