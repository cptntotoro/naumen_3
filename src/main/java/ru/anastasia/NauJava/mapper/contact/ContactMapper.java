package ru.anastasia.NauJava.mapper.contact;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.entity.contact.Contact;

/**
 * Маппер контактов
 */
@Mapper(componentModel = "spring")
public interface ContactMapper {

    /**
     * Смаппить DTO создания контакта в контакт
     *
     * @param contactCreateDto DTO создания контакта
     * @return Контакт
     */
    @Mapping(target = "contactDetails", ignore = true)
    @Mapping(target = "socialProfiles", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "contactTags", ignore = true)
    @Mapping(target = "companies", ignore = true)
    Contact contactCreateDtoToContact(ContactCreateDto contactCreateDto);

    /**
     * Смаппить DTO обновления контакта в контакт
     *
     * @param contactUpdateDto DTO обновления контакта
     * @return Контакт
     */
    @Mapping(target = "contactDetails", ignore = true)
    @Mapping(target = "socialProfiles", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "contactTags", ignore = true)
    @Mapping(target = "companies", ignore = true)
    Contact contactUpdateDtoToContact(ContactUpdateDto contactUpdateDto);
}
