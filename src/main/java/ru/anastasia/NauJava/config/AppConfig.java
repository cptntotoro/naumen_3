package ru.anastasia.NauJava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
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
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<Contact> contactContainer() {
        return new ArrayList<>();
    }

    @Bean
    public CommandLineRunner commandScanner(CommandProcessor commandProcessor) {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Введите команду");
                System.out.println("Доступные команды:");
                System.out.println("  add <имя> <телефон> <email> - добавить новый контакт");
                System.out.println("  find <id> - найти контакт по ID");
                System.out.println("  delete <id> - удалить контакт по ID");
                System.out.println("  update <id> <имя> <телефон> <email> - обновить контакт");
                System.out.println("  list - показать все контакты");
                System.out.println("  search <имя> - искать контакты по имени");
                System.out.println("  exit - завершение работы программы");
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