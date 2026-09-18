package es.uniovi.raul.solutions50.schedule;

import java.time.LocalTime;
import java.util.*;

public final class Schedule {
    private final List<ScheduleEntry> entries;

    public Schedule() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(ScheduleEntry entry) {
        entries.add(entry);
    }

    public Optional<ScheduleEntry> findMatchingEntry(String day, LocalTime time) {
        return entries.stream()
                .filter(entry -> entry.includes(day, time))
                .findFirst();

    }

    public int getEntryCount() {
        return entries.size();
    }

    public List<ScheduleEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

}
