package ru.anastasia.NauJava.entity.contact;

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
import ru.anastasia.NauJava.entity.enums.SocialPlatform;

/**
 * Профиль в соцсети
 */
@Entity
@Table(name = "social_profiles")
public class SocialProfile {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public SocialProfile() {
    }

    public SocialProfile(SocialPlatform platform, String username) {
        this.platform = platform;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public SocialPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SocialPlatform platform) {
        this.platform = platform;
    }

    public String getCustomPlatformName() {
        return customPlatformName;
    }

    public void setCustomPlatformName(String customPlatformName) {
        this.customPlatformName = customPlatformName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
