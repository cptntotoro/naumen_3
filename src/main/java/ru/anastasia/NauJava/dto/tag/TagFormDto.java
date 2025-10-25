package ru.anastasia.NauJava.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для формы тега (создание и редактирование)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagFormDto {
    /**
     * Идентификатор
     */
    @NotNull(message = "Идентификатор тега обязателен")
    private Long id;

    /**
     * Название
     */
    @NotBlank(message = "Название тега обязательно")
    private String name;

    /**
     * Цвет
     */
    private String color;

    /**
     * Описание
     */
    private String description;
}
