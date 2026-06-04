package domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    @DisplayName("1.3.1 - Builds schedule with selected classes")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void buildsScheduleWithSelectedClasses() {
        Topic topic = new Topic("COMP1701", "Game Design");
        ClassAvailability availability = new ClassAvailability("In person", "Tonsley", "S2", 1);
        TopicClass topicClass = new TopicClass("Workshop", "Workshop");
        ClassInstance classInstance = new ClassInstance(
                1,
                "Workshop",
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Monday",
                "Tonsley T1",
                "1.08"
        );

        Schedule schedule = new Schedule(1, topic, availability, topicClass, classInstance);

        assertNotNull(schedule);
        assertEquals(1, schedule.getRecordId());
        assertEquals("COMP1701", schedule.getTopic().getTopicCode());
        assertEquals("Game Design", schedule.getTopic().getTopicName());
        assertEquals("Tonsley", schedule.getAvailability().getCampus());
        assertEquals("Workshop", schedule.getTopicClass().getClassFormat());
        assertEquals("Monday", schedule.getClassInstance().getDay());
        assertEquals(LocalTime.of(9, 0), schedule.getClassInstance().getStartTime());
    }

    @DisplayName("1.3.2 - Copies schedule with same class data")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void copiesScheduleWithSameClassData() {
        Topic topic = new Topic("COMP1701", "Game Design");
        ClassAvailability availability = new ClassAvailability("In person", "Tonsley", "S2", 1);
        TopicClass topicClass = new TopicClass("Workshop", "Workshop");
        ClassInstance classInstance = new ClassInstance(
                1,
                "Workshop",
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Monday",
                "Tonsley T1",
                "1.08"
        );

        Schedule original = new Schedule(1, topic, availability, topicClass, classInstance);
        Schedule copy = original.copy();

        copy.getAvailability().setCampus("Bedford Park");

        assertNotSame(original, copy);
        assertNotSame(original.getAvailability(), copy.getAvailability());
        assertEquals("Tonsley", original.getAvailability().getCampus());
        assertEquals("Bedford Park", copy.getAvailability().getCampus());
        assertEquals(original.getTopic().getTopicCode(), copy.getTopic().getTopicCode());
        assertEquals(original.getClassInstance().getDay(), copy.getClassInstance().getDay());
    }
}