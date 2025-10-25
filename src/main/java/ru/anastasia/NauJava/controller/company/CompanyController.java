package ru.anastasia.NauJava.controller.company;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.company.CompanyCreateDto;
import ru.anastasia.NauJava.dto.company.CompanyUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.mapper.company.CompanyMapper;
import ru.anastasia.NauJava.service.company.CompanyService;

import java.util.List;

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

    @GetMapping
    public String listCompanies(Model model) {
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "company/list";
    }

    @GetMapping("/new")
    public String newCompanyForm(Model model) {
        model.addAttribute("companyDto", new CompanyCreateDto());
        return "company/form";
    }

    @PostMapping
    public String createCompany(@Valid @ModelAttribute("companyDto") CompanyCreateDto companyCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "company/form";
        }
        companyService.create(companyMapper.toEntity(companyCreateDto));
        return "redirect:/companies";
    }

    @GetMapping("/{id}/edit")
    public String editCompanyForm(@PathVariable Long id, Model model) {
        Company company = companyService.findById(id);
        if (company == null) {
            return "redirect:/companies";
        }

        CompanyUpdateDto dto = companyMapper.companyToCompanyUpdateDto(company);
        model.addAttribute("companyDto", dto);
        model.addAttribute("companyId", id);
        return "company/edit";
    }

    @PostMapping("/{id}")
    public String updateCompany(@PathVariable Long id, @Valid @ModelAttribute("companyDto") CompanyUpdateDto companyUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "company/edit";
        }

        Company company = companyMapper.toEntity(companyUpdateDto);
        company.setId(id);

        companyService.update(company);
        return "redirect:/companies";
    }

    @PostMapping("/{id}/delete")
    public String deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return "redirect:/companies";
    }
}
