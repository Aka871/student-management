package raisetech.studentmanagement;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

  private final StudentRepository repository;

  @Autowired
  public StudentManagementApplication(StudentRepository repository) {
    this.repository = repository;
  }

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

  @GetMapping("/students")
  public String getStudent(@RequestParam String name) {
    Student student = repository.searchByName(name);
    return student.getName() + " " + student.getAge() + "歳";
  }

  @GetMapping("/allStudents")
  public List<Student> getAll() {
    return repository.getAll();
  }

  @PostMapping("/students")
  public void registerStudent(String name, int age) {
    repository.registerStudent(name, age);
  }

  @PatchMapping("/students")
  public void updateStudent(String name, int age) {
    repository.updateStudent(name, age);
  }

  @DeleteMapping("/students")
  public void deleteStudent(String name) {
    repository.deleteStudent(name);
  }

  @PostMapping("/addStudents")
  public String addStudent(@RequestParam String name, @RequestParam Integer age) {
    students.put(name, age);
    return "生徒を追加しました：" + name + " " + age + "歳";
  }

  @GetMapping("/getAllStudents")
  public Map<String, Integer> getAllStudents() {
    return students;
  }

  @PutMapping("/students/{name}")
  public String updateStudentAge(@PathVariable String name, @RequestParam int newAge) {
    if (students.containsKey(name)) {
      students.put(name, newAge);
      return name + "の年齢を" + newAge + "歳に更新しました";
    } else {
      return name + " という名前の生徒は存在しません";
    }
  }
}
