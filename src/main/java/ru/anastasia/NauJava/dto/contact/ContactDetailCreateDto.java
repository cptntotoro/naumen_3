package ru.anastasia.NauJava.dto.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailCreateDto {
    @NotNull(message = "Тип обязателен")
    private DetailType detailType;
    @NotNull(message = "Лейбл обязателен")
    private DetailLabel label;
    @NotBlank(message = "Значение обязательно")
    private String value;
    private Boolean isPrimary;
}
