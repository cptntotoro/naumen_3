package ru.anastasia.NauJava.exception.company;

/**
 * Исключение для некорректного состояния компании
 */
public class IllegalCompanyStateException extends RuntimeException {
    public IllegalCompanyStateException(String message) {
        super(message);
    }
}