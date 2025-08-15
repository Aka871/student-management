package raisetech.studentmanagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

/**
 * 受講生詳細情報 (受講生情報と受講生コース情報)を扱うクラスです。
 * 複数のデータベースの情報をJavaの1つのオブジェクトとして扱えるようにします。
 */
@Schema(description = "受講生詳細情報 (受講生情報と受講生コース情報)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private List<StudentCourse> studentsCourses;
}
