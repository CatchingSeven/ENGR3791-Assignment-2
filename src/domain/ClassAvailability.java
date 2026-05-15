package domain;

public class ClassAvailability {
    private String attendanceMode;
    private String campus;
    private String semester;
    private int availabilityNo;

    public ClassAvailability(String attendanceMode, String campus, String semester, int availabilityNo) {
        this.attendanceMode = attendanceMode;
        this.campus = campus;
        this.semester = semester;
        this.availabilityNo = availabilityNo;
    }

    public String getAttendanceMode() { return attendanceMode; }
    public void setAttendanceMode(String attendanceMode) { this.attendanceMode = attendanceMode; }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getAvailabilityNo() { return availabilityNo; }
    public void setAvailabilityNo(int availabilityNo) { this.availabilityNo = availabilityNo; }
}
