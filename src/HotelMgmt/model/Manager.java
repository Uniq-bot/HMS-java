package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Manager class representing managers in the system.
 * Managers can view sales reports and handle customer issues.
 */
public class Manager extends Staff {

    public Manager(String id, String name, String email, String password, UserStatus status) {
        super(id, name, email, password, UserRole.MANAGER, status, "Management");
    }

    public Manager(String id, String name, String email, String password, 
                   UserStatus status, String phone) {
        super(id, name, email, password, UserRole.MANAGER, status, phone, "Management");
    }

    /**
     * Convert manager to file storage format
     * Format: ID|Name|Email|Password|Role|Status|Phone|Department
     */
    @Override
    public String toFileString() {
        return String.join("|", id, name, email, password, role.name(), status.name(), phone, department);
    }

    /**
     * Create Manager from file line
     */
    public static Manager fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 6) {
            Manager manager = new Manager(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                UserStatus.fromString(parts[5].trim())
            );
            if (parts.length >= 7) manager.setPhone(parts[6].trim());
            return manager;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Manager{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
