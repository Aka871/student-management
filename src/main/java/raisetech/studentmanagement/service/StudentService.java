package raisetech.studentmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.studentmanagement.data.CourseType;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.exception.StudentNotFoundException;
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

      if (studentCourse.getCourseStartDate() == null) {
        studentCourse.setCourseStartDate(LocalDate.now());
      }
      if (studentCourse.getCourseExpectedEndDate() == null) {
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

  @Transactional
  public void updateStudentDetail(StudentDetail studentDetail) {

    // 受講生更新メソッド。受講生情報をデータベースのstudentsテーブルに保存
    // 目的：画面から受け取った受講生基本情報を永続化する
    repository.updateStudent(studentDetail.getStudent());

    // コース情報の更新。受講生に紐づく全てのコース情報を処理するループ
    // 目的：1人の受講生が複数のコースを受講できるようにする
    for (StudentCourse studentCourse : studentDetail.getStudentsCourses()) {

      // コース名からコースIDを取得(コースIDを明示的に設定)
      String courseID = CourseType.fromCourseName(studentCourse.getCourseName()).getCourseId();
      studentCourse.setCourseId(courseID);

      //フォームから送信された StudentCourse オブジェクトに日付情報がない場合でも、自動的に値が設定されるようになる
      if (studentCourse.getCourseStartDate() == null) {
        studentCourse.setCourseStartDate(LocalDate.now());
      }
      if (studentCourse.getCourseExpectedEndDate() == null) {
        studentCourse.setCourseExpectedEndDate(LocalDate.now().plusYears(1));
      }

      // レコードの存在確認
      // 目的：該当するコースがDB上に存在するか確認し、適切な処理（更新または新規登録）を行う
      List<StudentCourse> existingCourses = repository.findCourseById(studentCourse.getStudentId());
      boolean courseExists = false;

      // 該当する受講生が受講している全コースから、更新対象のコースIDと一致するものを探す
      for (StudentCourse existing : existingCourses) {
        if (existing.getCourseId().equals(courseID)) {
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

  // 特定のIDを持つ受講生の詳細情報を取得するメソッド
  public StudentDetail getStudentDetailById(String studentId) {

    // 特定のIDを持つ受講生情報を取得
    Student student = repository.findById(studentId);

    // 該当する受講生が見つからない場合の処理
    if (student == null) {
      // StudentNotFoundExceptionをスローして処理を中断し、エラー処理に移る
      throw new StudentNotFoundException(studentId);
    }

    // 受講生が受講しているコース情報を取得
    List<StudentCourse> courses = repository.findCourseById(studentId);

    // 受講生情報とコース情報を組み合わせてStudentDetailを作成
    // データを格納するための「入れ物」が必要なので、インスタンスを作成する
    // メソッドを使用し、何かデータが戻ってくるときはインスタンスを自分で作成する必要はない
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentsCourses(courses);

    return studentDetail;
  }

  // 削除登録されていない受講生のみを受講生一覧に表示する
  public List<Student> getNotDeletedStudents() {
    List<Student> allStudents = repository.searchStudents();
    return allStudents.stream()
        .filter(student -> !student.isDeleted())
        .collect(Collectors.toList());
  }
}
