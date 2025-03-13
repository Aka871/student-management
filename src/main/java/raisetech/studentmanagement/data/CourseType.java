package raisetech.studentmanagement.data;

// Enumを定義（列挙型）。固定されたコース情報を管理するためのクラス
public enum CourseType {

  // 各コースの定数定義。引数は(コース名, コースID)の順
  JAVA_FULL("Javaフルコース", "A001"),
  AWS_FULL("AWSフルコース", "A002"),
  WORDPRESS("WordPress副業コース", "A003"),
  DESIGN("デザインコース", "A004"),
  WEB_MARKETING("Webマーケティングコース", "A005"),
  UNKNOWN("", "A999"); // 未知のコースの場合

  private final String courseName;
  private final String courseId;

  CourseType(String courseName, String courseId) {
    this.courseName = courseName;
    this.courseId = courseId;
  }

  public String getCourseName() {
    return courseName;
  }

  public String getCourseId() {
    return courseId;
  }

  // コース名からCourseTypeを探すクラスメソッド
  public static CourseType fromCourseName(String courseName) {

    // CourseType.values()で全てのEnum定数(JAVA_FULL, AWS_FULL, WORDPRESS...)の配列を取得し、順番に処理
    for (CourseType type : CourseType.values()) {

      // 現在処理中のEnum定数のコース名(type.getCourseName())が、引数で渡されたコース名と完全に一致するか確認
      if (type.getCourseName().equals(courseName)) {

        // コース名が一致したEnum定数が見つかったら、そのEnum定数(JAVA_FULLなど)をそのまま返す
        return type;
      }
    }
    // forループを最後まで処理しても一致するEnum定数が見つからなかった場合、UNKNOWN定数を返す
    return UNKNOWN;
  }
}
