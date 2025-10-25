package ru.anastasia.NauJava.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDto {
    @NotNull(message = "Тип события обязателен")
    private EventType eventType;
    private String customEventName;
    @NotNull(message = "Дата события обязательна")
    private LocalDate eventDate;
    private String notes;
    private Boolean yearlyRecurrence;
}
