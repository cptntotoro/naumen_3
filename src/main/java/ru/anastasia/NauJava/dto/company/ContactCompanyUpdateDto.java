package ru.anastasia.NauJava.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO обновления компании контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCompanyUpdateDto {
    /**
     * Идентификатор контакта
     */
    private Long id;

    /**
     * Идентификатор компании
     */
    private Long companyId;

    /**
     * Идентификатор должности
     */
    private Long jobTitleId;

    /**
     * Является ли текущим местом работы
     */
    private Boolean isCurrent;
}
