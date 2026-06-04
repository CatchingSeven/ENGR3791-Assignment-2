package domain;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class TimetablePreferencesTest {
    TimetablePreferences preferenceDefault = new TimetablePreferences();
    @Test
    @DisplayName("Test campus, time & day getters")
    @Tag("Thomas")
    @Tag("Critical")
    void testGetters1() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        assertAll(
                () -> assertEquals("Tonsley", preferences.getPreferredCampus()),
                () -> assertEquals("Morning", preferences.getPreferredTime()),
                () -> assertEquals("Monday", preferences.getPreferredDay())
        );
    }

    @Test
    @DisplayName("Test campus, time & day setters")
    @Tag("Thomas")
    @Tag("Critical")
    void testSetters1() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        preferences.setPreferredCampus("Bedford Park");
        preferences.setPreferredTime("Afternoon");
        preferences.setPreferredDay("Tuesday");

        assertAll(
                () -> assertEquals("Bedford Park", preferences.getPreferredCampus()),
                () -> assertEquals("Afternoon", preferences.getPreferredTime()),
                () -> assertEquals("Tuesday", preferences.getPreferredDay())
        );
    }

    @Test
    @DisplayName("Test getTimetableName and setTimetableName")
    @Tag("Thomas")
    @Tag("Critical")
    void getTimetableNameTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setTimetableName("Timetable Test");
        assertEquals("Timetable Test", preferences.getTimetableName());
    }

    @Test
    @DisplayName("Test getSemseter and setSemester")
    @Tag("Thomas")
    @Tag("Critical")
    void getSemesterTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setSemester("Semester 2");
        assertEquals("Semester 2", preferences.getSemester());
    }

    @Test
    @DisplayName("Test isAllowLectureOverlap and setAllowLectureOverlap setters")
    @Tag("Thomas")
    @Tag("Critical")
    void getIsLectureOverlap() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");
        preferences.setAllowLectureOverlap(true);
        assertTrue(preferences.isAllowLectureOverlap());

        preferences.setAllowLectureOverlap(false);
        assertFalse(preferences.isAllowLectureOverlap());

    }

    @Test
    @DisplayName("Test selected topic codes setters and getters")
    @Tag("Thomas")
    @Tag("Critical")
    void selectedTopicCodesTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> topicCodes1 = new ArrayList<>();
        Collections.addAll(topicCodes1, "COMP3802", "COMP9707", "ENGR3705", "COMP2012");

        preferences.setSelectedTopicCodes(topicCodes1);
        assertEquals(topicCodes1, preferences.getSelectedTopicCodes());

        ArrayList<String> topicCodes2 = new ArrayList<>();
        Collections.addAll(topicCodes2, "COMP3723", "COMP1103", "COMP3019", "COMP1702");
        preferences.setSelectedTopicCodes(topicCodes2);
        assertEquals(topicCodes2, preferences.getSelectedTopicCodes());

    }

    @Test
    @DisplayName("Test selected campuses setters and getters")
    @Tag("Thomas")
    @Tag("Critical")
    void selectedCompausesTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> campuses1 = new ArrayList<>();
        Collections.addAll(campuses1, "Tonsley", "Beford Park");

        preferences.setSelectedCampuses(campuses1);
        assertEquals(campuses1, preferences.getSelectedCampuses());

        ArrayList<String> campuses2 = new ArrayList<>();
        Collections.addAll(campuses2, "City", "Sturt");
        preferences.setSelectedCampuses(campuses2);
        assertEquals(campuses2, preferences.getSelectedCampuses());

    }

    @Test
    @DisplayName("Test ordered preferences setters and getters")
    @Tag("Thomas")
    @Tag("Critical")
    void OrderedPreferemcesTest() {
        TimetablePreferences preferences = new TimetablePreferences("Tonsley", "Morning", "Monday");

        ArrayList<String> orderedPreferences = new ArrayList<>();
        Collections.addAll(orderedPreferences, "Time", "Campus", "Day");

        preferences.setOrderedPreferences(orderedPreferences);
        assertEquals(orderedPreferences, preferences.getOrderedPreferences());

        ArrayList<String> orderedPreferences2 = new ArrayList<>();
        Collections.addAll(orderedPreferences2, "City", "Sturt");
        preferences.setOrderedPreferences(orderedPreferences2);
        assertEquals(orderedPreferences2, preferences.getOrderedPreferences());

    }

    @Test
    @DisplayName("Test copy function")
    @Tag("Thomas")
    @Tag("Critical")
    void copyTest() {
        TimetablePreferences original = new TimetablePreferences("Tonsley", "Morning", "Monday");
        original.setTimetableName("My Timetable 1");
        original.setSemester("Semester 1");
        original.setAllowLectureOverlap(true);

        ArrayList<String> orderedPreferences = new ArrayList<>();
        Collections.addAll(orderedPreferences, "Time", "Campus", "Day");

        original.setOrderedPreferences(orderedPreferences);

        TimetablePreferences copy = original.copy();

        assertEquals(original.getPreferredCampus(), copy.getPreferredCampus());
        assertEquals(original.getPreferredTime(), copy.getPreferredTime());
        assertEquals(original.getPreferredDay(), copy.getPreferredDay());
        assertEquals(original.getTimetableName(), copy.getTimetableName());
        assertEquals(original.getSemester(), copy.getSemester());
        assertEquals(original.isAllowLectureOverlap(), copy.isAllowLectureOverlap());


    }


}