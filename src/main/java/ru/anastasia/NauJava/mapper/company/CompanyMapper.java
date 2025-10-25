package ru.anastasia.NauJava.mapper.company;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.anastasia.NauJava.dto.company.CompanyCreateDto;
import ru.anastasia.NauJava.dto.company.CompanyUpdateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.JobTitleCreateDto;
import ru.anastasia.NauJava.dto.company.JobTitleUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.company.JobTitle;

/**
 * Маппер компаний
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyCreateDto dto);

    Company toEntity(CompanyUpdateDto dto);

    JobTitle toEntity(JobTitleCreateDto dto);

    JobTitle toEntity(JobTitleUpdateDto dto);

    @Mapping(target = "contact", ignore = true)
    ContactCompany toEntity(ContactCompanyCreateDto dto);

    CompanyUpdateDto companyToCompanyUpdateDto(Company company);

    JobTitleUpdateDto jobTitleToJobTitleUpdateDto(JobTitle jobTitle);
}
