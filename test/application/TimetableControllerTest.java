package application;

import businesslogic.*;
import domain.Schedule;
import domain.Timetable;
import domain.TimetablePreferences;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import persistence.PersistenceAdapter;

import java.util.ArrayList;
import java.util.List;

import static application.TimetableController.SwapResult.FAILURE;
import static application.TimetableController.SwapResult.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimetableControllerTest {
    ScheduleEngine scheduleEngine;
    PreferenceEngine preferenceEngine;
    ValidationService validationService;
    PersistenceAdapter persistenceAdapter;
    SearchService searchService;
    TimetablePreferences timetablePreferences;
    ImportService importService;
    ClassController classController;
    List<String> prefTopic;



    @BeforeEach
    void setUp() throws Exception {
        scheduleEngine = new ScheduleEngine();
        preferenceEngine = new PreferenceEngine();
        validationService = new ValidationService();
        persistenceAdapter = new PersistenceAdapter();
        searchService = new SearchService();
        timetablePreferences = new TimetablePreferences();
        importService = new ImportService();
        classController = new ClassController(importService, searchService, validationService, persistenceAdapter);

        prefTopic = new ArrayList<>();
        classController.loadSavedClasses();

        prefTopic.add("COM1102");
        prefTopic.add("COMP1103");
        prefTopic.add("COM1002");
        timetablePreferences.setSelectedTopicCodes(prefTopic);
    }

    @AfterEach
    void tearDown() {
        prefTopic.clear();
    }



    /*@ParameterizedTest
    @ValueSource(strings = {"COM1102", "COMP1103"})
    @Order(1)
    @DisplayName("Getters and Setters work for ")
    @Tag("Max")
    @Tag("Additional")
    void unusedGetterAndSetterTest(String topicCode) {
        assumeTrue(timetablePreferences != null, "Preferences must be initialized");
        assumingThat(prefTopic != null,
                () -> assertTrue(prefTopic.contains(topicCode), "Topic code should be loaded from BeforeEach")
        );
    }*/

    @Test
    @Order(1)
    @DisplayName("Generates, stores and exports a timetable using the schedule and preference engines")
    @Tag("Max")
    @Tag("Critical")
    void generateStoreAndExportTimetableSuccessfully() {
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine,
                validationService, persistenceAdapter, searchService);
        TimetablePreferences timetablePreferences1 = new TimetablePreferences();

        Timetable timetable = timetableController.generateTimetable(timetablePreferences, classController.getAllClasses());

        assertAll(
                () -> assertNotNull(timetableController.getTimetables().get(0), "The generated timetable should exist"),
                () -> assertNotNull(timetableController.findTimetable("TT-1")),
                () -> assertTrue(timetableController.exportTimetable("TT-1", persistenceAdapter.safeExportPath(timetable.getTimetableName()))),
                () -> assertThrows(IllegalArgumentException.class, () -> timetableController.generateTimetable(timetablePreferences1, classController.getAllClasses())),
                () -> assertTrue(timetableController.deleteTimetable("TT-1", true))
        );
    }

    @Test
    @Order(2)
    @DisplayName("Finds valid swap candidates and replaces a class in an existing timetable")
    @Tag("Max")
    @Tag("Core")
    void findValidSwapCandidateAndReplaceClassInExistingTimetable(){
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine,
                validationService, persistenceAdapter, searchService);

        Timetable timetable = timetableController.generateTimetable(timetablePreferences, classController.getAllClasses());
        Timetable timetable1 = timetableController.generateTimetable(timetablePreferences, classController.getAllClasses());
        List<Schedule> list = timetableController.getSwapCandidates(timetable, 63, classController.getAllClasses());

        TimetableController.SwapResult result1 = timetableController.swapClass("TT-1", 63, 64, classController.getAllClasses(), true);
        TimetableController.SwapResult result2 = timetableController.swapClass("TT-2", 0, 0, classController.getAllClasses(), false);

        assertAll(
                () -> assertEquals(SUCCESS, result1),
                () -> assertEquals(FAILURE, result2)
        );
    }
}