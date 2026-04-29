package bankinfo.web.controller;

import bankinfo.web.service.ValidationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException exception, Model model) {
        model.addAttribute("errorTitle", "Validation error");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorTitle", "Bad request");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception exception, Model model) {
        model.addAttribute("errorTitle", "Server error");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }
}
