package raisetech.studentmanagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.exception.TestException;
import raisetech.studentmanagement.service.StudentService;

/**
 * 受講生情報と受講生コース情報の取得・登録・更新などを行うREST APIのControllerクラスです。
 * 各エンドポイントはJSON形式でリクエストとレスポンスをやり取りします。
 * 主にService層を呼び出して、ビジネスロジックの実行結果を返します。
 */
@Validated
@RestController
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  /**
   * コンストラクタ
   *
   * @param service   受講生サービス
   * @param converter 受講生コンバーター
   */
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

    return ResponseEntity.ok(studentDetail);
  }

  /**
   * 例外処理が正しく行われるかを確認します。
   *
   * @throws TestException 確認用に発生させる例外
   */
  @GetMapping("/exception")
  public ResponseEntity<String> exceptionConfirmation() throws TestException {
    throw new TestException("例外処理の確認用です");
  }

  /**
   * 新規で受講生詳細情報を登録します。
   * 受講生情報と受講生コース情報をそれぞれ登録します。
   *
   * @param studentDetail 登録対象の受講生詳細情報 (受講生情報と受講生コース情報)
   * @return 登録処理の結果メッセージ
   */
  @PostMapping("/students")
  public ResponseEntity<String> registerStudents(@RequestBody @Valid StudentDetail studentDetail) {

    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }
    service.registerStudent(studentDetail);

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
  @PutMapping("/students")
  public ResponseEntity<String> updateStudentDetail(@RequestBody StudentDetail studentDetail) {

    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }
    return ResponseEntity.ok("更新処理が成功しました！");
  }

  // TODO: 現在は未使用です。
  //  今後、仕様が固まった段階で、削除または修正することを検討します。
  //   @GetMapping("/students/details")を削除する場合は、一緒にこのメソッドも削除する予定です。
  @GetMapping("/courses")
  @ResponseBody
  public List<StudentCourse> getCourses(

      @RequestParam(required = false) String courseName) {
    return service.getCourses(courseName);
  }

  // TODO: 現在は未使用です。
  //  今後、論理削除済みの受講生を含む全件取得や、将来的な検索機能の土台として活用する可能性があります。
  //  仕様が固まった段階で、削除または修正することを検討します。
  @GetMapping("/students/details")
  @ResponseBody
  public List<StudentDetail> searchStudents() {

    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentCourses = service.getCourses(null);

    return converter.convertStudentDetails(students, studentCourses);
  }
}
