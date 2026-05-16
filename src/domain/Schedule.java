package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Domain aggregate that connects Topic, ClassAvailability, TopicClass and ClassInstance.
 * This maps the DMCD relationships into one usable in-memory object.
 */
public class Schedule {
    private int recordId;
    private Topic topic;
    private ClassAvailability availability;
    private TopicClass topicClass;
    private ClassInstance classInstance;

    public Schedule(int recordId, Topic topic, ClassAvailability availability,
                    TopicClass topicClass, ClassInstance classInstance) {
        this.recordId = recordId;
        this.topic = topic;
        this.availability = availability;
        this.topicClass = topicClass;
        this.classInstance = classInstance;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public ClassAvailability getAvailability() { return availability; }
    public void setAvailability(ClassAvailability availability) { this.availability = availability; }

    public TopicClass getTopicClass() { return topicClass; }
    public void setTopicClass(TopicClass topicClass) { this.topicClass = topicClass; }

    public ClassInstance getClassInstance() { return classInstance; }
    public void setClassInstance(ClassInstance classInstance) { this.classInstance = classInstance; }

    public String duplicateKey() {
        return normal(topic.displayName()) + "|" + normal(availability.displayAvailability()) + "|" +
                normal(topicClass.getClassFormat()) + "|" + classInstance.getClassInstanceNo() + "|" +
                classInstance.getStartDate() + "|" + classInstance.getEndDate() + "|" + normal(classInstance.getDay());
    }

    public String browseGroupKey() {
        return normal(topic.getTopicCode()) + "|" + normal(topic.getTopicName()) + "|" +
                normal(availability.getAttendanceMode()) + "|" + normal(availability.getCampus()) + "|" +
                normal(availability.getSemester()) + "|" + availability.getAvailabilityNo() + "|" +
                normal(topicClass.getClassFormat()) + "|" + classInstance.getClassInstanceNo();
    }

    public String timetableChoiceGroupKey() {
        return normal(topic.getTopicCode()) + "|" + normal(topicClass.getClassFormat());
    }

    public String classOfferingKey() {
        return browseGroupKey();
    }

    public boolean sameTopicAndClass(Schedule other) {
        return other != null
                && topic.getTopicCode().equalsIgnoreCase(other.topic.getTopicCode())
                && topicClass.getClassFormat().equalsIgnoreCase(other.topicClass.getClassFormat());
    }

    public boolean isLecture() {
        return topicClass.getClassFormat().toLowerCase(Locale.ROOT).contains("lecture");
    }

    public LocalDate startDate() { return classInstance.getStartDate(); }
    public LocalDate endDate() { return classInstance.getEndDate(); }
    public LocalTime startTime() { return classInstance.getStartTime(); }
    public LocalTime endTime() { return classInstance.getEndTime(); }
    public String day() { return classInstance.getDay(); }
    public String campus() { return availability.getCampus(); }

    public String normalDay() {
        String d = day() == null ? "" : day().trim().toLowerCase(Locale.ROOT);
        int space = d.indexOf(' ');
        if (space > 0) d = d.substring(0, space);
        return d.replaceAll("[^a-z]", "");
    }

    public String displayLocation() {
        String building = classInstance.getBuilding();
        String room = classInstance.getRoom();
        if (building == null || building.isBlank()) return room == null ? "" : room;
        if (room == null || room.isBlank()) return building;
        return building + ", " + room;
    }

    public Schedule copy() {
        return new Schedule(recordId, topic.copy(), availability.copy(), topicClass.copy(), classInstance.copy());
    }

    public String compactLabel() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM");
        return topic.getTopicCode() + " | " + topicClass.getClassFormat() + " " +
                classInstance.getClassInstanceNo() + " | " + availability.getCampus() + " | " +
                classInstance.getDay() + " " + classInstance.getStartTime() + "-" + classInstance.getEndTime() +
                " | " + classInstance.getStartDate().format(dateFmt) + "-" + classInstance.getEndDate().format(dateFmt);
    }

    private static String normal(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return recordId == schedule.recordId;
    }

    @Override
    public int hashCode() { return Objects.hash(recordId); }
}
