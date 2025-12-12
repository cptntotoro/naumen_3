package ru.anastasia.NauJava.mapper.contact;

import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Маппер для управления контактами со сложными преобразованиями
 */
@Mapper(componentModel = "spring")
public interface ContactManagementMapper {
    default ContactUpdateDto contactFullDetailsToContactUpdateDto(ContactFullDetails details) {
        if (details == null) {
            return null;
        }

        return ContactUpdateDto.builder()
                .id(details.getContact() != null ? details.getContact().getId() : null)
                .firstName(details.getContact() != null ? details.getContact().getFirstName() : null)
                .lastName(details.getContact() != null ? details.getContact().getLastName() : null)
                .displayName(details.getContact() != null ? details.getContact().getDisplayName() : null)
                .avatarUrl(details.getContact() != null ? details.getContact().getAvatarUrl() : null)
                .isFavorite(details.getContact() != null ? details.getContact().getIsFavorite() : null)
                .contactDetails(details.getContactDetails() != null ?
                        details.getContactDetails().stream()
                                .map(detail -> ContactDetailUpdateDto.builder()
                                        .id(detail.getId())
                                        .detailType(detail.getDetailType())
                                        .label(detail.getLabel())
                                        .value(detail.getValue())
                                        .isPrimary(detail.getIsPrimary())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .socialProfiles(details.getSocialProfiles() != null ?
                        details.getSocialProfiles().stream()
                                .map(profile -> SocialProfileUpdateDto.builder()
                                        .id(profile.getId())
                                        .platform(profile.getPlatform())
                                        .customPlatformName(profile.getCustomPlatformName())
                                        .username(profile.getUsername())
                                        .profileUrl(profile.getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .companies(details.getContact() != null && details.getContact().getCompanies() != null ?
                        details.getContact().getCompanies().stream()
                                .map(company -> ContactCompanyUpdateDto.builder()
                                        .id(company.getId())
                                        .companyId(company.getCompany() != null ? company.getCompany().getId() : null)
                                        .jobTitleId(company.getJobTitle() != null ? company.getJobTitle().getId() : null)
                                        .isCurrent(company.getIsCurrent())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .events(details.getEvents() != null ?
                        details.getEvents().stream()
                                .map(event -> EventUpdateDto.builder()
                                        .id(event.getId())
                                        .eventType(event.getEventType())
                                        .customEventName(event.getCustomEventName())
                                        .eventDate(event.getEventDate())
                                        .notes(event.getNotes())
                                        .yearlyRecurrence(event.getYearlyRecurrence())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .notes(details.getNotes() != null ?
                        details.getNotes().stream()
                                .map(note -> NoteUpdateDto.builder()
                                        .id(note.getId())
                                        .content(note.getContent())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .tagIds(details.getTags() != null ?
                        details.getTags().stream()
                                .map(Tag::getId)
                                .collect(Collectors.toSet()) :
                        Collections.emptySet())
                .build();
    }
}