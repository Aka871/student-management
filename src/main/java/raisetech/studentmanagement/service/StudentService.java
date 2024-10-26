package raisetech.studentmanagement.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.repository.StudentRepository;

@Service
public class StudentService {

  private final StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudents(Integer minAge, Integer maxAge) {
    List<Student> allStudents = repository.searchStudents();
    List<Student> filteredStudents = allStudents.stream()
        .filter(student -> minAge == null || student.getAge() >= minAge)
        .filter(student -> maxAge == null || student.getAge() <= maxAge)
        .collect(Collectors.toList());
    return filteredStudents;
  }

  public List<StudentCourse> searchCourses(String courseName) {
    List<StudentCourse> allCourses = repository.searchCourses();
    if (courseName != null && !courseName.isEmpty()) {
      return allCourses.stream()
          .filter(course -> course.getCourseName().equalsIgnoreCase(courseName))
          .collect(Collectors.toList());
    }
    return allCourses;
  }
}
