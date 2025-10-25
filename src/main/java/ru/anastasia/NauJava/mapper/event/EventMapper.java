package ru.anastasia.NauJava.mapper.event;

import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.event.Event;

/**
 * Маппер событий
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    Event eventCreateDtoToEvent(EventCreateDto dto);

    Event eventCreateDtoToEvent(EventUpdateDto dto);

    EventUpdateDto eventToEventUpdateDto(Event event);

    Event eventUpdateDtoToEvent(@Valid EventUpdateDto eventUpdateDto);
}
