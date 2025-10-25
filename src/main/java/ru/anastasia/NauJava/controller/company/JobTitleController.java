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
import ru.anastasia.NauJava.dto.company.JobTitleCreateDto;
import ru.anastasia.NauJava.dto.company.JobTitleUpdateDto;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.mapper.company.CompanyMapper;
import ru.anastasia.NauJava.service.company.JobTitleService;

import java.util.List;

@Controller
@RequestMapping("/jobtitles")
@RequiredArgsConstructor
class JobTitleController {

    /**
     * Сервис управления должностями
     */
    private final JobTitleService jobTitleService;

    /**
     * Маппер компаний
     */
    private final CompanyMapper companyMapper;

    @GetMapping
    public String listJobTitles(Model model) {
        List<JobTitle> jobTitles = jobTitleService.findAll();
        model.addAttribute("jobTitles", jobTitles);
        return "jobtitle/list";
    }

    @GetMapping("/new")
    public String newJobTitleForm(Model model) {
        model.addAttribute("jobTitleDto", new JobTitleCreateDto());
        return "jobtitle/form";
    }

    @PostMapping
    public String createJobTitle(@Valid @ModelAttribute("jobTitleDto") JobTitleCreateDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "jobtitle/form";
        }
        jobTitleService.create(dto.getTitle());
        return "redirect:/jobtitles";
    }

    @GetMapping("/{id}/edit")
    public String editJobTitleForm(@PathVariable Long id, Model model) {
        JobTitle jobTitle = jobTitleService.findById(id);
        if (jobTitle == null) {
            return "redirect:/jobtitles";
        }

        JobTitleUpdateDto dto = companyMapper.jobTitleToJobTitleUpdateDto(jobTitle);

        model.addAttribute("jobTitleDto", dto);
        model.addAttribute("jobTitleId", id);
        return "jobtitle/edit";
    }

    @PostMapping("/{id}")
    public String updateJobTitle(@PathVariable Long id,
                                 @Valid @ModelAttribute("jobTitleDto") JobTitleUpdateDto jobTitleUpdateDto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "jobtitle/edit";
        }

        JobTitle jobTitle = companyMapper.toEntity(jobTitleUpdateDto);
        jobTitle.setId(id);

        jobTitleService.update(jobTitle);
        return "redirect:/jobtitles";
    }

    @PostMapping("/{id}/delete")
    public String deleteJobTitle(@PathVariable Long id) {
        jobTitleService.delete(id);
        return "redirect:/jobtitles";
    }
}
