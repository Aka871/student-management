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

  public String getCourseStartDateFormatted() {

    // nullのときに空文字列を返し、そうでないときに日付を文字列形式で返す
    if (courseStartDate == null) {
      return "";
    }
    return courseStartDate.toString();
  }

  public String getCourseExpectedEndDateFormatted() {
    if (courseExpectedEndDate == null) {
      return "";
    }
    return courseExpectedEndDate.toString();
  }
}
