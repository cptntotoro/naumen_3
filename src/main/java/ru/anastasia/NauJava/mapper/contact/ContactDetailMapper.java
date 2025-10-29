package ru.anastasia.NauJava.mapper.contact;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.entity.contact.ContactDetail;

/**
 * Маппер способов связи
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactDetailMapper {

    /**
     * Смаппить DTO обновления способа связи в способ связи
     *
     * @param contactDetailUpdateDto DTO обновления способа связи
     * @return Способ связи
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    ContactDetail contactDetailUpdateDtoToContactDetail(ContactDetailUpdateDto contactDetailUpdateDto);

    /**
     * Смаппить DTO создания способа связи в способ связи
     *
     * @param contactDetailCreateDto DTO создания способа связи
     * @return Способ связи
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    ContactDetail contactDetailCreateDtoToContactDetail(ContactDetailCreateDto contactDetailCreateDto);
}
