package HotelMgmt.repository;

import HotelMgmt.model.Payment;
import HotelMgmt.util.FileUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Payment data management.
 * Handles CRUD operations for payments.
 */
public class PaymentRepository implements Repository<Payment> {
    
    private static final String FILE_PATH = "data/payments.txt";
    private List<Payment> payments;
    private static PaymentRepository instance;
    
    private PaymentRepository() {
        this.payments = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        payments.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                Payment payment = Payment.fromFileString(line);
                if (payment != null) {
                    payments.add(payment);
                }
            } catch (Exception e) {
                System.err.println("Error parsing payment: " + line);
            }
        }
    }
    
    private void saveToFile() {
        List<String> lines = payments.stream()
            .map(Payment::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(Payment entity) {
        payments.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(Payment entity) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equals(entity.getId())) {
                payments.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        payments.removeIf(p -> p.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<Payment> findById(String id) {
        return payments.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find payment by booking ID
     */
    public Optional<Payment> findByBookingId(String bookingId) {
        return payments.stream()
            .filter(p -> p.getBookingId().equals(bookingId))
            .findFirst();
    }
    
    /**
     * Find payments by customer ID
     */
    public List<Payment> findByCustomerId(String customerId) {
        return payments.stream()
            .filter(p -> p.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get total revenue for a period
     */
    public double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return payments.stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
            .filter(p -> p.getPaymentDate() != null && 
                        !p.getPaymentDate().isBefore(start) && 
                        !p.getPaymentDate().isAfter(end))
            .mapToDouble(Payment::getAmount)
            .sum();
    }
}
