package ru.anastasia.NauJava.dto.contact;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;

import java.util.List;

/**
 * DTO обновления контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUpdateDto {
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    private String displayName;

    private String avatarUrl;

    private Boolean isFavorite;

    private List<ContactDetailUpdateDto> contactDetails;

    private List<SocialProfileUpdateDto> socialProfiles;

    private List<EventUpdateDto> events;

    private List<NoteUpdateDto> notes;

    private List<String> tagNames;

    private List<ContactCompanyCreateDto> companies;
}
