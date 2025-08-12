package raisetech.studentmanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 受講生管理アプリケーションの起動クラスです。
 * Spring Bootによりアプリケーションを起動します。
 * OpenAPI (Swagger UI) による API ドキュメントの設定も行います。
 */
@OpenAPIDefinition
@SpringBootApplication
public class StudentManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudentManagementApplication.class, args);
  }
}
