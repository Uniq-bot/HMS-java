package HotelMgmt.GUI.admin;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.BookingStatus;
import HotelMgmt.services.BookingService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Booking Management Frame for admin to manage all bookings in the system.
 */
public class BookingManagementFrame extends JPanel {
    
    private Administrator admin;
    private AdminDashboard parent;
    private BookingService bookingService;
    
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    
    public BookingManagementFrame(Administrator admin, AdminDashboard parent) {
        this.admin = admin;
        this.parent = parent;
        this.bookingService = new BookingService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        JLabel headerLabel = UIComponents.createTitleLabel("Booking Management");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        filterPanel.add(UIComponents.createLabel("Status:"));
        String[] statuses = {"All", "Pending", "Confirmed", "Cancelled", "Completed"};
        statusFilter = UIComponents.createComboBox(statuses);
        statusFilter.addActionListener(e -> loadBookings());
        filterPanel.add(statusFilter);
        
        JButton refreshBtn = UIComponents.createPrimaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());
        filterPanel.add(refreshBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] columns = {"Booking ID", "Customer", "Hall", "Date", "Time", "Amount (RM)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(bookingTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewBookingDetails());
        buttonPanel.add(viewBtn);
        
        JButton confirmBtn = UIComponents.createSuccessButton("Confirm Booking");
        confirmBtn.addActionListener(e -> confirmBooking());
        buttonPanel.add(confirmBtn);
        
        JButton cancelBtn = UIComponents.createDangerButton("Cancel Booking");
        cancelBtn.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelBtn);
        
        JButton completeBtn = UIComponents.createSecondaryButton("Mark Complete");
        completeBtn.addActionListener(e -> completeBooking());
        buttonPanel.add(completeBtn);
        
        JButton deleteBtn = UIComponents.createDangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteBooking());
        buttonPanel.add(deleteBtn);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        loadBookings();
    }
    
    private void loadBookings() {
        tableModel.setRowCount(0);
        String statusFilterValue = (String) statusFilter.getSelectedItem();
        
        List<Booking> bookings = bookingService.getAllBookings();
        
        for (Booking booking : bookings) {
            if (matchesFilter(booking, statusFilterValue)) {
                tableModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getCustomerName(),
                    booking.getHallName(),
                    booking.getStartDateTime().toLocalDate().toString(),
                    booking.getStartDateTime().toLocalTime() + " - " + booking.getEndDateTime().toLocalTime(),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private boolean matchesFilter(Booking booking, String status) {
        if ("All".equals(status)) return true;
        return booking.getStatus().getDisplayName().equalsIgnoreCase(status);
    }
    
    private void viewBookingDetails() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to view.");
            return;
        }
        
        String bookingId = tableModel.getValueAt(row, 0).toString();
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        
        if (booking != null) {
            String details = String.format(
                "Booking ID: %s\n" +
                "Customer: %s (%s)\n" +
                "Hall: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Duration: %d hours\n" +
                "Amount: RM %.2f\n" +
                "Status: %s\n" +
                "Remarks: %s",
                booking.getId(),
                booking.getCustomerName(),
                booking.getCustomerId(),
                booking.getHallName(),
                booking.getStartDateTime().toLocalDate(),
                booking.getStartDateTime().toLocalTime(),
                booking.getEndDateTime().toLocalTime(),
                booking.getDurationHours(),
                booking.getTotalAmount(),
                booking.getStatus().getDisplayName(),
                booking.getRemarks().isEmpty() ? "-" : booking.getRemarks()
            );
            
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(UIComponents.LABEL_FONT);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Booking Details", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void confirmBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to confirm.");
            return;
        }
        
        String bookingId = tableModel.getValueAt(row, 0).toString();
        try {
            bookingService.confirmBooking(bookingId);
            loadBookings();
            UIComponents.showSuccess(this, "Booking confirmed successfully!");
        } catch (IllegalArgumentException e) {
            UIComponents.showError(this, e.getMessage());
        }
    }
    
    private void cancelBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to cancel.");
            return;
        }
        
        String bookingId = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.cancelBooking(bookingId);
                loadBookings();
                UIComponents.showSuccess(this, "Booking cancelled successfully!");
            } catch (IllegalArgumentException e) {
                UIComponents.showError(this, e.getMessage());
            }
        }
    }
    
    private void completeBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to complete.");
            return;
        }
        
        String bookingId = tableModel.getValueAt(row, 0).toString();
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        
        if (booking != null) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingService.updateBooking(booking);
            loadBookings();
            UIComponents.showSuccess(this, "Booking marked as completed!");
        }
    }
    
    private void deleteBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to delete.");
            return;
        }
        
        String bookingId = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete this booking?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            bookingService.deleteBooking(bookingId);
            loadBookings();
            UIComponents.showSuccess(this, "Booking deleted successfully!");
        }
    }
}
