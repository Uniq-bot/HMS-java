package HotelMgmt;

import HotelMgmt.GUI.LoginFrame;
import HotelMgmt.util.FileUtil;

import javax.swing.*;

/**
 * Hall Booking Management System
 * Main entry point for the application.
 * 
 * @author Hall Booking System Team
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }
        
        // Ensure data files exist
        initializeDataFiles();
        
        // Launch application
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
    
    /**
     * Initialize required data files if they don't exist.
     */
    private static void initializeDataFiles() {
        String[] dataFiles = {
            "data/users.txt",
            "data/halls.txt",
            "data/bookings.txt",
            "data/payments.txt",
            "data/issues.txt",
            "data/maintenance.txt"
        };
        
        for (String file : dataFiles) {
            FileUtil.ensureFileExists(file);
        }
    }
}