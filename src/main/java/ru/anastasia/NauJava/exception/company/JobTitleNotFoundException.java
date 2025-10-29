package ru.anastasia.NauJava.exception.company;

/**
 * Исключение для несуществующей должности
 */
public class JobTitleNotFoundException extends RuntimeException {
    public JobTitleNotFoundException(String message) {
        super(message);
    }
}