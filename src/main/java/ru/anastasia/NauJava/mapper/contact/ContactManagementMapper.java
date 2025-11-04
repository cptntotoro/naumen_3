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
    default ContactUpdateDto contactFullDetailsToContactUpdateDto(ContactFullDetails contactDetails) {
        return ContactUpdateDto.builder()
                .id(contactDetails.getContact().getId())
                .firstName(contactDetails.getContact().getFirstName())
                .lastName(contactDetails.getContact().getLastName())
                .displayName(contactDetails.getContact().getDisplayName())
                .avatarUrl(contactDetails.getContact().getAvatarUrl())
                .isFavorite(contactDetails.getContact().getIsFavorite())
                .tagIds(contactDetails.getTags().stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet()))
                .contactDetails(contactDetails.getContactDetails().stream().map(contactDetail -> ContactDetailUpdateDto.builder()
                        .id(contactDetail.getId())
                        .detailType(contactDetail.getDetailType())
                        .value(contactDetail.getValue())
                        .isPrimary(contactDetail.getIsPrimary())
                        .label(contactDetail.getLabel())
                        .build()).collect(Collectors.toList())
                )
                .socialProfiles(contactDetails.getSocialProfiles().stream().map(socialProfile -> SocialProfileUpdateDto.builder()
                        .id(socialProfile.getId())
                        .platform(socialProfile.getPlatform())
                        .profileUrl(socialProfile.getProfileUrl())
                        .customPlatformName(socialProfile.getCustomPlatformName())
                        .username(socialProfile.getUsername())
                        .build()).collect(Collectors.toList())
                )
                .companies(contactDetails.getContact().getCompanies().stream().map(contactCompany -> ContactCompanyUpdateDto.builder()
                        .id(contactCompany.getId())
                        .jobTitleId(contactCompany.getJobTitle().getId())
                        .companyId(contactCompany.getCompany().getId())
                        .build()).collect(Collectors.toList())
                )
                .notes(contactDetails.getNotes().stream().map(note -> NoteUpdateDto.builder()
                        .id(note.getId())
                        .content(note.getContent())
                        .build()).collect(Collectors.toList())
                )
                .events(contactDetails.getEvents().stream().map(event -> EventUpdateDto.builder()
                        .id(event.getId())
                        .eventType(event.getEventType())
                        .customEventName(event.getCustomEventName())
                        .eventDate(event.getEventDate())
                        .notes(event.getNotes())
                        .yearlyRecurrence(event.getYearlyRecurrence())
                        .build()).collect(Collectors.toList())
                )
                .build();
    }
}