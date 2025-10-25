package ru.anastasia.NauJava.dto.socialprofile;

import jakarta.validation.constraints.NotBlank;
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
    private Long id;

    @NotNull(message = "Платформа обязательна")
    private SocialPlatform platform;

    private String customPlatformName;

    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    private String profileUrl;
}
