public class ClassInstance {
    private String classInstanceNo;
    private String classCode;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String day;
    private String building;
    private String room;
    private ClassAvailability availability;

    public ClassInstance(String classInstanceNo, String classCode, String startDate, String endDate,
                         String startTime, String endTime, String day, String building, String room,
                         ClassAvailability availability) {
        this.classInstanceNo = classInstanceNo;
        this.classCode = classCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.building = building;
        this.room = room;
        this.availability = availability;
    }

    public String getClassInstanceNo() { return classInstanceNo; }
    public String getClassCode() { return classCode; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getDay() { return day; }
    public String getBuilding() { return building; }
    public String getRoom() { return room; }
    public ClassAvailability getAvailability() { return availability; }

    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setBuilding(String building) { this.building = building; }
    public void setRoom(String room) { this.room = room; }
}