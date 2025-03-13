package raisetech.studentmanagement.exception;

// 受講生が見つからない場合に発生させる例外クラス
// RuntimeExceptionとは、プログラム実行中に発生する可能性があるエラーの基本クラス
// これを継承することで、try-catchで囲む必要がなくなり、コードがすっきりする
public class StudentNotFoundException extends RuntimeException {

  // コンストラクタ - 受講生IDを受け取ってエラーメッセージを作る
  public StudentNotFoundException(String studentId) {

    // 親クラス(RuntimeException)のコンストラクタを呼び出し、エラーメッセージを設定
    super("受講生ID: " + studentId + " が見つかりません");
  }
}
