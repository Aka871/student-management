package raisetech.studentmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
   * 受講生情報の全件検索を行います。
   *
   * @return 受講生情報一覧(全件)
   */
  //@Selectアノテーションは、このメソッドがSQLのSELECT文を実行することを指定
  //"SELECT * FROM students" は、students テーブルからすべての列と行を取得するSQLクエリ
  //引数なしで呼び出され、すべての学生データをStudentオブジェクトのリストとして返す
  //MyBatisのSQLにカラム名エイリアス(isDeleted AS deleted)を追加。DBのカラム名isDeletedとJavaフィールド名deletedを紐付け
  @Select("SELECT *, isDeleted AS deleted FROM students")
  List<Student> searchStudents();

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourses();

  /**
   * 受講生検索を行います。
   * 受講生IDに紐づく任意の受講生の情報を取得します。
   * データが存在しない場合は、Optional.empty() が返されます。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生情報 (存在しない場合は、Optional.empty ())
   */
  // MyBatisのSQLにカラム名エイリアス(isDeleted AS deleted)を追加。DBのカラム名isDeletedとJavaフィールド名deletedを紐付け
  // Optionalを返すことで、Service層でのnullチェックが不要になり、例外処理を明確に記述できる
  @Select("SELECT *, isDeleted AS deleted FROM students WHERE student_id = #{studentId}")
  Optional<Student> findById(String studentId);

  /**
   * 受講生検索を行います。
   * 受講生IDに紐づく任意の受講生のコース情報を取得します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生情報
   */
  // 受講生のIDを指定して、すべてのコース情報を取得
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> findCourseById(String studentId);

  /**
   * 受講生テーブルに新規登録を行います。
   *
   * @param student 受講生情報
   */
  // @Insertアノテーションは、このメソッドがSQLのINSERT文を実行することを指定
  // 目的：Javaオブジェクトの値をSQL文に埋め込んでデータベースに保存する
  @Insert(

      //student_idはUUID()で自動生成されるように設定
      //#{}の部分は、メソッド引数のStudentオブジェクトのフィールド値を対応する場所に埋め込むことを意味する
      //MySQLのstudentsテーブルの各列に、Studentオブジェクトの各フィールドの値を挿入している
      //例えば、MySQLのstudentsテーブルのfull_nameというカラムに、Studentクラスで定義されている変数（フィールド）fullNameの値を挿入している
      "INSERT INTO students (student_id, full_name, furigana_name, nick_name, phone_number, mail_address, municipality_name, age, sex, occupation, remark)"
          + "VALUES (#{studentId}, #{fullName}, #{furiganaName}, #{nickName}, #{phoneNumber}, #{mailAddress}, #{municipalityName},"
          + " #{age},#{sex}, #{occupation}, #{remark})")

  //saveStudentメソッドが呼ばれると、データベースに新しいStudentデータを挿入する
  void saveStudent(Student student);

  /**
   * 受講生コース情報テーブルに新規登録を行います。
   *
   * @param studentCourse 受講生コース情報
   */

  @Insert(
      // 保存先のテーブルとカラムを指定し、各パラメータを対応する場所に埋め込む
      // 目的：StudentCourseオブジェクトの各フィールドをデータベースの適切なカラムに対応させる
      "INSERT INTO students_courses(course_id, student_id, course_name, course_start_date, course_expected_end_date)"
          + "VALUES (#{courseId}, #{studentId}, #{courseName}, #{courseStartDate}, #{courseExpectedEndDate})")

  // コース情報保存用メソッドのインターフェース宣言
  // 目的：MyBatisがこのメソッド呼び出しを上記のSQL実行に変換する
  void saveStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生情報テーブルに更新処理を行います。
   *
   * @param student 受講生情報
   */
  @Update(
      "UPDATE students SET full_name = #{fullName}, furigana_name = #{furiganaName}, nick_name = #{nickName}, phone_number = #{phoneNumber}, mail_address = #{mailAddress},"
          + " municipality_name = #{municipalityName}, age = #{age}, sex = #{sex}, occupation = #{occupation}, remark = #{remark}, isDeleted = #{deleted} WHERE student_id = #{studentId}")
  void updateStudent(Student student);

  /**
   * 受講生コース情報テーブルに更新処理を行います。
   *
   * @param studentCourse 受講生コース情報
   */
  // 特定の受講生の特定のコースだけを更新したい場合、WHERE句には、studentIdとcourseIdの両方を指定する必要がある
  @Update(
      "UPDATE students_courses SET course_name = #{courseName}, course_start_date = #{courseStartDate}, course_expected_end_date = #{courseExpectedEndDate} "
          + "WHERE student_id = #{studentId} AND course_id = #{courseId}")
  void updateStudentCourse(StudentCourse studentCourse);
}
