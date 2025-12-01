package ru.anastasia.NauJava.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
     * Идентификатор контакта
     */
    @NotNull(message = "ID контакта обязателен")
    private Long contactId;

    /**
     * Тело заметки
     */
    @NotBlank(message = "Содержимое заметки обязательно")
    private String content;

    /**
     * Дата и время создания
     */
    private LocalDateTime createdAt;
}
