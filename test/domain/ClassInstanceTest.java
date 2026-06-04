package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClassInstanceTest {



    @Test
    @Order(1)
    @DisplayName("1.2.1 - Builds a class instance with the expected code, dates, time and room")
    @Tag("Max")
    @Tag("Critical")
    void buildClassInstanceSuccessfully(){
        ClassInstance instance = new ClassInstance(1, "COMP1002", LocalDate.parse("2026-03-11"), LocalDate.parse("2026-04-11"), LocalTime.parse("14:00:00"), LocalTime.parse("16:00:00"), "Wednesday", "Info Sci & Tech", "301 BYOD Computer Lab");

        assertAll(
                () -> assertEquals(1,instance.getClassInstanceNo()),
                () -> assertEquals("COMP1002", instance.getClassCode()),
                () -> assertEquals(LocalDate.parse("2026-03-11"), instance.getStartDate()),
                () -> assertEquals(LocalDate.parse("2026-04-11"), instance.getEndDate()),
                () -> assertEquals(LocalTime.parse("14:00:00"), instance.getStartTime()),
                () -> assertEquals(LocalTime.parse("16:00:00"), instance.getEndTime()),
                () -> assertEquals("Wednesday",instance.getDay()),
                () -> assertEquals("Info Sci & Tech", instance.getBuilding()),
                () -> assertEquals("301 BYOD Computer Lab", instance.getRoom())
        );
    }

    @Test
    @Order(2)
    @DisplayName("1.2.2 - Copies a class instance so later edits do not affect the original")
    @Tag("Max")
    @Tag("Core")
    void ensureCopyChangesDontEffectOriginal(){
        ClassInstance instance = new ClassInstance(1, "COMP1002", LocalDate.parse("2026-03-11"), LocalDate.parse("2026-04-11"), LocalTime.parse("14:00:00"), LocalTime.parse("16:00:00"), "Wednesday", "Info Sci & Tech", "301 BYOD Computer Lab");
        ClassInstance copiedClassInst =  instance.copy();

        copiedClassInst.setClassInstanceNo(2);
        copiedClassInst.setClassCode("COMP1102");
        copiedClassInst.setStartDate(LocalDate.parse("2026-03-12"));
        copiedClassInst.setEndDate(LocalDate.parse("2026-04-21"));
        copiedClassInst.setStartTime(LocalTime.parse("08:00:00"));
        copiedClassInst.setEndTime(LocalTime.parse("10:00:00"));
        copiedClassInst.setDay("Thursday");
        copiedClassInst.setBuilding("Info Sci & Tech");
        copiedClassInst.setRoom("302 BYOD Computer Lab");

        assertAll(
                () -> assertEquals(2,copiedClassInst.getClassInstanceNo()),
                () -> assertEquals("COMP1102", copiedClassInst.getClassCode()),
                () -> assertEquals(LocalDate.parse("2026-03-12"), copiedClassInst.getStartDate()),
                () -> assertEquals(LocalDate.parse("2026-04-21"), copiedClassInst.getEndDate()),
                () -> assertEquals(LocalTime.parse("08:00:00"), copiedClassInst.getStartTime()),
                () -> assertEquals(LocalTime.parse("10:00:00"), copiedClassInst.getEndTime()),
                () -> assertEquals("Thursday",copiedClassInst.getDay()),
                () -> assertEquals("Info Sci & Tech", copiedClassInst.getBuilding()),
                () -> assertEquals("302 BYOD Computer Lab", copiedClassInst.getRoom()),

                () -> assertEquals(1,instance.getClassInstanceNo()),
                () -> assertEquals("COMP1002", instance.getClassCode()),
                () -> assertEquals(LocalDate.parse("2026-03-11"), instance.getStartDate()),
                () -> assertEquals(LocalDate.parse("2026-04-11"), instance.getEndDate()),
                () -> assertEquals(LocalTime.parse("14:00:00"), instance.getStartTime()),
                () -> assertEquals(LocalTime.parse("16:00:00"), instance.getEndTime()),
                () -> assertEquals("Wednesday",instance.getDay()),
                () -> assertEquals("Info Sci & Tech", instance.getBuilding()),
                () -> assertEquals("301 BYOD Computer Lab", instance.getRoom())
        );
    }
}