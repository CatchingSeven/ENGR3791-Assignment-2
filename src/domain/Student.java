package domain;

public class Student {
    private String studentID;
    private String studentName;
    private String course;

    public Student(String studentID, String studentName, String course) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.course = course;
    }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
}
