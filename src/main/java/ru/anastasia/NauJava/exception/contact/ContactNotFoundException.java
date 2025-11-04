package ru.anastasia.NauJava.exception.contact;

/**
 * Исключение для несуществующего контакта
 */
public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(String message) {
        super(message);
    }
}