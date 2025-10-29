package ru.anastasia.NauJava.exception.socialprofile;

/**
 * Исключение для некорректного состояния профиля в соцсетях
 */
public class IllegalSocialProfileStateException extends RuntimeException {
    public IllegalSocialProfileStateException(String message) {
        super(message);
    }
}