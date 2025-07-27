package raisetech.studentmanagement.data;

/**
 * 受講可能なコースの種類を表すEnumクラスです。
 * 各コースに対応するコース名とコースIDのペアを定義します。
 */
public enum CourseType {

  JAVA_FULL("Javaフルコース", "A001"),
  AWS_FULL("AWSフルコース", "A002"),
  WORDPRESS("WordPress副業コース", "A003"),
  DESIGN("デザインコース", "A004"),
  WEB_MARKETING("Webマーケティングコース", "A005"),
  UNKNOWN("", "A999"); // 未知のコースの場合

  private final String courseName;
  private final String courseId;

  /**
   * コンストラクタ
   *
   * @param courseName コース名
   * @param courseId   コースID
   */
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

  /**
   * コース名からコースIDを取得します。
   * コース名がEnumで設定されているコース名と一致している場合は、ペアになっているコースIDを返します。
   * 一致しなければ、UNKNOWN(未知のコース)のIDを返します。
   *
   * @param courseName コース名
   */
  public static CourseType fromCourseName(String courseName) {

    for (CourseType type : CourseType.values()) {
      if (type.getCourseName().equals(courseName)) {

        return type;
      }
    }
    return UNKNOWN;
  }
}
