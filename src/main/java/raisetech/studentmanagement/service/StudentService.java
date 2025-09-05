package raisetech.studentmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.CourseType;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.exception.StudentNotFoundException;
import raisetech.studentmanagement.repository.StudentRepository;

/**
 * 受講生情報と受講生コース情報に関するビジネスロジックを提供する、Serviceクラスです。
 * Repository層を通じてデータの取得・登録・更新などを行います。
 */
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生情報の一覧を取得します。
   * 論理削除済みの受講生を除きます。
   *
   * @return 受講生情報のリスト (論理削除済みの受講生を除く)
   */
  public List<Student> getNotDeletedStudents() {
    List<Student> allStudents = repository.searchStudents();

    return allStudents.stream()
        .filter(student -> !student.isDeleted())
        .collect(Collectors.toList());
  }

  /**
   * 受講生詳細情報の一覧を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 論理削除済みの受講生を除きます。
   *
   * @return 受講生詳細情報のリスト（受講生情報と受講生コース情報を結合したもの。論理削除済みの受講生を除く）
   */
  public List<StudentDetail> getNotDeletedStudentsDetails() {
    List<Student> students = getNotDeletedStudents();

    // コース情報は全件取得する。絞り込みは行わないため、nullを引数に渡す
    List<StudentCourse> studentsCourses = getCourses(null);

    return converter.convertStudentDetails(students, studentsCourses);
  }

  /**
   * 受講生詳細情報(個別)を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、指定した受講生IDに紐づく、受講生詳細情報です。論理削除済みの受講生を含みます。
   * <p>
   *
   * @param studentId 受講生ID
   * @return 指定したIDの受講生詳細情報（受講生情報と受講生コース情報を結合したもの。論理削除済みの受講生を含む）
   * @throws StudentNotFoundException 指定したIDの受講生が存在しない場合にスロー
   */
  public StudentDetail getStudentDetailById(String studentId) {

    Student student = repository.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException((studentId)));

    List<StudentCourse> courses = repository.findCourseById(studentId);

    StudentDetail studentDetail = new StudentDetail(student, courses);

    return studentDetail;
  }

  /**
   * 受講生コース情報の一覧を取得します。
   * 対象は、指定したコース名と一致する受講生コース情報です。(大文字小文字の区別はしません。)
   * 論理削除済みの受講生を含みます。
   * <p>
   * コース名が未指定（nullまたは空文字）の場合、すべての受講生コース情報を返します。
   *
   * @param courseName コース名 (nullまたは空文字の場合は、すべての受講生コース情報が対象)
   * @return 受講生コース情報のリスト（コース名を指定した場合は該当コースのみ。論理削除済みの受講生を含む）
   */
  public List<StudentCourse> getCourses(String courseName) {
    List<StudentCourse> allCourses = repository.searchCourses();

    if (courseName != null && !courseName.trim().isEmpty()) {
      return allCourses.stream()

          .filter(course -> course.getCourseName().equalsIgnoreCase(courseName))
          .collect(Collectors.toList());
    }
    return allCourses;
  }

  /**
   * 受講生詳細情報を登録します。
   * 受講生情報と受講生コース情報をそれぞれ登録します。
   * <p>
   * UUIDを受講生IDとして付与し、コース情報と関連付けてデータベースに保存します。<br>
   * コース開始日・コース終了日がnullの場合は、自動的に日付が補完されます。(詳細は{@link #setDefaultCourseDatesIfNull(StudentCourse
   * studentCourse)}を参照)
   *
   * @param studentDetail 登録対象の受講生詳細情報 (受講生情報と受講生コース情報)
   */
  @Transactional
  public void registerStudent(StudentDetail studentDetail) {

    String studentUuid = UUID.randomUUID().toString();

    studentDetail.getStudent().setStudentId(studentUuid);

    repository.saveStudent(studentDetail.getStudent());

    // 受講生コース情報の登録
    // コースと受講生を関連付け、どの受講生がどのコースを受講しているかを管理
    // 1人の受講生が複数のコースを受講可能
    studentDetail.getStudentsCourses().forEach(studentCourse -> {

      String courseID = getCommonCourseId(studentCourse.getCourseName());

      initStudentCourse(studentCourse, courseID, studentUuid);

      setDefaultCourseDatesIfNull(studentCourse);

      repository.saveStudentCourse(studentCourse);
    });
  }

  /**
   * UUIDを共通の受講生IDとして登録することにより、受講生情報と受講生コース情報を紐付けます。
   */
  private static void initStudentCourse(StudentCourse studentCourse, String courseID,
      String studentUuid) {

    studentCourse.setCourseId(courseID);
    studentCourse.setStudentId(studentUuid);
  }

  /**
   * コース名からコースIDを取得します。
   *
   * @param courseName コース名
   * @return Enumで設定されているコース名とペアになっているコースID
   */
  private String getCommonCourseId(String courseName) {

    return CourseType.fromCourseName(courseName).getCourseId();
  }

  /**
   * 受講生詳細情報を更新します。
   * 受講生情報と受講生コース情報をそれぞれ更新します。
   * 論理削除状態 (削除済みフラグ) の更新もここで行います。
   * <p>
   * リクエストボディに含まれる受講生IDとコース名をもとに対象を特定し、更新を行います。
   * 対象となる受講生コース情報の特定は、受講生IDに紐づくコース情報の中から、コース名に対応するコースIDを取得し、
   * そのコースIDと一致するものを検索することで行います。<br>
   * 該当コースが存在する場合は更新、存在しない場合は新規登録を行います。
   * <p>
   * 受講生情報と受講生コース情報を関連付けるため、受講生情報の受講生IDを、受講生コース情報の受講生IDに紐付けます。<br>
   * コース開始日・コース終了日がnullの場合は、自動的に日付が補完されます。(詳細は{@link #setDefaultCourseDatesIfNull(StudentCourse
   * studentCourse)}を参照)
   *
   * @param studentDetail 更新対象の受講生詳細情報 (受講生情報と受講生コース情報)
   * @throws StudentNotFoundException 指定したIDの受講生が存在しない場合にスロー
   */
  @Transactional
  public void updateStudentDetail(StudentDetail studentDetail) {

    String studentId = studentDetail.getStudent().getStudentId();

    repository.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException(studentId));

    repository.updateStudent(studentDetail.getStudent());

    List<StudentCourse> existingCourses = repository.findCourseById(studentId);

    // 受講生が複数のコースを受講できるよう、全てのコース情報を処理する
    for (StudentCourse studentCourse : studentDetail.getStudentsCourses()) {

      String courseId = CourseType.fromCourseName(studentCourse.getCourseName()).getCourseId();

      studentCourse.setCourseId(courseId);

      initStudentCourse(studentCourse, courseId, studentId);

      setDefaultCourseDatesIfNull(studentCourse);

      boolean courseExists = existingCourses.stream()
          .anyMatch(existing -> existing.getCourseId().equals(courseId));

      if (courseExists) {
        repository.updateStudentCourse(studentCourse);
      } else {
        repository.saveStudentCourse(studentCourse);
      }
    }
  }

  /**
   * 登録された受講生コース情報に、コース開始日とコース終了予定日の情報がない場合、自動的に日付を設定します。
   * <ul>
   *  <li> コース開始日がnullの場合、入力日を設定します。</li>
   *  <li> コース終了予定日がnullの場合、コース開始日から1年後の日付を設定します。</li>
   * </ul>
   * また、更新時にコース開始日のみが変更され、コース終了予定日がnullの場合も、
   * 変更後のコース開始日から1年後の日付をコース終了予定日として設定します。
   *
   * @param studentCourse 受講生コース情報
   */
  private static void setDefaultCourseDatesIfNull(StudentCourse studentCourse) {
    LocalDate now = LocalDate.now();

    if (Objects.isNull(studentCourse.getCourseStartDate())) {
      studentCourse.setCourseStartDate(now);
    }
    if (Objects.isNull(studentCourse.getCourseExpectedEndDate())) {
      studentCourse.setCourseExpectedEndDate(studentCourse.getCourseStartDate().plusYears(1));
    }
  }

  /**
   * 受講生情報を取得します。
   * 対象は、すべての受講生 (論理削除済みの受講生を含む) の中で、指定した範囲の年齢に該当する受講生です。
   * <p>
   * 下限もしくは上限の年齢がnullの場合は、nullは無視し、指定された範囲のみで絞り込みを行います。
   * 例えば、下限がnullで上限が30の場合、30歳以下の受講生情報を返します。
   * また、年齢の下限と上限の両方がnullの場合は、すべての受講生情報を返します。
   *
   * @param minAge 年齢の下限（nullの場合は下限なし）
   * @param maxAge 年齢の上限（nullの場合は上限なし）
   * @return 指定した範囲の年齢に該当する受講生情報のリスト (論理削除済みの受講生を含む)
   */
  public List<Student> getStudents(Integer minAge, Integer maxAge) {
    List<Student> allStudents = repository.searchStudents();
    return allStudents.stream()

        .filter(student -> (minAge == null || student.getAge() >= minAge)
            && (maxAge == null || student.getAge() <= maxAge))
        .collect(Collectors.toList());
  }
}
