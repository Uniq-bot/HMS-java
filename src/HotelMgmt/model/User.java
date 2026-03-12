package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Abstract base class for all users in the Hall Booking Management System.
 * Implements encapsulation with protected fields and public getters/setters.
 */
public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected UserRole role;
    protected UserStatus status;
    protected String phone;

    public User(String id, String name, String email, String password, UserRole role, UserStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.phone = "";
    }

    public User(String id, String name, String email, String password, UserRole role, UserStatus status, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.phone = phone;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public String getPhone() { return phone; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Check if user account is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Abstract method to convert user to file storage format
     */
    public abstract String toFileString();

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + 
               "', role=" + role + ", status=" + status + "}";
    }
}
