import java.util.ArrayList;

public class Timetable {
    private String timetableCode;
    private String timetableName;
    private String semester;
    private boolean allowOverlap;
    private ArrayList<TimetablePreferences> preferences;
    private ArrayList<ClassInstance> scheduledClasses;

    public Timetable(String timetableCode, String timetableName, String semester, boolean allowOverlap) {
        this.timetableCode = timetableCode;
        this.timetableName = timetableName;
        this.semester = semester;
        this.allowOverlap = allowOverlap;
        this.preferences = new ArrayList<>();
        this.scheduledClasses = new ArrayList<>();
    }

    public String getTimetableCode() { return timetableCode; }
    public String getTimetableName() { return timetableName; }
    public String getSemester() { return semester; }
    public boolean isAllowOverlap() { return allowOverlap; }
    public ArrayList<TimetablePreferences> getPreferences() { return preferences; }
    public ArrayList<ClassInstance> getScheduledClasses() { return scheduledClasses; }
}