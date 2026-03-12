package HotelMgmt.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * Handles parsing, formatting, and validation of dates and times.
 */
public class DateUtil {
    
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    
    // Business hours
    public static final int BUSINESS_START_HOUR = 8;
    public static final int BUSINESS_END_HOUR = 18;
    
    /**
     * Parse date-time string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Format LocalDateTime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Format LocalDateTime for display
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }
    
    /**
     * Parse date string to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parse time string to LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Check if time is within business hours (8 AM - 6 PM)
     */
    public static boolean isWithinBusinessHours(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        return hour >= BUSINESS_START_HOUR && hour < BUSINESS_END_HOUR;
    }
    
    /**
     * Check if date-time range is valid for booking
     */
    public static boolean isValidBookingTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return false;
        if (!start.isBefore(end)) return false;
        if (start.isBefore(LocalDateTime.now())) return false;
        
        // Check business hours
        int startHour = start.getHour();
        int endHour = end.getHour();
        int endMinute = end.getMinute();
        
        boolean startValid = startHour >= BUSINESS_START_HOUR && startHour < BUSINESS_END_HOUR;
        boolean endValid = (endHour > BUSINESS_START_HOUR && endHour < BUSINESS_END_HOUR) || 
                          (endHour == BUSINESS_END_HOUR && endMinute == 0);
        
        return startValid && endValid;
    }
    
    /**
     * Calculate duration in hours between two date-times
     */
    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }
    
    /**
     * Calculate days between two dates
     */
    public static long getDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Get start of day
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atTime(BUSINESS_START_HOUR, 0);
    }
    
    /**
     * Get end of day (business hours)
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(BUSINESS_END_HOUR, 0);
    }
    
    /**
     * Get start of week (Monday)
     */
    public static LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
    
    /**
     * Get start of month
     */
    public static LocalDate getStartOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }
    
    /**
     * Get start of year
     */
    public static LocalDate getStartOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }
    
    /**
     * Check if a date is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if a date is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Get days until a future date
     */
    public static long getDaysUntil(LocalDateTime future) {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), future);
    }
}
