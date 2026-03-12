package HotelMgmt.constants;

/**
 * Enum representing all possible booking statuses in the system.
 */
public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromString(String status) {
        for (BookingStatus bs : BookingStatus.values()) {
            if (bs.name().equalsIgnoreCase(status) || bs.displayName.equalsIgnoreCase(status)) {
                return bs;
            }
        }
        return PENDING;
    }
}
