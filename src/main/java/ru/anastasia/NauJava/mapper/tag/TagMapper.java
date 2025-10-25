package ru.anastasia.NauJava.mapper.tag;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.tag.TagFormDto;
import ru.anastasia.NauJava.entity.tag.Tag;

/**
 * Маппер тегов
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {

    /**
     * Смаппить DTO для формы тега в тег
     *
     * @param tagFormDto DTO для формы тега
     * @return Тег
     */
    @Mapping(target = "contactTags", ignore = true)
    Tag tagFormDtoToTag(TagFormDto tagFormDto);

    /**
     * Смаппить тег в DTO для формы тега
     *
     * @param tag Тег
     * @return DTO для формы тега
     */
    TagFormDto tagToTagFormDto(Tag tag);
}
