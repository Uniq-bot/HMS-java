package HotelMgmt.GUI.customer;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.IssueStatus;
import HotelMgmt.services.IssueService;
import HotelMgmt.services.BookingService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Issue Frame for customers to raise and view issues.
 * Allows creating new issues and tracking existing ones.
 */
public class IssueFrame extends JPanel {
    
    private Customer customer;
    private CustomerDashboard parent;
    private IssueService issueService;
    private BookingService bookingService;
    
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JComboBox<BookingItem> bookingCombo;
    private JTextArea descriptionArea;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    
    public IssueFrame(Customer customer, CustomerDashboard parent) {
        this.customer = customer;
        this.parent = parent;
        this.issueService = new IssueService();
        this.bookingService = new BookingService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = UIComponents.createTitleLabel("Issues & Support");
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Left panel - Raise new issue
        JPanel leftPanel = createNewIssuePanel();
        mainPanel.add(leftPanel);
        
        // Right panel - View issues
        JPanel rightPanel = createIssueListPanel();
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Load data
        loadBookings();
        loadIssues();
    }
    
    private JPanel createNewIssuePanel() {
        JPanel panel = UIComponents.createCardPanel();
        panel.setLayout(new BorderLayout(0, 15));
        
        JLabel title = UIComponents.createSubtitleLabel("Raise New Issue");
        panel.add(title, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Booking selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIComponents.createLabel("Select Booking:"), gbc);
        gbc.gridx = 1;
        bookingCombo = new JComboBox<>();
        bookingCombo.setFont(UIComponents.LABEL_FONT);
        bookingCombo.setPreferredSize(new Dimension(250, 35));
        formPanel.add(bookingCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(UIComponents.createLabel("Issue Description:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        descriptionArea = UIComponents.createTextArea(5, 25);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(250, 120));
        formPanel.add(scrollPane, gbc);
        
        // Submit button
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = UIComponents.createPrimaryButton("Submit Issue");
        submitBtn.addActionListener(e -> submitIssue());
        formPanel.add(submitBtn, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createIssueListPanel() {
        JPanel panel = UIComponents.createCardPanel();
        panel.setLayout(new BorderLayout(0, 10));
        
        JLabel title = UIComponents.createSubtitleLabel("My Issues");
        panel.add(title, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Issue ID", "Hall", "Status", "Created", "Response"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issueTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(issueTable);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewIssueDetails());
        buttonPanel.add(viewBtn);
        
        JButton refreshBtn = UIComponents.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadIssues());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadBookings() {
        bookingCombo.removeAllItems();
        bookingCombo.addItem(new BookingItem(null, "-- Select a booking --"));
        
        List<Booking> bookings = bookingService.getBookingsByCustomer(customer.getId());
        for (Booking booking : bookings) {
            String display = booking.getId() + " - " + booking.getHallName() + 
                " (" + booking.getStartDateTime().toLocalDate() + ")";
            bookingCombo.addItem(new BookingItem(booking, display));
        }
    }
    
    private void loadIssues() {
        tableModel.setRowCount(0);
        
        List<Issue> issues = issueService.getIssuesByCustomer(customer.getId());
        for (Issue issue : issues) {
            tableModel.addRow(new Object[]{
                issue.getId(),
                issue.getHallName(),
                issue.getStatus().getDisplayName(),
                issue.getCreatedAt().format(formatter),
                issue.getResponse().isEmpty() ? "-" : 
                    (issue.getResponse().length() > 30 ? 
                        issue.getResponse().substring(0, 30) + "..." : issue.getResponse())
            });
        }
    }
    
    private void submitIssue() {
        BookingItem selected = (BookingItem) bookingCombo.getSelectedItem();
        if (selected == null || selected.getBooking() == null) {
            UIComponents.showError(this, "Please select a booking.");
            return;
        }
        
        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            UIComponents.showError(this, "Please enter issue description.");
            return;
        }
        
        try {
            Booking booking = selected.getBooking();
            issueService.createIssue(
                booking.getId(),
                customer.getId(),
                customer.getName(),
                booking.getHallName(),
                description
            );
            
            UIComponents.showSuccess(this, "Issue submitted successfully. Our team will respond soon.");
            descriptionArea.setText("");
            bookingCombo.setSelectedIndex(0);
            loadIssues();
            
        } catch (Exception e) {
            UIComponents.showError(this, "Error submitting issue: " + e.getMessage());
        }
    }
    
    private void viewIssueDetails() {
        int row = issueTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select an issue to view.");
            return;
        }
        
        String issueId = tableModel.getValueAt(row, 0).toString();
        Issue issue = issueService.getIssueById(issueId).orElse(null);
        
        if (issue != null) {
            String details = String.format(
                "Issue ID: %s\n" +
                "Booking ID: %s\n" +
                "Hall: %s\n" +
                "Status: %s\n" +
                "Created: %s\n\n" +
                "Description:\n%s\n\n" +
                "Response:\n%s",
                issue.getId(),
                issue.getBookingId(),
                issue.getHallName(),
                issue.getStatus().getDisplayName(),
                issue.getCreatedAt().format(formatter),
                issue.getDescription(),
                issue.getResponse().isEmpty() ? "No response yet." : issue.getResponse()
            );
            
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(UIComponents.LABEL_FONT);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Issue Details", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    // Helper class for combo box items
    private static class BookingItem {
        private Booking booking;
        private String display;
        
        public BookingItem(Booking booking, String display) {
            this.booking = booking;
            this.display = display;
        }
        
        public Booking getBooking() {
            return booking;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
