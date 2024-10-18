package raisetech.studentmanagement;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

  private final StudentRepository repository;

  @Autowired
  public StudentManagementApplication(StudentRepository repository) {
    this.repository = repository;
  }

  public static void main(String[] args) {
    SpringApplication.run(StudentManagementApplication.class, args);
  }

  @GetMapping("/students")
  public List<Student> getStudents() {
    return repository.searchStudent();
  }

  @GetMapping("/studentsCourses")
  public List<StudentCourse> getStudentsCourse() {
    return repository.searchCourse();
  }
}
