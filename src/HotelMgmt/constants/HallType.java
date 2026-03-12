package HotelMgmt.constants;

/**
 * Enum representing types of halls available for booking.
 * Each hall type has specific capacity and hourly rate.
 */
public enum HallType {
    AUDITORIUM("Auditorium", 1000, 300.0),
    BANQUET_HALL("Banquet Hall", 300, 100.0),
    MEETING_ROOM("Meeting Room", 30, 50.0);

    private final String displayName;
    private final int capacity;
    private final double pricePerHour;

    HallType(String displayName, int capacity, double pricePerHour) {
        this.displayName = displayName;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public static HallType fromString(String type) {
        for (HallType ht : HallType.values()) {
            if (ht.name().equalsIgnoreCase(type) || ht.displayName.equalsIgnoreCase(type)) {
                return ht;
            }
        }
        return MEETING_ROOM;
    }

    @Override
    public String toString() {
        return displayName + " (Capacity: " + capacity + ", RM" + pricePerHour + "/hr)";
    }
}
