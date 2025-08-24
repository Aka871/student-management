package raisetech.studentmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @Operation(summary = "受講生詳細情報【一覧取得 (削除済みを除く) 】", description = "受講生の詳細情報 (受講生情報と受講生コース情報) の一覧を取得します。論理削除されている受講生の情報は除きます。")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "受講生詳細情報 (一覧・削除済みを除く) の取得に成功しました")})

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
  @Operation(summary = "受講生詳細情報【個別取得】",
      description = "受講生IDをもとに、対象の受講生を特定して、受講生の詳細情報 (受講生情報と受講生コース情報) を取得します。")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "受講生詳細情報 (個別) の取得に成功しました"),
      @ApiResponse(responseCode = "404", description = "受講生IDが見つかりません")})

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
  @Operation(summary = "例外処理の動作確認用", description = "例外処理が正しく行われるかを確認します")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "例外処理の動作確認に成功しました")})

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
  @Operation(summary = "受講生詳細情報【登録】", description = "受講生の詳細情報 (受講生情報と受講生コース情報) を登録します")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "受講生詳細情報の登録に成功しました"),
      @ApiResponse(responseCode = "400", description = "入力値が不正です"),
      @ApiResponse(responseCode = "404", description = "コース名が見つかりません")})

  @PostMapping("/students")
  public ResponseEntity<String> registerStudents(@RequestBody @Valid StudentDetail studentDetail) {
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
  @Operation(summary = "受講生詳細情報【更新】",
      description = "受講生の詳細情報 (受講生情報と受講生コース情報) を更新します。リクエストボディに含まれる受講生IDをもとに、対象の受講生を特定して更新します。")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "対象の受講生詳細情報の更新に成功しました"),
      @ApiResponse(responseCode = "404", description = "受講生IDまたはコース名が見つかりません")})

  @PutMapping("/students")
  public ResponseEntity<String> updateStudentDetail(
      @RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudentDetail(studentDetail);

    return ResponseEntity.ok("更新処理が成功しました！");
  }

  /**
   * 受講生コース情報一覧を取得します。
   * 対象は、すべての受講生 (論理削除されている受講生を含む) です。
   * <p>
   * コース名を指定した場合、該当するコースのみを取得します。
   * コース名が未指定の場合は、すべての受講生コース情報を取得します。
   *
   * @param courseName コース名
   * @return 受講生コース情報一覧
   */
  @Operation(summary = "受講生コース情報【一覧取得 (コース名指定可）】",
      description = "受講生コース情報の一覧を取得します。コース名を指定することで、該当するコースのみ取得可能です。受講生の論理削除状態に関係なく、すべての受講生のコース情報を取得します。")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "受講生コース情報 (一覧) の取得に成功しました")})

  @GetMapping("/courses")
  @ResponseBody
  public List<StudentCourse> getCourses(

      @RequestParam(required = false) String courseName) {
    return service.getCourses(courseName);
  }

  /**
   * 受講生詳細情報一覧を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、すべての受講生 (論理削除されている受講生を含む) です。
   * <p>
   * 論理削除されていない受講生のみを取得したい場合は /students エンドポイントを使用してください。
   *
   * @return 受講生詳細情報のリスト（受講生情報と受講生コース情報を結合したもの）
   */
  @Operation(summary = "受講生詳細情報【一覧取得 (削除済みを含む) 】", description = "受講生の詳細情報 (受講生情報と受講生コース情報) の一覧を取得します。論理削除されている受講生の情報を含めます。")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "受講生詳細情報 (一覧・削除済みを含む) の取得に成功しました")})

  @GetMapping("/students/details")
  @ResponseBody
  public List<StudentDetail> searchStudents() {

    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentCourses = service.getCourses(null);

    return converter.convertStudentDetails(students, studentCourses);
  }
}
