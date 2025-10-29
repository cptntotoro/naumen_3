package ru.anastasia.NauJava.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO создания и обновления компании
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFormDto {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Название
     */
    @NotBlank(message = "Название компании обязательно")
    private String name;

    /**
     * Адрес сайта
     */
    private String website;
}
