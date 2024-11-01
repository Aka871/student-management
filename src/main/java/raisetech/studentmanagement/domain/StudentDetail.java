package raisetech.studentmanagement.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

@Getter
@Setter
public class StudentDetail {

  private Student student;
  private List<StudentCourse> studentsCourses;
}
