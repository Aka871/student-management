package raisetech.studentmanagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;

//Componentアノテーションを付与すると、そのクラスはBean化されて、内部メモリに格納される
//Bean化されたクラスは、他のクラスから@Autowiredアノテーションを使って自動的に取得できるようになる
@Component
public class StudentConverter {

  //StudentリストとStudentCourseリストをを結合して、StudentDetailクラスのリストを作成するメソッド
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourse> studentCourses) {

    //最終的に返すための空のリストstudentDetailsを作成
    List<StudentDetail> studentDetails = new ArrayList<>();

    //studentsリストの要素を1つずつ取り出し、StudentDetailクラスのインスタンスを作成
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();

      //StudentDetailクラスのインスタンスに、Studentクラスのインスタンスをセット
      studentDetail.setStudent(student);

      //StudentCourseリストから、StudentクラスのstudentIdと一致するものを抽出し、リストに格納
      List<StudentCourse> convertStudentCourses = studentCourses.stream()
          .filter(studentCourse -> student.getStudentId().equals(studentCourse.getStudentId()))
          .collect(Collectors.toList());

      //StudentDetailクラスのインスタンスに、StudentCourseリスト(1人の生徒が受講している複数のコース情報)をセット
      studentDetail.setStudentsCourses(convertStudentCourses);

      //studentDetailsリストに、StudentDetailクラスのインスタンスを追加
      studentDetails.add(studentDetail);
    });

    //生徒情報とその生徒が受講しているコース情報を結合したデータのリストを返す
    return studentDetails;
  }
}
