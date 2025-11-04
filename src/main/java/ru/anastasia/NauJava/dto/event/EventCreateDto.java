package ru.anastasia.NauJava.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;

/**
 * DTO создания события
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDto {
    /**
     * Идентификатор контакта
     */
    @NotNull(message = "Идентификатор контакта обязателен")
    private Long contactId;

    /**
     * Тип события
     */
    @NotNull(message = "Тип события обязателен")
    private EventType eventType;

    /**
     * Кастомный тип события
     */
    private String customEventName;

    /**
     * Дата события
     */
    @NotNull(message = "Дата события обязательна")
    private LocalDate eventDate;

    /**
     * Заметка
     */
    private String notes;

    /**
     * Флаг, повторяется ли событие ежегодно
     */
    private Boolean yearlyRecurrence;
}
