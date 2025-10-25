package ru.anastasia.NauJava.service.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.event.Event;

/**
 * Контакт с днем рождения
 */
@Data
@AllArgsConstructor
public class ContactWithBirthday {
    /**
     * Контакт
     */
    private Contact contact;

    /**
     * День рождения
     */
    private Event birthday;
}