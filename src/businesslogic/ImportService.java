package businesslogic;

import domain.ClassAvailability;
import domain.ClassInstance;
import domain.CourseClass;
import domain.Topic;
import persistence.PerseveranceAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the importation, parsing, and deduplication of class data from CSV files.
 * Adheres strictly to the Layer 3 architecture by utilizing CSVMerger and DuplicateResolver logic.
 */
public class ImportService {

    private PerseveranceAdapter persistenceAdapter;

    // In-memory data store representing the parsed and deduplicated state
    private List<ParsedRecordWrapper> masterDatabase;

    public ImportService(PerseveranceAdapter persistenceAdapter) {
        this.persistenceAdapter = persistenceAdapter;
        this.masterDatabase = new ArrayList<>();
    }

    /**
     * Imports topic data from a CSV file.
     * If a record with the same Topic, Availability, Class, Class instance, Date,
     * and Day already exists, it updates the Time and Location instead of duplicating.
     * * @param filePath The path to the CSV file.
     * @return A summary string detailing new records imported and records updated.
     */
    public String importClassData(String filePath) {
        int newRecordsCount = 0;
        int updatedRecordsCount = 0;

        // Temporary staging area to allow for a full rollback if the file is malformed
        List<ParsedRecordWrapper> stagingDatabase = new ArrayList<>(this.masterDatabase);

        try {
            List<String[]> rawData = persistenceAdapter.readCSV(filePath);
            CSVMerger parser = new CSVMerger();
            DuplicateResolver resolver = new DuplicateResolver();

            for (String[] row : rawData) {
                // Ensure the row has the exact number of required columns (8 based on spec)
                if (row.length < 8) continue;

                // 1. Map raw CSV strings to Domain Objects
                ParsedRecordWrapper parsedRecord = parser.mapRowToDomain(row);

                // 2. Resolve Duplicates against the staging database
                boolean isDuplicate = resolver.resolveAndMerge(stagingDatabase, parsedRecord);

                if (isDuplicate) {
                    updatedRecordsCount++;
                } else {
                    stagingDatabase.add(parsedRecord);
                    newRecordsCount++;
                }
            }

            // Commit changes to the master database only if the entire file parses successfully
            this.masterDatabase = stagingDatabase;
            return String.format("Import successful. New records: %d, Updated records: %d",
                    newRecordsCount, updatedRecordsCount);

        } catch (IOException e) {
            return "Error reading the file: " + e.getMessage() + ". Import rolled back.";
        } catch (Exception e) {
            return "Import failed: Data is not in the correct format. Import rolled back.";
        }
    }

    /**
     * Returns the current in-memory dataset.
     */
    public List<ParsedRecordWrapper> getImportedData() {
        return this.masterDatabase;
    }

    // ===================================================================================
    // INTERNAL ARCHITECTURE COMPONENTS (CSVMerger & DuplicateResolver)
    // ===================================================================================

    /**
     * Responsible for parsing raw CSV strings into normalized Domain Entities.
     */
    private class CSVMerger {
        public ParsedRecordWrapper mapRowToDomain(String[] row) throws IllegalArgumentException {
            try {
                // Parse Topic (e.g., "COMP1701 Game Design")
                String[] topicParts = row[0].split(" ", 2);
                Topic topic = new Topic(topicParts[0].trim(), topicParts.length > 1 ? topicParts[1].trim() : "");

                // Parse Availability (e.g., "In person - Flinders City Campus - S2 - 1")
                String[] availParts = row[1].split(" - ");
                ClassAvailability availability = new ClassAvailability(
                        availParts[0].trim(),
                        availParts[1].trim(),
                        availParts[2].trim(),
                        Integer.parseInt(availParts[3].trim())
                );

                // Parse CourseClass (e.g., "Workshop-1")
                CourseClass courseClass = new CourseClass(row[2].trim(), topic.getTopicCode());

                // Parse Dates, Times, and Locations
                String[] dateParts = row[4].split(" - ");
                String[] timeParts = row[6].split(" - ");
                String[] locParts = row[7].split(", ");

                ClassInstance instance = new ClassInstance(
                        Integer.parseInt(row[3].trim()), // Class instance number
                        courseClass.getClassCode(),
                        dateParts[0].trim(),             // Start Date
                        dateParts.length > 1 ? dateParts[1].trim() : dateParts[0].trim(), // End Date
                        timeParts[0].trim(),             // Start Time
                        timeParts.length > 1 ? timeParts[1].trim() : timeParts[0].trim(), // End Time
                        row[5].trim(),                   // Day
                        locParts[0].trim(),              // Building
                        locParts.length > 1 ? locParts[1].trim() : "" // Room
                );

                return new ParsedRecordWrapper(topic, availability, courseClass, instance);

            } catch (Exception e) {
                throw new IllegalArgumentException("Malformed row data detected.");
            }
        }
    }

    /**
     * Responsible for enforcing the duplicate updating rules.
     */
    private class DuplicateResolver {
        public boolean resolveAndMerge(List<ParsedRecordWrapper> database, ParsedRecordWrapper newRecord) {
            for (ParsedRecordWrapper existing : database) {
                if (isMatchingRecord(existing, newRecord)) {
                    // Update Time and Location instead of duplicating
                    existing.getInstance().setStartTime(newRecord.getInstance().getStartTime());
                    existing.getInstance().setEndTime(newRecord.getInstance().getEndTime());
                    existing.getInstance().setBuilding(newRecord.getInstance().getBuilding());
                    existing.getInstance().setRoom(newRecord.getInstance().getRoom());
                    return true;
                }
            }
            return false;
        }

        private boolean isMatchingRecord(ParsedRecordWrapper a, ParsedRecordWrapper b) {
            return a.getTopic().getTopicCode().equals(b.getTopic().getTopicCode()) &&
                    a.getTopic().getTopicName().equals(b.getTopic().getTopicName()) &&
                    a.getAvailability().getAttendanceMode().equals(b.getAvailability().getAttendanceMode()) &&
                    a.getAvailability().getCampus().equals(b.getAvailability().getCampus()) &&
                    a.getAvailability().getSemester().equals(b.getAvailability().getSemester()) &&
                    a.getAvailability().getAvailabilityNo() == b.getAvailability().getAvailabilityNo() &&
                    a.getCourseClass().getClassFormat().equals(b.getCourseClass().getClassFormat()) &&
                    a.getInstance().getClassInstanceNo() == b.getInstance().getClassInstanceNo() &&
                    a.getInstance().getStartDate().equals(b.getInstance().getStartDate()) &&
                    a.getInstance().getEndDate().equals(b.getInstance().getEndDate()) &&
                    a.getInstance().getDay().equals(b.getInstance().getDay());
        }
    }

    // ===================================================================================
    // DATA TRANSFER OBJECT
    // ===================================================================================

    /**
     * A Data Transfer Object (DTO) used to keep the normalized domain objects
     * relationally bound together while in memory.
     */
    public static class ParsedRecordWrapper {
        private Topic topic;
        private ClassAvailability availability;
        private CourseClass courseClass;
        private ClassInstance instance;

        public ParsedRecordWrapper(Topic topic, ClassAvailability availability, CourseClass courseClass, ClassInstance instance) {
            this.topic = topic;
            this.availability = availability;
            this.courseClass = courseClass;
            this.instance = instance;
        }

        public Topic getTopic() { return topic; }
        public ClassAvailability getAvailability() { return availability; }
        public CourseClass getCourseClass() { return courseClass; }
        public ClassInstance getInstance() { return instance; }
    }
}