package ru.anastasia.NauJava.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO создания и обновления должности
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTitleFormDto {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Название
     */
    @NotBlank(message = "Название должности обязательно")
    private String title;
}
