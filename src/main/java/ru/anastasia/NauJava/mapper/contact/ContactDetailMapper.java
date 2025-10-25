package ru.anastasia.NauJava.mapper.contact;

import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.entity.contact.ContactDetail;

/**
 * Маппер способов связи
 */
@Mapper(componentModel = "spring")
public interface ContactDetailMapper {

    ContactDetail contactDetailUpdateDtoToContactDetail(ContactDetailUpdateDto contactDetailUpdateDto);


    ContactDetail contactDetailCreateDtoToContactDetail(ContactDetailCreateDto contactDetailCreateDto);
}
