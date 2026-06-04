package businesslogic;

import domain.Timetable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValidationServiceTest {

    private ValidationService service;

    @BeforeAll
    static void setUpAll() {
        System.out.println("ValidationService tests starting");
    }

    @BeforeEach
    void setUp() {
        service = new ValidationService();
    }

    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    @Order(1)
    @DisplayName("6.01 - Valid date and time range raises no exception")
    @Tag("Critical")
    @Tag("Samuel")
    void validDateAndTimeRangeRaisesNoException() {
        assertDoesNotThrow(() -> service.validateDateAndTime(
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 11, 30),
                LocalTime.of(9, 0), LocalTime.of(10, 0)));
    }

    @Test
    @Order(2)
    @DisplayName("6.02 - Null start date throws IllegalArgumentException")
    @Tag("Critical")
    @Tag("Samuel")
    void nullStartDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                service.validateDateAndTime(null, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)));
    }

    @Test
    @Order(3)
    @DisplayName("6.03 - Start date after end date throws IllegalArgumentException")
    @Tag("Critical")
    @Tag("Samuel")
    void startDateAfterEndDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                service.validateDateAndTime(
                        LocalDate.of(2026, 12, 1), LocalDate.of(2026, 1, 1),
                        LocalTime.of(9, 0), LocalTime.of(10, 0)));
    }

    @Test
    @Order(4)
    @DisplayName("6.04 - 24h format string parsed to correct LocalTime")
    @Tag("Core")
    @Tag("Samuel")
    void twentyFourHourStringParsedCorrectly() {
        assertEquals(LocalTime.of(14, 30), service.enforce24HourFormat("14:30"));
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("6.05 - 12h AM/PM input converted to correct 24h time")
    @Tag("Core")
    @Tag("Samuel")
    @CsvSource({"9:00 AM, 9, 0", "2:30 PM, 14, 30", "12:00 AM, 0, 0"})
    void twelveHourInputConvertedCorrectly(String input, int hour, int minute) {
        LocalTime result = service.enforce24HourFormat(input);
        assertAll(
                () -> assertEquals(hour,   result.getHour()),
                () -> assertEquals(minute, result.getMinute())
        );
    }

    @ParameterizedTest
    @Order(6)
    @DisplayName("6.06 - Invalid time formats throw IllegalArgumentException")
    @Tag("Core")
    @Tag("Samuel")
    @ValueSource(strings = {"25:00", "abc", "14-30", "99:99"})
    void invalidTimeFormatsThrow(String input) {
        assertThrows(IllegalArgumentException.class, () -> service.enforce24HourFormat(input));
    }

    @Test
    @Order(7)
    @DisplayName("6.07 - Null time input throws IllegalArgumentException")
    @Tag("Critical")
    @Tag("Samuel")
    void nullTimeInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.enforce24HourFormat(null));
    }

    @Test
    @Order(8)
    @DisplayName("6.08 - generateUniqueTimetableName returns 'timetable 1' for empty list")
    @Tag("Core")
    @Tag("Samuel")
    void generateUniqueNameEmptyList() {
        assertEquals("timetable 1", service.generateUniqueTimetableName(new ArrayList<>()));
    }

    @Test
    @Order(9)
    @DisplayName("6.09 - ensureUniqueTimetableName appends suffix on collision")
    @Tag("Core")
    @Tag("Samuel")
    void ensureUniqueNameAppendsSuffix() {
        List<Timetable> existing = new ArrayList<>();
        existing.add(new Timetable("TT-1", "My Timetable", "S2", false));
        assertEquals("My Timetable 2", service.ensureUniqueTimetableName("My Timetable", existing));
    }

    @RepeatedTest(3)
    @Order(10)
    @DisplayName("6.10 - timetableNameExists is case-insensitive and consistent")
    @Tag("Additional")
    @Tag("Samuel")
    void timetableNameExistsIsCaseInsensitive() {
        List<Timetable> existing = new ArrayList<>();
        existing.add(new Timetable("TT-1", "Semester 2", "S2", false));
        assertTrue(service.timetableNameExists(existing, "semester 2"));
    }
}