package businesslogic;

import domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImportServiceTest {

    @Test
    void mergeImportedSchedulesUpdatesDuplicatesAndAssignsIdsToNewClasses() {
        ImportService service = new ImportService();

        Schedule existing = createSchedule(
                5,
                "ENGR3791",
                "Software Testing",
                "Workshop",
                1,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Old Building",
                "Old Room"
        );

        List<Schedule> database = new ArrayList<>();
        database.add(existing);

        Schedule duplicateImport = createSchedule(
                0,
                "ENGR3791",
                "Software Testing",
                "Workshop",
                1,
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "New Building",
                "New Room"
        );

        Schedule newImport = createSchedule(
                0,
                "ENGR3791",
                "Software Testing",
                "Practical",
                2,
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                "Lab Building",
                "Lab 2"
        );

        ImportService.ImportResult result =
                service.mergeImportedSchedules(List.of(duplicateImport, newImport), database);

        assertEquals(1, result.getUpdatedRecords());
        assertEquals(1, result.getNewRecords());

        assertEquals(2, database.size());

        assertEquals(5, existing.getRecordId());
        assertEquals(LocalTime.of(13, 0), existing.getClassInstance().getStartTime());
        assertEquals(LocalTime.of(14, 0), existing.getClassInstance().getEndTime());
        assertEquals("New Building", existing.getClassInstance().getBuilding());
        assertEquals("New Room", existing.getClassInstance().getRoom());

        assertEquals(6, newImport.getRecordId());
        assertTrue(database.contains(newImport));
    }

    private Schedule createSchedule(
            int recordId,
            String topicCode,
            String topicName,
            String classFormat,
            int classInstanceNo,
            LocalTime startTime,
            LocalTime endTime,
            String building,
            String room
    ) {
        return new Schedule(
                recordId,
                new Topic(topicCode, topicName),
                new ClassAvailability("Internal", "Tonsley", "S1", 1),
                new TopicClass(classFormat, classFormat),
                new ClassInstance(
                        classInstanceNo,
                        classFormat,
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 6, 1),
                        startTime,
                        endTime,
                        "Monday",
                        building,
                        room
                )
        );
    }
}