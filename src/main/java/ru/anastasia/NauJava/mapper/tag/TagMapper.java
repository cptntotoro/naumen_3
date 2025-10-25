package ru.anastasia.NauJava.mapper.tag;

import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.tag.TagCreateDto;
import ru.anastasia.NauJava.dto.tag.TagUpdateDto;
import ru.anastasia.NauJava.entity.tag.Tag;

/**
 * Маппер тегов
 */
@Mapper(componentModel = "spring")
public interface TagMapper {
    /**
     * Смаппить DTO создания тега в тег
     *
     * @param tagCreateDto DTO создания тега
     * @return Тег
     */
    Tag tagCreateDtoToTag(TagCreateDto tagCreateDto);

    /**
     * Смаппить DTO обновления тега в тег
     *
     * @param tagUpdateDto DTO обновления тега
     * @return Тег
     */
    Tag tagUpdateDtoToTag(TagUpdateDto tagUpdateDto);


    TagUpdateDto tagToTagUpdateDto(Tag tag);
}
