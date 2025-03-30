package raisetech.studentmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

/**
 * 受講生の検索や登録、更新などを行うREST APIとして受け付けるControllerです。
 * 各エンドポイントはJSON形式でレスポンスを返します。
 */
// @Validatedをつけることで、このクラス全体で入力チェック（バリデーション）を行うことを示す
// @RestControllerを付けることで、このクラスがREST APIのコントローラーであることを示す
// クライアント(Postmanなど)からのリクエストに対して、JSON形式のレスポンスを返す
// @Controller + @ResponseBodyの組み合わせと同じ機能を持つ

@Validated
@RestController
public class StudentController {

  //コンストラクタインジェクション
  //依存関係をコンストラクタで明示でき、よりテストがしやすい
  //finalを付けることができるので、インスタンスが生成された後に このフィールドが変更されないことを保証できる
  //@Autowired アノテーションをコンストラクタに付け、コンストラクタの引数にStudentServiceやStudentConverterを指定することで、Springがインスタンスを注入する
  private final StudentService service;
  private final StudentConverter converter;

  /**
   * コンストラクタ
   *
   * @param service   受講生サービス
   * @param converter 受講生コンバーター
   */
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

  /**
   * 受講生詳細情報一覧を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、論理削除されていない受講生のみです。
   *
   * @return 論理削除されていない受講生詳細情報のリスト（受講生情報と受講生コース情報を結合したもの）
   */
  @GetMapping("/students")
  public List<StudentDetail> getStudents() {
    return service.getNotDeletedStudentsDetails();
  }

  /**
   * 受講生詳細情報(個別)を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、指定した受講生IDに紐づく、受講生詳細情報です。
   *
   * @param studentId 受講生ID
   * @return 指定したIDの受講生詳細情報 (受講生情報と受講生コース情報を結合したもの)
   */
  @GetMapping("/students/{studentId}")

  public ResponseEntity<StudentDetail> getStudentById(@PathVariable String studentId) {
    StudentDetail studentDetail = service.getStudentDetailById(studentId);
    // StudentDetailオブジェクトをそのままJSON形式で返す
    return ResponseEntity.ok(studentDetail);
  }

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
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

  // TODO: 今後、削除するか要検討 (仕様が明確になった段階で、削除または機能追加)
  //  現時点では未使用だが、論理削除済みの受講生を含む全件取得や、将来的な検索機能の土台として活用する可能性がある。
  @GetMapping("/students/details")

  //@ResponseBodyアノテーションを付与することで、このメソッドが返す値がJSON形式で返されるようになる
  //@RestController をクラスに付けると、全メソッドがデフォルトでデータ（JSONやXML）を返す設定になり、HTMLのテンプレートを使用する場合にエラーが発生する
  //HTMLテンプレートを返すメソッドとデータだけを返すメソッドが混在している/students/details場合、
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

  /**
   * 新規で受講生詳細情報を登録します。
   * 受講生情報と受講生コース情報をそれぞれ登録します。
   *
   * @param studentDetail 登録対象の受講生詳細情報 (受講生情報と受講生コース情報)
   * @return 登録処理の結果メッセージ
   */
  @PostMapping("/registerStudents")

  // ResponseEntityは、SpringBootでHTTPレスポンスを返すための特別なクラス
  public ResponseEntity<String> registerStudents(@RequestBody StudentDetail studentDetail) {
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }

    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.registerStudent(studentDetail);

    //学生が登録された後、一覧画面（/students）にリダイレクトして確認できるようにする
    return ResponseEntity.ok("登録処理が成功しました！");
  }

  /**
   * 受講生詳細情報を更新します。
   * 受講生情報と受講生コース情報をそれぞれ更新します。
   * キャンセルフラグの更新もここで行います。(論理削除)
   *
   * @param studentDetail 更新対象の受講生詳細情報 (受講生情報と受講生コース情報)
   * @return 更新処理の結果メッセージ
   */
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

    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.updateStudentDetail(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました！");
  }
}
