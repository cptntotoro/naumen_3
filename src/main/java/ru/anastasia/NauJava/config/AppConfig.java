package ru.anastasia.NauJava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация приложения
 */
@Configuration
public class AppConfig {
    /**
     * Название приложения
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Версия приложения
     */
    @Value("${app.version}")
    private String appVersion;

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }
}