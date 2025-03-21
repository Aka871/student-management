package raisetech.studentmanagement.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.CourseType;
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

  @GetMapping("/students/new")

  //Webアプリの画面に表示するデータを準備し、テンプレート名を返すメソッド。引数のModelオブジェクトは、テンプレートにデータを渡すためのもの
  //Controllerがデータを準備する
  public String newStudents(Model model) {

    StudentDetail studentDetail = new StudentDetail();

    // デフォルトのコース情報を作成
    StudentCourse course = new StudentCourse();

    // デフォルトの日付を設定
    course.setCourseStartDate(LocalDate.now());
    course.setCourseExpectedEndDate(LocalDate.now().plusYears(1));

    //StudentDetailの中に「空のコース」を1つ作って入れている。フォームで「コースを入力する欄」を最初から1つ表示するため
    //配列や複数の要素を受け取り、固定サイズのリストを作成するためにArrays.asList()を使用
    //新しいStudentCourse（空のコース）を1つ作り、それをリストにまとめている
    studentDetail.setStudentsCourses(Arrays.asList(new StudentCourse()));

    //model.addAttributeは、テンプレート（ビュー）にデータを渡すためのメソッド
    //空のStudentDetailオブジェクトをテンプレートに渡し、フォームの初期値として使う
    //テンプレートでは、studentDetail.student.fullNameのようにして、StudentDetailオブジェクトのフィールドにアクセスできる
    //データにstudentDetailという名前をつけてモデルに追加
    model.addAttribute("studentDetail", studentDetail);

    // UNKNOWNを除いたCourseTypeの一覧をモデルに追加
    model.addAttribute("courseTypes",
        Arrays.stream(CourseType.values())
            .filter(type -> type != CourseType.UNKNOWN)
            .collect(Collectors.toList()));

    //registerStudent.htmlテンプレートを表示するための名前を返す。この名前は、テンプレートファイルの名前と一致している
    return "registerStudent";
  }

  @PostMapping("/registerStudents")

  //Thymeleafを使うときは確実に行うもの
  //@ModelAttributeアノテーションを使って、フォームから送信されたStudentDetailのデータを受け取る
  //BindingResultは、入力チェックの結果を受け取るためのもの
  //入力チェックしたいものをBindingResultに入れて、エラーが発生したら、元の画面に戻す
  //ユーザーがフォームに無効なデータを入力した場合（必須項目の未入力、形式エラーなど）を検出
  //バリデーション機能を追加した際に機能するように準備
  public String registerStudents(@ModelAttribute StudentDetail studentDetail,
      BindingResult result) {
    if (result.hasErrors()) {
      return "registerStudent";
    }

    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
      }
    }

    //新規受講生を登録する処理を実装する
    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.registerStudent(studentDetail);

    //学生が登録された後、一覧画面（/students）にリダイレクトして確認できるようにするn
    return "redirect:/students";
  }

  @GetMapping("/students/{studentId}")

  //Webアプリの画面に表示するデータを準備し、テンプレート名を返すメソッド。引数のModelオブジェクトは、テンプレートにデータを渡すためのもの
  public String getStudentDetailById(@PathVariable String studentId, Model model) {

    // Serviceクラスのメソッドを呼び出して既存データを取得
    StudentDetail studentDetail = service.getStudentDetailById(studentId);

    // 日付の文字列をHTMLに表示するためのマップを作成
    Map<String, String> dates = new HashMap<>();

    // コース情報のリストをループ処理するためのカウンター
    int i = 0;

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

      // 日付をThymeleafで表示できるように文字列に変換してマップに保存
      // 各コースごとに「startDate0」「endDate0」のような名前をつける
      dates.put("startDate" + i, course.getCourseStartDate().toString());
      dates.put("endDate" + i, course.getCourseExpectedEndDate().toString());

      // 次のコースのためにカウンターを増やす
      i++;
    }

    //model.addAttributeは、テンプレート（ビュー）にデータを渡すためのメソッド
    //テンプレートでは、studentDetail.student.fullNameのようにして、StudentDetailオブジェクトのフィールドにアクセスできる
    //データにstudentDetailという名前をつけてモデルに追加
    model.addAttribute("studentDetail", studentDetail);

    // HTMLでdates['startDate0']のように参照できるようにモデルに追加
    model.addAttribute("dates", dates);

    // UNKNOWNを除いたCourseTypeの一覧をモデルに追加
    // "courseTypes"という名前で結果をHTMLテンプレートに渡す
    model.addAttribute("courseTypes",

        // CourseType.values()でEnum定数の配列を取得し、Streamに変換
        Arrays.stream(CourseType.values())

            // UNKNOWN定数だけを除外する
            .filter(type -> type != CourseType.UNKNOWN)

            // StreamをListに変換して結果を返す
            .collect(Collectors.toList()));

    //updateStudent.htmlテンプレートを表示するための名前を返す。この名前は、テンプレートファイルの名前と一致している
    return "updateStudent";
  }

  @PostMapping("/updateStudents")

// ResponseEntityは、SpringBootでHTTPレスポンスを返すための特別なクラス
// ResponseEntity<String> を使用して、更新結果のメッセージをHTTPレスポンスとして返す
// @RequestBodyにより、リクエストのJSONデータをStudentDetail型のオブジェクトとして受け取る
  public ResponseEntity<String> updateStudentDetail(@RequestBody StudentDetail studentDetail) {
    for (StudentCourse course : studentDetail.getStudentsCourses()) {
      if (course.getCourseStartDate() != null) {
        course.setCourseExpectedEndDate(course.getCourseStartDate().plusYears(1));
        //新規受講生を登録する処理を実装する
        //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
        service.updateStudentDetail(studentDetail);
      }
    }
    return ResponseEntity.ok("更新処理が成功しました！");
  }
}
