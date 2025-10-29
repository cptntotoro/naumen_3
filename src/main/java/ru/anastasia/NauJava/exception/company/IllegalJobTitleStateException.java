package ru.anastasia.NauJava.exception.company;

/**
 * Исключение для некорректного состояния должности
 */
public class IllegalJobTitleStateException extends RuntimeException {
    public IllegalJobTitleStateException(String message) {
        super(message);
    }
}