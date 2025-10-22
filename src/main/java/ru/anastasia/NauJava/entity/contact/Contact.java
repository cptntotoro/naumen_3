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
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.tag.ContactTag;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Контакт
 */
@Entity
@Table(name = "contacts")
public class Contact {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Set<ContactCompany> companies = new HashSet<>();

    /**
     * Способы связи
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactDetail> contactDetails = new HashSet<>();

    /**
     * Профили в соцсетях
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialProfile> socialProfiles = new HashSet<>();

    /**
     * События
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();

    /**
     * Заметки
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Note> notes = new HashSet<>();

    /**
     * Теги
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactTag> contactTags = new HashSet<>();

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

    public Contact() {
    }

    public Contact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Set<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(Set<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public Set<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public void setSocialProfiles(Set<SocialProfile> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ContactCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<ContactCompany> companies) {
        this.companies = companies;
    }

    public Set<ContactTag> getContactTags() {
        return contactTags;
    }

    public void setContactTags(Set<ContactTag> contactTags) {
        this.contactTags = contactTags;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", isFavorite=" + isFavorite +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", companies=" + companies +
                ", contactDetails=" + contactDetails +
                ", socialProfiles=" + socialProfiles +
                ", events=" + events +
                ", notes=" + notes +
                ", contactTags=" + contactTags +
                '}';
    }
}
