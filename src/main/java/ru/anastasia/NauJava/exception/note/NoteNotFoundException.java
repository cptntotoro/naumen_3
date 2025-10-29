package ru.anastasia.NauJava.exception.note;

/**
 * Исключение для несуществующей заметки
 */
public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}