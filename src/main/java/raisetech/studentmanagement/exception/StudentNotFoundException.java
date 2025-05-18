package raisetech.studentmanagement.exception;

public class StudentNotFoundException extends RuntimeException {

  public StudentNotFoundException(String studentId) {
    super("受講生ID: " + studentId + " が見つかりません");
  }
}
