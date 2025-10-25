package ru.anastasia.NauJava.service.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.event.Event;

import java.util.List;

/**
 * Контакт с событиями
 */
@Data
@AllArgsConstructor
public class ContactWithEvents {
    /**
     * Контакт
     */
    private Contact contact;

    /**
     * Список событий
     */
    private List<Event> events;
}