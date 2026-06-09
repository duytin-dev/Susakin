package com.susakin.app.config;

/**
 * Kiểm tra sớm cấu hình DB trên Render trước khi Spring khởi động.
 */
public final class RenderDatabaseBootstrap {

    private RenderDatabaseBootstrap() {}

    public static void configure() {
        DatabaseSettings settings = DatabaseSettings.resolve();
        settings.validateForRender();
        System.out.println("[Susakin] DB url: " + settings.jdbcUrl());
        System.out.println("[Susakin] DB user: " + settings.username());
    }
}
