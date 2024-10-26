package raisetech.studentmanagement.data;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {

  private String courseId;
  private String studentId;
  private String courseName;
  private LocalDate courseStartDate;
  private LocalDate courseExpectedEndDate;
}
