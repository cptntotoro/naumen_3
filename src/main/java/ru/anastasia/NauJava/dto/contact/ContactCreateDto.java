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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO создания контакта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCreateDto {
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
    @Builder.Default
    private Boolean isFavorite = false;

    /**
     * Список компаний
     */
    @Builder.Default
    private List<ContactCompanyCreateDto> companies = new ArrayList<>();

    /**
     * Список способов связи
     */
    @Builder.Default
    private List<ContactDetailCreateDto> contactDetails = new ArrayList<>();

    /**
     * Список социальных сетей
     */
    @Builder.Default
    private List<SocialProfileCreateDto> socialProfiles = new ArrayList<>();

    /**
     * Идентификаторы тегов
     */
    @Builder.Default
    private Set<Long> tagIds = new HashSet<>();

    /**
     * Список событий
     */
    @Builder.Default
    private List<EventCreateDto> events = new ArrayList<>();

    /**
     * Список заметок
     */
    @Builder.Default
    private List<NoteCreateDto> notes = new ArrayList<>();
}
