package ru.anastasia.NauJava.controller.socialprofile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
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

    @PostMapping("/new")
    public String createSocialProfileFromDetails(
            @Valid @ModelAttribute("socialProfileDto") SocialProfileCreateDto socialProfileCreateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        Long contactId = socialProfileCreateDto.getContactId();

        log.info("POST /social-profiles/new - создание соц. профиля для контакта ID: {}", contactId);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании соц. профиля: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.socialProfileDto", bindingResult);
            redirectAttributes.addFlashAttribute("socialProfileDto", socialProfileCreateDto);
            redirectAttributes.addAttribute("error", "social_profile_validation");
            return "redirect:/contacts/" + contactId;
        }

        try {
            SocialProfile profile = socialProfileMapper.socialProfileCreateDtoToSocialProfile(socialProfileCreateDto);
            SocialProfile createdProfile = socialProfileService.createForContact(contactId, profile);

            log.info("Социальный профиль успешно создан [ID: {}] для контакта ID: {}",
                    createdProfile.getId(), contactId);

            redirectAttributes.addAttribute("success", "social_profile_created");
            return "redirect:/contacts/" + contactId;
        } catch (Exception e) {
            log.error("Ошибка при создании соц. профиля для контакта ID: {}", contactId, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании профиля: " + e.getMessage());
            redirectAttributes.addAttribute("error", "social_profile_creation");
            return "redirect:/contacts/" + contactId;
        }
    }

    @PostMapping("/{id}/update")
    public String updateSocialProfileFromDetails(
            @PathVariable Long id,
            @Valid @ModelAttribute("socialProfileDto") SocialProfileUpdateDto socialProfileUpdateDto,
            BindingResult bindingResult,
            @RequestParam("contactId") Long contactId,
            RedirectAttributes redirectAttributes) {

        log.info("POST /social-profiles/{}/update - обновление соц. профиля", id);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении соц. профиля: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.socialProfileDto", bindingResult);
            redirectAttributes.addFlashAttribute("socialProfileDto", socialProfileUpdateDto);
            redirectAttributes.addAttribute("error", "social_profile_update_failed");
            return "redirect:/contacts/" + contactId;
        }

        try {
            SocialProfile profile = socialProfileMapper.socialProfileUpdateDtoToSocialProfile(socialProfileUpdateDto);
            profile.setId(id);
            socialProfileService.update(profile);

            log.info("Социальный профиль успешно обновлен [ID: {}]", id);
            redirectAttributes.addAttribute("success", "social_profile_updated");
            return "redirect:/contacts/" + contactId;
        } catch (Exception e) {
            log.error("Ошибка при обновлении соц. профиля ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "social_profile_update_failed");
            return "redirect:/contacts/" + contactId;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSocialProfile(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        log.info("POST /social-profiles/{}/delete - удаление соц. профиля", id);

        try {
            Long contactId = socialProfileService.findById(id).getContact().getId();
            socialProfileService.delete(id);
            log.info("Социальный профиль успешно удален [ID: {}]", id);
            redirectAttributes.addAttribute("success", "social_profile_deleted");
            return "redirect:/contacts/" + contactId;
        } catch (Exception e) {
            log.error("Ошибка при удалении соц. профиля [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "social_profile_delete_failed");
            try {
                Long contactId = socialProfileService.findById(id).getContact().getId();
                return "redirect:/contacts/" + contactId;
            } catch (Exception ex) {
                return "redirect:/contacts";
            }
        }
    }
}
