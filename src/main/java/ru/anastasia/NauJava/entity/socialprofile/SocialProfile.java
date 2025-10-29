package ru.anastasia.NauJava.entity.socialprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;

/**
 * Профиль в соцсети
 */
@Entity
@Table(name = "social_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SocialProfile {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Контакт
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    /**
     * Тип социальной платформы
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialPlatform platform;

    /**
     * Кастомный тип социальной платформы
     */
    @Column(name = "custom_platform_name")
    private String customPlatformName;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * URL профиля
     */
    @Column(name = "profile_url")
    private String profileUrl;
}
