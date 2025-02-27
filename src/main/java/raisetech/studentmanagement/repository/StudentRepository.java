package raisetech.studentmanagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
  @Select("SELECT * FROM students")
  List<Student> searchStudents();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourses();

  //@Insertアノテーションは、このメソッドがSQLのINSERT文を実行することを指定
  @Insert(

      //student_idはUUID()で自動生成されるように設定
      //#{}の部分は、メソッド引数のStudentオブジェクトのフィールド値を対応する場所に埋め込むことを意味する
      //MySQLのstudentsテーブルの各列に、Studentオブジェクトの各フィールドの値を挿入している
      //例えば、MySQLのstudentsテーブルのfull_nameというカラムに、Studentクラスで定義されている変数（フィールド）fullNameの値を挿入している
      "INSERT INTO students (student_id, full_name, furigana_name, nick_name, phone_number, mail_address, municipality_name, age, sex, occupation, remark)"
          + "VALUES (UUID(), #{fullName}, #{furiganaName}, #{nickName}, #{phoneNumber}, #{mailAddress}, #{municipalityName},"
          + " #{age},#{sex}, #{occupation}, #{remark})")

  //saveStudentメソッドが呼ばれると、データベースに新しいStudentデータを挿入する
  void saveStudent(Student student);

  @Insert(
      "INSERT INTO students_courses(student_id, course_name, course_start_date, course_expected_end_date)"
          + "VALUES (#{studentId}, #{courseName}, #{courseStartDate}, #{courseExpectedEndDate})")
  void saveStudentCourse(StudentCourse studentCourse);
}
