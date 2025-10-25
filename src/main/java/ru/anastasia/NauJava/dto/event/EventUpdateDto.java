package ru.anastasia.NauJava.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;

/**
 * DTO обновления события
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {
    /**
     * Идентификатор
     */
    @NotNull(message = "Идентификатор события обязателен")
    private Long id;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
