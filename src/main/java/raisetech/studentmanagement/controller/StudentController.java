package raisetech.studentmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.service.StudentService;

// @RestControllerを付けることで、このクラスがREST APIのコントローラーであることを示す
// クライアント(Postmanなど)からのリクエストに対して、JSON形式のレスポンスを返す
// @Controller + @ResponseBodyの組み合わせと同じ機能を持つ
@RestController
public class StudentController {

  //クラス内で使う変数(フィールド)を定義
  //コンストラクタインジェクション
  //依存関係をコンストラクタで明示でき、よりテストがしやすい
  //finalを付けることができるので、インスタンスが生成された後に このフィールドが変更されないことを保証できる
  //@Autowired アノテーションをコンストラクタに付け、コンストラクタの引数にStudentServiceやStudentConverterを指定することで、Springがインスタンスを注入する
  private final StudentService service;
  private final StudentConverter converter;

  //@Autowiredアノテーションを使用することで、依存性を注入
  //StudentServiceとStudentConverterのインスタンスをSpringが自動的にStudentControllerに渡してくれている
  //このコンストラクタが呼ばれると、serviceとconverterフィールド(クラスブロック内の直下で作った変数)に
  //StudentServiceとStudentConverterのインスタンスが設定され、
  //serviceやconverterクラスで設定したメソッドやメンバー変数(フィールド)を使うことができるようになる
  //StudentController自体にリポジトリのインスタンスが直接設定されているわけではなく、
  //StudentService、StudentConverterに設定されたリポジトリ経由でデータベースの機能が使えるようになる
  //依存関係をコンストラクタで明示することによって、必要なものをはっきりと示すことができる
  //必要なものを自動的に用意してくれる便利な機能のようなもの。ビジネスロジックの実装に集中できるようになる
  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  //URLパス"/students"にアクセスした際に、このメソッドが呼び出される
  @GetMapping("/students")
  public List<StudentDetail> getStudents() {

    //論理削除されていない生徒情報とコース情報を取得(引数がnullのため)
    //List<Student>の中には、Studentクラスのインスタンスが格納されている。変数名は任意で良い
    List<Student> students = service.getNotDeletedStudents();

    // すべての受講コース情報を取得（フィルタなし）
    List<StudentCourse> studentsCourses = service.getCourses(null);

    // 生徒情報とコース情報を統合し、リストとして返す
    return converter.convertStudentDetails(students, studentsCourses);
  }

  @GetMapping("/courses")
  //戻り値をHTTPレスポンスボディとして返す（JavaオブジェクトからJSONに自動変換）
  @ResponseBody

  //StudentCourseというクラスのオブジェクトを複数の要素として持つリストを返す
  public List<StudentCourse> getCourses(

      //メソッドの引数courseNameは、クエリパラメータ（URLの?courseName=value部分）を受け取る
      //@RequestParam(required = false)により、courseNameは省略可能（値が渡されなくても動作する)
      @RequestParam(required = false) String courseName) {
    return service.getCourses(courseName);
  }

  @GetMapping("/students/details")

  //@ResponseBodyアノテーションを付与することで、このメソッドが返す値がJSON形式で返されるようになる
  //@RestController をクラスに付けると、全メソッドがデフォルトでデータ（JSONやXML）を返す設定になり、HTMLのテンプレートを使用する場合にエラーが発生する
  //HTMLテンプレートを返すメソッドとデータだけを返すメソッドが混在している場合、
  //@ResponseBodyアノテーションを付けることで、データだけを返すメソッドであることを示す
  @ResponseBody
  public List<StudentDetail> searchStudents() {

    //すべての生徒情報とコース情報を取得(引数がnullのため)
    //List<Student>の中には、Studentクラスのインスタンスが格納されている。変数名は任意で良い
    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentCourses = service.getCourses(null);

    //List<StudentDetail>型のconverter.convertStudentDetails()は、生徒情報とコース情報をもとに、StudentDetailクラスのリストを作成して返す
    return converter.convertStudentDetails(students, studentCourses);
  }
  
  @PostMapping("/registerStudents")

  // ResponseEntityは、SpringBootでHTTPレスポンスを返すための特別なクラス
  public ResponseEntity<String> registerStudents(@RequestBody StudentDetail studentDetail) {
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }

    //新規受講生を登録する処理を実装する
    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.registerStudent(studentDetail);

    //学生が登録された後、一覧画面（/students）にリダイレクトして確認できるようにする
    return ResponseEntity.ok("登録処理が成功しました！");
  }

  @GetMapping("/students/{studentId}")

  public ResponseEntity<StudentDetail> getStudentById(@PathVariable String studentId) {

    // Serviceクラスのメソッドを呼び出して既存データを取得
    StudentDetail studentDetail = service.getStudentDetailById(studentId);

    // 取得した各コースの日付情報をチェックし、nullの場合はデフォルト値を設定
    // studentDetailから受講コース情報のリスト(studentsCourses)を取得し、各コース情報を1つずつcourse変数に入れて処理
    // studentDetail：学生一人の情報 + その学生が受講している複数のコース情報
    // course：コース1つ分の情報（StudentCourse型）
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() == null) {
        course.setCourseStartDate(LocalDate.now());
      }
      if (course.getCourseExpectedEndDate() == null) {
        course.setCourseExpectedEndDate(LocalDate.now().plusYears(1));
      }
    }

    // StudentDetailオブジェクトをそのままJSON形式で返す
    return ResponseEntity.ok(studentDetail);
  }

  @PostMapping("/updateStudents")
  // ResponseEntityは、SpringBootでHTTPレスポンスを返すための特別なクラス
  // ResponseEntity<String> を使用して、更新結果のメッセージをHTTPレスポンスとして返す
  // @RequestBodyにより、リクエストのJSONデータをStudentDetail型のオブジェクトとして受け取る
  public ResponseEntity<String> updateStudentDetail(@RequestBody StudentDetail studentDetail) {
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }
    //新規受講生を登録する処理を実装する
    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.updateStudentDetail(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました！");
  }
}
