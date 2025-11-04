package ru.anastasia.NauJava.exception.tag;

/**
 * Исключение для несуществующего тега
 */
public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String message) {
        super(message);
    }
}