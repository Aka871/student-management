package raisetech.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//SpringBootアプリケーションのスタートポイントを示すアノテーション
//アプリケーションのmainメソッドを含むクラスに付けて使用する
@SpringBootApplication

public class StudentManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudentManagementApplication.class, args);
  }
}
