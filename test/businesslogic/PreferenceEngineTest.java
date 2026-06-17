package businesslogic;

import application.ClassController;
import application.TimetableController;
import domain.*;
import org.junit.jupiter.api.*;
import persistence.PersistenceAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PreferenceEngineTest {
    ScheduleEngine scheduleEngine;
    PreferenceEngine preferenceEngine;
    ValidationService validationService;
    PersistenceAdapter persistenceAdapter;
    SearchService searchService;
    ImportService importService;
    ClassController classController;

    List<String> prefTopicA;
    List<String> prefTopicB;
    List<String> prefTopicC;

    TimetablePreferences timetablePreferencesA;
    TimetablePreferences timetablePreferencesB;
    TimetablePreferences timetablePreferencesC;
    TimetablePreferences timetablePreferences;



    @BeforeEach
    void setUp() throws Exception {
        scheduleEngine = new ScheduleEngine();
        preferenceEngine = new PreferenceEngine();
        validationService = new ValidationService();
        persistenceAdapter = new PersistenceAdapter();
        searchService = new SearchService();
        importService = new ImportService();
        classController = new ClassController(importService, searchService, validationService, persistenceAdapter);
        //get rid of class controller
        prefTopicA = new ArrayList<>();
        prefTopicB = new ArrayList<>();
        prefTopicC = new ArrayList<>();

        timetablePreferencesA = new TimetablePreferences("Bedford Park", "17:00", "Wednesday");
        timetablePreferencesB = new TimetablePreferences("Online", "16:00", "Friday");
        timetablePreferencesC = new TimetablePreferences("Online", "08:00", "Wednesday");
        timetablePreferences = new TimetablePreferences("Bedford Park", "12:00", "Wednesday");

        timetablePreferences.setOrderedPreferences(Arrays.asList(
                "same campus", "morning", "afternoon", "spread", "compact", "few days",
                "monday", "tuesday", "wednesday", "thursday", "friday", "Bedford Park"
        ));

        classController.loadSavedClasses();

        prefTopicA.add("COM1102");
        prefTopicA.add("COMP1103");
        prefTopicA.add("COM1002");

        timetablePreferencesA.setSelectedTopicCodes(prefTopicA);
        timetablePreferencesB.setSelectedTopicCodes(prefTopicA);
        timetablePreferencesC.setSelectedTopicCodes(prefTopicA);
    }

    @AfterEach
    void tearDown() {
        prefTopicA.clear();
    }



    @Test
    @Order(1)
    @DisplayName("Ranks timetables so the best preference match appears first")
    @Tag("Max")
    @Tag("Core")
    void rankTimetableSoBestPreferenceMatchAppearFirst(){
        assumeTrue(classController != null, "Controller must be loaded");
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine,
                validationService, persistenceAdapter, searchService);

        timetablePreferences.setSelectedTopicCodes(prefTopicA);

        Timetable timetableA = timetableController.generateTimetable(timetablePreferencesA, classController.getAllClasses());
        Timetable timetableB = timetableController.generateTimetable(timetablePreferencesB, classController.getAllClasses());
        Timetable timetableC = timetableController.generateTimetable(timetablePreferencesC, classController.getAllClasses());

        List<Timetable> timetables = new ArrayList<>();
        timetables.add(timetableA);
        timetables.add(timetableB);
        timetables.add(timetableC);

        List<Timetable> ranked = preferenceEngine.rankPreferences(timetables, timetablePreferences);

        assertAll(
                () -> assertNotNull(ranked),
                () -> assertEquals(timetableA, ranked.get(0))
        );
    }

    @Test
    @Order(2)
    @DisplayName("Handles blank and unknown preferences without crashing")
    @Tag("Max")
    @Tag("Additional")
    void handleBlankAndUnknownPrefGracefully(){
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine,
                validationService, persistenceAdapter, searchService);

        Timetable timetableA = timetableController.generateTimetable(timetablePreferencesA, classController.getAllClasses());
        List<Timetable> timetables = new ArrayList<>();
        timetables.add(timetableA);

        TimetablePreferences badPrefs = new TimetablePreferences();
        badPrefs.setOrderedPreferences(Arrays.asList(
                null,
                "",
                "   ",
                "random unknown pref",
                "no"
        ));

        assertDoesNotThrow(() -> {
            List<Timetable> ranked = preferenceEngine.rankPreferences(timetables, badPrefs);
            assertNotNull(ranked);
            assertEquals(1, ranked.size());
        });
    }
}