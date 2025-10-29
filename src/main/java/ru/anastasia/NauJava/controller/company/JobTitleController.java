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
import ru.anastasia.NauJava.dto.company.JobTitleFormDto;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.mapper.company.CompanyMapper;
import ru.anastasia.NauJava.service.company.JobTitleService;

import java.util.List;

@Controller
@RequestMapping("/jobtitles")
@RequiredArgsConstructor
public class JobTitleController {

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
        model.addAttribute("jobTitleDto", new JobTitleFormDto());
        model.addAttribute("isEdit", false);
        return "jobtitle/form";
    }

    @PostMapping
    public String createJobTitle(@Valid @ModelAttribute("jobTitleDto") JobTitleFormDto jobTitleFormDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "jobtitle/form";
        }
        try {
            jobTitleService.create(jobTitleFormDto.getTitle());
            return "redirect:/jobtitles";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "jobtitle/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editJobTitleForm(@PathVariable Long id, Model model) {
        JobTitle jobTitle = jobTitleService.findById(id);
        if (jobTitle == null) {
            return "redirect:/jobtitles";
        }

        JobTitleFormDto dto = companyMapper.jobTitleToJobTitleFormDto(jobTitle);
        model.addAttribute("jobTitleDto", dto);
        model.addAttribute("isEdit", true);
        return "jobtitle/form";
    }

    @PostMapping("/{id}")
    public String updateJobTitle(@PathVariable Long id,
                                 @Valid @ModelAttribute("jobTitleDto") JobTitleFormDto jobTitleFormDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "jobtitle/form";
        }

        try {
            JobTitle jobTitle = companyMapper.jobTitleFormDtoToJobTitle(jobTitleFormDto);
            jobTitle.setId(id);
            jobTitleService.update(jobTitle);
            return "redirect:/jobtitles";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "jobtitle/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteJobTitle(@PathVariable Long id) {
        jobTitleService.delete(id);
        return "redirect:/jobtitles";
    }
}
