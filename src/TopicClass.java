import java.util.ArrayList;

public class TopicClass {
    private String classFormat;
    private String classCode;
    private ArrayList<ClassInstance> classInstances;

    public TopicClass(String classFormat, String classCode) {
        this.classFormat = classFormat;
        this.classCode = classCode;
        this.classInstances = new ArrayList<>();
    }

    public String getClassFormat() { return classFormat; }
    public String getClassCode() { return classCode; }
    public ArrayList<ClassInstance> getClassInstances() { return classInstances; }
}