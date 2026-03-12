package HotelMgmt.repository;

import HotelMgmt.model.Booking;
import HotelMgmt.constants.BookingStatus;
import HotelMgmt.util.FileUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Booking data management.
 * Handles CRUD operations for bookings.
 */
public class BookingRepository implements Repository<Booking> {
    
    private static final String FILE_PATH = "data/bookings.txt";
    private List<Booking> bookings;
    private static BookingRepository instance;
    
    private BookingRepository() {
        this.bookings = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        bookings.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                Booking booking = Booking.fromFileString(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            } catch (Exception e) {
                System.err.println("Error parsing booking: " + line);
            }
        }
    }
    
    private void saveToFile() {
        List<String> lines = bookings.stream()
            .map(Booking::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(Booking entity) {
        bookings.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(Booking entity) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(entity.getId())) {
                bookings.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        bookings.removeIf(b -> b.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<Booking> findById(String id) {
        return bookings.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find bookings by customer ID
     */
    public List<Booking> findByCustomerId(String customerId) {
        return bookings.stream()
            .filter(b -> b.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find bookings by hall ID
     */
    public List<Booking> findByHallId(String hallId) {
        return bookings.stream()
            .filter(b -> b.getHallId().equals(hallId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find bookings by status
     */
    public List<Booking> findByStatus(BookingStatus status) {
        return bookings.stream()
            .filter(b -> b.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Find upcoming bookings
     */
    public List<Booking> findUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
            .filter(b -> b.getStartDateTime().isAfter(now) && 
                        (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING))
            .sorted(Comparator.comparing(Booking::getStartDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Find past bookings
     */
    public List<Booking> findPast() {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
            .filter(b -> b.getEndDateTime().isBefore(now) || b.getStatus() == BookingStatus.COMPLETED)
            .sorted(Comparator.comparing(Booking::getStartDateTime).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Find upcoming bookings for a customer
     */
    public List<Booking> findUpcomingByCustomer(String customerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
            .filter(b -> b.getCustomerId().equals(customerId) &&
                        b.getStartDateTime().isAfter(now) && 
                        (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING))
            .sorted(Comparator.comparing(Booking::getStartDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Find past bookings for a customer
     */
    public List<Booking> findPastByCustomer(String customerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
            .filter(b -> b.getCustomerId().equals(customerId) &&
                        (b.getEndDateTime().isBefore(now) || b.getStatus() == BookingStatus.COMPLETED))
            .sorted(Comparator.comparing(Booking::getStartDateTime).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Check for booking conflicts
     */
    public boolean hasConflict(String hallId, LocalDateTime start, LocalDateTime end, String excludeBookingId) {
        return bookings.stream()
            .filter(b -> b.getHallId().equals(hallId))
            .filter(b -> excludeBookingId == null || !b.getId().equals(excludeBookingId))
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
            .anyMatch(b -> !b.getEndDateTime().isBefore(start) && !b.getStartDateTime().isAfter(end));
    }
    
    /**
     * Get bookings within date range
     */
    public List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return bookings.stream()
            .filter(b -> !b.getStartDateTime().isAfter(end) && !b.getEndDateTime().isBefore(start))
            .sorted(Comparator.comparing(Booking::getStartDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Get total sales amount for a period
     */
    public double getTotalSales(LocalDateTime start, LocalDateTime end) {
        return bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
            .filter(b -> !b.getCreatedAt().isBefore(start) && !b.getCreatedAt().isAfter(end))
            .mapToDouble(Booking::getTotalAmount)
            .sum();
    }
    
    /**
     * Get booking count for a period
     */
    public long getBookingCount(LocalDateTime start, LocalDateTime end) {
        return bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
            .filter(b -> !b.getCreatedAt().isBefore(start) && !b.getCreatedAt().isAfter(end))
            .count();
    }
}
