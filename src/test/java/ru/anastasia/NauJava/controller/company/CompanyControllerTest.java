package ru.anastasia.NauJava.controller.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.anastasia.NauJava.dto.company.CompanyFormDto;
import ru.anastasia.NauJava.dto.company.CompanySearchDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.company.CompanyMapper;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private TagService tagService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CompanyController companyController;

    private Company testCompany;
    private CompanyFormDto testCompanyFormDto;
    private CompanySearchDto testCompanySearchDto;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1L)
                .name("Тестовая компания")
                .website("https://test-company.ru")
                .build();

        testCompanyFormDto = CompanyFormDto.builder()
                .name("Тестовая компания")
                .website("https://test-company.ru")
                .build();

        testCompanySearchDto = CompanySearchDto.builder()
                .search("тест")
                .build();
    }

    @Test
    void listCompanies_WhenSearchParamIsNull_ShouldReturnAllCompanies() {
        List<Company> companies = Arrays.asList(testCompany, createAnotherCompany());
        List<Tag> tags = Arrays.asList(new Tag(), new Tag());

        when(companyService.findAll()).thenReturn(companies);
        when(tagService.findAll()).thenReturn(tags);

        String viewName = companyController.listCompanies(null, model);

        assertEquals("company/list", viewName);

        verify(companyService, times(1)).findAll();
        verify(companyService, never()).findByNameContaining(anyString());
        verify(tagService, times(1)).findAll();

        verify(model).addAttribute(eq("companies"), eq(companies));
        verify(model).addAttribute(eq("tags"), eq(tags));
        verify(model).addAttribute(eq("searchParam"), eq(null));
        verify(model).addAttribute(eq("searchDto"), any(CompanySearchDto.class));
    }

    @Test
    void listCompanies_WhenSearchParamIsEmpty_ShouldReturnAllCompanies() {
        List<Company> companies = Arrays.asList(testCompany, createAnotherCompany());
        List<Tag> tags = Collections.singletonList(new Tag());

        when(companyService.findAll()).thenReturn(companies);
        when(tagService.findAll()).thenReturn(tags);

        String viewName = companyController.listCompanies("   ", model);

        assertEquals("company/list", viewName);

        verify(companyService, times(1)).findAll();
        verify(companyService, never()).findByNameContaining(anyString());
    }

    @Test
    void listCompanies_WhenSearchParamIsNotEmpty_ShouldReturnFilteredCompanies() {
        String searchQuery = "тест";
        List<Company> filteredCompanies = Collections.singletonList(testCompany);
        List<Tag> tags = Collections.singletonList(new Tag());

        when(companyService.findByNameContaining(searchQuery.trim())).thenReturn(filteredCompanies);
        when(tagService.findAll()).thenReturn(tags);

        String viewName = companyController.listCompanies(searchQuery, model);

        assertEquals("company/list", viewName);

        verify(companyService, times(1)).findByNameContaining(searchQuery.trim());
        verify(companyService, never()).findAll();
        verify(tagService, times(1)).findAll();

        verify(model).addAttribute(eq("companies"), eq(filteredCompanies));
        verify(model).addAttribute(eq("searchParam"), eq(searchQuery));
    }

    @Test
    void listCompanies_WhenNoCompaniesFound_ShouldReturnEmptyList() {
        String searchQuery = "несуществующая";
        List<Company> emptyList = Collections.emptyList();
        List<Tag> tags = Collections.singletonList(new Tag());

        when(companyService.findByNameContaining(searchQuery.trim())).thenReturn(emptyList);
        when(tagService.findAll()).thenReturn(tags);

        String viewName = companyController.listCompanies(searchQuery, model);

        assertEquals("company/list", viewName);

        verify(companyService, times(1)).findByNameContaining(searchQuery.trim());
        verify(model).addAttribute(eq("companies"), eq(emptyList));
    }

    @Test
    void newCompanyForm_ShouldReturnFormView() {
        String viewName = companyController.newCompanyForm(model);

        assertEquals("company/form", viewName);
        verify(model).addAttribute(eq("companyDto"), any(CompanyFormDto.class));
        verify(model).addAttribute(eq("isEdit"), eq(false));
    }

    @Test
    void createCompany_WhenValidData_ShouldRedirectToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(companyMapper.companyFormDtoToCompany(testCompanyFormDto)).thenReturn(testCompany);
        when(companyService.create(testCompany)).thenReturn(testCompany);

        String viewName = companyController.createCompany(testCompanyFormDto, bindingResult, model);

        assertEquals("redirect:/companies", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, times(1)).companyFormDtoToCompany(testCompanyFormDto);
        verify(companyService, times(1)).create(testCompany);
        verify(model, never()).addAttribute(eq("error"), anyString());
    }

    @Test
    void createCompany_WhenValidationErrors_ShouldReturnFormView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = companyController.createCompany(testCompanyFormDto, bindingResult, model);

        assertEquals("company/form", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, never()).companyFormDtoToCompany(any());
        verify(companyService, never()).create(any());
        verify(model).addAttribute(eq("isEdit"), eq(false));
    }

    @Test
    void createCompany_WhenServiceThrowsException_ShouldReturnFormWithError() {
        String errorMessage = "Компания с таким именем уже существует";

        when(bindingResult.hasErrors()).thenReturn(false);
        when(companyMapper.companyFormDtoToCompany(testCompanyFormDto)).thenReturn(testCompany);
        when(companyService.create(testCompany)).thenThrow(new RuntimeException(errorMessage));

        String viewName = companyController.createCompany(testCompanyFormDto, bindingResult, model);

        assertEquals("company/form", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, times(1)).companyFormDtoToCompany(testCompanyFormDto);
        verify(companyService, times(1)).create(testCompany);
        verify(model).addAttribute(eq("error"), eq(errorMessage));
        verify(model).addAttribute(eq("isEdit"), eq(false));
    }

    @Test
    void editCompanyForm_WhenCompanyExists_ShouldReturnFormView() {
        CompanyFormDto companyFormDto = CompanyFormDto.builder()
                .name("Тестовая компания")
                .website("https://test-company.ru")
                .build();

        when(companyService.findById(1L)).thenReturn(testCompany);
        when(companyMapper.companyToCompanyFormDto(testCompany)).thenReturn(companyFormDto);

        String viewName = companyController.editCompanyForm(1L, model);

        assertEquals("company/form", viewName);

        verify(companyService, times(1)).findById(1L);
        verify(companyMapper, times(1)).companyToCompanyFormDto(testCompany);
        verify(model).addAttribute(eq("companyDto"), eq(companyFormDto));
        verify(model).addAttribute(eq("isEdit"), eq(true));
    }

    @Test
    void editCompanyForm_WhenCompanyNotFound_ShouldRedirectToList() {
        when(companyService.findById(999L)).thenReturn(null);

        String viewName = companyController.editCompanyForm(999L, model);

        assertEquals("redirect:/companies", viewName);

        verify(companyService, times(1)).findById(999L);
        verify(companyMapper, never()).companyToCompanyFormDto(any());
    }

    @Test
    void updateCompany_WhenValidData_ShouldRedirectToList() {
        testCompanyFormDto.setName("Обновленное название");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(companyMapper.companyFormDtoToCompany(testCompanyFormDto)).thenReturn(testCompany);
        when(companyService.update(testCompany)).thenReturn(testCompany);

        String viewName = companyController.updateCompany(1L, testCompanyFormDto, bindingResult, model);

        assertEquals("redirect:/companies", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, times(1)).companyFormDtoToCompany(testCompanyFormDto);
        verify(companyService, times(1)).update(testCompany);
        assertNotNull(testCompany.getId());
        assertEquals(1L, testCompany.getId());
    }

    @Test
    void updateCompany_WhenValidationErrors_ShouldReturnFormView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = companyController.updateCompany(1L, testCompanyFormDto, bindingResult, model);

        assertEquals("company/form", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, never()).companyFormDtoToCompany(any());
        verify(companyService, never()).update(any());
        verify(model).addAttribute(eq("isEdit"), eq(true));
    }

    @Test
    void updateCompany_WhenServiceThrowsException_ShouldReturnFormWithError() {
        String errorMessage = "Компания с таким именем уже существует";

        when(bindingResult.hasErrors()).thenReturn(false);
        when(companyMapper.companyFormDtoToCompany(testCompanyFormDto)).thenReturn(testCompany);
        when(companyService.update(testCompany)).thenThrow(new RuntimeException(errorMessage));

        String viewName = companyController.updateCompany(1L, testCompanyFormDto, bindingResult, model);

        assertEquals("company/form", viewName);

        verify(bindingResult, times(1)).hasErrors();
        verify(companyMapper, times(1)).companyFormDtoToCompany(testCompanyFormDto);
        verify(companyService, times(1)).update(testCompany);
        verify(model).addAttribute(eq("error"), eq(errorMessage));
        verify(model).addAttribute(eq("isEdit"), eq(true));
    }

    @Test
    void deleteCompany_WhenValidId_ShouldRedirectToList() {
        doNothing().when(companyService).delete(1L);

        String viewName = companyController.deleteCompany(1L);

        assertEquals("redirect:/companies", viewName);

        verify(companyService, times(1)).delete(1L);
    }

    @Test
    void deleteCompany_WhenServiceThrowsException_ShouldRethrowException() {
        RuntimeException exception = new RuntimeException("Ошибка удаления");
        doThrow(exception).when(companyService).delete(1L);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> companyController.deleteCompany(1L));

        assertEquals(exception, thrown);
        verify(companyService, times(1)).delete(1L);
    }

    @Test
    void searchCompanies_WhenValidSearch_ShouldRedirectWithSearchParam() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = companyController.searchCompanies(testCompanySearchDto, bindingResult);

        assertEquals("redirect:/companies?search=тест", viewName);

        verify(bindingResult, times(1)).hasErrors();
    }

    @Test
    void searchCompanies_WhenSearchIsNull_ShouldRedirectWithEmptyParam() {
        CompanySearchDto searchDto = new CompanySearchDto();
        searchDto.setSearch(null);

        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = companyController.searchCompanies(searchDto, bindingResult);

        assertEquals("redirect:/companies?search=", viewName);

        verify(bindingResult, times(1)).hasErrors();
    }

    @Test
    void searchCompanies_WhenSearchHasSpaces_ShouldTrimAndRedirect() {
        CompanySearchDto searchDto = new CompanySearchDto();
        searchDto.setSearch("  тестовая компания  ");

        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = companyController.searchCompanies(searchDto, bindingResult);

        assertEquals("redirect:/companies?search=тестовая компания", viewName);

        verify(bindingResult, times(1)).hasErrors();
    }

    @Test
    void searchCompanies_WhenValidationErrors_ShouldRedirectToList() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = companyController.searchCompanies(testCompanySearchDto, bindingResult);

        assertEquals("redirect:/companies", viewName);

        verify(bindingResult, times(1)).hasErrors();
    }

    private Company createAnotherCompany() {
        return Company.builder()
                .id(2L)
                .name("Другая компания")
                .website("https://another-company.ru")
                .build();
    }
}