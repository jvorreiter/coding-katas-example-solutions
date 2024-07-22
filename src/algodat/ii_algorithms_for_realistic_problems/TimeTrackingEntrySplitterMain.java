package algodat.ii_algorithms_for_realistic_problems;

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

class TimeTrackingEntry {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String message;

    public TimeTrackingEntry(LocalDateTime startDateTime, LocalDateTime endDateTime, String message) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.message = message;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getMessage() {
        return message;
    }
}

class TimeTrackingEntrySplitter {
    public List<TimeTrackingEntry> splitEntries(ArrayList<TimeTrackingEntry> entries) {
        // Üverlappungen paarweise auflösen, bis keine mehr existieren
        outer:
        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                if (splitEntries(entries, i, j)) {
                    i--;
                    continue outer;
                }
            }
        }

        return entries;
    }

    private boolean splitEntries(ArrayList<TimeTrackingEntry> entries, int index1, int index2) {
        var entry1 = entries.get(index1);
        var entry2 = entries.get(index2);

        if (!entriesOverlap(entry1, entry2)) {
            return false;
        }

        // Elemente aus der Liste löschen, da für diese neue Elemente eingefügt werden
        entries.remove(Math.max(index1, index2));
        entries.remove(Math.min(index1, index2));

        var entryWithLaterStart = entry1.getStartDateTime().isAfter(entry2.getStartDateTime())
                ? entry1
                : entry2;

        var entryWithEarlierEnd = entry1.getEndDateTime().isBefore(entry2.getEndDateTime())
                ? entry1
                : entry2;

        // Überlappung geht vom späteren Start bis zum früheren Ende der beiden Termine
        var overlapStart = entryWithLaterStart.getStartDateTime();
        var overlapEnd = entryWithEarlierEnd.getEndDateTime();

        // Überlappungsbereich
        resolveEntryOverlaps(entry1, entry2, overlapStart, overlapEnd, entries);

        return true;
    }

    private void resolveEntryOverlaps(TimeTrackingEntry entry1, TimeTrackingEntry entry2, LocalDateTime overlapStart, LocalDateTime overlapEnd, ArrayList<TimeTrackingEntry> entries) {
        // beide Einträge starten und enden gleichzeitig: Entry1 wird übernommen, Entry2 verliert
        if (entry1.getStartDateTime().equals(entry2.getStartDateTime()) && entry1.getEndDateTime().equals(entry2.getEndDateTime())) {
            entries.add(entry1);
            return;
        }

        // beide Einträge starten gleichzeitig: Eintrag mit früherem Ende ist der vordere Eintrag, der andere der hintere Eintrag
        if (entry1.getStartDateTime().equals(entry2.getStartDateTime())) {
            resolveOverlapWithSameStart(entry1, entry2, overlapStart, overlapEnd, entries);
            return;
        }

        // beide Einträge enden gleichzeitig: Eintrag mit früherem Start ist der vordere Eintrag, der andere der hintere Eintrag
        if (entry1.getEndDateTime().equals(entry2.getEndDateTime())) {
            resolveOverlapWithSameEnd(entry1, entry2, overlapStart, overlapEnd, entries);
            return;
        }

        // Entry1 startet vor und endet nach Entry2
        // dann werden 2 neue Einträge hinzugefügt:
        // je 1 Eintrag vor und nach Entry2 mit dem Inhalt von Entry1
        // Entry2 wird unverändert wieder hinzugefügt
        if (entry1.getStartDateTime().isBefore(entry2.getStartDateTime()) && entry1.getEndDateTime().isAfter(entry2.getEndDateTime())) {
            resolveOverlapWithOuterAndInnerEntry(entry1, entry2, overlapStart, overlapEnd, entries);
            return;
        }
        // Entry2 startet vor und endet nach Entry1
        // Code wiederverwenden, daher erneuter Aufruf der Methode mit vertauschten Entries
        if (entry2.getStartDateTime().isBefore(entry1.getStartDateTime()) && entry2.getEndDateTime().isAfter(entry1.getEndDateTime())) {
            resolveOverlapWithOuterAndInnerEntry(entry2, entry1, overlapStart, overlapEnd, entries);
            return;
        }

        // Entry1 startet vor Entry2 und endet bevor Entry2 endet, allerdings gibt es einen Overlap in der Mitte:
        // wird hier aufgelöst, indem ein neuer Eintrag mit der Nachricht beider Einträge, und Entry1 und Entry2 für den Bereich ohne Überschneidung eingefügt werden
        if (entry1.getStartDateTime().isBefore(entry2.getStartDateTime()) && entry1.getEndDateTime().isBefore(entry2.getEndDateTime())) {
            resolvePartialOverlap(entry1, entry2, overlapStart, overlapEnd, entries);
            return;
        }
        // Entry1 startet vor Entry2 und endet bevor Entry2 endet, allerdings gibt es einen Overlap in der Mitte:
        // Code wiederverwenden, daher erneuter Aufruf der Methode mit vertauschten Entries
        if (entry2.getStartDateTime().isBefore(entry1.getStartDateTime()) && entry2.getEndDateTime().isBefore(entry1.getEndDateTime())) {
            resolvePartialOverlap(entry2, entry1, overlapStart, overlapEnd, entries);
            return;
        }

        // übrig bleibt nur ein ungültiger unbehandelter Fall, daher Exception werfen
        throw new IllegalStateException();
    }

    private static void resolvePartialOverlap(TimeTrackingEntry entry1, TimeTrackingEntry entry2, LocalDateTime overlapStart, LocalDateTime overlapEnd, ArrayList<TimeTrackingEntry> entries) {
        var newEarlyEntry = new TimeTrackingEntry(
                entry1.getStartDateTime(),
                overlapStart,
                entry1.getMessage()
        );
        entries.add(newEarlyEntry);

        var newOverlapEntry = new TimeTrackingEntry(
                overlapStart,
                overlapEnd,
                "Aufgelöst: " + entry1.getMessage() + "+" + entry2.getMessage()
        );
        entries.add(newOverlapEntry);

        var newLateEntry = new TimeTrackingEntry(
                overlapEnd,
                entry2.getEndDateTime(),
                entry2.getMessage()
        );
        entries.add(newLateEntry);
    }

    private static void resolveOverlapWithOuterAndInnerEntry(TimeTrackingEntry outerEntry, TimeTrackingEntry innerEntry, LocalDateTime overlapStart, LocalDateTime overlapEnd, ArrayList<TimeTrackingEntry> entries) {
        var newEarlyEntry = new TimeTrackingEntry(
                outerEntry.getStartDateTime(),
                overlapStart,
                outerEntry.getMessage()
        );
        entries.add(newEarlyEntry);

        // mittleren Eintrag unverändert wieder einfügen
        entries.add(innerEntry);

        var newLateEntry = new TimeTrackingEntry(
                overlapEnd,
                outerEntry.getEndDateTime(),
                outerEntry.getMessage()
        );
        entries.add(newLateEntry);
    }

    private static void resolveOverlapWithSameEnd(TimeTrackingEntry entry1, TimeTrackingEntry entry2, LocalDateTime overlapStart, LocalDateTime overlapEnd, ArrayList<TimeTrackingEntry> entries) {
        var entryWithEarlierStart = entry1.getStartDateTime().isBefore(entry2.getStartDateTime()) ? entry1 : entry2;
        var entryWithLaterStart = entryWithEarlierStart == entry1 ? entry2 : entry1;

        var newEarlyEntry = new TimeTrackingEntry(
                entryWithEarlierStart.getStartDateTime(),
                overlapStart,
                entryWithEarlierStart.getMessage()
        );
        entries.add(newEarlyEntry);

        var newLateEntry = new TimeTrackingEntry(
                overlapStart,
                overlapEnd,
                entryWithLaterStart.getMessage()
        );
        entries.add(newLateEntry);
    }

    private static void resolveOverlapWithSameStart(TimeTrackingEntry entry1, TimeTrackingEntry entry2, LocalDateTime overlapStart, LocalDateTime overlapEnd, ArrayList<TimeTrackingEntry> entries) {
        var entryWithEarlierEnd = entry1.getEndDateTime().isBefore(entry2.getEndDateTime()) ? entry1 : entry2;
        var entryWithLaterEnd = entryWithEarlierEnd == entry1 ? entry2 : entry1;

        var newEarlyEntry = new TimeTrackingEntry(
                overlapStart,
                overlapEnd,
                entryWithEarlierEnd.getMessage()
        );
        entries.add(newEarlyEntry);

        var newLateEntry = new TimeTrackingEntry(
                overlapEnd,
                entryWithLaterEnd.getEndDateTime(),
                entryWithLaterEnd.getMessage()
        );
        entries.add(newLateEntry);
    }

    private static boolean entriesOverlap(TimeTrackingEntry entry1, TimeTrackingEntry entry2) {
        return entry1.getEndDateTime().isAfter(entry2.getStartDateTime())
                && entry1.getStartDateTime().isBefore(entry2.getEndDateTime());
    }
}