package ru.anastasia.NauJava.dto.socialprofile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;

/**
 * DTO обновления профиля в соцсети
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialProfileUpdateDto {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Тип социальной платформы
     */
    @NotNull(message = "Платформа обязательна")
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
