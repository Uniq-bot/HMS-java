package HotelMgmt.constants;

/**
 * Enum representing user status in the system.
 */
public enum UserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    BLOCKED("Blocked");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserStatus fromString(String status) {
        for (UserStatus us : UserStatus.values()) {
            if (us.name().equalsIgnoreCase(status) || us.displayName.equalsIgnoreCase(status)) {
                return us;
            }
        }
        return ACTIVE;
    }
}
