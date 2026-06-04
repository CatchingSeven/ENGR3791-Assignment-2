package domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    @DisplayName("1.4.1 - Stores student timetable data correctly")
    @Tag("Critical")
    @Tag("Junaid")
    @Test
    void storesStudentTimetableDataCorrectly() {
        Student student = new Student("12345", "Junaid", "Software Engineering");

        assertNotNull(student);
        assertEquals("12345", student.getStudentID());
        assertEquals("Junaid", student.getStudentName());
        assertEquals("Software Engineering", student.getCourse());
    }
}