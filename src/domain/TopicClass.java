package domain;
public class TopicClass {
    private String classFormat;
    private String classCode;

    public TopicClass(String classFormat, String classCode) {
        this.classFormat = classFormat;
        this.classCode = classCode;
    }

    public String getClassFormat() { return classFormat; }
    public void setClassFormat(String classFormat) { this.classFormat = classFormat; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
}