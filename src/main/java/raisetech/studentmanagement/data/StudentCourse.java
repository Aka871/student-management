package raisetech.studentmanagement.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {

  // アプリ側で自動補完される場合でも、安全のためnull禁止にしておく
  @NotNull(message = "コースIDは必須です")
  private String courseId;

  @NotNull(message = "受講生IDは必須です")
  private String studentId;

  @NotBlank(message = "コース名は必須です")
  @Size(max = 100, message = "コース名は100文字以内で入力してください")
  private String courseName;

  @NotNull(message = "開始日は必須です")
  private LocalDate courseStartDate;

  @NotNull(message = "終了予定日は必須です")
  @Future(message = "終了予定日は今日より後の日付を入力してください")
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
