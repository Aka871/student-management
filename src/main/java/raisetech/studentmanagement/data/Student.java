package raisetech.studentmanagement.data;

import lombok.Getter;
import lombok.Setter;

// Data層。データの設計図のようなもの(どんな情報を扱うのかが分かる)
// Lombokで自動的にフィールドに対して、getterメソッドとsetterメソッドを生成
@Getter
@Setter
public class Student {

  private String studentId;
  private String fullName;
  private String furiganaName;
  private String nickName;
  private String phoneNumber;
  private String mailAddress;
  private String municipalityName;

  //デフォルト値がnullのInteger型に変更。int型だとデフォルト値が0になるため、未登録でも0と表示される。
  //StudentList.htmlでageがnullの場合、空白になるように設定
  private Integer age;
  private String sex;
  private String occupation;
  private String remark;

  //フィールド名をdeletedにした。thymeleafとの連携問題を解決するため
  private boolean deleted;
}
