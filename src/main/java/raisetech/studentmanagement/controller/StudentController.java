package raisetech.studentmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import raisetech.studentmanagement.controller.converter.StudentConverter;
import raisetech.studentmanagement.data.Student;
import raisetech.studentmanagement.data.StudentCourse;
import raisetech.studentmanagement.domain.StudentDetail;
import raisetech.studentmanagement.service.StudentService;

@Controller//@RestControllerから変更
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  //URLパス"/students"にアクセスした際に、このメソッドが呼び出される
  @GetMapping("/students")

  //戻り値の型はStringで、ビューの名前を返す
  //Model型のパラメータ(呼び出し元が値を定義する特殊な変数)modelを受け取る。これにデータを設定すると、ビューに渡すことができる
  public String getStudents(Model model) {

    //すべての生徒情報とコース情報を取得
    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentsCourses = service.getCourses(null);

    //studentLst.htmlの<tr th:each="studentDetail : ${students}">のstudentsに値をセット
    //modelに"students"という名前で、converter.convertStudentDetails()の戻り値を設定する
    //converter.convertStudentDetails()は、生徒情報とコース情報をもとに、StudentDetailクラスのリストを作成する
    model.addAttribute("students", converter.convertStudentDetails(students, studentsCourses));

    //ビューの名前を返す。テンプレートファイルの名前が"studentList.html"であることを示している
    return "studentList";
  }

  @GetMapping("/courses")
  public List<StudentCourse> getCourses(
      @RequestParam(required = false) String courseName) {
    return service.getCourses(courseName);
  }

  @GetMapping("/students/details")
  public List<StudentDetail> searchStudents() {
    List<Student> students = service.getStudents(null, null);
    List<StudentCourse> studentCourses = service.getCourses(null);
    return converter.convertStudentDetails(students, studentCourses);
  }
}
