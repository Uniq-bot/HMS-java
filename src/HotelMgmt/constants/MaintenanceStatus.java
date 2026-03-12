package HotelMgmt.constants;

/**
 * Enum representing maintenance status.
 */
public enum MaintenanceStatus {
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    MaintenanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MaintenanceStatus fromString(String status) {
        for (MaintenanceStatus ms : MaintenanceStatus.values()) {
            if (ms.name().equalsIgnoreCase(status) || ms.displayName.equalsIgnoreCase(status)) {
                return ms;
            }
        }
        return SCHEDULED;
    }
}
