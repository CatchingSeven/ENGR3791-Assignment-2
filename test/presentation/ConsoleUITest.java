package presentation;

import application.*;
import businesslogic.*;
import persistence.PersistenceAdapter;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsoleUITest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn  = System.in;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private ConsoleUI buildConsoleUI() {
        PersistenceAdapter adapter       = new PersistenceAdapter();
        MenusController menus            = new MenusController();
        ClassController classController  = new ClassController(
                new ImportService(), new SearchService(), new ValidationService(), adapter);
        TimetableController ttController = new TimetableController(
                new ScheduleEngine(), new PreferenceEngine(), new ValidationService(), adapter, new SearchService());
        return new ConsoleUI(menus, classController, ttController, new ValidationService(), adapter);
    }

    @Test
    @Order(1)
    @DisplayName("20.01 - ConsoleUI can be instantiated without throwing an exception")
    @Tag("Critical")
    @Tag("Samuel")
    void testConsoleUI_Instantiation_NoException() {
        assertDoesNotThrow(this::buildConsoleUI);
    }

    @Test
    @Order(2)
    @DisplayName("20.02 - AsciiBanner.printTitle prints output to System.out")
    @Tag("Core")
    @Tag("Samuel")
    void testAsciiBanner_PrintTitle_ProducesOutput() {
        ConsoleUI ui = buildConsoleUI();
        ui.asciiBanner.printTitle();
        assertFalse(capturedOut.toString().isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("20.03 - AsciiBanner.printTitle output contains ASCII art characters")
    @Tag("Core")
    @Tag("Samuel")
    void testAsciiBanner_PrintTitle_ContainsAsciiArt() {
        ConsoleUI ui = buildConsoleUI();
        ui.asciiBanner.printTitle();
        String out = capturedOut.toString();
        assertTrue(out.contains("_") || out.contains("|"));
    }

    @Test
    @Order(4)
    @DisplayName("20.04 - ExitRequestedException is a RuntimeException (clean stop)")
    @Tag("Critical")
    @Tag("Samuel")
    void testExitRequestedException_IsRuntimeException() {
        assertInstanceOf(RuntimeException.class, new InputHandler.ExitRequestedException());
    }

    @Test
    @Order(5)
    @DisplayName("20.05 - ConsoleUI inner objects are non-null")
    @Tag("Core")
    @Tag("Samuel")
    void testConsoleUI_InnerObjects_NotNull() {
        ConsoleUI ui = buildConsoleUI();
        assertAll(
                () -> assertNotNull(ui.asciiBanner),
                () -> assertNotNull(ui.mainMenu),
                () -> assertNotNull(ui.subMenu)
        );
    }

    @Test
    @Order(6)
    @DisplayName("20.06 - start() exits cleanly when option 13 selected")
    @Tag("Core")
    @Tag("Samuel")
    void testStart_ExitsCleanly_OnExitCommand() {
        System.setIn(new ByteArrayInputStream("13\n".getBytes()));
        ConsoleUI ui = buildConsoleUI();
        assertDoesNotThrow(ui::start);
        assertTrue(capturedOut.toString().contains("Goodbye"));
    }

    @Test
    @Order(7)
    @DisplayName("20.07 - AsciiBanner.printTitle output mentions 'EXIT' command")
    @Tag("Additional")
    @Tag("Samuel")
    void testAsciiBanner_PrintTitle_MentionsExit() {
        ConsoleUI ui = buildConsoleUI();
        ui.asciiBanner.printTitle();
        assertTrue(capturedOut.toString().contains("EXIT"));
    }
}