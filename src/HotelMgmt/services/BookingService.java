package HotelMgmt.services;

import HotelMgmt.model.*;
import HotelMgmt.constants.BookingStatus;
import HotelMgmt.repository.BookingRepository;
import HotelMgmt.repository.HallRepository;
import HotelMgmt.repository.MaintenanceRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.DateUtil;
import HotelMgmt.util.ValidationUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Booking management operations.
 * Handles booking creation, cancellation, and queries.
 */
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final HallRepository hallRepository;
    private final MaintenanceRepository maintenanceRepository;
    
    public BookingService() {
        this.bookingRepository = BookingRepository.getInstance();
        this.hallRepository = HallRepository.getInstance();
        this.maintenanceRepository = MaintenanceRepository.getInstance();
    }
    
    /**
     * Create a new booking
     */
    public Booking createBooking(Customer customer, String hallId, 
                                 LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                 String remarks) {
        // Validate inputs
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        
        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (!hallOpt.isPresent()) {
            throw new IllegalArgumentException("Hall not found.");
        }
        Hall hall = hallOpt.get();
        
        if (!hall.isAvailable()) {
            throw new IllegalArgumentException("Hall is not available.");
        }
        
        // Validate booking time
        if (!DateUtil.isValidBookingTime(startDateTime, endDateTime)) {
            throw new IllegalArgumentException("Invalid booking time. Business hours are 8 AM - 6 PM.");
        }
        
        // Check for conflicts
        if (bookingRepository.hasConflict(hallId, startDateTime, endDateTime, null)) {
            throw new IllegalArgumentException("Time slot is already booked.");
        }
        
        // Check for maintenance conflicts
        if (maintenanceRepository.hasConflict(hallId, startDateTime, endDateTime, null)) {
            throw new IllegalArgumentException("Hall is under maintenance during this period.");
        }
        
        // Calculate total amount
        long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
        if (hours <= 0) {
            throw new IllegalArgumentException("Booking duration must be at least 1 hour.");
        }
        double totalAmount = hall.getPricePerHour() * hours;
        
        // Create booking
        String bookingId = IdGenerator.generateBookingId();
        Booking booking = new Booking(
            bookingId,
            customer.getId(),
            customer.getName(),
            hall.getId(),
            hall.getName(),
            startDateTime,
            endDateTime,
            totalAmount,
            BookingStatus.PENDING,
            ValidationUtil.sanitize(remarks)
        );
        
        bookingRepository.save(booking);
        return booking;
    }
    
    /**
     * Confirm a booking (after payment)
     */
    public void confirmBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found.");
        }
        
        Booking booking = bookingOpt.get();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.update(booking);
    }
    
    /**
     * Cancel a booking (must be at least 3 days before event)
     */
    public void cancelBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found.");
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.canCancel()) {
            throw new IllegalArgumentException("Cannot cancel booking. Cancellation must be at least 3 days before the event.");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.update(booking);
    }
    
    /**
     * Complete a booking
     */
    public void completeBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.update(booking);
        }
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    /**
     * Get booking by ID
     */
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }
    
    /**
     * Get bookings by customer
     */
    public List<Booking> getBookingsByCustomer(String customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get upcoming bookings for customer
     */
    public List<Booking> getUpcomingBookings(String customerId) {
        return bookingRepository.findUpcomingByCustomer(customerId);
    }
    
    /**
     * Get past bookings for customer
     */
    public List<Booking> getPastBookings(String customerId) {
        return bookingRepository.findPastByCustomer(customerId);
    }
    
    /**
     * Get all upcoming bookings
     */
    public List<Booking> getAllUpcomingBookings() {
        return bookingRepository.findUpcoming();
    }
    
    /**
     * Get all past bookings
     */
    public List<Booking> getAllPastBookings() {
        return bookingRepository.findPast();
    }
    
    /**
     * Get bookings by status
     */
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    /**
     * Get bookings for a specific hall
     */
    public List<Booking> getBookingsByHall(String hallId) {
        return bookingRepository.findByHallId(hallId);
    }
    
    /**
     * Get total sales for a period
     */
    public double getTotalSales(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.getTotalSales(start, end);
    }
    
    /**
     * Get booking count for a period
     */
    public long getBookingCount(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.getBookingCount(start, end);
    }
    
    /**
     * Update booking status
     */
    public void updateBookingStatus(String bookingId, BookingStatus status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(status);
            bookingRepository.update(booking);
        }
    }
    
    /**
     * Delete a booking (admin only)
     */
    public void deleteBooking(String bookingId) {
        bookingRepository.delete(bookingId);
    }
    
    /**
     * Refresh booking data
     */
    public void refresh() {
        bookingRepository.refresh();
    }
    
    /**
     * Get today's bookings
     */
    public List<Booking> getTodayBookings() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return bookingRepository.findAll().stream()
            .filter(b -> !b.getStartDateTime().isBefore(todayStart) && b.getStartDateTime().isBefore(todayEnd))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Update booking
     */
    public void updateBooking(Booking booking) {
        bookingRepository.update(booking);
    }
}
