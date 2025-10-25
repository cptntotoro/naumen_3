package ru.anastasia.NauJava.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCompanyCreateDto {
    @NotBlank(message = "Название компании обязательно")
    private String companyName;
    private String companyWebsite;
    @NotBlank(message = "Название должности обязательно")
    private String jobTitle;
    private Boolean isCurrent;
}
