package raisetech.studentmanagement.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.repository.StudentRepository;

//ビジネスロジックを記述するクラスには@Serviceアノテーションを付与。そのクラスはBean化され、内部メモリ上に格納される
//Bean化とは、そのクラスがSpringの管理下に置かれ、インスタンス（オブジェクト）がSpringフレームワークによって生成・管理されることを意味する
//結果、@Autowiredアノテーションを使用することで、依存性注入（DI）が可能となり、
//このクラスのインスタンスは、他のクラスから@Autowiredアノテーションを使って自動的に取得できるようになる
@Service
public class StudentService {

  private final StudentRepository repository;

  //@Autowiredによって、SpringがStudentRepositoryのインスタンスを自動的に渡してくれる(依存性の注入)
  //このコンストラクタが呼ばれると、repositoryフィールドにStudentRepositoryのインスタンスが設定され、
  //StudentServiceクラスの中でrepositoryのインスタンスを使えるようになる
  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> getStudents(Integer minAge, Integer maxAge) {
    List<Student> allStudents = repository.searchStudents();
    return allStudents.stream()

        //minAgeが指定されている場合は、ageがminAge以上、maxAgeが指定されている場合は、maxAge以下の生徒情報を取得
        .filter(student -> (minAge == null || student.getAge() >= minAge)
            && (maxAge == null || student.getAge() <= maxAge))
        //条件に合致した生徒情報をリストに格納
        .collect(Collectors.toList());
  }

  public List<StudentCourse> getCourses(String courseName) {
    List<StudentCourse> allCourses = repository.searchCourses();

    //courseNameがnullでなく、かつ空文字でない場合のみ次の処理を行う
    //trim()は、文字列の前後の空白を取り除くためのメソッド
    if (courseName != null && !courseName.trim().isEmpty()) {
      return allCourses.stream()

          //各courseのgetCourseName()が、equalsIgnoreCase()で大文字と小文字を区別せずに、
          //指定されたcourseNameと一致する場合のみ、そのcourseを残す
          .filter(course -> course.getCourseName().equalsIgnoreCase(courseName))
          .collect(Collectors.toList());
    }
    return allCourses;
  }

  //Serviceクラスの登録、更新、削除という一連のデータベースに変更を加えるメソッドには、必ず@Transactionalをつける
  //トランザクション管理を行うためのアノテーションで、メソッドが正常に終わればデータを確定（コミット）、エラー時は変更を取り消す（ロールバック）
  @Transactional
  public void registerStudent(Student student) {
    repository.saveStudent(student);
    // TODO:コース情報登録も行う
  }
}
