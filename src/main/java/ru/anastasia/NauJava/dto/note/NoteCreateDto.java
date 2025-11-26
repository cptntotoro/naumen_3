package ru.anastasia.NauJava.dto.note;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO создания заметки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateDto {
    /**
     * Идентификатор контакта
     */
    @NotNull(message = "Идентификатор контакта обязателен")
    private Long contactId;

    /**
     * Тело заметки
     */
    @NotBlank(message = "Содержимое заметки обязательно")
    private String content;

    /**
     * Дата и время создания
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
