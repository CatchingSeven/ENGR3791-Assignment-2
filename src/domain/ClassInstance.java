package domain;

/**
 * Represents a specific instance of a class (e.g., a specific weekly workshop).
 */
public class ClassInstance {
    private int classInstanceNo;
    private String classCode;
    private String startDate; // Type pending team decision
    private String endDate;   // Type pending team decision
    private String startTime; // Type pending team decision
    private String endTime;   // Type pending team decision
    private String day;
    private String building;
    private String room;

    public ClassInstance(int classInstanceNo, String classCode, String startDate, String endDate,
                         String startTime, String endTime, String day, String building, String room) {
        this.classInstanceNo = classInstanceNo;
        this.classCode = classCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.building = building;
        this.room = room;
    }

    public int getClassInstanceNo() { return classInstanceNo; }
    public void setClassInstanceNo(int classInstanceNo) { this.classInstanceNo = classInstanceNo; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}