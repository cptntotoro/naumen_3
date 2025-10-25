package ru.anastasia.NauJava.dto.contact;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO обновления контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUpdateDto {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Имя
     */
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    /**
     * Фамилия
     */
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    /**
     * Имя для отображения
     */
    private String displayName;

    /**
     * URL аватара
     */
    private String avatarUrl;

    /**
     * Флаг избранного контакта
     */
    private Boolean isFavorite;

    /**
     * Список DTO обновления способа связи
     */
    @Builder.Default
    private List<ContactDetailUpdateDto> contactDetails = new ArrayList<>();

    /**
     * Список DTO обновления профиля в соцсети
     */
    @Builder.Default
    private List<SocialProfileUpdateDto> socialProfiles = new ArrayList<>();

    /**
     * Список DTO обновления события
     */
    @Builder.Default
    private List<EventUpdateDto> events = new ArrayList<>();

    /**
     * Список DTO обновления заметки
     */
    @Builder.Default
    private List<NoteUpdateDto> notes = new ArrayList<>();

    /**
     * Список идентификаторов тегов
     */
    @Builder.Default
    private Set<Long> tagIds = new HashSet<>();

    /**
     * Список DTO обновления компании контакта
     */
    @Builder.Default
    private List<ContactCompanyUpdateDto> companies = new ArrayList<>();
}
