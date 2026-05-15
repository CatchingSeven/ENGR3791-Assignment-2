package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'Class' entity from the Domain Model Class Diagram.
 * Named 'CourseClass' to avoid compilation conflicts with the native Java 'Class' keyword.
 */
public class CourseClass {
    private String classFormat;
    private String classCode;

    // Represents the 1 to 1..* relationship with classInstance
    private List<ClassInstance> classInstances;

    public CourseClass(String classFormat, String classCode) {
        this.classFormat = classFormat;
        this.classCode = classCode;
        this.classInstances = new ArrayList<>();
    }

    public String getClassFormat() {
        return classFormat;
    }

    public void setClassFormat(String classFormat) {
        this.classFormat = classFormat;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public List<ClassInstance> getClassInstances() {
        return classInstances;
    }

    public void setClassInstances(List<ClassInstance> classInstances) {
        this.classInstances = classInstances;
    }

    public void addClassInstance(ClassInstance instance) {
        this.classInstances.add(instance);
    }
}
