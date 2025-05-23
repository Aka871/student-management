package raisetech.studentmanagement.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import raisetech.studentmanagement.exception.StudentNotFoundException;

/**
 * アプリケーション全体の例外をまとめて管理するグローバル例外ハンドラーです。
 * このクラスに定義されたメソッドは、特定の例外が発生した際に呼び出され、
 * クライアントに適切なエラーレスポンスを返します。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 受講生IDが見つからない例外をハンドリングします。
   * クライアントに404 Not Foundステータスと詳細なエラーメッセージを返します。
   *
   * @param ex 発生したStudentNotFoundException
   * @return エラーメッセージを含むJSON形式のレスポンスとHTTPステータスコード 404 (NOT FOUND)
   */
  @ExceptionHandler(StudentNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleStudentNotFoundException(
      StudentNotFoundException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}
