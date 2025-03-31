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
 * 受講生情報を取り扱うサービスです。 受講生の検索や登録・更新処理を行います。
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
   * 対象は、論理削除されていない受講生のみです。
   *
   * @return 論理削除されていない受講生情報のリスト
   */
  // TODO:受講生情報の一覧だけが必要な場合を想定して、残しておく
  public List<Student> getNotDeletedStudents() {
    List<Student> allStudents = repository.searchStudents();

    return allStudents.stream()
        .filter(student -> !student.isDeleted())
        .collect(Collectors.toList());
  }

  /**
   * 受講生詳細情報の一覧を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、論理削除されていない受講生のみです。
   *
   * @return 論理削除されていない受講生詳細情報のリスト（受講生情報と受講生コース情報を結合したもの）
   */
  public List<StudentDetail> getNotDeletedStudentsDetails() {
    List<Student> students = getNotDeletedStudents();
    List<StudentCourse> studentsCourses = getCourses(null);

    return converter.convertStudentDetails(students, studentsCourses);
  }

  /**
   * 受講生詳細情報(個別)を取得します。
   * 受講生情報と受講生コース情報を合わせたものを取得します。
   * 対象は、指定した受講生IDに紐づく、受講生詳細情報です。
   *
   * @param studentId 受講生ID
   * @return 指定したIDの受講生詳細情報（受講生情報と受講生コース情報を結合したもの）
   * @throws StudentNotFoundException 指定したIDの受講生が存在しない場合にスロー
   */
  public StudentDetail getStudentDetailById(String studentId) {

    // Optionalを使うと、値があるかを明示的にチェックでき、nullによる予期せぬエラー（NullPointerException）を防げる
    Student student = repository.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException((studentId)));

    List<StudentCourse> courses = repository.findCourseById(studentId);

    StudentDetail studentDetail = new StudentDetail(student, courses);

    // 取得した各コースの日付情報をチェックし、nullの場合はデフォルト値を設定
    // Objects.isNullでnullチェックを明示的に行い、安全かつ読みやすくする
    studentDetail.getStudentsCourses().forEach(course -> {
      setDefaultCourseDatesIfNull(course);
    });
    return studentDetail;
  }

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
  public List<Student> getStudents(Integer minAge, Integer maxAge) {
    List<Student> allStudents = repository.searchStudents();
    return allStudents.stream()

        .filter(student -> (minAge == null || student.getAge() >= minAge)
            && (maxAge == null || student.getAge() <= maxAge))
        .collect(Collectors.toList());
  }

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
  // コース検索メソッド (全コースを取得し、コース名でフィルタリング。大文字と小文字の区別なし)
  public List<StudentCourse> getCourses(String courseName) {
    List<StudentCourse> allCourses = repository.searchCourses();

    if (courseName != null && !courseName.trim().isEmpty()) {
      return allCourses.stream()

          //各courseのgetCourseName()が、equalsIgnoreCase()で大文字と小文字を区別せずに、
          //指定されたcourseNameと一致する場合のみ、そのcourseを残す
          .filter(course -> course.getCourseName().equalsIgnoreCase(courseName))
          .collect(Collectors.toList());
    }
    return allCourses;
  }

  /**
   * 受講生詳細情報を新規登録します。
   * 受講生情報と受講生コース情報をそれぞれ登録します。
   * UUIDを受講生IDとして付与し、コース情報と関連付けてデータベースに保存します。
   *
   * @param studentDetail 登録対象の受講生詳細情報 (受講生情報と受講生コース情報)
   */
  @Transactional
  public void registerStudent(StudentDetail studentDetail) {

    String studentUuid = UUID.randomUUID().toString();

    // 生成したUUIDを受講生オブジェクトに設定し、データベース保存時に使用するID値を事前に確定させる
    studentDetail.getStudent().setStudentId(studentUuid);

    repository.saveStudent(studentDetail.getStudent());

    // コース情報の登録。コースと受講生を関連付け、どの受講生がどのコースを受講しているかを管理。1人の受講生が複数のコースを受講できる。
    studentDetail.getStudentsCourses().forEach(studentCourse -> {

      String courseID = getCommonCourseId(studentCourse.getCourseName());
      initStudentCourse(studentCourse, courseID, studentUuid);
      LocalDate now = LocalDate.now();

      if (Objects.isNull(studentCourse.getCourseStartDate())) {
        studentCourse.setCourseStartDate(now);
      }
      if (Objects.isNull(studentCourse.getCourseExpectedEndDate())) {
        studentCourse.setCourseExpectedEndDate(now.plusYears(1));
      }
      repository.saveStudentCourse(studentCourse);
    });
  }

  private static void initStudentCourse(StudentCourse studentCourse, String courseID,
      String studentUuid) {
    studentCourse.setCourseId(courseID);
    studentCourse.setStudentId(studentUuid);
  }

  // コース名に基づいて固定IDを取得
  private String getCommonCourseId(String courseName) {
    return CourseType.fromCourseName(courseName).getCourseId();
  }

  /**
   * 受講生詳細情報を更新します。
   * 受講生情報と受講生コース情報をそれぞれ更新します。
   * キャンセルフラグの更新もここで行います。(論理削除)
   * 受講生IDに紐づいている受講生の情報を取得し、該当する更新対象のコースIDと一致するものを探します。
   * 該当コースが存在する場合は更新、存在しない場合は新規登録を行います。
   *
   * @param studentDetail 更新対象の受講生詳細情報 (受講生情報と受講生コース情報)
   */
  @Transactional
  public void updateStudentDetail(StudentDetail studentDetail) {

    String studentId = studentDetail.getStudent().getStudentId();

    // 存在確認：見つからない場合は例外をスロー
    repository.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException(studentId));

    repository.updateStudent(studentDetail.getStudent());

    // 該当する受講生が受講している全コース情報を取得
    List<StudentCourse> existingCourses = repository.findCourseById(studentId);

    // コース情報の更新。受講生に紐づく全てのコース情報を処理するループ
    // 目的：1人の受講生が複数のコースを受講できるようにする
    for (StudentCourse studentCourse : studentDetail.getStudentsCourses()) {

      // コース名からコースIDを取得(コースIDを明示的に設定)
      String courseId = CourseType.fromCourseName(studentCourse.getCourseName()).getCourseId();
      studentCourse.setCourseId(courseId);

      // フォームから送信された StudentCourse オブジェクトに日付情報がない場合でも、自動的に値が設定されるようになる
      setDefaultCourseDatesIfNull(studentCourse);

      boolean courseExists = existingCourses.stream()
          .anyMatch(existing -> existing.getCourseId().equals(courseId));

      // 該当する受講生が受講している全コースから、更新対象のコースIDと一致するものを探す
      // 該当コースが存在する場合は更新、存在しない場合は新規登録を行う
      if (courseExists) {
        repository.updateStudentCourse(studentCourse);
      } else {
        repository.saveStudentCourse(studentCourse);
      }
    }
  }

  private static void setDefaultCourseDatesIfNull(StudentCourse studentCourse) {
    LocalDate now = LocalDate.now();
    if (Objects.isNull(studentCourse.getCourseStartDate())) {
      studentCourse.setCourseStartDate(now);
    }
    if (Objects.isNull(studentCourse.getCourseExpectedEndDate())) {
      studentCourse.setCourseExpectedEndDate(now.plusYears(1));
    }
  }
}
