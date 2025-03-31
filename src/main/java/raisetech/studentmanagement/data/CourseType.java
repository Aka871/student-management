package raisetech.studentmanagement.data;

public enum CourseType {

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

  public static CourseType fromCourseName(String courseName) {

    for (CourseType type : CourseType.values()) {
      if (type.getCourseName().equals(courseName)) {

        return type;
      }
    }
    return UNKNOWN;
  }
}
