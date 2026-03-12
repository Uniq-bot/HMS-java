package HotelMgmt.model;

import HotelMgmt.constants.IssueStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Issue class representing customer complaints or issues about bookings.
 * Issues can be raised by customers and handled by managers.
 */
public class Issue {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private String id;
    private String bookingId;
    private String customerId;
    private String customerName;
    private String hallName;
    private String description;
    private IssueStatus status;
    private String assignedSchedulerId;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public Issue(String id, String bookingId, String customerId, String customerName, 
                 String hallName, String description) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.hallName = hallName;
        this.description = description;
        this.status = IssueStatus.OPEN;
        this.assignedSchedulerId = "";
        this.response = "";
        this.createdAt = LocalDateTime.now();
        this.resolvedAt = null;
    }

    public Issue(String id, String bookingId, String customerId, String customerName, 
                 String hallName, String description, IssueStatus status, 
                 String assignedSchedulerId, String response, 
                 LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.hallName = hallName;
        this.description = description;
        this.status = status;
        this.assignedSchedulerId = assignedSchedulerId;
        this.response = response;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getHallName() { return hallName; }
    public String getDescription() { return description; }
    public IssueStatus getStatus() { return status; }
    public String getAssignedSchedulerId() { return assignedSchedulerId; }
    public String getResponse() { return response; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setHallName(String hallName) { this.hallName = hallName; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(IssueStatus status) { this.status = status; }
    public void setAssignedSchedulerId(String assignedSchedulerId) { this.assignedSchedulerId = assignedSchedulerId; }
    public void setResponse(String response) { this.response = response; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    /**
     * Mark issue as resolved
     */
    public void resolve(String response) {
        this.response = response;
        this.status = IssueStatus.DONE;
        this.resolvedAt = LocalDateTime.now();
    }

    /**
     * Convert issue to file storage format
     */
    public String toFileString() {
        return String.join(",",
            id,
            bookingId,
            customerId,
            customerName,
            hallName,
            description.replace(",", ";"),
            status.name(),
            assignedSchedulerId,
            response.replace(",", ";"),
            createdAt.format(FORMATTER),
            resolvedAt != null ? resolvedAt.format(FORMATTER) : ""
        );
    }

    /**
     * Create Issue from file line
     */
    public static Issue fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 10) {
            LocalDateTime resolved = null;
            if (parts.length >= 11 && !parts[10].trim().isEmpty()) {
                resolved = LocalDateTime.parse(parts[10].trim(), FORMATTER);
            }
            return new Issue(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                parts[5].trim().replace(";", ","),
                IssueStatus.fromString(parts[6].trim()),
                parts[7].trim(),
                parts[8].trim().replace(";", ","),
                LocalDateTime.parse(parts[9].trim(), FORMATTER),
                resolved
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "Issue{id='" + id + "', booking='" + bookingId + "', status=" + status + "}";
    }
}
