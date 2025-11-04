package ru.anastasia.NauJava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.anastasia.NauJava.exception.company.CompanyNotFoundException;
import ru.anastasia.NauJava.exception.company.IllegalCompanyStateException;
import ru.anastasia.NauJava.exception.company.IllegalJobTitleStateException;
import ru.anastasia.NauJava.exception.company.JobTitleNotFoundException;
import ru.anastasia.NauJava.exception.contact.ContactDetailNotFoundException;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.exception.event.EventNotFoundException;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.exception.note.NoteNotFoundException;
import ru.anastasia.NauJava.exception.socialprofile.IllegalSocialProfileStateException;
import ru.anastasia.NauJava.exception.socialprofile.SocialProfileNotFoundException;
import ru.anastasia.NauJava.exception.tag.IllegalTagStateException;
import ru.anastasia.NauJava.exception.tag.TagNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(CompanyNotFoundException e, Model model) {
        String reason = "Компания не найдена. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(JobTitleNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(JobTitleNotFoundException e, Model model) {
        String reason = "Должность не найдена. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(ContactDetailNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(ContactDetailNotFoundException e, Model model) {
        String reason = "Способ связи не найден. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(ContactNotFoundException e, Model model) {
        String reason = "Контакт не найден. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(EventNotFoundException e, Model model) {
        String reason = "Событие не найдено. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(NoteNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(NoteNotFoundException e, Model model) {
        String reason = "Заметка не найдена. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(SocialProfileNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(SocialProfileNotFoundException e, Model model) {
        String reason = "Профиль в соцсети не найден. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String exceptionNotFound(TagNotFoundException e, Model model) {
        String reason = "Тег не найден. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(IllegalCompanyStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String exceptionIllegalState(IllegalCompanyStateException e, Model model) {
        String reason = "Некорректное состояние компании. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(IllegalJobTitleStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String exceptionIllegalState(IllegalJobTitleStateException e, Model model) {
        String reason = "Некорректное состояние должности. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(IllegalEventStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String exceptionIllegalState(IllegalEventStateException e, Model model) {
        String reason = "Некорректное состояние события. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(IllegalSocialProfileStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String exceptionIllegalState(IllegalSocialProfileStateException e, Model model) {
        String reason = "Некорректное состояние профиля в соцсетях. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(IllegalTagStateException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String exceptionIllegalState(IllegalTagStateException e, Model model) {
        String reason = "Некорректное состояние тега. " + e.getMessage();
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.toString(), reason, e.getMessage(), LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionNotFound(Exception e, Model model) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.toString(), "Возникла проблема на стороне сервера", LocalDateTime.now());
        model.addAttribute("error", error);
        return "error";
    }
}
