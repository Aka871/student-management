package raisetech.studentmanagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;

//@Mapperアノテーションは、MyBatisにこのインターフェースがデータベースの操作を行うためのものだと伝える
@Mapper

//インターフェイスは、クラスに含まれるメソッドの具体的な処理内容を記述せず、変数とメソッドの型のみを定義したもの
//クラスがどのようなメソッドを持っているのかをあらかじめ定義する、いわば設計書のような存在
//実際の動作はそのインターフェイスを実装するクラスで記述する
//MyBatisによりデータベースと直接やり取りを行い、学生情報の検索（searchStudents）や学生情報の登録（saveStudent）などを行う
public interface StudentRepository {

  //@Selectアノテーションは、このメソッドがSQLのSELECT文を実行することを指定
  //"SELECT * FROM students" は、students テーブルからすべての列と行を取得するSQLクエリ
  //引数なしで呼び出され、すべての学生データをStudentオブジェクトのリストとして返す
  @Select("SELECT * FROM students")
  List<Student> searchStudents();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourses();

  // 受講生のIDを指定して、1人の学生データを取得
  // students テーブルから、student_id が指定された値と一致するレコードを取得する
  // #{studentId} の部分は、メソッドの引数 studentId の値が埋め込まれる
  @Select("SELECT * FROM students WHERE student_id = #{studentId}")
  Student findById(String studentId);

  // 受講生のIDを指定して、すべてのコース情報を取得
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> findCourseById(String studentId);

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

  @Insert(
      // 保存先のテーブルとカラムを指定し、各パラメータを対応する場所に埋め込む
      // 目的：StudentCourseオブジェクトの各フィールドをデータベースの適切なカラムに対応させる
      "INSERT INTO students_courses(course_id, student_id, course_name, course_start_date, course_expected_end_date)"
          + "VALUES (#{courseId}, #{studentId}, #{courseName}, #{courseStartDate}, #{courseExpectedEndDate})")

    // コース情報保存用メソッドのインターフェース宣言
    // 目的：MyBatisがこのメソッド呼び出しを上記のSQL実行に変換する
  void saveStudentCourse(StudentCourse studentCourse);

  @Update(
      "UPDATE students SET full_name = #{fullName}, furigana_name = #{furiganaName}, nick_name = #{nickName}, phone_number = #{phoneNumber}, mail_address = #{mailAddress}, municipality_name = #{municipalityName}, age = #{age}, sex = #{sex}, occupation = #{occupation}, remark = #{remark} WHERE student_id = #{studentId}")
  void updateStudent(Student student);

  @Update(
      "UPDATE students_courses SET course_name = #{courseName}, course_start_date = #{courseStartDate}, course_expected_end_date = #{courseExpectedEndDate} WHERE course_id = #{courseId}")
  void updateStudentCourse(StudentCourse studentCourse);
}
