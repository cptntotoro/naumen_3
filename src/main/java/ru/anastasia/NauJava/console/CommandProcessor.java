package ru.anastasia.NauJava.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.Contact;
import ru.anastasia.NauJava.service.ContactService;

import java.util.List;

/**
 * Обработчик консольного ввода
 */
@Component
public class CommandProcessor {

    private final ContactService contactService;

    @Autowired
    public CommandProcessor(ContactService contactService) {
        this.contactService = contactService;
    }

    public void processCommand(String input) {
        String[] cmd = input.split(" ");
        if (cmd.length == 0) {
            System.out.println("Вы не ввели команду");
            return;
        }
        switch (cmd[0].toLowerCase()) {
            case "add" -> {
                if (cmd.length < 4) {
                    System.out.println("Введите: add <name> <phone> <email>");
                    return;
                }
                String name = cmd[1];
                String phone = cmd[2];
                String email = cmd[3];
                contactService.addContact(name, phone, email);
                System.out.println("Контакт успешно добавлен");
            }
            case "find" -> {
                if (cmd.length < 2) {
                    System.out.println("Введите: find <id>");
                    return;
                }
                Long id = Long.valueOf(cmd[1]);
                Contact contact = contactService.findById(id);
                if (contact != null) {
                    System.out.println("Контакт: " + contact);
                } else {
                    System.out.println("Контакт не найден");
                }
            }
            case "delete" -> {
                if (cmd.length < 2) {
                    System.out.println("Введите: delete <id>");
                    return;
                }
                Long id = Long.valueOf(cmd[1]);
                contactService.deleteById(id);
                System.out.println("Контакт успешно удален");
            }
            case "update" -> {
                if (cmd.length < 5) {
                    System.out.println("Введите: update <id> <name> <phone> <email>");
                    return;
                }
                Long id = Long.valueOf(cmd[1]);
                String name = cmd[2];
                String phone = cmd[3];
                String email = cmd[4];
                contactService.updateContact(id, name, phone, email);
                System.out.println("Контакт успешно обновлен");
            }
            case "list" -> {
                List<Contact> contacts = contactService.findAll();
                if (contacts.isEmpty()) {
                    System.out.println("Список контактов пуст");
                } else {
                    contacts.forEach(c -> System.out.println(c.toString()));
                }
            }
            case "search" -> {
                if (cmd.length < 2) {
                    System.out.println("Введите: search <name>");
                    return;
                }
                String name = cmd[1];
                List<Contact> contacts = contactService.findByName(name);
                if (contacts.isEmpty()) {
                    System.out.println("Не найдено контактов с именем, содержащим: " + name);
                } else {
                    contacts.forEach(c -> System.out.println(c.toString()));
                }
            }
            default -> System.out.println("Неизвестная команда: " + cmd[0]);
        }
    }
}
