package ru.anastasia.NauJava.exception.event;

/**
 * Исключение для несуществующего события
 */
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}