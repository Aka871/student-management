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
//ビジネスロジックを記述するクラスには@Serviceアノテーションを付与。そのクラスはBean化され、内部メモリ上に格納される
//Bean化とは、そのクラスがSpringの管理下に置かれ、インスタンス（オブジェクト）がSpringフレームワークによって生成・管理されることを意味する
//結果、@Autowiredアノテーションを使用することで、依存性注入（DI）が可能となり、
//このクラスのインスタンスは、他のクラスから@Autowiredアノテーションを使って自動的に取得できるようになる
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentConverter converter;

  //@Autowiredによって、SpringがStudentRepositoryのインスタンスを自動的に渡してくれる(依存性の注入)
  //このコンストラクタが呼ばれると、repositoryフィールドにStudentRepositoryのインスタンスが設定され、
  //StudentServiceクラスの中でrepositoryのインスタンスを使えるようになる
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
  // 受講生情報の一覧だけが必要な場合を想定して、残しておく
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

    // データを格納するための「入れ物」が必要なので、インスタンスを作成する
    // メソッドを使用し、何かデータが戻ってくるときはインスタンスを自分で作成する必要はない
    StudentDetail studentDetail = new StudentDetail(student, courses);

    // 取得した各コースの日付情報をチェックし、nullの場合はデフォルト値を設定
    // studentDetailから受講コース情報のリスト(studentsCourses)を取得し、各コース情報を1つずつcourse変数に入れて処理
    // studentDetail：学生一人の情報 + その学生が受講している複数のコース情報
    // course：コース1つ分の情報（StudentCourse型）
    // Objects.isNullでnullチェックを明示的に行い、安全かつ読みやすくする
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (Objects.isNull(course.getCourseStartDate())) {
        course.setCourseStartDate(LocalDate.now());
      }
      if (Objects.isNull(course.getCourseExpectedEndDate())) {
        course.setCourseExpectedEndDate(LocalDate.now().plusYears(1));
      }
    }
    return studentDetail;
  }

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定

  // 受講生検索メソッド (全受講生を取得し、年齢でフィルタリング)
  public List<Student> getStudents(Integer minAge, Integer maxAge) {
    List<Student> allStudents = repository.searchStudents();
    return allStudents.stream()

        //minAgeが指定されている場合は、ageがminAge以上、maxAgeが指定されている場合は、maxAge以下の生徒情報を取得
        .filter(student -> (minAge == null || student.getAge() >= minAge)
            && (maxAge == null || student.getAge() <= maxAge))
        //条件に合致した生徒情報をリストに格納
        .collect(Collectors.toList());
  }

  // TODO: 将来的に /students/details を削除する際、一緒にこのメソッドも削除する予定
  // コース検索メソッド (全コースを取得し、コース名でフィルタリング。大文字と小文字の区別なし)
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

  /**
   * 受講生詳細情報を新規登録します。
   * 受講生情報と受講生コース情報をそれぞれ登録します。
   * UUIDを受講生IDとして付与し、コース情報と関連付けてデータベースに保存します。
   *
   * @param studentDetail 登録対象の受講生詳細情報 (受講生情報と受講生コース情報)
   */
  // Serviceクラスの登録、更新、削除という一連のデータベースに変更を加えるメソッドには、必ず@Transactionalをつける
  // 関連する処理をひとまとまりとして扱い、途中でエラーが発生した場合、すべての変更を取り消す
  @Transactional
  public void registerStudent(StudentDetail studentDetail) {

    // ランダムなUUIDを生成して受講生IDとして使用
    // 目的：各受講生に一意の識別子を付与し、データの重複や混同を防ぐ
    String studentUuid = UUID.randomUUID().toString();

    // 生成したUUIDを受講生オブジェクトに設定
    // 目的：データベース保存時に使用するID値を事前に確定させる
    // StudentDetailオブジェクトから、Student型のオブジェクトを取得。
    // 取得したStudentオブジェクトのstudentIdフィールドに、UUIDの値をセット
    studentDetail.getStudent().setStudentId(studentUuid);

    // 受講生登録メソッド。受講生情報をデータベースのstudentsテーブルに保存
    // 目的：画面から受け取った受講生基本情報を永続化する
    repository.saveStudent(studentDetail.getStudent());

    // コース情報の登録。受講生に紐づく全てのコース情報を処理するループ
    // 目的：1人の受講生が複数のコースを受講できるようにする
    for (StudentCourse studentCourse : studentDetail.getStudentsCourses()) {

      // コース名に基づいた固定IDを取得
      String courseID = getCommonCourseId(studentCourse.getCourseName());

      // コースIDを設定
      studentCourse.setCourseId(courseID);

      // 受講生IDをコース情報に設定
      // 目的：コースと受講生を関連付け、どの受講生がどのコースを受講しているかを管理
      studentCourse.setStudentId(studentUuid);

      if (Objects.isNull(studentCourse.getCourseStartDate())) {
        studentCourse.setCourseStartDate(LocalDate.now());
      }
      if (Objects.isNull(studentCourse.getCourseExpectedEndDate())) {
        studentCourse.setCourseExpectedEndDate(LocalDate.now().plusYears(1));
      }

      // コース情報をデータベースのstudents_coursesテーブルに保存
      // 目的：受講コース情報を永続化し、後から検索・参照できるようにする
      repository.saveStudentCourse(studentCourse);
    }
  }

  // コース名に基づいて固定IDを取得するメソッド
  private String getCommonCourseId(String courseName) {

    // CourseTypeクラスのfromCourseNameメソッドを使い、コース名からCourseType定数(Enum（列挙型）定数)を取得
    // さらにgetCourseIdメソッドで、その定数のコースIDを取得して返す
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

    // 受講生更新メソッド。受講生情報をデータベースのstudentsテーブルに保存
    // 目的：画面から受け取った受講生基本情報を永続化する
    repository.updateStudent(studentDetail.getStudent());

    String studentId = studentDetail.getStudent().getStudentId();

    // 該当する受講生が受講している全コース情報を取得
    List<StudentCourse> existingCourses = repository.findCourseById(studentId);

    // コース情報の更新。受講生に紐づく全てのコース情報を処理するループ
    // 目的：1人の受講生が複数のコースを受講できるようにする
    for (StudentCourse studentCourse : studentDetail.getStudentsCourses()) {

      // コース名からコースIDを取得(コースIDを明示的に設定)
      String courseId = CourseType.fromCourseName(studentCourse.getCourseName()).getCourseId();
      studentCourse.setCourseId(courseId);

      //フォームから送信された StudentCourse オブジェクトに日付情報がない場合でも、自動的に値が設定されるようになる
      if (Objects.isNull(studentCourse.getCourseStartDate())) {
        studentCourse.setCourseStartDate(LocalDate.now());
      }
      if (Objects.isNull(studentCourse.getCourseExpectedEndDate())) {
        studentCourse.setCourseExpectedEndDate(LocalDate.now().plusYears(1));
      }

      boolean courseExists = false;

      // 該当する受講生が受講している全コースから、更新対象のコースIDと一致するものを探す
      for (StudentCourse existing : existingCourses) {
        if (existing.getCourseId().equals(courseId)) {
          courseExists = true;
          break;
        }
      }

      // 該当コースが存在する場合は更新、存在しない場合は新規登録を行う
      // 目的：APIからの更新リクエストを柔軟に処理し、データの整合性を保つ
      if (courseExists) {
        // 既存のコースを更新
        repository.updateStudentCourse(studentCourse);
      } else {
        // 新規コースとして登録
        repository.saveStudentCourse(studentCourse);
      }
    }
  }
}
