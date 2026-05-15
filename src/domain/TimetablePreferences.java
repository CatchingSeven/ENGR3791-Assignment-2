package domain;

public class TimetablePreferences {
    private String preferredCampus;
    private String preferredTime;
    private String preferredDay;

    public TimetablePreferences(String preferredCampus, String preferredTime, String preferredDay) {
        this.preferredCampus = preferredCampus;
        this.preferredTime = preferredTime;
        this.preferredDay = preferredDay;
    }
    public String getPreferredCampus() { return preferredCampus; }
    public void setPreferredCampus(String preferredCampus) { this.preferredCampus = preferredCampus; }

    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }

    public String getPreferredDay() { return preferredDay; }
    public void setPreferredDay(String preferredDay) { this.preferredDay = preferredDay; }
}
