package service;

import domain.ClassInstance;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ImportService {

    /**
     * Temporary representation of a CSV row before mapping to the strict Domain Model.
     */
    public static class RawCSVRecord {
        public String topicCode;
        public int availabilityNo;
        public String classFormat;
        public int classInstanceNo;
        public LocalDate startDate;
        public String day;
        public LocalTime startTime;
        public LocalTime endTime;
        public String location;
    }

    /**
     * Resolves duplicates and merges CSV data into the existing dataset.
     * Updates Time and Location if exact identifiers exist, otherwise adds a new record.
     */
    public void processImport(List<RawCSVRecord> incomingRecords, List<RawCSVRecord> existingDatabase) {
        int newRecords = 0;
        int updatedRecords = 0;

        for (RawCSVRecord incoming : incomingRecords) {
            Optional<RawCSVRecord> duplicateMatch = existingDatabase.stream()
                    .filter(existing -> existing.topicCode.equalsIgnoreCase(incoming.topicCode) &&
                            existing.availabilityNo == incoming.availabilityNo &&
                            existing.classFormat.equalsIgnoreCase(incoming.classFormat) &&
                            existing.classInstanceNo == incoming.classInstanceNo &&
                            existing.startDate.isEqual(incoming.startDate) &&
                            existing.day.equalsIgnoreCase(incoming.day))
                    .findFirst();

            if (duplicateMatch.isPresent()) {
                // DuplicateResolver: Update Time and Location instead of creating a duplicate
                RawCSVRecord existing = duplicateMatch.get();
                existing.startTime = incoming.startTime;
                existing.endTime = incoming.endTime;
                existing.location = incoming.location;
                updatedRecords++;
            } else {
                // CSVMerger: Add to database
                existingDatabase.add(incoming);
                newRecords++;
            }
        }

        System.out.printf("Import complete. New records: %d | Updated records: %d%n", newRecords, updatedRecords);
    }
}