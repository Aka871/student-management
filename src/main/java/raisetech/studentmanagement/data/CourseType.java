package raisetech.studentmanagement.data;

import raisetech.studentmanagement.exception.CourseNotFoundException;

/**
 * 受講可能なコースの種類を表すEnumクラスです。
 * 各コースに対応するコース名とコースIDのペアを定義します。
 */
public enum CourseType {

  JAVA_FULL("Javaフルコース", "A001"),
  AWS_FULL("AWSフルコース", "A002"),
  WORDPRESS("WordPress副業コース", "A003"),
  DESIGN("デザインコース", "A004"),
  WEB_MARKETING("Webマーケティングコース", "A005");

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
   * コース名がEnumで定義されているコース名と一致している場合は、ペアになっているコースIDを返します。
   * 一致しなければ、CourseNotFoundExceptionをスローします。
   *
   * @param courseName コース名
   * @throws CourseNotFoundException 指定したコース名が存在しない場合にスロー
   */
  public static CourseType fromCourseName(String courseName) {

    for (CourseType type : CourseType.values()) {
      if (type.getCourseName().equals(courseName)) {

        return type;
      }
    }
    throw new CourseNotFoundException(courseName);
  }
}
