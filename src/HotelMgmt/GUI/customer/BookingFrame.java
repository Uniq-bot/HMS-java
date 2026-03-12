package HotelMgmt.GUI.customer;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.*;
import HotelMgmt.services.*;
import HotelMgmt.util.DateUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Booking Frame for customers to book halls.
 * Allows selecting halls, dates, times and processing payment.
 */
public class BookingFrame extends JPanel {
    
    private Customer customer;
    private CustomerDashboard parent;
    private HallServices hallServices;
    private BookingService bookingService;
    private PaymentService paymentService;
    
    private JTable hallTable;
    private DefaultTableModel hallTableModel;
    private JComboBox<String> hallTypeFilter;
    private JTextField dateField;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;
    private JTextArea remarksArea;
    private JLabel priceLabel;
    
    private Hall selectedHall;
    
    public BookingFrame(Customer customer, CustomerDashboard parent) {
        this.customer = customer;
        this.parent = parent;
        this.hallServices = new HallServices();
        this.bookingService = new BookingService();
        this.paymentService = new PaymentService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = UIComponents.createTitleLabel("Book a Hall");
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Left panel - Hall selection
        JPanel leftPanel = createHallSelectionPanel();
        mainPanel.add(leftPanel);
        
        // Right panel - Booking form
        JPanel rightPanel = createBookingFormPanel();
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Load halls
        loadHalls();
    }
    
    private JPanel createHallSelectionPanel() {
        JPanel panel = UIComponents.createCardPanel();
        panel.setLayout(new BorderLayout(0, 10));
        
        JLabel title = UIComponents.createSubtitleLabel("Available Halls");
        panel.add(title, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(UIComponents.createLabel("Filter by type:"));
        
        String[] types = {"All Types", "Auditorium", "Banquet Hall", "Meeting Room"};
        hallTypeFilter = UIComponents.createComboBox(types);
        hallTypeFilter.setPreferredSize(new Dimension(150, 35));
        hallTypeFilter.addActionListener(e -> filterHalls());
        filterPanel.add(hallTypeFilter);
        
        // Table
        String[] columns = {"ID", "Name", "Type", "Capacity", "Price/Hour (RM)"};
        hallTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hallTable = UIComponents.createTable(hallTableModel);
        hallTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectHall();
            }
        });
        
        JScrollPane scrollPane = UIComponents.createScrollPane(hallTable);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBookingFormPanel() {
        JPanel panel = UIComponents.createCardPanel();
        panel.setLayout(new BorderLayout(0, 15));
        
        JLabel title = UIComponents.createSubtitleLabel("Booking Details");
        panel.add(title, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIComponents.createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = UIComponents.createTextField("");
        dateField.setText(LocalDate.now().plusDays(1).toString());
        dateField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(dateField, gbc);
        
        // Start time
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIComponents.createLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        String[] times = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        startTimeCombo = UIComponents.createComboBox(times);
        startTimeCombo.setPreferredSize(new Dimension(200, 35));
        startTimeCombo.addActionListener(e -> updatePrice());
        formPanel.add(startTimeCombo, gbc);
        
        // End time
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIComponents.createLabel("End Time:"), gbc);
        gbc.gridx = 1;
        String[] endTimes = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};
        endTimeCombo = UIComponents.createComboBox(endTimes);
        endTimeCombo.setPreferredSize(new Dimension(200, 35));
        endTimeCombo.setSelectedIndex(2);
        endTimeCombo.addActionListener(e -> updatePrice());
        formPanel.add(endTimeCombo, gbc);
        
        // Remarks
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIComponents.createLabel("Remarks:"), gbc);
        gbc.gridx = 1;
        remarksArea = UIComponents.createTextArea(3, 20);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(remarksScroll, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UIComponents.createLabel("Total Price:"), gbc);
        gbc.gridx = 1;
        priceLabel = new JLabel("RM 0.00");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        priceLabel.setForeground(UIComponents.SUCCESS_COLOR);
        formPanel.add(priceLabel, gbc);
        
        // Book button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton bookBtn = UIComponents.createSuccessButton("Confirm & Pay");
        bookBtn.setPreferredSize(new Dimension(200, 45));
        bookBtn.addActionListener(e -> processBooking());
        formPanel.add(bookBtn, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadHalls() {
        hallTableModel.setRowCount(0);
        List<Hall> halls = hallServices.getAvailableHalls();
        
        for (Hall hall : halls) {
            hallTableModel.addRow(new Object[]{
                hall.getId(),
                hall.getName(),
                hall.getType().getDisplayName(),
                hall.getCapacity(),
                String.format("%.2f", hall.getPricePerHour())
            });
        }
    }
    
    private void filterHalls() {
        hallTableModel.setRowCount(0);
        List<Hall> halls;
        
        String selected = (String) hallTypeFilter.getSelectedItem();
        if ("All Types".equals(selected)) {
            halls = hallServices.getAvailableHalls();
        } else {
            HallType type = HallType.fromString(selected);
            halls = hallServices.getHallsByType(type);
        }
        
        for (Hall hall : halls) {
            if (hall.isAvailable()) {
                hallTableModel.addRow(new Object[]{
                    hall.getId(),
                    hall.getName(),
                    hall.getType().getDisplayName(),
                    hall.getCapacity(),
                    String.format("%.2f", hall.getPricePerHour())
                });
            }
        }
    }
    
    private void selectHall() {
        int row = hallTable.getSelectedRow();
        if (row >= 0) {
            String hallId = hallTableModel.getValueAt(row, 0).toString();
            selectedHall = hallServices.getHallById(hallId).orElse(null);
            updatePrice();
        }
    }
    
    private void updatePrice() {
        if (selectedHall == null) {
            priceLabel.setText("RM 0.00");
            return;
        }
        
        try {
            String startStr = (String) startTimeCombo.getSelectedItem();
            String endStr = (String) endTimeCombo.getSelectedItem();
            
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);
            
            if (end.isAfter(start)) {
                long hours = java.time.Duration.between(start, end).toHours();
                double price = selectedHall.getPricePerHour() * hours;
                priceLabel.setText(String.format("RM %.2f", price));
            } else {
                priceLabel.setText("Invalid time range");
            }
        } catch (Exception e) {
            priceLabel.setText("RM 0.00");
        }
    }
    
    private void processBooking() {
        if (selectedHall == null) {
            UIComponents.showError(this, "Please select a hall first.");
            return;
        }
        
        try {
            LocalDate date = DateUtil.parseDate(dateField.getText());
            if (date == null) {
                UIComponents.showError(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
            
            String startStr = (String) startTimeCombo.getSelectedItem();
            String endStr = (String) endTimeCombo.getSelectedItem();
            
            LocalTime startTime = LocalTime.parse(startStr);
            LocalTime endTime = LocalTime.parse(endStr);
            
            if (!endTime.isAfter(startTime)) {
                UIComponents.showError(this, "End time must be after start time.");
                return;
            }
            
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
            
            // Check availability
            if (!hallServices.isHallAvailable(selectedHall.getId(), startDateTime, endDateTime)) {
                UIComponents.showError(this, "Hall is not available for the selected time slot.");
                return;
            }
            
            // Confirm booking
            int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirm booking for %s\nDate: %s\nTime: %s - %s\nTotal: %s",
                    selectedHall.getName(), date, startStr, endStr, priceLabel.getText()),
                "Confirm Booking", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Create booking
                Booking booking = bookingService.createBooking(
                    customer, selectedHall.getId(), startDateTime, endDateTime, 
                    remarksArea.getText()
                );
                
                // Show payment dialog
                showPaymentDialog(booking);
            }
            
        } catch (IllegalArgumentException e) {
            UIComponents.showError(this, e.getMessage());
        } catch (Exception e) {
            UIComponents.showError(this, "Error creating booking: " + e.getMessage());
        }
    }
    
    private void showPaymentDialog(Booking booking) {
        JDialog paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Payment", true);
        paymentDialog.setSize(400, 300);
        paymentDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = UIComponents.createSubtitleLabel("Complete Payment");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        detailsPanel.setBackground(Color.WHITE);
        
        detailsPanel.add(UIComponents.createLabel("Booking ID:"));
        detailsPanel.add(UIComponents.createLabel(booking.getId()));
        
        detailsPanel.add(UIComponents.createLabel("Hall:"));
        detailsPanel.add(UIComponents.createLabel(booking.getHallName()));
        
        detailsPanel.add(UIComponents.createLabel("Amount:"));
        detailsPanel.add(UIComponents.createLabel(String.format("RM %.2f", booking.getTotalAmount())));
        
        detailsPanel.add(UIComponents.createLabel("Payment Method:"));
        String[] methods = {"Credit Card", "Debit Card", "Online Banking", "Cash"};
        JComboBox<String> methodCombo = UIComponents.createComboBox(methods);
        detailsPanel.add(methodCombo);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton payBtn = UIComponents.createSuccessButton("Pay Now");
        payBtn.addActionListener(e -> {
            try {
                Payment.PaymentMethod method = Payment.PaymentMethod.values()[methodCombo.getSelectedIndex()];
                Payment payment = paymentService.processPayment(
                    booking.getId(), customer.getId(), booking.getTotalAmount(), method
                );
                
                paymentDialog.dispose();
                showReceipt(booking, payment);
                
            } catch (Exception ex) {
                UIComponents.showError(paymentDialog, "Payment failed: " + ex.getMessage());
            }
        });
        buttonPanel.add(payBtn);
        
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> {
            bookingService.cancelBooking(booking.getId());
            paymentDialog.dispose();
        });
        buttonPanel.add(cancelBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        paymentDialog.add(panel);
        paymentDialog.setVisible(true);
    }
    
    private void showReceipt(Booking booking, Payment payment) {
        JFrame receiptFrame = new JFrame("Booking Receipt");
        receiptFrame.setSize(500, 600);
        receiptFrame.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = UIComponents.createTitleLabel("Booking Receipt");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Receipt content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIComponents.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        
        addReceiptRow(contentPanel, "Booking ID:", booking.getId());
        addReceiptRow(contentPanel, "Transaction Ref:", payment.getTransactionRef());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addReceiptRow(contentPanel, "Customer:", customer.getName());
        addReceiptRow(contentPanel, "Email:", customer.getEmail());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addReceiptRow(contentPanel, "Hall:", booking.getHallName());
        addReceiptRow(contentPanel, "Date:", booking.getStartDateTime().toLocalDate().toString());
        addReceiptRow(contentPanel, "Time:", booking.getStartDateTime().toLocalTime() + " - " + 
            booking.getEndDateTime().toLocalTime());
        addReceiptRow(contentPanel, "Duration:", booking.getDurationHours() + " hours");
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel amountLabel = new JLabel(String.format("Total: RM %.2f", booking.getTotalAmount()));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(UIComponents.SUCCESS_COLOR);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(amountLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel statusLabel = new JLabel("✓ Payment Successful");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(UIComponents.SUCCESS_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(statusLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Close button
        JButton closeBtn = UIComponents.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> {
            receiptFrame.dispose();
            parent.refreshDashboard();
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        receiptFrame.add(panel);
        receiptFrame.setVisible(true);
    }
    
    private void addReceiptRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setPreferredSize(new Dimension(120, 20));
        row.add(labelComp);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(valueComp);
        
        panel.add(row);
    }
}
