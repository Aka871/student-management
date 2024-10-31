package raisetech.studentmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.service.StudentService;

@RestController
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/students")
  public List<Student> getStudents(
      @RequestParam(required = false) Integer minAge,
      @RequestParam(required = false) Integer maxAge) {
    return service.getStudents(minAge, maxAge);
  }

  @GetMapping("/courses")
  public List<StudentCourse> getCourses(
      @RequestParam(required = false) String courseName) {
    return service.getCourses(courseName);
  }

  @GetMapping("/students/details")
  public List<StudentDetail> searchStudents() {
    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentCourses = service.getCourses(null);
    return converter.convertStudentDetails(students, studentCourses);
  }
}
