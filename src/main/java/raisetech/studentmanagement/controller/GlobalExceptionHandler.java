package raisetech.studentmanagement.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import raisetech.studentmanagement.exception.StudentNotFoundException;

// アプリケーション全体の例外を処理するためのクラスであることを示す
@ControllerAdvice
public class GlobalExceptionHandler {

  // StudentNotFoundExceptionが発生した場合に呼び出されるメソッド
  @ExceptionHandler(StudentNotFoundException.class)

  // 例外から取得したエラーメッセージをモデルに追加し、ビュー(HTML)で表示できるようにする
  public String handleStudentNotFoundException(StudentNotFoundException ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    // エラー画面のテンプレート名
    return "error";
  }
}
