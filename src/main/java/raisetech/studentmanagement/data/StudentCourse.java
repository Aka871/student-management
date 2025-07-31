package raisetech.studentmanagement.data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 受講生コース情報を扱うクラスです。
 * データベースの情報をJavaオブジェクトとして扱えるようにします。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {

  // courseNameに対応するコースIDが自動で設定される（入力不要）
  private String courseId;

  // UUIDで自動生成されたStudentのIDが紐づく（入力不要）
  private String studentId;

  @NotBlank(message = "コース名は必須です")
  @Size(max = 100, message = "コース名は100文字以内で入力してください")
  private String courseName;

  // 未入力の場合、登録日の日付が自動で設定される
  private LocalDate courseStartDate;

  // 未入力の場合、コース開始日の1年後が自動で設定される
  @Future(message = "終了予定日は今日より後の日付を入力してください")
  private LocalDate courseExpectedEndDate;

  public String getCourseStartDateFormatted() {

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
