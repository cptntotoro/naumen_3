package ru.anastasia.NauJava.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация приложения
 */
@Getter
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

}