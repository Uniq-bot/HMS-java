package HotelMgmt.services;

import HotelMgmt.model.Payment;
import HotelMgmt.model.Booking;
import HotelMgmt.repository.PaymentRepository;
import HotelMgmt.repository.BookingRepository;
import HotelMgmt.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Payment management operations.
 * Handles payment processing and tracking.
 */
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    
    public PaymentService() {
        this.paymentRepository = PaymentRepository.getInstance();
        this.bookingRepository = BookingRepository.getInstance();
        this.bookingService = new BookingService();
    }
    
    /**
     * Create and process payment for a booking
     */
    public Payment processPayment(String bookingId, String customerId, double amount, 
                                  Payment.PaymentMethod method) {
        // Verify booking exists
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found.");
        }
        
        // Check if payment already exists
        Optional<Payment> existingPayment = paymentRepository.findByBookingId(bookingId);
        if (existingPayment.isPresent() && 
            existingPayment.get().getStatus() == Payment.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment already completed for this booking.");
        }
        
        // Create payment
        String paymentId = IdGenerator.generatePaymentId();
        Payment payment = new Payment(paymentId, bookingId, customerId, amount, method);
        
        // Process payment (simulated)
        boolean success = payment.processPayment();
        
        if (success) {
            paymentRepository.save(payment);
            // Confirm the booking
            bookingService.confirmBooking(bookingId);
        } else {
            throw new RuntimeException("Payment processing failed.");
        }
        
        return payment;
    }
    
    /**
     * Refund a payment
     */
    public void refundPayment(String bookingId) {
        Optional<Payment> paymentOpt = paymentRepository.findByBookingId(bookingId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.refund();
            paymentRepository.update(payment);
        }
    }
    
    /**
     * Get payment by booking ID
     */
    public Optional<Payment> getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
    
    /**
     * Get all payments by customer
     */
    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    /**
     * Get total revenue for a period
     */
    public double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.getTotalRevenue(start, end);
    }
    
    /**
     * Get total revenue overall
     */
    public double getTotalRevenue() {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    /**
     * Refresh payment data
     */
    public void refresh() {
        paymentRepository.refresh();
    }
}
