package domain;

import java.time.LocalDate;
import java.time.LocalTime;

/** DMCD entity: classInstance. */
public class ClassInstance {
    private int classInstanceNo;
    private String classCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String day;
    private String building;
    private String room;

    public ClassInstance(int classInstanceNo, String classCode, LocalDate startDate, LocalDate endDate,
                         LocalTime startTime, LocalTime endTime, String day, String building, String room) {
        this.classInstanceNo = classInstanceNo;
        this.classCode = clean(classCode);
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = clean(day);
        this.building = clean(building);
        this.room = clean(room);
    }

    public int getClassInstanceNo() { return classInstanceNo; }
    public void setClassInstanceNo(int classInstanceNo) { this.classInstanceNo = classInstanceNo; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = clean(classCode); }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = clean(day); }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = clean(building); }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = clean(room); }

    public ClassInstance copy() {
        return new ClassInstance(classInstanceNo, classCode, startDate, endDate, startTime, endTime, day, building, room);
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
