package ru.anastasia.NauJava.dto.company;

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
    private Long companyId;

    /**
     * Идентификатор должности
     */
    private Long jobTitleId;
}
