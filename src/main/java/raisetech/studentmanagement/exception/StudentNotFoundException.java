package raisetech.studentmanagement.exception;

/**
 * 指定された受講生IDが見つからなかった場合にスローされる、例外クラスです。
 */
public class StudentNotFoundException extends RuntimeException {

  public StudentNotFoundException(String studentId) {
    super("受講生ID: " + studentId + " が見つかりません");
  }
}
