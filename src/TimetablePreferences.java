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
    public String getPreferredTime() { return preferredTime; }
    public String getPreferredDay() { return preferredDay; }
}
