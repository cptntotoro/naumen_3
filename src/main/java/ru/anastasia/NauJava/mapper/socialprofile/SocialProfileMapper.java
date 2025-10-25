package ru.anastasia.NauJava.mapper.socialprofile;

import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;

/**
 * Маппер профилей в соцсетях
 */
@Mapper(componentModel = "spring")
public interface SocialProfileMapper {

    /**
     * Смаппить профиль в соцсети
     *
     * @param socialProfileCreateDto DTO создания профиля в соцсети
     * @return Профиль в соцсети
     */
    SocialProfile toEntity(SocialProfileCreateDto socialProfileCreateDto);

    SocialProfile socialProfileUpdateDtoToSocialProfile(SocialProfileUpdateDto socialProfileUpdateDto);
}
