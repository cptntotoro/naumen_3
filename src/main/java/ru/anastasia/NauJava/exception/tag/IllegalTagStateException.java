package ru.anastasia.NauJava.exception.tag;

/**
 * Исключение для некорректного состояния тега
 */
public class IllegalTagStateException extends RuntimeException {
    public IllegalTagStateException(String message) {
        super(message);
    }
}