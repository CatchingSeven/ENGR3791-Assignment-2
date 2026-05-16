package domain;

import java.util.ArrayList;
import java.util.List;

/** DMCD entity: Timetable. */
public class Timetable {
    private String timetableCode;
    private String timetableName;
    private String semester;
    private boolean allowOverlap;
    private List<Schedule> schedules;
    private List<String> warnings;
    private int score;

    public Timetable(String timetableCode, String timetableName, String semester, boolean allowOverlap) {
        this.timetableCode = timetableCode;
        this.timetableName = clean(timetableName);
        this.semester = clean(semester);
        this.allowOverlap = allowOverlap;
        this.schedules = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.score = 0;
    }

    public String getTimetableCode() { return timetableCode; }
    public void setTimetableCode(String timetableCode) { this.timetableCode = clean(timetableCode); }

    public String getTimetableName() { return timetableName; }
    public void setTimetableName(String timetableName) { this.timetableName = clean(timetableName); }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = clean(semester); }

    public boolean isAllowOverlap() { return allowOverlap; }
    public void setAllowOverlap(boolean allowOverlap) { this.allowOverlap = allowOverlap; }

    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = new ArrayList<>(schedules); }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = new ArrayList<>(warnings); }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public void addSchedule(Schedule schedule) { this.schedules.add(schedule); }
    public void addWarning(String warning) { this.warnings.add(warning); }

    public Timetable copy() {
        Timetable t = new Timetable(timetableCode, timetableName, semester, allowOverlap);
        List<Schedule> copied = new ArrayList<>();
        for (Schedule s : schedules) copied.add(s.copy());
        t.setSchedules(copied);
        t.setWarnings(warnings);
        t.setScore(score);
        return t;
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
