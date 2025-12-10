package ru.anastasia.NauJava.entity.contact;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.entity.tag.ContactTag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Контакт
 */
@Entity
@Table(name = "contacts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Имя
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Фамилия
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Имя для отображения
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * URL аватара
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * Флаг избранного контакта
     */
    @Column(name = "is_favorite")
    @Builder.Default
    private Boolean isFavorite = false;

    /**
     * Дата и время создания
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время обновления
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Компании
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<ContactCompany> companies = new ArrayList<>();

    /**
     * Способы связи
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<ContactDetail> contactDetails = new ArrayList<>();

    /**
     * Профили в соцсетях
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<SocialProfile> socialProfiles = new ArrayList<>();

    /**
     * События
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<Event> events = new ArrayList<>();

    /**
     * Заметки
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<Note> notes = new ArrayList<>();

    /**
     * Теги
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<ContactTag> contactTags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (displayName == null) {
            displayName = firstName + " " + lastName;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addCompany(ContactCompany company) {
        this.companies.add(company);
        company.setContact(this);
    }

    public void removeCompany(ContactCompany company) {
        this.companies.remove(company);
        company.setContact(null);
    }

    public void addContactDetail(ContactDetail detail) {
        this.contactDetails.add(detail);
        detail.setContact(this);
    }

    public void removeContactDetail(ContactDetail detail) {
        this.contactDetails.remove(detail);
        detail.setContact(null);
    }

    public void addSocialProfile(SocialProfile profile) {
        this.socialProfiles.add(profile);
        profile.setContact(this);
    }

    public void removeSocialProfile(SocialProfile profile) {
        this.socialProfiles.remove(profile);
        profile.setContact(null);
    }

    public void addEvent(Event event) {
        this.events.add(event);
        event.setContact(this);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
        event.setContact(null);
    }

    public void addNote(Note note) {
        this.notes.add(note);
        note.setContact(this);
    }

    public void removeNote(Note note) {
        this.notes.remove(note);
        note.setContact(null);
    }

    public void addContactTag(ContactTag tag) {
        this.contactTags.add(tag);
        tag.setContact(this);
    }

    public void removeContactTag(ContactTag tag) {
        this.contactTags.remove(tag);
        tag.setContact(null);
    }

    public Object getFullName() {
        return firstName + " " + lastName;
    }
}