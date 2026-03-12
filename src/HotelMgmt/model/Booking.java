package HotelMgmt.model;

import HotelMgmt.constants.BookingStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Booking class representing a hall reservation.
 * Contains all booking details including customer, hall, timing, and payment information.
 */
public class Booking {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private String id;
    private String customerId;
    private String customerName;
    private String hallId;
    private String hallName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double totalAmount;
    private BookingStatus status;
    private String remarks;
    private LocalDateTime createdAt;

    public Booking(String id, String customerId, String customerName, String hallId, 
                   String hallName, LocalDateTime startDateTime, LocalDateTime endDateTime, 
                   double totalAmount, BookingStatus status, String remarks) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.hallId = hallId;
        this.hallName = hallName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.totalAmount = totalAmount;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public double getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setHallId(String hallId) { this.hallId = hallId; }
    public void setHallName(String hallName) { this.hallName = hallName; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Calculate booking duration in hours
     */
    public long getDurationHours() {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * Check if booking can be cancelled (at least 3 days before event)
     */
    public boolean canCancel() {
        if (status == BookingStatus.CANCELLED || status == BookingStatus.COMPLETED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        long daysUntilEvent = ChronoUnit.DAYS.between(now, startDateTime);
        return daysUntilEvent >= 3;
    }

    /**
     * Check if this booking is upcoming
     */
    public boolean isUpcoming() {
        return startDateTime.isAfter(LocalDateTime.now()) && 
               (status == BookingStatus.CONFIRMED || status == BookingStatus.PENDING);
    }

    /**
     * Check if this booking is past
     */
    public boolean isPast() {
        return endDateTime.isBefore(LocalDateTime.now()) || status == BookingStatus.COMPLETED;
    }

    /**
     * Convert booking to file storage format
     */
    public String toFileString() {
        return String.join(",", 
            id, 
            customerId, 
            customerName, 
            hallId, 
            hallName,
            startDateTime.format(FORMATTER), 
            endDateTime.format(FORMATTER), 
            String.valueOf(totalAmount), 
            status.name(), 
            remarks.replace(",", ";"),
            createdAt.format(FORMATTER)
        );
    }

    /**
     * Create Booking from file line
     */
    public static Booking fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 10) {
            Booking booking = new Booking(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                LocalDateTime.parse(parts[5].trim(), FORMATTER),
                LocalDateTime.parse(parts[6].trim(), FORMATTER),
                Double.parseDouble(parts[7].trim()),
                BookingStatus.fromString(parts[8].trim()),
                parts[9].trim().replace(";", ",")
            );
            if (parts.length >= 11) {
                booking.setCreatedAt(LocalDateTime.parse(parts[10].trim(), FORMATTER));
            }
            return booking;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Booking{id='" + id + "', hall='" + hallName + "', customer='" + customerName + 
               "', from=" + startDateTime.format(FORMATTER) + ", to=" + endDateTime.format(FORMATTER) + 
               ", status=" + status + "}";
    }
}
