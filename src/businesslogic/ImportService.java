package businesslogic;

import domain.Schedule;

import java.util.List;
import java.util.Optional;

/** Layer 3 component: ImportService with CSVMerger and DuplicateResolver responsibilities. */
public class ImportService {
    public static class ImportResult {
        private final int newRecords;
        private final int updatedRecords;

        public ImportResult(int newRecords, int updatedRecords) {
            this.newRecords = newRecords;
            this.updatedRecords = updatedRecords;
        }

        public int getNewRecords() { return newRecords; }
        public int getUpdatedRecords() { return updatedRecords; }
    }

    public ImportResult mergeImportedSchedules(List<Schedule> incomingRecords, List<Schedule> existingDatabase) {
        int newRecords = 0;
        int updatedRecords = 0;
        int nextId = nextRecordId(existingDatabase);

        for (Schedule incoming : incomingRecords) {
            Optional<Schedule> duplicate = existingDatabase.stream()
                    .filter(existing -> existing.duplicateKey().equals(incoming.duplicateKey()))
                    .findFirst();

            if (duplicate.isPresent()) {
                updateTimeAndLocation(duplicate.get(), incoming);
                updatedRecords++;
            } else {
                incoming.setRecordId(nextId++);
                existingDatabase.add(incoming);
                newRecords++;
            }
        }
        return new ImportResult(newRecords, updatedRecords);
    }

    private void updateTimeAndLocation(Schedule existing, Schedule incoming) {
        existing.getClassInstance().setStartTime(incoming.getClassInstance().getStartTime());
        existing.getClassInstance().setEndTime(incoming.getClassInstance().getEndTime());
        existing.getClassInstance().setBuilding(incoming.getClassInstance().getBuilding());
        existing.getClassInstance().setRoom(incoming.getClassInstance().getRoom());
    }

    private int nextRecordId(List<Schedule> schedules) {
        int max = 0;
        for (Schedule schedule : schedules) {
            if (schedule.getRecordId() > max) max = schedule.getRecordId();
        }
        return max + 1;
    }
}
