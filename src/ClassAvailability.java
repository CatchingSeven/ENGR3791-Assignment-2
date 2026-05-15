public class ClassAvailability {
    private String attendanceMode;
    private String campus;
    private String semester;
    private String availabilityNo;

    public ClassAvailability(String attendanceMode, String campus, String semester, String availabilityNo) {
        this.attendanceMode = attendanceMode;
        this.campus = campus;
        this.semester = semester;
        this.availabilityNo = availabilityNo;
    }

    public String getAttendanceMode() { return attendanceMode; }
    public String getCampus() { return campus; }
    public String getSemester() { return semester; }
    public String getAvailabilityNo() { return availabilityNo; }
}