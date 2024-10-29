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

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/students/all")
  public List<Student> getStudents() {
    return service.getStudents();
  }

  @GetMapping("/courses/all")
  public List<StudentCourse> getCourses() {
    return service.getCourses();
  }

  @GetMapping("/students")
  public List<Student> searchStudents(
      @RequestParam(required = false) Integer minAge,
      @RequestParam(required = false) Integer maxAge) {
    return service.searchStudents(minAge, maxAge);
  }

  @GetMapping("/students/courses")
  public List<StudentCourse> searchCourses(
      @RequestParam(required = false) String courseName) {
    return service.searchCourses(courseName);
  }

  @GetMapping("/students/details")
  public List<StudentDetail> searchStudents() {
    List<Student> students = service.getStudents();
    List<StudentCourse> studentCourses = service.getCourses();
    return converter.convertStudentDetails(students, studentCourses);
  }
}
