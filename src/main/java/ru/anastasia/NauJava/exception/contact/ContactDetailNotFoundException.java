package ru.anastasia.NauJava.exception.contact;

/**
 * Исключение для несуществующего способа связи
 */
public class ContactDetailNotFoundException extends RuntimeException {
    public ContactDetailNotFoundException(String message) {
        super(message);
    }
}