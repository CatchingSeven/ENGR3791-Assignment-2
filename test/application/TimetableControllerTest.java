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





    @Test
    @Order(1)
    @DisplayName("4.3.1 - Generates and store timetable successfully")
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
                () -> assertEquals(timetable, timetableController.findTimetable("TT-1"))

        );
    }

    @Test
    @Order(2)
    @DisplayName("4.3.2 - Finds valid swap candidates and replaces a class in an existing timetable")
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
                () -> assertEquals(SUCCESS, result2)
        );
    }

    @Test
    @Order(3)
    @DisplayName("4.3.3 - Export and delete timetable successfully")
    @Tag("Max")
    @Tag("Critical")
    void exportAndDeleteSuccessfully() {
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine,
                validationService, persistenceAdapter, searchService);
        TimetablePreferences timetablePreferences1 = new TimetablePreferences();

        timetableController.generateTimetable(timetablePreferences, classController.getAllClasses());
        String timetableName = timetableController.getTimetables().get(0).getTimetableName();

        assertAll(
                () -> assertTrue(timetableController.exportTimetable(timetableName, persistenceAdapter.safeExportPath(timetableName))),
                () -> assertTrue(timetableController.deleteTimetable("TT-1", true))
        );
    }

    private String toString(String timetableName) {
        return timetableName;
    }
}