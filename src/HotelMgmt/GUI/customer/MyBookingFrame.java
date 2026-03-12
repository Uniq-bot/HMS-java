package HotelMgmt.GUI.customer;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.BookingStatus;
import HotelMgmt.services.BookingService;
import HotelMgmt.util.DateUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * My Bookings Frame for customers to view their bookings.
 * Shows upcoming and past bookings with cancellation option.
 */
public class MyBookingFrame extends JPanel {
    
    private Customer customer;
    private CustomerDashboard parent;
    private BookingService bookingService;
    
    private JTable upcomingTable;
    private DefaultTableModel upcomingModel;
    private JTable pastTable;
    private DefaultTableModel pastModel;
    private JComboBox<String> filterCombo;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    
    public MyBookingFrame(Customer customer, CustomerDashboard parent) {
        this.customer = customer;
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
        
        JLabel headerLabel = UIComponents.createTitleLabel("My Bookings");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        filterPanel.add(UIComponents.createLabel("Filter:"));
        
        String[] filters = {"All", "Confirmed", "Pending", "Cancelled", "Completed"};
        filterCombo = UIComponents.createComboBox(filters);
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.addActionListener(e -> loadBookings());
        filterPanel.add(filterCombo);
        
        JButton refreshBtn = UIComponents.createPrimaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.addActionListener(e -> loadBookings());
        filterPanel.add(refreshBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for upcoming and past bookings
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIComponents.LABEL_FONT);
        
        // Upcoming bookings panel
        JPanel upcomingPanel = createUpcomingPanel();
        tabbedPane.addTab("Upcoming Bookings", upcomingPanel);
        
        // Past bookings panel
        JPanel pastPanel = createPastPanel();
        tabbedPane.addTab("Past Bookings", pastPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load data
        loadBookings();
    }
    
    private JPanel createUpcomingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"Booking ID", "Hall", "Date", "Start Time", "End Time", "Amount (RM)", "Status"};
        upcomingModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        upcomingTable = UIComponents.createTable(upcomingModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(upcomingTable);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelBtn = UIComponents.createDangerButton("Cancel Booking");
        cancelBtn.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelBtn);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewBookingDetails(upcomingTable, upcomingModel));
        buttonPanel.add(viewBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPastPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"Booking ID", "Hall", "Date", "Start Time", "End Time", "Amount (RM)", "Status"};
        pastModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pastTable = UIComponents.createTable(pastModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(pastTable);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewBookingDetails(pastTable, pastModel));
        buttonPanel.add(viewBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadBookings() {
        upcomingModel.setRowCount(0);
        pastModel.setRowCount(0);
        
        String filterValue = (String) filterCombo.getSelectedItem();
        
        // Load upcoming bookings
        List<Booking> upcomingBookings = bookingService.getUpcomingBookings(customer.getId());
        for (Booking booking : upcomingBookings) {
            if (matchesFilter(booking, filterValue)) {
                upcomingModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getHallName(),
                    booking.getStartDateTime().toLocalDate().toString(),
                    booking.getStartDateTime().toLocalTime().toString(),
                    booking.getEndDateTime().toLocalTime().toString(),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus().getDisplayName()
                });
            }
        }
        
        // Load past bookings
        List<Booking> pastBookings = bookingService.getPastBookings(customer.getId());
        for (Booking booking : pastBookings) {
            if (matchesFilter(booking, filterValue)) {
                pastModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getHallName(),
                    booking.getStartDateTime().toLocalDate().toString(),
                    booking.getStartDateTime().toLocalTime().toString(),
                    booking.getEndDateTime().toLocalTime().toString(),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private boolean matchesFilter(Booking booking, String filter) {
        if ("All".equals(filter)) return true;
        return booking.getStatus().getDisplayName().equalsIgnoreCase(filter);
    }
    
    private void cancelBooking() {
        int row = upcomingTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to cancel.");
            return;
        }
        
        String bookingId = upcomingModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?\n" +
            "Note: Cancellation is only allowed 3 days before the event.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.cancelBooking(bookingId);
                UIComponents.showSuccess(this, "Booking cancelled successfully.");
                loadBookings();
            } catch (IllegalArgumentException e) {
                UIComponents.showError(this, e.getMessage());
            }
        }
    }
    
    private void viewBookingDetails(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a booking to view.");
            return;
        }
        
        String bookingId = model.getValueAt(row, 0).toString();
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        
        if (booking != null) {
            String details = String.format(
                "Booking ID: %s\n" +
                "Hall: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Duration: %d hours\n" +
                "Amount: RM %.2f\n" +
                "Status: %s\n" +
                "Remarks: %s",
                booking.getId(),
                booking.getHallName(),
                booking.getStartDateTime().toLocalDate(),
                booking.getStartDateTime().toLocalTime(),
                booking.getEndDateTime().toLocalTime(),
                booking.getDurationHours(),
                booking.getTotalAmount(),
                booking.getStatus().getDisplayName(),
                booking.getRemarks().isEmpty() ? "-" : booking.getRemarks()
            );
            
            JOptionPane.showMessageDialog(this, details, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
