package com.susakin.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        DatabaseSettings settings = DatabaseSettings.resolve();
        settings.validateForRender();

        log.info("[Susakin] Database URL: {}", settings.jdbcUrl());
        log.info("[Susakin] Database user: {}", settings.username());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(settings.jdbcUrl());
        config.setUsername(settings.username());
        config.setPassword(settings.password());
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30_000);

        HikariDataSource dataSource = new HikariDataSource(config);
        verifyConnection(dataSource);
        return dataSource;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            hibernateProperties.put("hibernate.boot.allow_jdbc_metadata_access", false);
        };
    }

    private void verifyConnection(HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            log.info("[Susakin] Database connection successful");
        } catch (SQLException ex) {
            log.error("[Susakin] Database connection failed: {}", ex.getMessage());
            throw new IllegalStateException(
                    "Không kết nối được PostgreSQL. Kiểm tra SPRING_DATASOURCE_PASSWORD và DATABASE_URL trên Render.",
                    ex
            );
        }
    }
}
