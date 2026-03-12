package HotelMgmt.constants;

/**
 * Enum representing all possible issue statuses.
 */
public enum IssueStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    CLOSED("Closed"),
    CANCELLED("Cancelled");

    private final String displayName;

    IssueStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static IssueStatus fromString(String status) {
        for (IssueStatus is : IssueStatus.values()) {
            if (is.name().equalsIgnoreCase(status) || is.displayName.equalsIgnoreCase(status)) {
                return is;
            }
        }
        return OPEN;
    }
}
