package domain;

/** DMCD entity: Class. Named TopicClass because Java reserves the word Class. */
public class TopicClass {
    private String classFormat;
    private String classCode;

    public TopicClass(String classFormat, String classCode) {
        this.classFormat = clean(classFormat);
        this.classCode = clean(classCode == null || classCode.isBlank() ? classFormat : classCode);
    }

    public String getClassFormat() { return classFormat; }
    public void setClassFormat(String classFormat) { this.classFormat = clean(classFormat); }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = clean(classCode); }

    public TopicClass copy() { return new TopicClass(classFormat, classCode); }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
