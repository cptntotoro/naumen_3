package ru.anastasia.NauJava.controller.company;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.company.CompanyFormDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.company.CompanyMapper;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {
    /**
     * Сервис управления компаниями
     */
    private final CompanyService companyService;

    /**
     * Маппер компаний
     */
    private final CompanyMapper companyMapper;

    /**
     * Сервис управления тегами
     */
    private final TagService tagService;

    @GetMapping
    public String listCompanies(Model model) {
        log.debug("GET /companies - список компаний");

        List<Company> companies = companyService.findAll();
        List<Tag> tags = tagService.findAll();

        model.addAttribute("companies", companies);
        model.addAttribute("tags", tags);

        log.debug("Загружено компаний: {}, тегов: {}", companies.size(), tags.size());

        return "company/list";
    }

    @GetMapping("/new")
    public String newCompanyForm(Model model) {
        model.addAttribute("companyDto", new CompanyFormDto());
        model.addAttribute("isEdit", false);
        return "company/form";
    }

    @PostMapping
    public String createCompany(@Valid @ModelAttribute("companyDto") CompanyFormDto companyFormDto,
                                BindingResult bindingResult, Model model) {
        log.info("POST /companies - создание компании [название: {}]", companyFormDto.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании компании: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", false);
            return "company/form";
        }

        try {
            Company company = companyMapper.companyFormDtoToCompany(companyFormDto);
            Company savedCompany = companyService.create(company);
            log.info("Компания успешно создана [ID: {}, название: {}]", savedCompany.getId(), savedCompany.getName());
            return "redirect:/companies";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании компании [название: {}]", companyFormDto.getName(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "company/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editCompanyForm(@PathVariable Long id, Model model) {
        Company company = companyService.findById(id);
        if (company == null) {
            return "redirect:/companies";
        }

        CompanyFormDto dto = companyMapper.companyToCompanyFormDto(company);
        model.addAttribute("companyDto", dto);
        model.addAttribute("isEdit", true);
        return "company/form";
    }

    @PostMapping("/{id}")
    public String updateCompany(@PathVariable Long id,
                                @Valid @ModelAttribute("companyDto") CompanyFormDto companyFormDto,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "company/form";
        }

        try {
            Company company = companyMapper.companyFormDtoToCompany(companyFormDto);
            company.setId(id);
            companyService.update(company);
            return "redirect:/companies";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "company/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCompany(@PathVariable Long id) {
        log.info("POST /companies/{}/delete - удаление компании", id);

        try {
            companyService.delete(id);
            log.info("Компания успешно удалена [ID: {}]", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении компании [ID: {}]", id, e);
            throw e;
        }

        return "redirect:/companies";
    }
}
