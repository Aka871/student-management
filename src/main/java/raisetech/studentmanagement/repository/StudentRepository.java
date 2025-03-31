package raisetech.studentmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

/**
 * 受講生テーブル、受講生コース情報テーブルと紐づくRepositoryです。
 */
//@Mapperアノテーションは、MyBatisにこのインターフェースがデータベースの操作を行うためのものだと伝える
@Mapper

//インターフェイスは、クラスに含まれるメソッドの具体的な処理内容を記述せず、変数とメソッドの型のみを定義したもの
//クラスがどのようなメソッドを持っているのかをあらかじめ定義する、いわば設計書のような存在
//実際の動作はそのインターフェイスを実装するクラスで記述する
//MyBatisによりデータベースと直接やり取りを行い、学生情報の検索（searchStudents）や学生情報の登録（saveStudent）などを行う
public interface StudentRepository {

  /**
   * 受講生情報を全件取得します。
   *
   * @return 受講生情報一覧(全件)
   */
  //@Selectアノテーションは、このメソッドがSQLのSELECT文を実行することを指定
  //"SELECT * FROM students" は、students テーブルからすべての列と行を取得するSQLクエリ
  //引数なしで呼び出され、すべての学生データをStudentオブジェクトのリストとして返す
  //MyBatisのSQLにカラム名エイリアス(isDeleted AS deleted)を追加。DBのカラム名isDeletedとJavaフィールド名deletedを紐付け
  List<Student> searchStudents();

  /**
   * 受講生情報を取得します。
   * 受講生IDに紐づく任意の受講生情報を取得します。
   * データが存在しない場合は、Optional.empty() が返されます。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生情報 (存在しない場合は、Optional.empty ())
   */
  // MyBatisのSQLにカラム名エイリアス(isDeleted AS deleted)を追加。DBのカラム名isDeletedとJavaフィールド名deletedを紐付け
  // Optionalを返すことで、Service層でのnullチェックが不要になり、例外処理を明確に記述できる
  Optional<Student> findById(String studentId);

  /**
   * 受講生コース情報を取得します。
   * 受講生IDに紐づく任意の受講生コース情報を取得します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生コース情報
   */
  // 受講生のIDを指定して、すべてのコース情報を取得
  List<StudentCourse> findCourseById(String studentId);

  /**
   * 受講生情報を新規登録します。(受講生情報テーブル)
   * 受講生IDはUUIDで設定します。
   *
   * @param student 受講生情報
   */
  //saveStudentメソッドが呼ばれると、データベースに新しいStudentデータを挿入する
  void saveStudent(Student student);

  /**
   * 受講生コース情報を新規登録します。(受講生コース情報テーブル)
   * 受講生IDはUUIDで設定します。
   *
   * @param studentCourse 受講生コース情報
   */
  // コース情報保存用メソッドのインターフェース宣言
  // 目的：MyBatisがこのメソッド呼び出す
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

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
  List<StudentCourse> searchCourses();
}
