package ru.anastasia.NauJava.exception.company;

/**
 * Исключение для несуществующей компании
 */
public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String message) {
        super(message);
    }
}