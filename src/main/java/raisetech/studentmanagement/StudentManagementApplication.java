package raisetech.studentmanagement;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

  private String name = "Enami Kouji";
  private int age = 37;

  private Map<String, Integer> students = new HashMap<>();

  public static void main(String[] args) {
    SpringApplication.run(StudentManagementApplication.class, args);
  }

  @PostConstruct
  public void initializeStudents() {
    students.put("Yamada Tarou", 20);
    students.put("Suzuki Hanako", 32);
    students.put("Tanaka Jirou", 45);
  }

  @GetMapping("/hello")
  public String hello() {
    String message = "Hello, World!";
    if (StringUtils.isEmpty(message)) {
      return "Message is empty!";
    } else {
      return message;
    }
  }

  @GetMapping("/studentInfo")
  public String getStudentInfo() {
    return name + " " + age + "歳";
  }

  @PostMapping("/studentInfo")
  public void setStudentInfo(String name, int age) {
    this.name = name;
    this.age = age;
  }

  @PostMapping("/studentName")
  public void updateStudentName(String name) {
    this.name = name;
  }

  @PostMapping("/addStudent")
  public String addStudent(@RequestParam String name, @RequestParam Integer age) {
    students.put(name, age);
    return "生徒を追加しました：" + name + " " + age + "歳";
  }

  @GetMapping("/students")
  public Map<String, Integer> getAllStudents() {
    return students;
  }

  @PutMapping("/student/{name}")
  public String updateStudentAge(@PathVariable String name, @RequestParam int newAge) {
    if (students.containsKey(name)) {
      students.put(name, newAge);
      return name + "の年齢を" + newAge + "歳に更新しました";
    } else {
      return name + " という名前の生徒は存在しません";
    }
  }
}
