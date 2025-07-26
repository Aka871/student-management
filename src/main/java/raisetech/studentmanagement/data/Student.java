package raisetech.studentmanagement.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  // UUIDが自動で設定される（入力不要）
  private String studentId;

  @NotBlank(message = "氏名は必須です")
  @Size(max = 100, message = "氏名は100文字以内で入力してください")
  private String fullName;

  @NotBlank(message = "ふりがなは必須です")
  @Size(max = 100, message = "ふりがなは100文字以内で入力してください")
  private String furiganaName;

  @NotBlank(message = "ニックネームは必須です")
  // DBの設定は50文字までの制限だが、20文字で十分だと判断
  @Size(max = 20, message = "ニックネームは20文字以内で入力してください")
  private String nickName;

  @NotBlank(message = "電話番号は必須です")
  @Pattern(
      regexp = "^(0[6789]0-\\d{4}-\\d{4}|0[6789]0\\d{8})$",
      message = "電話番号の形式が正しくありません（例: 090-1234-5678 または 09012345678）"
  )
  private String phoneNumber;

  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレスの形式で入力してください")
  @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
  private String mailAddress;

  @NotBlank(message = "市区町村名は必須です")
  // 日本の市区町村の最長が全角17文字程度。DBは50文字までの制限だが、25文字で十分だと判断
  @Size(max = 25, message = "市区町村名は25文字以内で入力してください")
  private String municipalityName;

  @NotNull(message = "年齢は必須です")
  @Min(value = 5, message = "5歳以上で入力してください")
  @Max(value = 110, message = "110歳以下で入力してください")
  // デフォルト値がnullのInteger型に変更。int型だとデフォルト値が0になるため、未登録でも0と表示されてしまう。
  private Integer age;

  @NotBlank(message = "性別は必須です")
  private String sex;

  @NotBlank(message = "職業は必須です")
  @Size(max = 50, message = "職業は50文字以内で入力してください")
  private String occupation;

  @Size(max = 255, message = "備考は255文字以内で入力してください")
  private String remark;

  // thymeleafとの連携問題を解決するため、フィールド名をdeletedにした
  private boolean deleted;
}
