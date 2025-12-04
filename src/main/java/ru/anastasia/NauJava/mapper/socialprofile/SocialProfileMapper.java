package ru.anastasia.NauJava.mapper.socialprofile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;

/**
 * Маппер профилей в соцсетях
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SocialProfileMapper {

    /**
     * Смаппить DTO обновления профиля в соцсети в профиль в соцсети
     *
     * @param socialProfileUpdateDto DTO обновления профиля в соцсети
     * @return Профиль в соцсети
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    SocialProfile socialProfileUpdateDtoToSocialProfile(SocialProfileUpdateDto socialProfileUpdateDto);

    /**
     * Смаппить DTO создания профиля в соцсетях в профиль в соцсети
     *
     * @param socialProfileCreateDto DTO создания профиля в соцсетях
     * @return Профиль в соцсети
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    SocialProfile socialProfileCreateDtoToSocialProfile(SocialProfileCreateDto socialProfileCreateDto);
}
