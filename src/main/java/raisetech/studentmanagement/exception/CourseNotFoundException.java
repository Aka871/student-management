package raisetech.studentmanagement.exception;

/**
 * 入力されたコース名が見つからなかった場合にスローされる、例外クラスです。
 */
public class CourseNotFoundException extends RuntimeException {

  public CourseNotFoundException(String courseName) {
    super("コース名: " + courseName + " が見つかりません");
  }
}
