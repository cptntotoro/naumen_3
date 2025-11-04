package ru.anastasia.NauJava.dto.note;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO обновления заметки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteUpdateDto {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Тело заметки
     */
    @NotBlank(message = "Содержимое заметки обязательно")
    private String content;
}
