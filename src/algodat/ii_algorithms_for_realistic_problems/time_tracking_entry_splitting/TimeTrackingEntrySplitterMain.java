package algodat.ii_algorithms_for_realistic_problems.time_tracking_entry_splitting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeTrackingEntrySplitterMain {
    public static void main(String[] args) {
        var splitter = new TimeTrackingEntrySplitter();

        var date = LocalDate.of(2024, 4, 1);
        var entries = new ArrayList<>(List.of(
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(9, 0, 0)),
                        LocalDateTime.of(date, LocalTime.of(10, 0, 0)),
                        "9:00 bis 10:00"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(10, 0, 0)),
                        LocalDateTime.of(date, LocalTime.of(11, 0, 0)),
                        "10:00 bis 11:00"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(10, 0, 0)),
                        LocalDateTime.of(date, LocalTime.of(10, 30, 0)),
                        "10:00 bis 10:30"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(10, 30, 0)),
                        LocalDateTime.of(date, LocalTime.of(11, 0, 0)),
                        "10:30 bis 11:00"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
                        LocalDateTime.of(date, LocalTime.of(14, 0, 0)),
                        "12:00 bis 14:00"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(12, 45, 0)),
                        LocalDateTime.of(date, LocalTime.of(13, 15, 0)),
                        "12:45 bis 13:15"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(14, 00, 0)),
                        LocalDateTime.of(date, LocalTime.of(15, 30, 0)),
                        "14:00 bis 15:30"
                ),
                new TimeTrackingEntry(
                        LocalDateTime.of(date, LocalTime.of(15, 00, 0)),
                        LocalDateTime.of(date, LocalTime.of(16, 00, 0)),
                        "15:00 bis 16:00"
                )
        ));

        var resolvedEntries = splitter.splitEntries(entries);
        resolvedEntries.sort(Comparator.comparing(TimeTrackingEntry::getStartDateTime));
    }
}

