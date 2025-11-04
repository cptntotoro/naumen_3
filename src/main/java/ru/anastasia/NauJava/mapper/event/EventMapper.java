package ru.anastasia.NauJava.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.event.Event;

/**
 * Маппер событий
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    /**
     * Смаппить DTO создания события в событие
     *
     * @param eventCreateDto DTO создания события
     * @return Событие
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    Event eventCreateDtoToEvent(EventCreateDto eventCreateDto);

    /**
     * Смаппить событие в DTO обновления события
     *
     * @param event Событие
     * @return DTO обновления события
     */
    EventUpdateDto eventToEventUpdateDto(Event event);

    /**
     * Смаппить DTO обновления события в событие
     *
     * @param eventUpdateDto DTO обновления события
     * @return Событие
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    Event eventUpdateDtoToEvent(EventUpdateDto eventUpdateDto);
}
