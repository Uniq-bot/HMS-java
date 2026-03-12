package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Abstract Staff class representing employees in the system.
 * Extends User base class with staff-specific functionality.
 */
public abstract class Staff extends User {
    
    protected String department;

    public Staff(String id, String name, String email, String password, 
                 UserRole role, UserStatus status, String department) {
        super(id, name, email, password, role, status);
        this.department = department;
    }

    public Staff(String id, String name, String email, String password, 
                 UserRole role, UserStatus status, String phone, String department) {
        super(id, name, email, password, role, status, phone);
        this.department = department;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Staff{id='" + id + "', name='" + name + "', role=" + role + 
               ", department='" + department + "'}";
    }
}
