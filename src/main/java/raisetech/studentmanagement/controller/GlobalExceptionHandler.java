package raisetech.studentmanagement.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import raisetech.studentmanagement.exception.StudentNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(StudentNotFoundException.class)

  public String handleStudentNotFoundException(StudentNotFoundException ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());

    return "error";
  }
}
