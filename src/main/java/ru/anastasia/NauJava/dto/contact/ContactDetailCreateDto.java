package ru.anastasia.NauJava.dto.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

/**
 * DTO создания способа связи
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailCreateDto {
    /**
     * Тип способа связи
     */
    @NotNull(message = "Тип обязателен")
    private DetailType detailType;

    /**
     * Лейбл способа связи
     */
    @NotNull(message = "Лейбл обязателен")
    private DetailLabel label;

    /**
     * Значение
     */
    @NotBlank(message = "Значение обязательно")
    private String value;

    /**
     * Флаг, является ли основным типом связи
     */
    private Boolean isPrimary;
}
