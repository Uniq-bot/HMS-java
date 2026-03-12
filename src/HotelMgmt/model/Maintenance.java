package HotelMgmt.model;

import HotelMgmt.constants.MaintenanceStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Maintenance class representing scheduled maintenance for halls.
 * Maintenance periods block hall availability during the scheduled time.
 */
public class Maintenance {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private String id;
    private String hallId;
    private String hallName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String remarks;
    private MaintenanceStatus status;
    private String schedulerId;
    private LocalDateTime createdAt;

    public Maintenance(String id, String hallId, String hallName, 
                       LocalDateTime startDateTime, LocalDateTime endDateTime, 
                       String remarks, String schedulerId) {
        this.id = id;
        this.hallId = hallId;
        this.hallName = hallName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.remarks = remarks;
        this.status = MaintenanceStatus.SCHEDULED;
        this.schedulerId = schedulerId;
        this.createdAt = LocalDateTime.now();
    }

    public Maintenance(String id, String hallId, String hallName,
                       LocalDateTime startDateTime, LocalDateTime endDateTime,
                       String remarks, MaintenanceStatus status, 
                       String schedulerId, LocalDateTime createdAt) {
        this.id = id;
        this.hallId = hallId;
        this.hallName = hallName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.remarks = remarks;
        this.status = status;
        this.schedulerId = schedulerId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getRemarks() { return remarks; }
    public MaintenanceStatus getStatus() { return status; }
    public String getSchedulerId() { return schedulerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setHallId(String hallId) { this.hallId = hallId; }
    public void setHallName(String hallName) { this.hallName = hallName; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setStatus(MaintenanceStatus status) { this.status = status; }
    public void setSchedulerId(String schedulerId) { this.schedulerId = schedulerId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Check if maintenance overlaps with given time period
     */
    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return !endDateTime.isBefore(start) && !startDateTime.isAfter(end);
    }

    /**
     * Convert maintenance to file storage format
     */
    public String toFileString() {
        return String.join(",",
            id,
            hallId,
            hallName,
            startDateTime.format(FORMATTER),
            endDateTime.format(FORMATTER),
            remarks.replace(",", ";"),
            status.name(),
            schedulerId,
            createdAt.format(FORMATTER)
        );
    }

    /**
     * Create Maintenance from file line
     */
    public static Maintenance fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 9) {
            return new Maintenance(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                LocalDateTime.parse(parts[3].trim(), FORMATTER),
                LocalDateTime.parse(parts[4].trim(), FORMATTER),
                parts[5].trim().replace(";", ","),
                MaintenanceStatus.fromString(parts[6].trim()),
                parts[7].trim(),
                LocalDateTime.parse(parts[8].trim(), FORMATTER)
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "Maintenance{id='" + id + "', hall='" + hallName + 
               "', from=" + startDateTime.format(FORMATTER) + 
               ", to=" + endDateTime.format(FORMATTER) + ", status=" + status + "}";
    }
}
