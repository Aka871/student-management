package raisetech.studentmanagement.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentDetail {

  private Student student;
  private List<StudentCourse> studentsCourses;
}
