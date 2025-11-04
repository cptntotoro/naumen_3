package ru.anastasia.NauJava.exception.socialprofile;

/**
 * Исключение для несуществующего профиля в сецсетях
 */
public class SocialProfileNotFoundException extends RuntimeException {
    public SocialProfileNotFoundException(String message) {
        super(message);
    }
}