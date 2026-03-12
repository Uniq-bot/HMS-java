package HotelMgmt.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payment class representing a payment transaction for a booking.
 * Supports simulated payment processing.
 */
public class Payment {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, CASH, ONLINE_BANKING
    }
    
    private String id;
    private String bookingId;
    private String customerId;
    private double amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionRef;
    private LocalDateTime paymentDate;

    public Payment(String id, String bookingId, String customerId, double amount, 
                   PaymentMethod method) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.transactionRef = "";
        this.paymentDate = null;
    }

    public Payment(String id, String bookingId, String customerId, double amount,
                   PaymentStatus status, PaymentMethod method, 
                   String transactionRef, LocalDateTime paymentDate) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.transactionRef = transactionRef;
        this.paymentDate = paymentDate;
    }

    // Getters
    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public PaymentMethod getMethod() { return method; }
    public String getTransactionRef() { return transactionRef; }
    public LocalDateTime getPaymentDate() { return paymentDate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    /**
     * Process payment (simulation)
     */
    public boolean processPayment() {
        // Simulate payment processing
        this.transactionRef = "TXN" + System.currentTimeMillis();
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.COMPLETED;
        return true;
    }

    /**
     * Refund payment
     */
    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    /**
     * Convert payment to file storage format
     */
    public String toFileString() {
        return String.join(",",
            id,
            bookingId,
            customerId,
            String.valueOf(amount),
            status.name(),
            method.name(),
            transactionRef,
            paymentDate != null ? paymentDate.format(FORMATTER) : ""
        );
    }

    /**
     * Create Payment from file line
     */
    public static Payment fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            LocalDateTime payDate = null;
            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                payDate = LocalDateTime.parse(parts[7].trim(), FORMATTER);
            }
            return new Payment(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                Double.parseDouble(parts[3].trim()),
                PaymentStatus.valueOf(parts[4].trim()),
                PaymentMethod.valueOf(parts[5].trim()),
                parts[6].trim(),
                payDate
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return "Payment{id='" + id + "', booking='" + bookingId + 
               "', amount=RM" + amount + ", status=" + status + "}";
    }
}
