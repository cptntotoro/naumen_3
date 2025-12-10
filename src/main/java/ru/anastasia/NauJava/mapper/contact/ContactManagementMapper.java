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

import java.util.stream.Collectors;

/**
 * Маппер для управления контактами со сложными преобразованиями
 */
@Mapper(componentModel = "spring")
public interface ContactManagementMapper {
    default ContactUpdateDto contactFullDetailsToContactUpdateDto(ContactFullDetails details) {
        return ContactUpdateDto.builder()
                .id(details.getContact().getId())
                .firstName(details.getContact().getFirstName())
                .lastName(details.getContact().getLastName())
                .displayName(details.getContact().getDisplayName())
                .avatarUrl(details.getContact().getAvatarUrl())
                .isFavorite(details.getContact().getIsFavorite())
                .contactDetails(details.getContactDetails().stream()
                        .map(detail -> ContactDetailUpdateDto.builder()
                                .id(detail.getId())
                                .detailType(detail.getDetailType())
                                .label(detail.getLabel())
                                .value(detail.getValue())
                                .isPrimary(detail.getIsPrimary())
                                .build())
                        .collect(Collectors.toList()))
                .socialProfiles(details.getSocialProfiles().stream()
                        .map(profile -> SocialProfileUpdateDto.builder()
                                .id(profile.getId())
                                .platform(profile.getPlatform())
                                .customPlatformName(profile.getCustomPlatformName())
                                .username(profile.getUsername())
                                .profileUrl(profile.getProfileUrl())
                                .build())
                        .collect(Collectors.toList()))
                .companies(details.getContact().getCompanies().stream()
                        .map(company -> ContactCompanyUpdateDto.builder()
                                .id(company.getId())
                                .companyId(company.getCompany().getId())
                                .jobTitleId(company.getJobTitle() != null ? company.getJobTitle().getId() : null)
                                .isCurrent(company.getIsCurrent())
                                .build())
                        .collect(Collectors.toList()))
                .events(details.getEvents().stream()
                        .map(event -> EventUpdateDto.builder()
                                .id(event.getId())
                                .eventType(event.getEventType())
                                .customEventName(event.getCustomEventName())
                                .eventDate(event.getEventDate())
                                .notes(event.getNotes())
                                .yearlyRecurrence(event.getYearlyRecurrence())
                                .build())
                        .collect(Collectors.toList()))
                .notes(details.getNotes().stream()
                        .map(note -> NoteUpdateDto.builder()
                                .id(note.getId())
                                .content(note.getContent())
                                .build())
                        .collect(Collectors.toList()))
                .tagIds(details.getTags().stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}