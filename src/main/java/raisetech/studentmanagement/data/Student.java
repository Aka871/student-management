package raisetech.studentmanagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private String studentId;
  private String fullName;
  private String furiganaName;
  private String nickName;
  private String mailAddress;
  private String municipalityName;
  private int age;
  private String sex;
  private String remark;
  private boolean isDeleted;
}
