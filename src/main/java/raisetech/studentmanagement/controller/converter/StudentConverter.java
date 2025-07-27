package raisetech.studentmanagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;

/**
 * 受講生情報や受講生コース情報から、受講生詳細情報への変換、もしくはその逆の変換を行うコンバーターです。
 * 現在は一方向（受講生/受講生コース → 詳細）の変換のみ対応していますが、将来的に双方向対応も想定しています。
 */

@Component
public class StudentConverter {

  /**
   * 受講生情報と受講生コース情報を結合し、受講生詳細情報のリストを生成します。
   * <p>
   * 受講生に対して複数のコース情報が紐づくため、受講生ごとにループ処理を行い、
   * 対応するコース情報をマッピングして受講生詳細情報を組み立てます。
   *
   * @param students       受講生情報のリスト
   * @param studentCourses 受講生コース情報のリスト
   * @return 受講生詳細情報のリスト（受講生情報とコース情報を結合したもの）
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourse> studentCourses) {

    List<StudentDetail> studentDetails = new ArrayList<>();

    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();

      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCourses = studentCourses.stream()
          .filter(studentCourse -> student.getStudentId().equals(studentCourse.getStudentId()))
          .collect(Collectors.toList());

      studentDetail.setStudentsCourses(convertStudentCourses);

      studentDetails.add(studentDetail);
    });

    return studentDetails;
  }
}
