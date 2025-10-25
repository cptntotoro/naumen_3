package ru.anastasia.NauJava.controller.socialprofile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.mapper.socialprofile.SocialProfileMapper;
import ru.anastasia.NauJava.service.socialprofile.SocialProfileService;

@Controller
@RequestMapping("/social-profiles")
@RequiredArgsConstructor
class SocialProfileController {
    /**
     * Сервис управления профилями в соцсетях
     */
    private final SocialProfileService socialProfileService;

    /**
     * Маппер профилей в соцсетях
     */
    private final SocialProfileMapper socialProfileMapper;

//    @GetMapping
//    public String listSocialProfiles(Model model) {
//        List<SocialProfile> profiles = socialProfileService.findAll();
//        model.addAttribute("profiles", profiles);
//        return "socialprofile/list";
//    }

    @GetMapping("/new")
    public String newSocialProfileForm(Model model) {
        model.addAttribute("profileDto", new SocialProfileCreateDto());
        return "socialprofile/form";
    }

    @PostMapping
    public String createSocialProfile(@Valid @ModelAttribute("profileDto") SocialProfileCreateDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "socialprofile/form";
        }
        SocialProfile profile = socialProfileMapper.toEntity(dto);
        socialProfileService.create(profile);
        return "redirect:/social-profiles";
    }

    @GetMapping("/{id}/edit")
    public String editSocialProfileForm(@PathVariable Long id, Model model) {
        SocialProfile profile = socialProfileService.findById(id);
        if (profile == null) {
            return "redirect:/social-profiles";
        }
        SocialProfileCreateDto dto = SocialProfileCreateDto.builder()
                .platform(profile.getPlatform())
                .customPlatformName(profile.getCustomPlatformName())
                .username(profile.getUsername())
                .profileUrl(profile.getProfileUrl())
                .build();
        model.addAttribute("profileDto", dto);
        model.addAttribute("profileId", id);
        return "socialprofile/edit";
    }

    @PatchMapping("/{id}")
    public String updateSocialProfile(@Valid @ModelAttribute("profileDto") SocialProfileUpdateDto socialProfileUpdateDto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "socialprofile/edit";
        }
        SocialProfile profile = socialProfileMapper.socialProfileUpdateDtoToSocialProfile(socialProfileUpdateDto);
        socialProfileService.update(profile);
        return "redirect:/social-profiles";
    }

    @PostMapping("/{id}/delete")
    public String deleteSocialProfile(@PathVariable Long id) {
        socialProfileService.delete(id);
        return "redirect:/social-profiles";
    }
}
