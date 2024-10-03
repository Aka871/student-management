package raisetech.studentmanagement;

import io.micrometer.common.util.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApplication.class, args);
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
}
