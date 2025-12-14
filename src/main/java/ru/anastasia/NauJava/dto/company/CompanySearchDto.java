package ru.anastasia.NauJava.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для поиска компаний
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchDto {
    /**
     * Поисковый запрос (часть названия компании)
     */
    private String search;
}