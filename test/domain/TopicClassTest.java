package domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


class TopicClassTest {
    Topic topic = new Topic("COMP303", "Professional Skills");
    @ParameterizedTest
    @CsvSource({"Lecture, Workshop",
            "Laboratory, Tutorial",
            "Intensive, Workshop "})
    @DisplayName("Test get and set class format")
    @Order(1)
    @Tag("1.8.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testClassFormat1(String format1, String format2) {
        TopicClass topicClassTest = new TopicClass(format1, "COMP1702");
        assertEquals(format1, topicClassTest.getClassFormat());

        topicClassTest.setClassFormat(format2);
        assertEquals(format2, topicClassTest.getClassFormat());
    }

    @ParameterizedTest
    @CsvSource({"COMP1702, ENGR0342",
            "COMP1892, COMP0965",
            "ENGR1298, ENGR0965"})
    @DisplayName("Test get and set class code")
    @Order(2)
    @Tag("1.8.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testClassCode1(String code1, String code2) {
        TopicClass topicClassTest = new TopicClass("Lecture", code1);
        assertEquals(code1, topicClassTest.getClassCode());

        topicClassTest.setClassCode(code2);
        assertEquals(code2, topicClassTest.getClassCode());

    }

    @ParameterizedTest
    @CsvSource({"Lecture, COMP1702", "Workshop, COMP1802", "Intensive, COMP3802"})
    @DisplayName("Test class copy")
    @Order(3)
    @Tag("1.8.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testCopy1(String format, String code) {
        TopicClass original = new TopicClass(format, code);
        TopicClass copy = original.copy();

        assertAll(
                () -> assertEquals(original.getClassFormat(), copy.getClassFormat()),
                () -> assertEquals(original.getClassCode(), copy.getClassCode())
        );

    }
}