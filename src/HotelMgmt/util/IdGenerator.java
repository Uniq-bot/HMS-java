package HotelMgmt.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs.
 * Uses timestamp and atomic counter for uniqueness.
 */
public class IdGenerator {
    
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    /**
     * Generate a unique customer ID
     */
    public static String generateCustomerId() {
        return "CUS" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique scheduler ID
     */
    public static String generateSchedulerId() {
        return "SCH" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique admin ID
     */
    public static String generateAdminId() {
        return "ADM" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique manager ID
     */
    public static String generateManagerId() {
        return "MGR" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique hall ID
     */
    public static String generateHallId() {
        return "HALL" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique booking ID
     */
    public static String generateBookingId() {
        return "BK" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique issue ID
     */
    public static String generateIssueId() {
        return "ISS" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique maintenance ID
     */
    public static String generateMaintenanceId() {
        return "MNT" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a unique payment ID
     */
    public static String generatePaymentId() {
        return "PAY" + System.currentTimeMillis() + counter.incrementAndGet();
    }
    
    /**
     * Generate a transaction reference
     */
    public static String generateTransactionRef() {
        return "TXN" + System.currentTimeMillis();
    }
}
