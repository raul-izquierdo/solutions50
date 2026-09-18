package es.uniovi.raul.solutions50.schedule;

import static java.lang.Integer.*;
import static java.lang.String.*;
import static java.time.format.DateTimeFormatter.*;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.*;

/**
 * Utility class to load schedules from a CSV file.
 */
public final class ScheduleParser {

    /**
     * Loads schedules from a CSV file. The expected format is:
     * group, weekday, start_time, duration
     *
     * @param scheduleFile the path to the CSV file
     * @return a mapping from group names to their schedules
     */
    public static Schedule load(Path scheduleFile) throws IOException, InvalidScheduleFormat {

        try (var reader = java.nio.file.Files.newBufferedReader(scheduleFile)) {
            return load(reader);
        }
    }

    /**
     * Loads schedules from a CSV source. The expected format is:
     * group, weekday, start_time, duration
     *
     * @param reader the source of the CSV content
     * @return a mapping from group names to their schedules
     */
    public static Schedule load(Reader reader) throws IOException, InvalidScheduleFormat {

        var schedule = new Schedule();

        try (var csvParser = new CSVParser(reader, CSVFormat.Builder.create()
                .setIgnoreSurroundingSpaces(true)
                .setTrim(true)
                .setSkipHeaderRecord(false)
                .build())) {

            for (CSVRecord csvRecord : csvParser) {

                var group = getValue(csvRecord, 0);
                var weekday = getValue(csvRecord, 1);
                var startTime = parseTime(getValue(csvRecord, 2));
                var minutes = toMinutes(csvRecord, 3);

                schedule.addEntry(new ScheduleEntry(group, weekday, startTime, minutes));
            }

            return schedule;
        }
    }

    private static int toMinutes(CSVRecord csvRecord, int column) throws InvalidScheduleFormat {

        var value = findValue(csvRecord, column);
        if (value.isEmpty())
            return 60 * 2; // 2 hours

        // The value can be "2" (hours), "2h" (hours) or "2m" (minutes)
        var matcher = Pattern.compile("^(\\d+)([hm])?$").matcher(value.get());
        if (!matcher.matches())
            throw newFormatException(csvRecord, column, "<integer> or <integer>h or <integer>m>");

        int minutes = parseInt(matcher.group(1));
        if ("m".equals(matcher.group(2)))
            return minutes;

        return minutes * 60;
    }

    private static String getValue(CSVRecord csvRecord, int columnNumber) throws InvalidScheduleFormat {

        return findValue(csvRecord, columnNumber)
                .orElseThrow(() -> newFormatException(csvRecord, columnNumber, "cannot be blank"));
    }

    private static Optional<String> findValue(CSVRecord csvRecord, int columnNumber) {

        try {
            String value = csvRecord.get(columnNumber);

            if (value == null || value.isBlank())
                return Optional.empty();

            return Optional.of(value);

        } catch (ArrayIndexOutOfBoundsException e) { // column not found
            return Optional.empty();
        }
    }

    private static InvalidScheduleFormat newFormatException(CSVRecord csvRecord, int column, String message) {

        return new InvalidScheduleFormat(
                format("Record #%d: '%s' -> column '%d' (zero based) has an invalid format: %s",
                        csvRecord.getRecordNumber(),
                        join(", ", csvRecord),
                        column,
                        message));
    }

    private static LocalTime parseTime(String input) throws InvalidScheduleFormat {
        try {
            return LocalTime.parse(input.trim(), ofPattern("[H:mm][HH:mm][H]"));
        } catch (DateTimeParseException e) {
            throw new InvalidScheduleFormat("Invalid time format: '" + input + "'. Expected `hh:mm`, `h:mm` or `h`");
        }
    }

    /**
     * Exception thrown when the schedule file has an invalid format.
     */
    public static class InvalidScheduleFormat extends Exception {
        public InvalidScheduleFormat(String message) {
            super(message);
        }
    }
}
