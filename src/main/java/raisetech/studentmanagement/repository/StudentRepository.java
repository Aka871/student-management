package raisetech.studentmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

/**
 * 受講生情報および受講生コース情報に関するデータベース操作を行う、Repositoryインターフェースです。
 * MyBatisによってSQL文とマッピングされ、受講生情報テーブルと受講生コース情報テーブルにアクセスします。
 */
@Mapper

public interface StudentRepository {

  /**
   * 受講生情報を全件取得します。
   *
   * @return 受講生情報のリスト(全件)
   */
  List<Student> searchStudents();

  /**
   * 受講生情報を取得します。
   * 指定した受講生IDに紐づく受講生情報を取得します。
   * <p>
   * データが存在しない場合は、Optional.empty() を返します。
   * Optionalを使うことで、呼び出し側が「値が存在しない場合」の処理を明確に記述することができ、nullチェックの必要がなくなります。
   *
   * @param studentId 受講生ID
   * @return 指定した受講生IDに紐づく受講生情報 (存在しない場合は、Optional.empty ())
   */
  Optional<Student> findById(String studentId);

  /**
   * 受講生コース情報を取得します。
   * 指定した受講生IDに紐づく受講生コース情報を取得します。
   *
   * @param studentId 受講生ID
   * @return 指定した受講生IDに紐づく受講生コース情報
   */
  List<StudentCourse> findCourseById(String studentId);

  /**
   * 受講生情報を登録します。(受講生情報テーブル)
   * 受講生IDはUUIDで設定します。
   *
   * @param student 受講生情報
   */
  void saveStudent(Student student);

  /**
   * 受講生コース情報を登録します。(受講生コース情報テーブル)
   * 受講生IDはUUIDで設定します。
   *
   * @param studentCourse 受講生コース情報
   */
  void saveStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生情報を更新します。(受講生情報テーブル)
   *
   * @param student 受講生情報
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報を更新します。(受講生コース情報テーブル)
   *
   * @param studentCourse 受講生コース情報
   */
  // 特定の受講生の特定のコースだけを更新したい場合、WHERE句には、studentIdとcourseIdの両方を指定する必要がある
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生コース情報を全件取得します。
   *
   * @return 受講生コース情報のリスト(全件)
   */
  List<StudentCourse> searchCourses();
}
