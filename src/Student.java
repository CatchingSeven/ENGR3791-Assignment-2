import java.util.ArrayList;

public class Student {
    private String studentID;
    private String studentName;
    private String course;
    private ArrayList<Topic> enrolledTopics;
    private ArrayList<Timetable> timetables;

    public Student(String studentID, String studentName, String course) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.course = course;
        this.enrolledTopics = new ArrayList<>();
        this.timetables = new ArrayList<>();
    }

    public String getStudentID() { return studentID; }
    public String getStudentName() { return studentName; }
    public String getCourse() { return course; }
    public ArrayList<Topic> getEnrolledTopics() { return enrolledTopics; }
    public ArrayList<Timetable> getTimetables() { return timetables; }
}