package domain;

import java.util.ArrayList;
import java.util.List;

/** DMCD entity: timetablePreferences. Supports the ordered preferences from the spec. */
public class TimetablePreferences {
    private String preferredCampus;
    private String preferredTime;
    private String preferredDay;
    private String timetableName;
    private String semester;
    private boolean allowLectureOverlap;
    private List<String> selectedTopicCodes;
    private List<String> selectedCampuses;
    private List<String> orderedPreferences;

    public TimetablePreferences() {
        this("", "", "");
    }

    public TimetablePreferences(String preferredCampus, String preferredTime, String preferredDay) {
        this.preferredCampus = clean(preferredCampus);
        this.preferredTime = clean(preferredTime);
        this.preferredDay = clean(preferredDay);
        this.timetableName = "";
        this.semester = "both";
        this.allowLectureOverlap = false;
        this.selectedTopicCodes = new ArrayList<>();
        this.selectedCampuses = new ArrayList<>();
        this.orderedPreferences = new ArrayList<>();
    }

    public String getPreferredCampus() { return preferredCampus; }
    public void setPreferredCampus(String preferredCampus) { this.preferredCampus = clean(preferredCampus); }

    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = clean(preferredTime); }

    public String getPreferredDay() { return preferredDay; }
    public void setPreferredDay(String preferredDay) { this.preferredDay = clean(preferredDay); }

    public String getTimetableName() { return timetableName; }
    public void setTimetableName(String timetableName) { this.timetableName = clean(timetableName); }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = clean(semester); }

    public boolean isAllowLectureOverlap() { return allowLectureOverlap; }
    public void setAllowLectureOverlap(boolean allowLectureOverlap) { this.allowLectureOverlap = allowLectureOverlap; }

    public List<String> getSelectedTopicCodes() { return selectedTopicCodes; }
    public void setSelectedTopicCodes(List<String> selectedTopicCodes) { this.selectedTopicCodes = new ArrayList<>(selectedTopicCodes); }

    public List<String> getSelectedCampuses() { return selectedCampuses; }
    public void setSelectedCampuses(List<String> selectedCampuses) { this.selectedCampuses = new ArrayList<>(selectedCampuses); }

    public List<String> getOrderedPreferences() { return orderedPreferences; }
    public void setOrderedPreferences(List<String> orderedPreferences) { this.orderedPreferences = new ArrayList<>(orderedPreferences); }

    public TimetablePreferences copy() {
        TimetablePreferences p = new TimetablePreferences(preferredCampus, preferredTime, preferredDay);
        p.setTimetableName(timetableName);
        p.setSemester(semester);
        p.setAllowLectureOverlap(allowLectureOverlap);
        p.setSelectedTopicCodes(selectedTopicCodes);
        p.setSelectedCampuses(selectedCampuses);
        p.setOrderedPreferences(orderedPreferences);
        return p;
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
