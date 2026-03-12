package HotelMgmt.GUI.Scheduler;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.GUI.LoginFrame;
import HotelMgmt.model.*;
import HotelMgmt.services.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Scheduler Dashboard - Main dashboard for scheduler users.
 * Provides access to hall management and maintenance scheduling.
 */
public class SchedulerDashboard extends JFrame {
    
    private Scheduler scheduler;
    private HallServices hallService;
    private MaintenanceService maintenanceService;
    private BookingService bookingService;
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public SchedulerDashboard(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.hallService = new HallServices();
        this.maintenanceService = new MaintenanceService();
        this.bookingService = new BookingService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hall Booking System - Scheduler Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        // Add panels
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(new HallManagementFrame(scheduler, this), "halls");
        contentPanel.add(new MaintenanceFrame(scheduler, this), "maintenance");
        
        add(contentPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(UIComponents.SECONDARY_COLOR);
        sidebar.setLayout(new BorderLayout());
        
        // Logo/Title section
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UIComponents.PRIMARY_COLOR);
        logoPanel.setPreferredSize(new Dimension(250, 80));
        logoPanel.setLayout(new BorderLayout());
        
        JLabel logoLabel = new JLabel("🏢 Hall Booking", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        sidebar.add(logoPanel, BorderLayout.NORTH);
        
        // User info
        JPanel userPanel = new JPanel();
        userPanel.setBackground(new Color(44, 62, 80));
        userPanel.setPreferredSize(new Dimension(250, 60));
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userPanel.add(userIcon);
        
        JLabel userName = new JLabel("<html><b>" + scheduler.getName() + "</b><br><small>Scheduler</small></html>");
        userName.setForeground(Color.WHITE);
        userName.setFont(UIComponents.LABEL_FONT);
        userPanel.add(userName);
        
        // Menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(UIComponents.SECONDARY_COLOR);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton dashboardBtn = createMenuButton("📊 Dashboard");
        dashboardBtn.addActionListener(e -> showPanel("dashboard"));
        menuPanel.add(dashboardBtn);
        
        JButton hallsBtn = createMenuButton("🏛️ Hall Management");
        hallsBtn.addActionListener(e -> showPanel("halls"));
        menuPanel.add(hallsBtn);
        
        JButton maintenanceBtn = createMenuButton("🔧 Maintenance");
        maintenanceBtn.addActionListener(e -> showPanel("maintenance"));
        menuPanel.add(maintenanceBtn);
        
        menuPanel.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createMenuButton("🚪 Logout");
        logoutBtn.addActionListener(e -> logout());
        menuPanel.add(logoutBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIComponents.SECONDARY_COLOR);
        centerPanel.add(userPanel, BorderLayout.NORTH);
        centerPanel.add(menuPanel, BorderLayout.CENTER);
        
        sidebar.add(centerPanel, BorderLayout.CENTER);
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIComponents.LABEL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIComponents.SECONDARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setPreferredSize(new Dimension(250, 50));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setOpaque(true);
                button.setBackground(new Color(44, 62, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
                button.setBackground(UIComponents.SECONDARY_COLOR);
            }
        });
        
        return button;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIComponents.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel headerLabel = UIComponents.createTitleLabel("Scheduler Dashboard");
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        List<Hall> halls = hallService.getAllHalls();
        long totalHalls = halls.size();
        long availableHalls = halls.stream().filter(Hall::isAvailable).count();
        long todayBookings = bookingService.getTodayBookings().size();
        long pendingMaintenance = maintenanceService.getPendingMaintenance().size();
        
        statsPanel.add(createStatCard("Total Halls", String.valueOf(totalHalls), UIComponents.PRIMARY_COLOR, "🏛️"));
        statsPanel.add(createStatCard("Available Halls", String.valueOf(availableHalls), UIComponents.SUCCESS_COLOR, "✅"));
        statsPanel.add(createStatCard("Today's Bookings", String.valueOf(todayBookings), new Color(142, 68, 173), "📅"));
        statsPanel.add(createStatCard("Pending Maintenance", String.valueOf(pendingMaintenance), UIComponents.DANGER_COLOR, "🔧"));
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        actionsPanel.add(createActionCard("Manage Halls", "Add, edit, or remove halls from the system", "halls"));
        actionsPanel.add(createActionCard("Schedule Maintenance", "Schedule maintenance for halls", "maintenance"));
        actionsPanel.add(createActionCard("View Today's Schedule", "Check today's hall bookings", "dashboard"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(actionsPanel, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = UIComponents.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        card.add(iconLabel, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(valueLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIComponents.LABEL_FONT);
        titleLabel.setForeground(UIComponents.SECONDARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(titleLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActionCard(String title, String description, String targetPanel) {
        JPanel card = UIComponents.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIComponents.SUBTITLE_FONT);
        titleLabel.setForeground(UIComponents.PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(UIComponents.LABEL_FONT);
        descLabel.setForeground(UIComponents.SECONDARY_COLOR);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showPanel(targetPanel);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(236, 240, 241));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
    
    public void refreshDashboard() {
        contentPanel.remove(0);
        contentPanel.add(createDashboardPanel(), "dashboard", 0);
        cardLayout.show(contentPanel, "dashboard");
    }
}
