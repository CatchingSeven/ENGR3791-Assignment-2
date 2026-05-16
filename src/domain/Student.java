package domain;

/** DMCD entity: Student. Included for conformity with the supplied domain model. */
public class Student {
    private String studentID;
    private String studentName;
    private String course;

    public Student(String studentID, String studentName, String course) {
        this.studentID = clean(studentID);
        this.studentName = clean(studentName);
        this.course = clean(course);
    }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = clean(studentID); }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = clean(studentName); }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = clean(course); }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
