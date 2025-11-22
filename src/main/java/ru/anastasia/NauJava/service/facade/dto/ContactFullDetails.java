package ru.anastasia.NauJava.service.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;

import java.util.List;

/**
 * DTO с полной информацией о контакте и всех связанных сущностях
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactFullDetails {
    /**
     * Контакт
     */
    private Contact contact;

    /**
     * Список способов связи
     */
    private List<ContactDetail> contactDetails;

    /**
     * Список основных типов связи
     */
    private List<ContactDetail> primaryContactDetails;

    /**
     * Список профилей в соцсетях
     */
    private List<SocialProfile> socialProfiles;

    /**
     * Список событий
     */
    private List<Event> events;

    /**
     * День рождения
     */
    private Event birthday;

    /**
     * Число дней до дня рождения
     */
    private Integer daysUntil;

    /**
     * Список заметок
     */
    private List<Note> notes;

    /**
     * Список тегов
     */
    private List<Tag> tags;

    /**
     * Список тегов контакта
     */
    private List<ContactTag> contactTags;

    public String getFullName() {
        return contact.getFirstName() + " " + contact.getLastName();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }

    public boolean isFavorite() {
        return Boolean.TRUE.equals(contact.getIsFavorite());
    }

    public boolean hasSocialProfiles() {
        return socialProfiles != null && !socialProfiles.isEmpty();
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}
