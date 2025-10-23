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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ContactCompany> companies = new HashSet<>();

    /**
     * Способы связи
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ContactDetail> contactDetails = new HashSet<>();

    /**
     * Профили в соцсетях
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SocialProfile> socialProfiles = new HashSet<>();

    /**
     * События
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Event> events = new HashSet<>();

    /**
     * Заметки
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Note> notes = new HashSet<>();

    /**
     * Теги
     */
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
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
}
