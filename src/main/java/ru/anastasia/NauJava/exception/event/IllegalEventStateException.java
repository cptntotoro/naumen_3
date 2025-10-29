package ru.anastasia.NauJava.exception.event;

/**
 * Исключение для некорректного состояния события
 */
public class IllegalEventStateException extends RuntimeException {
    public IllegalEventStateException(String message) {
        super(message);
    }
}