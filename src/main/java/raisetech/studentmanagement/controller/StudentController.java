package raisetech.studentmanagement.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.service.StudentService;

//@Controllerアノテーションを付与したクラスはまず、クライアントから送られてきたリクエストを受け取る
//受け取ったリクエストに基づき、適切なビジネスロジックを呼び出す
//ビジネスロジックから得られた結果をレスポンスデータとしてビューに渡し、クライアントに返却する
@Controller
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

  //Thymeleafを使用して使われるHTMLのビューにデータを渡して、ブラウザに表示できるようにするメソッド
  //戻り値の型はStringで、ビューの名前を指定する
  //Model型のパラメータ(呼び出し元が値を定義する特殊な変数)modelを受け取る。これにデータを設定すると、ビューに渡すことができる
  public String getStudents(Model model) {

    //すべての生徒情報とコース情報を取得(引数がnullのため)
    //List<Student>の中には、Studentクラスのインスタンスが格納されている。変数名は任意で良い
    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentsCourses = service.getCourses(null);

    //studentList.htmlの<tr th:each="studentDetail : ${students}">のstudentsに値をセット
    //modelに"students"という名前で、converter.convertStudentDetails()の戻り値を設定する
    //List<StudentDetail>型のconverter.convertStudentDetails()は、生徒情報とコース情報をもとに、StudentDetailクラスのリストを作成する
    //addAttribute()は、Thymeleafのテンプレートに表示したいデータを渡すために使うもの
    //第一引数は、テンプレートでデータにアクセスするためのキー、第二引数は、テンプレートに表示する実際のデータを指定する
    model.addAttribute("students", converter.convertStudentDetails(students, studentsCourses));

    //ビューの名前を返す。テンプレートファイルの名前が"studentList.html"であることを示している
    return "studentList";
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

  @GetMapping("/newStudents")

  //Webアプリの画面に表示するデータを準備し、テンプレート名を返すメソッド。引数のModelオブジェクトは、テンプレートにデータを渡すためのもの
  public String newStudents(Model model) {

    StudentDetail studentDetail = new StudentDetail();

    //StudentDetailの中に「空のコース」を1つ作って入れている。フォームで「コースを入力する欄」を最初から1つ表示するため
    //配列や複数の要素を受け取り、固定サイズのリストを作成するためにArrays.asList()を使用
    //新しいStudentCourse（空のコース）を1つ作り、それをリストにまとめている
    studentDetail.setStudentsCourses(Arrays.asList(new StudentCourse()));

    //model.addAttributeは、テンプレート（ビュー）にデータを渡すためのメソッド
    //空のStudentDetailオブジェクトをテンプレートに渡し、フォームの初期値として使う
    //テンプレートでは、studentDetail.student.fullNameのようにして、StudentDetailオブジェクトのフィールドにアクセスできる
    model.addAttribute("studentDetail", studentDetail);

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

    //新規受講生を登録する処理を実装する
    //サービス層のregisterStudentメソッドを呼び出し、studentDetailから取り出した学生情報を登録する
    service.registerStudent(studentDetail);

    //学生が登録された後、一覧画面（/students）にリダイレクトして確認できるようにする
    return "redirect:/students";
  }
}
