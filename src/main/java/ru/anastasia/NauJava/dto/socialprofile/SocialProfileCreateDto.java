package ru.anastasia.NauJava.dto.socialprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;

/**
 * DTO создания профиля в соцсетях
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialProfileCreateDto {

    /**
     * Идентификатор контакта
     */
    private Long contactId;

    /**
     * Тип социальной платформы
     */
    private SocialPlatform platform;

    /**
     * Кастомный тип социальной платформы
     */
    private String customPlatformName;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * URL профиля
     */
    private String profileUrl;
}
