package ru.anastasia.NauJava.dto.company;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO создания компании контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCompanyCreateDto {

    /**
     * Идентификатор компании
     */
    @NotNull(message = "Компания не может быть пустой")
    private Long companyId;

    /**
     * Идентификатор должности
     */
    @NotNull(message = "Должность не может быть пустой")
    private Long jobTitleId;

    /**
     * Является ли текущим местом работы
     */
    private Boolean isCurrent;
}
