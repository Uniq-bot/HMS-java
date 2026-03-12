package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Scheduler class representing staff members who manage hall scheduling.
 * Schedulers can add/edit/delete halls and schedule maintenance.
 */
public class Scheduler extends Staff {

    public Scheduler(String id, String name, String email, String password, UserStatus status) {
        super(id, name, email, password, UserRole.SCHEDULER, status, "Operations");
    }

    public Scheduler(String id, String name, String email, String password, 
                     UserStatus status, String phone) {
        super(id, name, email, password, UserRole.SCHEDULER, status, phone, "Operations");
    }

    /**
     * Convert scheduler to file storage format
     * Format: ID|Name|Email|Password|Role|Status|Phone|Department
     */
    @Override
    public String toFileString() {
        return String.join("|", id, name, email, password, role.name(), status.name(), phone, department);
    }

    /**
     * Create Scheduler from file line
     */
    public static Scheduler fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 6) {
            Scheduler scheduler = new Scheduler(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                UserStatus.fromString(parts[5].trim())
            );
            if (parts.length >= 7) scheduler.setPhone(parts[6].trim());
            return scheduler;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Scheduler{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
