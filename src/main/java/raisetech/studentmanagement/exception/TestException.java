package raisetech.studentmanagement.exception;

/**
 * 例外処理が正しく行われるかを確認するために投げられる例外クラスです。
 */
public class TestException extends RuntimeException {

  public TestException(String message) {
    super(message);
  }
}
