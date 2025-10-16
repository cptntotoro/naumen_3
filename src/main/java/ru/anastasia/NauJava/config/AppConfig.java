package ru.anastasia.NauJava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.anastasia.NauJava.console.CommandProcessor;
import ru.anastasia.NauJava.entity.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    @Bean
    public List<Contact> contactContainer() {
        return new ArrayList<>();
    }

    @Bean
    public CommandLineRunner commandScanner(CommandProcessor commandProcessor) {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("""
                        Введите команду
                        Доступные команды:
                          add <имя> <телефон> <email> - добавить новый контакт
                          find <id> - найти контакт по ID
                          delete <id> - удалить контакт по ID
                          update <id> <имя> <телефон> <email> - обновить контакт
                          list - показать все контакты
                          search <имя> - искать контакты по имени
                          exit - завершение работы программы""");
                while (true) {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();
                    if ("exit".equalsIgnoreCase(input)) {
                        System.out.println("Завершение программы...");
                        System.exit(0);
                    }
                    commandProcessor.processCommand(input);
                }
            }
        };
    }
}