package domain;

import java.util.List;

public class Timetable {
    private String timetableCode;
    private String timetableName;
    private String semester;
    private boolean allowOverlap;
    private List<ClassInstance> scheduledClasses;

    public Timetable(String timetableCode, String timetableName, String semester, boolean allowOverlap) {
        this.timetableCode = timetableCode;
        this.timetableName = timetableName;
        this.semester = semester;
        this.allowOverlap = allowOverlap;
    }
    public String getTimetableCode() { return timetableCode; }
    public void setTimetableCode(String timetableCode) { this.timetableCode = timetableCode; }

    public String getTimetableName() { return timetableName; }
    public void setTimetableName(String timetableName) { this.timetableName = timetableName; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public boolean isAllowOverlap() { return allowOverlap; }
    public void setAllowOverlap(boolean allowOverlap) { this.allowOverlap = allowOverlap; }

    public List<ClassInstance> getScheduledClasses() { return scheduledClasses; }
    public void setScheduledClasses(List<ClassInstance> scheduledClasses) { this.scheduledClasses = scheduledClasses; }
}
