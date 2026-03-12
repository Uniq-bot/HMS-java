package HotelMgmt.GUI.customer;

import HotelMgmt.GUI.LoginFrame;
import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.services.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Customer Dashboard - Main interface for customers.
 * Provides access to booking, viewing bookings, raising issues, and profile management.
 */
public class CustomerDashboard extends JFrame {
    
    private Customer customer;
    private JPanel contentPanel;
    private BookingService bookingService;
    
    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        this.bookingService = new BookingService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hall Booking System - Customer Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        add(contentPanel, BorderLayout.CENTER);
        
        // Show dashboard by default
        showDashboard();
        
        setVisible(true);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIComponents.SECONDARY_COLOR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        
        // User info panel
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(UIComponents.PRIMARY_COLOR);
        userPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JLabel welcomeLabel = new JLabel("Welcome,");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(255, 255, 255, 180));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(customer.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Customer");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(255, 255, 255, 150));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userPanel.add(welcomeLabel);
        userPanel.add(nameLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userPanel.add(roleLabel);
        
        sidebar.add(userPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Menu buttons
        String[] menuItems = {"Dashboard", "Book Hall", "My Bookings", "Raise Issue", "My Profile", "Logout"};
        
        for (String item : menuItems) {
            JButton btn = UIComponents.createSidebarButton(item, item.equals("Dashboard"));
            btn.addActionListener(e -> handleMenuClick(item, btn));
            sidebar.add(btn);
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private void handleMenuClick(String menuItem, JButton clickedButton) {
        // Reset all button styles
        Container parent = clickedButton.getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(UIComponents.SECONDARY_COLOR);
                btn.setForeground(UIComponents.TEXT_LIGHT);
            }
        }
        
        // Highlight clicked button
        clickedButton.setBackground(UIComponents.PRIMARY_COLOR);
        clickedButton.setForeground(Color.WHITE);
        
        switch (menuItem) {
            case "Dashboard":
                showDashboard();
                break;
            case "Book Hall":
                showBookingFrame();
                break;
            case "My Bookings":
                showMyBookings();
                break;
            case "Raise Issue":
                showIssueFrame();
                break;
            case "My Profile":
                showProfile();
                break;
            case "Logout":
                logout();
                break;
        }
    }
    
    private void showDashboard() {
        contentPanel.removeAll();
        
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(UIComponents.BACKGROUND_COLOR);
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel headerLabel = UIComponents.createTitleLabel("Dashboard");
        dashboard.add(headerLabel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        List<Booking> upcomingBookings = bookingService.getUpcomingBookings(customer.getId());
        List<Booking> pastBookings = bookingService.getPastBookings(customer.getId());
        List<Booking> allBookings = bookingService.getBookingsByCustomer(customer.getId());
        
        statsPanel.add(UIComponents.createStatCard("Upcoming Bookings", 
            String.valueOf(upcomingBookings.size()), UIComponents.PRIMARY_COLOR));
        statsPanel.add(UIComponents.createStatCard("Past Bookings", 
            String.valueOf(pastBookings.size()), UIComponents.SUCCESS_COLOR));
        statsPanel.add(UIComponents.createStatCard("Total Bookings", 
            String.valueOf(allBookings.size()), UIComponents.WARNING_COLOR));
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        
        JButton bookBtn = UIComponents.createPrimaryButton("Book a Hall");
        bookBtn.addActionListener(e -> showBookingFrame());
        actionsPanel.add(bookBtn);
        
        JButton viewBtn = UIComponents.createSuccessButton("View Bookings");
        viewBtn.addActionListener(e -> showMyBookings());
        actionsPanel.add(viewBtn);
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(UIComponents.BACKGROUND_COLOR);
        mainContent.add(statsPanel, BorderLayout.NORTH);
        mainContent.add(actionsPanel, BorderLayout.CENTER);
        
        dashboard.add(mainContent, BorderLayout.CENTER);
        
        contentPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showBookingFrame() {
        contentPanel.removeAll();
        contentPanel.add(new BookingFrame(customer, this), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showMyBookings() {
        contentPanel.removeAll();
        contentPanel.add(new MyBookingFrame(customer, this), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showIssueFrame() {
        contentPanel.removeAll();
        contentPanel.add(new IssueFrame(customer, this), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showProfile() {
        contentPanel.removeAll();
        
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(UIComponents.BACKGROUND_COLOR);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel headerLabel = UIComponents.createTitleLabel("My Profile");
        profilePanel.add(headerLabel, BorderLayout.NORTH);
        
        // Profile form
        JPanel formCard = UIComponents.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(UIComponents.createLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = UIComponents.createTextField("");
        nameField.setText(customer.getName());
        nameField.setPreferredSize(new Dimension(300, 40));
        formCard.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(UIComponents.createLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = UIComponents.createTextField("");
        emailField.setText(customer.getEmail());
        emailField.setEnabled(false);
        emailField.setPreferredSize(new Dimension(300, 40));
        formCard.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(UIComponents.createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = UIComponents.createTextField("");
        phoneField.setText(customer.getPhone());
        phoneField.setPreferredSize(new Dimension(300, 40));
        formCard.add(phoneField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveBtn = UIComponents.createPrimaryButton("Update Profile");
        saveBtn.addActionListener(e -> {
            try {
                AuthServices authServices = new AuthServices();
                authServices.updateProfile(customer, nameField.getText(), phoneField.getText());
                UIComponents.showSuccess(this, "Profile updated successfully!");
            } catch (IllegalArgumentException ex) {
                UIComponents.showError(this, ex.getMessage());
            }
        });
        formCard.add(saveBtn, gbc);
        
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setBackground(UIComponents.BACKGROUND_COLOR);
        centerWrapper.add(formCard);
        
        profilePanel.add(centerWrapper, BorderLayout.CENTER);
        
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthServices.setCurrentUser(null);
            new LoginFrame();
            dispose();
        }
    }
    
    public void refreshDashboard() {
        showDashboard();
    }
}
