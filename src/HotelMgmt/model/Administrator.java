package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Administrator class representing system administrators.
 * Administrators can manage users, schedulers, and view all bookings.
 */
public class Administrator extends Staff {

    public Administrator(String id, String name, String email, String password, UserStatus status) {
        super(id, name, email, password, UserRole.ADMIN, status, "Administration");
    }

    public Administrator(String id, String name, String email, String password, 
                         UserStatus status, String phone) {
        super(id, name, email, password, UserRole.ADMIN, status, phone, "Administration");
    }

    /**
     * Convert administrator to file storage format
     * Format: ID|Name|Email|Password|Role|Status|Phone|Department
     */
    @Override
    public String toFileString() {
        return String.join("|", id, name, email, password, role.name(), status.name(), phone, department);
    }

    /**
     * Create Administrator from file line
     */
    public static Administrator fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 6) {
            Administrator admin = new Administrator(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                UserStatus.fromString(parts[5].trim())
            );
            if (parts.length >= 7) admin.setPhone(parts[6].trim());
            return admin;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Administrator{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
