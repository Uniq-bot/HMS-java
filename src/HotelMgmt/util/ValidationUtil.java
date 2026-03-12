package HotelMgmt.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Contains methods for validating user input and data.
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,15}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^.{4,}$"  // Minimum 4 characters
    );
    
    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return true; // Phone is optional
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("[\\s-]", "")).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate name (letters, spaces, and common punctuation)
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) return false;
        return name.trim().length() >= 2 && name.trim().length() <= 100;
    }
    
    /**
     * Validate positive number
     */
    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }
    
    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(int number) {
        return number > 0;
    }
    
    /**
     * Parse integer safely
     */
    public static Integer parseInteger(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parse double safely
     */
    public static Double parseDouble(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Sanitize input string (remove special characters that could cause issues)
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replace(",", ";").replace("\n", " ").replace("\r", "");
    }
    
    /**
     * Validate that all required fields are filled
     */
    public static boolean areFieldsFilled(String... fields) {
        for (String field : fields) {
            if (isEmpty(field)) return false;
        }
        return true;
    }
}
