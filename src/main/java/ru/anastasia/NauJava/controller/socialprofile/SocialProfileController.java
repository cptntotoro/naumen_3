package ru.anastasia.NauJava.controller.socialprofile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.mapper.socialprofile.SocialProfileMapper;
import ru.anastasia.NauJava.service.socialprofile.SocialProfileService;

@Slf4j
@Controller
@RequestMapping("/social-profiles")
@RequiredArgsConstructor
public class SocialProfileController {

    /**
     * Сервис управления профилями в соцсетях
     */
    private final SocialProfileService socialProfileService;

    /**
     * Маппер профилей в соцсетях
     */
    private final SocialProfileMapper socialProfileMapper;

    @GetMapping("/{id}/edit")
    public String editSocialProfileForm(@PathVariable Long id, Model model) {
        log.debug("GET /social-profiles/{}/edit - форма редактирования соц. профиля", id);

        try {
            SocialProfile profile = socialProfileService.findById(id);
            if (profile == null) {
                log.warn("Социальный профиль не найден при редактировании [ID: {}]", id);
                return "redirect:/social-profiles";
            }

            SocialProfileUpdateDto dto = socialProfileMapper.socialProfileToSocialProfileUpdateDto(profile);
            model.addAttribute("profileDto", dto);

            log.debug("Данные для редактирования соц. профиля подготовлены [ID: {}, платформа: {}]",
                    id, profile.getPlatform());
            return "socialprofile/edit";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования соц. профиля [ID: {}]", id, e);
            throw e;
        }
    }

    @PostMapping("/edit")
    public String updateSocialProfile(@Valid @ModelAttribute("profileDto") SocialProfileUpdateDto socialProfileUpdateDto,
                                      BindingResult bindingResult) {
        log.info("POST /social-profiles/edit - обновление соц. профиля [ID: {}]",
                socialProfileUpdateDto.getId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении соц. профиля [ID: {}]: {}",
                    socialProfileUpdateDto.getId(), bindingResult.getAllErrors());
            return "socialprofile/edit";
        }

        try {
            SocialProfile profile = socialProfileMapper.socialProfileUpdateDtoToSocialProfile(socialProfileUpdateDto);
            SocialProfile updatedProfile = socialProfileService.update(profile);
            log.info("Социальный профиль успешно обновлен [ID: {}, платформа: {}, пользователь: {}]",
                    updatedProfile.getId(), updatedProfile.getPlatform(), updatedProfile.getUsername());
            return "redirect:/social-profiles";
        } catch (Exception e) {
            log.error("Ошибка при обновлении соц. профиля [ID: {}]", socialProfileUpdateDto.getId(), e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSocialProfile(@PathVariable Long id) {
        log.info("POST /social-profiles/{}/delete - удаление соц. профиля", id);

        try {
            socialProfileService.delete(id);
            log.info("Социальный профиль успешно удален [ID: {}]", id);
            return "redirect:/social-profiles";
        } catch (Exception e) {
            log.error("Ошибка при удалении соц. профиля [ID: {}]", id, e);
            throw e;
        }
    }
}
