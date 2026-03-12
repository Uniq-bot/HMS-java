package HotelMgmt.model;

import HotelMgmt.constants.HallType;

/**
 * Hall class representing a bookable hall in the system.
 * Contains hall information including type, capacity, and pricing.
 */
public class Hall {
    private String id;
    private String name;
    private HallType type;
    private int capacity;
    private double pricePerHour;
    private String description;
    private boolean available;

    public Hall(String id, String name, HallType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = type.getCapacity();
        this.pricePerHour = type.getPricePerHour();
        this.description = "";
        this.available = true;
    }

    public Hall(String id, String name, HallType type, int capacity, 
                double pricePerHour, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.available = available;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public HallType getType() { return type; }
    public int getCapacity() { return capacity; }
    public double getPricePerHour() { return pricePerHour; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return available; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(HallType type) { this.type = type; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
    public void setDescription(String description) { this.description = description; }
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Calculate booking cost based on hours
     */
    public double calculateCost(int hours) {
        return pricePerHour * hours;
    }

    /**
     * Convert hall to file storage format
     * Format: ID,Name,Type,Capacity,PricePerHour,Description,Available
     */
    public String toFileString() {
        return String.join(",", id, name, type.name(), 
                String.valueOf(capacity), String.valueOf(pricePerHour), 
                description.replace(",", ";"), String.valueOf(available));
    }

    /**
     * Create Hall from file line
     */
    public static Hall fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            return new Hall(
                parts[0].trim(),
                parts[1].trim(),
                HallType.fromString(parts[2].trim()),
                Integer.parseInt(parts[3].trim()),
                Double.parseDouble(parts[4].trim()),
                parts[5].trim().replace(";", ","),
                Boolean.parseBoolean(parts[6].trim())
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ") - RM" + pricePerHour + "/hr";
    }
}
