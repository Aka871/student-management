package raisetech.studentmanagement.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

//@Getterと@Setterのアノテーションにより、StudentDetailクラスにあるフィールドのゲッター・セッターメソッドを自動生成
@Getter
@Setter

//生徒情報と生徒が受講しているコース情報をまとめて扱うためのクラスを作成
public class StudentDetail {

  //フィールド宣言によって、「1人の学生に関連する詳細情報」を一度に取得・操作できるようになり、データの一貫性や管理が容易になる
  private Student student;

  //生徒が受講している複数のコース情報をリストで保持
  private List<StudentCourse> studentsCourses;

  public StudentDetail() {
  }

  public StudentDetail(Student student, List<StudentCourse> studentsCourses) {
    this.student = student;
    this.studentsCourses = studentsCourses;
  }
}
