package domain;

/** DMCD entity: classAvailability. */
public class ClassAvailability {
    private String attendanceMode;
    private String campus;
    private String semester;
    private int availabilityNo;

    public ClassAvailability(String attendanceMode, String campus, String semester, int availabilityNo) {
        this.attendanceMode = clean(attendanceMode);
        this.campus = clean(campus);
        this.semester = clean(semester);
        this.availabilityNo = availabilityNo;
    }

    public String getAttendanceMode() { return attendanceMode; }
    public void setAttendanceMode(String attendanceMode) { this.attendanceMode = clean(attendanceMode); }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = clean(campus); }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = clean(semester); }

    public int getAvailabilityNo() { return availabilityNo; }
    public void setAvailabilityNo(int availabilityNo) { this.availabilityNo = availabilityNo; }

    public String displayAvailability() {
        return attendanceMode + " - " + campus + " - " + semester + " - " + availabilityNo;
    }

    public ClassAvailability copy() {
        return new ClassAvailability(attendanceMode, campus, semester, availabilityNo);
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
