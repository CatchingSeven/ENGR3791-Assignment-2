package persistence;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PerseveranceAdapter {
    private CSVReader csvReader;
    private CSVWriter csvWriter;
    private SettingsStore settingsStore;
    private TimetableRepository timetableRepository;

    public PerseveranceAdapter() {
        this.csvReader = new CSVReader();
        this.csvWriter = new CSVWriter();
        this.settingsStore = new SettingsStore();
        this.timetableRepository = new TimetableRepository();
    }

    // Facade methods to interact with the underlying file operations
    public List<String[]> importCSV(String filePath) throws IOException {
        return csvReader.read(filePath);
    }

    public void exportCSV(String filePath, List<String[]> data, String header) throws IOException {
        csvWriter.write(filePath, data, header);
    }

    public void saveSettings(String settingsData) throws IOException {
        settingsStore.save(settingsData);
    }

    public String loadSettings() throws IOException {
        return settingsStore.load();
    }
}

class CSVReader {
    public List<String[]> read(String filePath) throws IOException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                records.add(line.split(","));
            }
        }
        return records;
    }
}

class CSVWriter {
    public void write(String filePath, List<String[]> data, String header) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
            bw.write(header);
            bw.newLine();
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }
}

class SettingsStore {
    private static final String SETTINGS_FILE = "last_used_settings.txt";

    public void save(String settingsData) throws IOException {
        Files.writeString(Paths.get(SETTINGS_FILE), settingsData);
    }

    public String load() throws IOException {
        Path path = Paths.get(SETTINGS_FILE);
        if (Files.exists(path)) {
            return Files.readString(path);
        }
        return "";
    }
}

class TimetableRepository {
    // Logic specific to saving and retrieving complex Timetable entity states
}