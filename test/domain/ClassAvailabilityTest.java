package domain;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassAvailabilityTest {



    @Test
    @Order(1)
    @DisplayName("1.1.1 - Builds an availability entry with the right mode, campus and semester")
    @Tag("Max")
    @Tag("Critical")
    void buildAvailabilityEntrySuccessfully(){
        ClassAvailability availability = new ClassAvailability("In person", "Bedford Park", "S1", 1);
        assertEquals("In person - Bedford Park - S1 - 1", availability.displayAvailability());
    }

    @Test
    @Order(2)
    @DisplayName("1.1.2 - Copies an availability entry so changes do not affect the original")
    @Tag("Max")
    @Tag("Critical")
    void ensureCopyChangesDontEffectOriginal(){
        ClassAvailability availability = new ClassAvailability("In person", "Bedford Park", "S1", 1);
        ClassAvailability copiedAvail = availability.copy();

        copiedAvail.setAttendanceMode("Online");
        copiedAvail.setCampus("Tonsley");
        copiedAvail.setSemester("S2");
        copiedAvail.setAvailabilityNo(2);

        assertAll(
                () -> assertEquals("In person",availability.getAttendanceMode()),
                () -> assertEquals("Bedford Park",availability.getCampus()),
                () -> assertEquals("S1",availability.getSemester()),
                () -> assertEquals(1,availability.getAvailabilityNo()),

                () -> assertEquals("Online",copiedAvail.getAttendanceMode()),
                () -> assertEquals("Tonsley",copiedAvail.getCampus()),
                () -> assertEquals("S2",copiedAvail.getSemester()),
                () -> assertEquals(2,copiedAvail.getAvailabilityNo())
        );
    }
}