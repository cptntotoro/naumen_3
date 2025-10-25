package ru.anastasia.NauJava.dto.contact;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;

import java.util.List;

/**
 * DTO создания контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCreateDto {
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;
    private String displayName;
    private String avatarUrl;
    private Boolean isFavorite;
    private List<ContactDetailCreateDto> contactDetails;
    private List<SocialProfileCreateDto> socialProfiles;
    private List<EventCreateDto> events;
    private List<NoteCreateDto> notes;
    private List<String> tagNames;
    private List<ContactCompanyCreateDto> companies;
}
