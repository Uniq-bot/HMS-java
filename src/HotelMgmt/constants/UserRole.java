package HotelMgmt.constants;

/**
 * Enum representing user roles in the system.
 */
public enum UserRole {
    CUSTOMER("Customer"),
    SCHEDULER("Scheduler"),
    ADMIN("Administrator"),
    MANAGER("Manager");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String role) {
        for (UserRole ur : UserRole.values()) {
            if (ur.name().equalsIgnoreCase(role) || ur.displayName.equalsIgnoreCase(role)) {
                return ur;
            }
        }
        return CUSTOMER;
    }
}
