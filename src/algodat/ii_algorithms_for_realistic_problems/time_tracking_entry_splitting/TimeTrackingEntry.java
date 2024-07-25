package algodat.ii_algorithms_for_realistic_problems.time_tracking_entry_splitting;

import java.time.LocalDateTime;

public class TimeTrackingEntry {
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
