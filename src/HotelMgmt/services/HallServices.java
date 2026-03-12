package HotelMgmt.services;

import HotelMgmt.model.Hall;
import HotelMgmt.model.Maintenance;
import HotelMgmt.model.Booking;
import HotelMgmt.constants.HallType;
import HotelMgmt.constants.BookingStatus;
import HotelMgmt.constants.MaintenanceStatus;
import HotelMgmt.repository.HallRepository;
import HotelMgmt.repository.BookingRepository;
import HotelMgmt.repository.MaintenanceRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Hall management operations.
 * Handles CRUD operations and availability checking for halls.
 */
public class HallServices {
    
    private final HallRepository hallRepository;
    private final BookingRepository bookingRepository;
    private final MaintenanceRepository maintenanceRepository;
    
    public HallServices() {
        this.hallRepository = HallRepository.getInstance();
        this.bookingRepository = BookingRepository.getInstance();
        this.maintenanceRepository = MaintenanceRepository.getInstance();
    }
    
    /**
     * Add a new hall
     */
    public Hall addHall(String name, HallType type, int capacity, double pricePerHour, String description) {
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid hall name.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        if (pricePerHour <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        if (hallRepository.nameExists(name)) {
            throw new IllegalArgumentException("Hall name already exists.");
        }
        
        String id = IdGenerator.generateHallId();
        Hall hall = new Hall(id, name, type, capacity, pricePerHour, 
                            ValidationUtil.sanitize(description), true);
        hallRepository.save(hall);
        return hall;
    }
    
    /**
     * Update an existing hall (simple version)
     */
    public void updateHall(Hall hall) {
        hallRepository.update(hall);
    }
    
    /**
     * Update an existing hall with full parameters
     */
    public void updateHall(Hall hall, String name, HallType type, int capacity, 
                          double pricePerHour, String description) {
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid hall name.");
        }
        
        // Check if name is changed and new name already exists
        if (!hall.getName().equals(name) && hallRepository.nameExists(name)) {
            throw new IllegalArgumentException("Hall name already exists.");
        }
        
        hall.setName(name);
        hall.setType(type);
        hall.setCapacity(capacity);
        hall.setPricePerHour(pricePerHour);
        hall.setDescription(ValidationUtil.sanitize(description));
        hallRepository.update(hall);
    }
    
    /**
     * Delete a hall
     */
    public void deleteHall(String hallId) {
        // Check for active bookings
        List<Booking> activeBookings = bookingRepository.findByHallId(hallId).stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
            .filter(b -> b.getEndDateTime().isAfter(LocalDateTime.now()))
            .collect(Collectors.toList());
        
        if (!activeBookings.isEmpty()) {
            throw new IllegalStateException("Cannot delete hall with active bookings.");
        }
        
        hallRepository.delete(hallId);
    }
    
    /**
     * Get all halls
     */
    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }
    
    /**
     * Get hall by ID
     */
    public Optional<Hall> getHallById(String id) {
        return hallRepository.findById(id);
    }
    
    /**
     * Get available halls
     */
    public List<Hall> getAvailableHalls() {
        return hallRepository.findAvailable();
    }
    
    /**
     * Get halls by type
     */
    public List<Hall> getHallsByType(HallType type) {
        return hallRepository.findByType(type);
    }
    
    /**
     * Check if hall is available for a specific time slot
     */
    public boolean isHallAvailable(String hallId, LocalDateTime start, LocalDateTime end) {
        // Check for booking conflicts
        if (bookingRepository.hasConflict(hallId, start, end, null)) {
            return false;
        }
        
        // Check for maintenance conflicts
        if (maintenanceRepository.hasConflict(hallId, start, end, null)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get halls available for a specific time slot
     */
    public List<Hall> getAvailableHallsForTimeSlot(LocalDateTime start, LocalDateTime end) {
        return hallRepository.findAvailable().stream()
            .filter(hall -> isHallAvailable(hall.getId(), start, end))
            .collect(Collectors.toList());
    }
    
    /**
     * Set hall availability
     */
    public void setHallAvailability(String hallId, boolean available) {
        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (hallOpt.isPresent()) {
            Hall hall = hallOpt.get();
            hall.setAvailable(available);
            hallRepository.update(hall);
        }
    }
    
    /**
     * Calculate booking cost
     */
    public double calculateBookingCost(String hallId, LocalDateTime start, LocalDateTime end) {
        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (hallOpt.isPresent()) {
            Hall hall = hallOpt.get();
            long hours = java.time.temporal.ChronoUnit.HOURS.between(start, end);
            return hall.getPricePerHour() * hours;
        }
        return 0;
    }
    
    /**
     * Refresh hall data
     */
    public void refresh() {
        hallRepository.refresh();
    }
}
