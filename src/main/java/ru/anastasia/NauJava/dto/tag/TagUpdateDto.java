package ru.anastasia.NauJava.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO обновления тега
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateDto {

    private Long id;
    @NotBlank(message = "Название тега обязательно")
    private String name;
    private String color;
    private String description;
}
