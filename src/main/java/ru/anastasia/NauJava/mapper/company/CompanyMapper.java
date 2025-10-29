package ru.anastasia.NauJava.mapper.company;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.company.CompanyFormDto;
import ru.anastasia.NauJava.dto.company.JobTitleFormDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.JobTitle;

/**
 * Маппер компаний
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {

    /**
     * Смаппить DTO создания и обновления компании в компанию
     *
     * @param companyFormDto DTO создания и обновления компании
     * @return Компания
     */
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Company companyFormDtoToCompany(CompanyFormDto companyFormDto);

    /**
     * Смаппить компанию в DTO создания и обновления компании
     *
     * @param company Компания
     * @return DTO создания и обновления компании
     */
    CompanyFormDto companyToCompanyFormDto(Company company);

    /**
     * Смаппить должность в DTO создания и обновления должности
     *
     * @param jobTitle Должность
     * @return DTO создания и обновления должности
     */
    JobTitleFormDto jobTitleToJobTitleFormDto(JobTitle jobTitle);

    /**
     * Смаппить DTO создания и обновления должности в должность
     *
     * @param jobTitleFormDto DTO создания и обновления должности
     * @return Должность
     */
    JobTitle jobTitleFormDtoToJobTitle(JobTitleFormDto jobTitleFormDto);
}
