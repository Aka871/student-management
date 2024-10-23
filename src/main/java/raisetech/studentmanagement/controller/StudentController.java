package raisetech.studentmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.service.StudentService;

@RestController
public class StudentController {

  private final StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  @GetMapping("/students/search")
  public List<Student> searchStudents(
      @RequestParam(required = false) Integer minAge,
      @RequestParam(required = false) Integer maxAge) {
    return service.searchStudents(minAge, maxAge);
  }

  @GetMapping("/studentsCourses/search")
  public List<StudentCourse> searchCourses(
      @RequestParam(required = false) String courseName) {
    return service.searchCourses(courseName);
  }
}
