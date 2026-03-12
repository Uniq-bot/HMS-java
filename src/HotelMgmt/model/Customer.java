package HotelMgmt.model;

import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;

/**
 * Customer class representing users who can book halls.
 * Extends User base class with customer-specific functionality.
 */
public class Customer extends User {
    
    private String address;

    public Customer(String id, String name, String email, String password, UserStatus status) {
        super(id, name, email, password, UserRole.CUSTOMER, status);
        this.address = "";
    }

    public Customer(String id, String name, String email, String password, UserStatus status, String phone, String address) {
        super(id, name, email, password, UserRole.CUSTOMER, status, phone);
        this.address = address;
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Convert customer to file storage format
     * Format: ID|Name|Email|Password|Role|Status|Phone|Address
     */
    @Override
    public String toFileString() {
        return String.join("|", id, name, email, password, role.name(), status.name(), phone, address);
    }

    /**
     * Create Customer from file line
     */
    public static Customer fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 6) {
            Customer customer = new Customer(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                UserStatus.fromString(parts[5].trim())
            );
            if (parts.length >= 7) customer.setPhone(parts[6].trim());
            if (parts.length >= 8) customer.setAddress(parts[7].trim());
            return customer;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
