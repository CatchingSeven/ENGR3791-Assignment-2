package application;

import businesslogic.*;
import domain.*;
import persistence.PersistenceAdapter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassControllerTest {

    private ClassController controller;
    private String originalCsv;


    @BeforeEach
    void setUp() throws IOException {
        originalCsv = Files.readString(Path.of("data/classes.csv"));

        controller = new ClassController(
                new ImportService(), new SearchService(),
                new ValidationService(), new PersistenceAdapter());
        controller.getAllClasses().add(make(1, "COMP1701", "Game Design",   "Tonsley",      "S2", "Workshop", "Monday"));
        controller.getAllClasses().add(make(2, "COMP1701", "Game Design",   "Tonsley",      "S2", "Tutorial", "Wednesday"));
        controller.getAllClasses().add(make(3, "COMP1702", "Programming",   "Bedford Park", "S1", "Lecture",  "Tuesday"));
    }


    @AfterEach
    void tearDown() throws IOException {
        Files.writeString(Path.of("data/classes.csv"), originalCsv);
    }

    private Schedule make(int id, String topicCode, String topicName, String campus,
                          String semester, String classFormat, String day) {
        return new Schedule(id,
                new Topic(topicCode, topicName),
                new ClassAvailability("In person", campus, semester, 1),
                new TopicClass(classFormat, classFormat),
                new ClassInstance(1, classFormat,
                        LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                        LocalTime.of(9, 0), LocalTime.of(10, 0), day, "Building", "Room"));
    }

    @Test
    @Order(1)
    @DisplayName("15.01 - getAllClasses returns all seeded records")
    @Tag("Critical")
    @Tag("Samuel")
    void getAllClassesReturnsAllSeededRecords() {
        assertEquals(3, controller.getAllClasses().size());
    }

    @Test
    @Order(2)
    @DisplayName("15.02 - findClassById returns correct schedule")
    @Tag("Critical")
    @Tag("Samuel")
    void findClassByIdReturnsCorrectSchedule() {
        Schedule s = controller.findClassById(2);
        assertAll(
                () -> assertNotNull(s),
                () -> assertEquals("Tutorial", s.getTopicClass().getClassFormat())
        );
    }

    @Test
    @Order(3)
    @DisplayName("15.03 - deleteClass confirmed removes the record")
    @Tag("Critical")
    @Tag("Samuel")
    void deleteClassConfirmedRemovesRecord() throws IOException {
        boolean removed = controller.deleteClass(3, true);
        assertAll(
                () -> assertTrue(removed),
                () -> assertNull(controller.findClassById(3)),
                () -> assertEquals(2, controller.getAllClasses().size())
        );
    }

    @Test
    @Order(4)
    @DisplayName("15.04 - deleteClass not confirmed leaves database unchanged")
    @Tag("Core")
    @Tag("Samuel")
    void deleteClassNotConfirmedLeavesDbUnchanged() throws IOException {
        assertFalse(controller.deleteClass(1, false));
        assertEquals(3, controller.getAllClasses().size());
    }

    @Test
    @Order(5)
    @DisplayName("15.05 - editClass confirmed replaces the record")
    @Tag("Critical")
    @Tag("Samuel")
    void editClassConfirmedReplacesRecord() throws IOException {
        Schedule updated = make(1, "COMP1701", "Game Design Updated",
                "Tonsley", "S2", "Workshop", "Monday");
        boolean edited = controller.editClass(1, updated, true);
        assertAll(
                () -> assertTrue(edited),
                () -> assertEquals("Game Design Updated",
                        controller.findClassById(1).getTopic().getTopicName())
        );
    }

    @Test
    @Order(6)
    @DisplayName("15.06 - searchClasses filters by topic code")
    @Tag("Core")
    @Tag("Samuel")
    void searchClassesFiltersByTopicCode() {
        SearchService.SearchCriteria criteria = new SearchService.QueryBuilder()
                .withTopicCode("COMP1701").build();
        assertEquals(2, controller.searchClasses(criteria).size());
    }

    @Test
    @Order(7)
    @DisplayName("15.07 - distinctTopicLabels returns unique sorted labels")
    @Tag("Additional")
    @Tag("Samuel")
    void distinctTopicLabelsReturnsUniqueLabels() {
        assertEquals(2, controller.distinctTopicLabels().size());
    }
}