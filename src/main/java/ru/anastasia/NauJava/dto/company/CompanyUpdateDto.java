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
public class CompanyUpdateDto {
    @NotBlank(message = "Название компании обязательно")
    private String name;
    private String website;
}
