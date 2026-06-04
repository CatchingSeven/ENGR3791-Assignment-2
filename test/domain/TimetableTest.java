package domain;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimetableTest {

    Topic topic;
    Topic topicTwo;
    ClassAvailability classAvailability;
    ClassAvailability classAvailabilityTwo;
    TopicClass topicClass;
    TopicClass topicClassTwo;
    ClassInstance classInstance;
    ClassInstance classInstanceTwo;

    List<String> warnings;
    List<Schedule> schedules;
    Schedule schedule;
    Schedule scheduleTwo;
    Timetable tt;



    @BeforeEach
    void setup(){
        topic = new Topic("COMP1002", "Fundamentals of AI");
        topicTwo = new Topic("COMP1102", "Programming Fundamentals");
        classAvailability = new ClassAvailability("In Person", "Bedford Park", "S1", 1);
        classAvailabilityTwo = new ClassAvailability("In Person", "Bedford Park", "S1", 1);
        topicClass = new TopicClass("Lecture", "1002");
        topicClassTwo = new TopicClass("Workshop", "1102");
        classInstance = new ClassInstance(1, "1002",LocalDate.parse("2026-03-06"), LocalDate.parse("2026-04-06"), LocalTime.parse("12:00"), LocalTime.parse("14:00"),"Wednesday", "Info Sci and Tech", "301");
        classInstanceTwo = new ClassInstance(1, "1102",LocalDate.parse("2026-03-06"), LocalDate.parse("2026-04-06"), LocalTime.parse("14:00"), LocalTime.parse("16:00"),"Wednesday", "Info Sci and Tech", "303");

        warnings = new ArrayList<>();
        schedules = new ArrayList<>();
        tt = new Timetable("01", "TT-01", "S1", false);
    }

    @AfterEach
    void tearDown() {
        warnings.clear();
        schedules.clear();
    }



    @Test
    @Order(1)
    @Tag("Max")
    @Tag("Critical")
    @DisplayName("Test Timetable construtor")
    void verifyTimetableConstructorAndRelatedGettersWork(){
        Timetable timetable = new Timetable("01", "TT-01", "S1", false);
        timetable.setScore(10);
        assertAll(
                () -> assertEquals("01", timetable.getTimetableCode()),
                () -> assertEquals("TT-01", timetable.getTimetableName()),
                () -> assertEquals("S1", timetable.getSemester()),
                () -> assertFalse(timetable.isAllowOverlap()),
                () -> assertEquals(10, timetable.getScore())
        );
    }

    @Test
    @Order(2)
    @Tag("Max")
    @Tag("Critical")
    @DisplayName("Adds schedules and warnings while keeping the timetable details intact")
    void testAddingSchedulesAndWarnings(){
        schedule = new Schedule(1, topic, classAvailability, topicClass, classInstance);
        scheduleTwo = new Schedule(2, topicTwo, classAvailabilityTwo, topicClassTwo, classInstanceTwo);
        schedules.add(schedule);
        warnings.add("Wrong Size");
        tt.setWarnings(warnings);
        tt.setSchedules(schedules);
        tt.addSchedule(scheduleTwo);
        tt.addWarning("Error... xyz");

        assertAll(
                () -> assertEquals("Wrong Size", tt.getWarnings().get(0)),
                () -> assertEquals("Error... xyz", tt.getWarnings().get(1)),
                () -> assertEquals(schedule, tt.getSchedules().get(0)),
                () -> assertEquals(scheduleTwo, tt.getSchedules().get(1))
        );
    }

    @Test
    @Order(3)
    @Tag("Max")
    @Tag("Critical")
    @DisplayName("Copies a timetable with separate schedule and warning lists")
    void testCopyTimetable(){
        schedule = new Schedule(1, topic, classAvailability, topicClass, classInstance);
        scheduleTwo = new Schedule(2, topicTwo, classAvailabilityTwo, topicClassTwo, classInstanceTwo);
        schedules.add(schedule);
        schedules.add(scheduleTwo);
        warnings.add("Wrong Size");
        warnings.add("Error... xyz");
        tt.setWarnings(warnings);
        tt.setSchedules(schedules);

        Timetable tt2 = tt.copy();
        tt2.setTimetableName("TT-02");
        tt2.setTimetableCode("2");
        tt2.setAllowOverlap(true);
        tt2.setSemester("S2");

        assertAll(
                () -> assertEquals("Wrong Size", tt.getWarnings().get(0)),
                () -> assertEquals("Error... xyz", tt.getWarnings().get(1)),
                () -> assertEquals(schedule, tt.getSchedules().get(0)),
                () -> assertEquals(scheduleTwo, tt.getSchedules().get(1)) ,
                () -> assertEquals("Wrong Size", tt2.getWarnings().get(0)),
                () -> assertEquals("Error... xyz", tt2.getWarnings().get(1)),
                () -> assertEquals(schedule, tt2.getSchedules().get(0)),
                () -> assertEquals(scheduleTwo, tt2.getSchedules().get(1))
        );
    }
}