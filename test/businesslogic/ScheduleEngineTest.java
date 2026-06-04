package businesslogic;

import domain.Timetable;
import domain.TimetablePreferences;
import java.util.Set;
import domain.ClassAvailability;
import domain.ClassInstance;
import domain.Schedule;
import domain.Topic;
import domain.TopicClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleEngineTest {

    private Schedule createSchedule(int id, String classFormat, String campus, String day,
                                    int startHour, int startMinute, int endHour, int endMinute) {
        Topic topic = new Topic("COMP1701", "Game Design");
        ClassAvailability availability = new ClassAvailability("In person", campus, "S2", 1);
        TopicClass topicClass = new TopicClass(classFormat, classFormat);

        ClassInstance classInstance = new ClassInstance(
                1,
                classFormat,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                LocalTime.of(startHour, startMinute),
                LocalTime.of(endHour, endMinute),
                day,
                campus + " Building",
                "1.08"
        );

        return new Schedule(id, topic, availability, topicClass, classInstance);
    }

    private Schedule createScheduleForTopic(int id, String topicCode, String classFormat, String campus, String day,
                                            int startHour, int startMinute, int endHour, int endMinute) {
        Topic topic = new Topic(topicCode, "Generated Topic");
        ClassAvailability availability = new ClassAvailability("In person", campus, "S2", 1);
        TopicClass topicClass = new TopicClass(classFormat, classFormat);

        ClassInstance classInstance = new ClassInstance(
                1,
                classFormat,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                LocalTime.of(startHour, startMinute),
                LocalTime.of(endHour, endMinute),
                day,
                campus + " Building",
                "1.08"
        );

        return new Schedule(id, topic, availability, topicClass, classInstance);
    }

    @DisplayName("3.4.1 - ScheduleEngine builds valid timetable")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void scheduleEngineBuildsValidTimetable() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Tonsley", "Monday", 10, 30, 11, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertTrue(issues.isEmpty());
    }

    @DisplayName("3.4.2 - ScheduleEngine blocks timetable with time clash")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void scheduleEngineBlocksTimetableWithTimeClash() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Tonsley", "Monday", 9, 30, 10, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertFalse(issues.isEmpty());
        assertTrue(issues.get(0).getMessage().contains("Time clash"));
    }

    @DisplayName("3.4.3 - ScheduleEngine detects insufficient commute time")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void scheduleEngineDetectsInsufficientCommuteTime() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Bedford Park", "Monday", 10, 15, 11, 15);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertFalse(issues.isEmpty());
    }

    @DisplayName("3.4.4 - ScheduleEngine accepts different campus classes with 30 minute gap")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void scheduleEngineAcceptsDifferentCampusClassesWithThirtyMinuteGap() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Bedford Park", "Monday", 10, 30, 11, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertTrue(issues.isEmpty());
    }

    @DisplayName("3.4.5 - ScheduleEngine allows lecture overlap when enabled")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineAllowsLectureOverlapWhenEnabled() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule lecture = createSchedule(1, "Lecture", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule tutorial = createSchedule(2, "Tutorial", "Tonsley", "Monday", 9, 30, 10, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(lecture, tutorial), true);

        assertTrue(issues.isEmpty());
    }

    @DisplayName("3.4.6 - ScheduleEngine blocks lecture overlap when disabled")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineBlocksLectureOverlapWhenDisabled() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule lecture = createSchedule(1, "Lecture", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule tutorial = createSchedule(2, "Tutorial", "Tonsley", "Monday", 9, 30, 10, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(lecture, tutorial), false);

        assertFalse(issues.isEmpty());
        assertTrue(issues.get(0).getMessage().contains("Time clash"));
    }

    @DisplayName("3.4.7 - ScheduleEngine ignores clashes on different days")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineIgnoresClashesOnDifferentDays() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Tonsley", "Tuesday", 9, 30, 10, 30);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertTrue(issues.isEmpty());
    }

    @DisplayName("3.4.8 - ScheduleEngine accepts back-to-back same campus classes")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineAcceptsBackToBackSameCampusClasses() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule classOne = createSchedule(1, "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule classTwo = createSchedule(2, "Tutorial", "Tonsley", "Monday", 10, 0, 11, 0);

        List<ScheduleEngine.ConflictIssue> issues =
                engine.findIssues(List.of(classOne, classTwo), false);

        assertTrue(issues.isEmpty());
    }

    @DisplayName("3.4.9 - ScheduleEngine filters classes by semester")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineFiltersClassesBySemester() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule semesterTwo = createScheduleForTopic(1, "COMP1701", "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule semesterOne = createScheduleForTopic(2, "COMP1701", "Workshop", "Tonsley", "Tuesday", 11, 0, 12, 0);
        semesterOne.getAvailability().setSemester("S1");

        TimetablePreferences preferences = new TimetablePreferences();
        preferences.setSemester("S2");

        List<Schedule> results = engine.filterClassesForPreferences(
                List.of(semesterTwo, semesterOne),
                preferences
        );

        assertEquals(1, results.size());
        assertEquals("S2", results.get(0).getAvailability().getSemester());
    }

    @DisplayName("3.4.10 - ScheduleEngine filters classes by selected topic")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineFiltersClassesBySelectedTopic() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule comp1701 = createScheduleForTopic(1, "COMP1701", "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule comp1702 = createScheduleForTopic(2, "COMP1702", "Workshop", "Tonsley", "Tuesday", 11, 0, 12, 0);

        TimetablePreferences preferences = new TimetablePreferences();
        preferences.setSelectedTopicCodes(List.of("COMP1702"));

        List<Schedule> results = engine.filterClassesForPreferences(
                List.of(comp1701, comp1702),
                preferences
        );

        assertEquals(1, results.size());
        assertEquals("COMP1702", results.get(0).getTopic().getTopicCode());
    }

    @DisplayName("3.4.11 - ScheduleEngine filters classes by selected campus")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void scheduleEngineFiltersClassesBySelectedCampus() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule tonsley = createScheduleForTopic(1, "COMP1701", "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule bedford = createScheduleForTopic(2, "COMP1701", "Workshop", "Bedford Park", "Tuesday", 11, 0, 12, 0);

        TimetablePreferences preferences = new TimetablePreferences();
        preferences.setSelectedCampuses(List.of("Bedford Park"));

        List<Schedule> results = engine.filterClassesForPreferences(
                List.of(tonsley, bedford),
                preferences
        );

        assertEquals(1, results.size());
        assertEquals("Bedford Park", results.get(0).getAvailability().getCampus());
    }

    @DisplayName("3.4.12 - ScheduleEngine builds timetable from matching classes")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void scheduleEngineBuildsTimetableFromMatchingClasses() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule workshop = createScheduleForTopic(1, "COMP1701", "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule tutorial = createScheduleForTopic(2, "COMP1701", "Tutorial", "Tonsley", "Monday", 10, 30, 11, 30);

        TimetablePreferences preferences = new TimetablePreferences();
        preferences.setTimetableName("Test Timetable");
        preferences.setSemester("S2");
        preferences.setSelectedTopicCodes(List.of("COMP1701"));
        preferences.setSelectedCampuses(List.of("Tonsley"));

        List<Timetable> timetables = engine.buildTimetables(
                List.of(workshop, tutorial),
                preferences,
                5
        );

        assertFalse(timetables.isEmpty());
        assertEquals("Test Timetable", timetables.get(0).getTimetableName());
        assertEquals(2, timetables.get(0).getSchedules().size());
    }

    @DisplayName("3.4.13 - ScheduleEngine returns unique campuses used")
    @Tag("Additional")
    @Tag("Junaid")
    @Test
    void scheduleEngineReturnsUniqueCampusesUsed() {
        ScheduleEngine engine = new ScheduleEngine();

        Schedule tonsleyOne = createScheduleForTopic(1, "COMP1701", "Workshop", "Tonsley", "Monday", 9, 0, 10, 0);
        Schedule tonsleyTwo = createScheduleForTopic(2, "COMP1701", "Tutorial", "Tonsley", "Tuesday", 11, 0, 12, 0);
        Schedule bedford = createScheduleForTopic(3, "COMP1701", "Practical", "Bedford Park", "Wednesday", 13, 0, 14, 0);

        Set<String> campuses = engine.campusesUsed(List.of(tonsleyOne, tonsleyTwo, bedford));

        assertEquals(2, campuses.size());
        assertTrue(campuses.contains("tonsley"));
        assertTrue(campuses.contains("bedford park"));
    }
}