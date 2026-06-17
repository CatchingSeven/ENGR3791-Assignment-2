package domain;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.Collections;

class TimetablePreferencesTest {
    TimetablePreferences preferenceDefault = new TimetablePreferences();
    @ParameterizedTest
    @CsvSource({"Tonsley, Morning, Monday", "Bedford Park, Afternoon, Wednesday", "Festival Plaza, Evening, Thursday"})
    @DisplayName("Test campus, time & day getters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void testGetters1(String campus, String time, String day) {
        TimetablePreferences preferences = new TimetablePreferences(campus, time, day);

        assertAll(
                () -> assertEquals(campus, preferences.getPreferredCampus()),
                () -> assertEquals(time, preferences.getPreferredTime()),
                () -> assertEquals(day, preferences.getPreferredDay())
        );
    }

    @ParameterizedTest
    @CsvSource({"Tonsley, Morning, Monday", "Bedford Park, Afternoon, Wednesday", "Festival Plaza, Evening, Thursday"})
    @DisplayName("Test campus, time & day setters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void testSetters1(String campus, String time, String day) {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        preferences.setPreferredCampus(campus);
        preferences.setPreferredTime(time);
        preferences.setPreferredDay(day);

        assertAll(
                () -> assertEquals(campus, preferences.getPreferredCampus()),
                () -> assertEquals(time, preferences.getPreferredTime()),
                () -> assertEquals(day, preferences.getPreferredDay())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Timetable Test", "Timetable 1", "Bob's timetable"})
    @DisplayName("Test getTimetableName and setTimetableName")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void getTimetableNameTest(String timetableName) {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setTimetableName(timetableName);
        assertEquals(timetableName, preferences.getTimetableName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Semester 2", "Semester 1", "Both"})
    @DisplayName("Test getSemseter and setSemester")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void getSemesterTest(String semester) {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setSemester(semester);
        assertEquals(semester, preferences.getSemester());
    }

    @Test
    @DisplayName("1.6.7: Test isAllowLectureOverlap and setAllowLectureOverlap setters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void getIsLectureOverlap() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setAllowLectureOverlap(true);
        assertTrue(preferences.isAllowLectureOverlap());

        preferences.setAllowLectureOverlap(false);
        assertFalse(preferences.isAllowLectureOverlap());

    }

    @ParameterizedTest
    @CsvSource({"COMP3802, COMP9707, ENGR3705, COMP2012",
            "COMP3723, COMP1103, COMP3019, COMP1702",
            "ENGR1234, COMP1289, ENGR9087, COMP2067"})
    @DisplayName("Test selected topic codes setters and getters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void selectedTopicCodesTest(String t1, String t2, String t3, String t4) {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> topicCodes1 = new ArrayList<>();
        Collections.addAll(topicCodes1, t1, t2, t3, t4);

        preferences.setSelectedTopicCodes(topicCodes1);
        assertEquals(topicCodes1, preferences.getSelectedTopicCodes());

    }

    @ParameterizedTest
    @CsvSource({"Tonsley, Bedford Park", "City, Sturt", "Bedford Park, Tonsley"})
    @DisplayName("Test selected campuses setters and getters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void selectedCompausesTest(String campus1, String campus2) {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> campuses1 = new ArrayList<>();
        Collections.addAll(campuses1, campus1, campus2);

        preferences.setSelectedCampuses(campuses1);
        assertEquals(campuses1, preferences.getSelectedCampuses());


    }

    @Test
    @DisplayName("First test for ordered preferences setters and getters")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void OrderedPreferemcesTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> orderedPreferences = new ArrayList<>();
        Collections.addAll(orderedPreferences, "Bedford Park", "Monday", "afternoons");

        preferences.setOrderedPreferences(orderedPreferences);
        assertEquals(orderedPreferences, preferences.getOrderedPreferences());

        ArrayList<String> orderedPreferences2 = new ArrayList<>();
        Collections.addAll(orderedPreferences2, "City", "Monday", "mornings");
        preferences.setOrderedPreferences(orderedPreferences2);
        assertEquals(orderedPreferences2, preferences.getOrderedPreferences());

    }

    @ParameterizedTest
    @CsvSource({"Tonsley, Morning, Wednesday, Timetable 1, Semester 1, true",
            "Bedford Park, Afternoon, Thursday, Timetable 2, Semester 2, false",
            "Tonsley, Morning, Monday, Timetable 3, Semester 1, true"})
    @DisplayName("Test copy function")
    @Tag("1.6.7")
    @Tag("Thomas")
    @Tag("Critical")
    void copyTest(String campus, String time, String day, String name, String semester, boolean allowOverlap) {
        TimetablePreferences original = new TimetablePreferences(campus, time, day);
        original.setTimetableName(name);
        original.setSemester(semester);
        original.setAllowLectureOverlap(allowOverlap);

        ArrayList<String> orderedPreferences = new ArrayList<>();
        Collections.addAll(orderedPreferences, time, campus, day);

        original.setOrderedPreferences(orderedPreferences);

        TimetablePreferences copy = original.copy();

        assertAll(
                () -> assertEquals(original.getPreferredCampus(), copy.getPreferredCampus()),
                () -> assertEquals(original.getPreferredTime(), copy.getPreferredTime()),
                () -> assertEquals(original.getPreferredDay(), copy.getPreferredDay()),
                () -> assertEquals(original.getTimetableName(), copy.getTimetableName()),
                () -> assertEquals(original.getSemester(), copy.getSemester()),
                () -> assertEquals(original.isAllowLectureOverlap(), copy.isAllowLectureOverlap())

        );




    }


}