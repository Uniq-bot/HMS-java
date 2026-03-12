package HotelMgmt.GUI.Scheduler;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.MaintenanceStatus;
import HotelMgmt.services.MaintenanceService;
import HotelMgmt.services.HallServices;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Maintenance Frame for scheduler to schedule and manage hall maintenance.
 */
public class MaintenanceFrame extends JPanel {
    
    private Scheduler scheduler;
    private SchedulerDashboard parent;
    private MaintenanceService maintenanceService;
    private HallServices hallService;
    
    private JTable maintenanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    public MaintenanceFrame(Scheduler scheduler, SchedulerDashboard parent) {
        this.scheduler = scheduler;
        this.parent = parent;
        this.maintenanceService = new MaintenanceService();
        this.hallService = new HallServices();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        JLabel headerLabel = UIComponents.createTitleLabel("Maintenance Management");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        String[] filters = {"All", "Scheduled", "In Progress", "Completed", "Cancelled"};
        filterCombo = UIComponents.createComboBox(filters);
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.addActionListener(e -> loadMaintenance());
        rightPanel.add(filterCombo);
        
        JButton addBtn = UIComponents.createPrimaryButton("+ Schedule Maintenance");
        addBtn.addActionListener(e -> showScheduleDialog());
        rightPanel.add(addBtn);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] columns = {"ID", "Hall", "Description", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        maintenanceTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(maintenanceTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton startBtn = UIComponents.createPrimaryButton("Start Maintenance");
        startBtn.addActionListener(e -> startMaintenance());
        buttonPanel.add(startBtn);
        
        JButton completeBtn = UIComponents.createSuccessButton("Mark Complete");
        completeBtn.addActionListener(e -> completeMaintenance());
        buttonPanel.add(completeBtn);
        
        JButton cancelBtn = UIComponents.createDangerButton("Cancel");
        cancelBtn.addActionListener(e -> cancelMaintenance());
        buttonPanel.add(cancelBtn);
        
        JButton refreshBtn = UIComponents.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadMaintenance());
        buttonPanel.add(refreshBtn);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        loadMaintenance();
    }
    
    private void loadMaintenance() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();
        
        List<Maintenance> maintenanceList = maintenanceService.getAllMaintenance();
        
        for (Maintenance m : maintenanceList) {
            if (matchesFilter(m, filter)) {
                String remarks = m.getRemarks();
                tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getHallName(),
                    remarks.length() > 40 ? remarks.substring(0, 40) + "..." : remarks,
                    m.getStartDateTime().toLocalDate().format(formatter),
                    m.getEndDateTime().toLocalDate().format(formatter),
                    m.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private boolean matchesFilter(Maintenance m, String filter) {
        if ("All".equals(filter)) return true;
        return m.getStatus().getDisplayName().equalsIgnoreCase(filter);
    }
    
    private void showScheduleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Maintenance", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Hall selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UIComponents.createLabel("Select Hall:"), gbc);
        gbc.gridx = 1;
        JComboBox<HallItem> hallCombo = new JComboBox<>();
        hallCombo.setFont(UIComponents.LABEL_FONT);
        List<Hall> halls = hallService.getAllHalls();
        for (Hall hall : halls) {
            hallCombo.addItem(new HallItem(hall));
        }
        panel.add(hallCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(UIComponents.createLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = UIComponents.createTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // Start Date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIComponents.createLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        JPanel startDatePanel = createDatePanel();
        panel.add(startDatePanel, gbc);
        
        // End Date
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(UIComponents.createLabel("End Date:"), gbc);
        gbc.gridx = 1;
        JPanel endDatePanel = createDatePanel();
        panel.add(endDatePanel, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = UIComponents.createPrimaryButton("Schedule");
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        saveBtn.addActionListener(e -> {
            HallItem selectedHall = (HallItem) hallCombo.getSelectedItem();
            if (selectedHall == null) {
                UIComponents.showError(dialog, "Please select a hall.");
                return;
            }
            
            String description = descArea.getText().trim();
            if (description.isEmpty()) {
                UIComponents.showError(dialog, "Please enter description.");
                return;
            }
            
            LocalDate startDate = getDateFromPanel(startDatePanel);
            LocalDate endDate = getDateFromPanel(endDatePanel);
            
            if (startDate.isAfter(endDate)) {
                UIComponents.showError(dialog, "End date must be after start date.");
                return;
            }
            
            // Convert LocalDate to LocalDateTime (start of day)
            java.time.LocalDateTime startDateTime = startDate.atTime(8, 0);
            java.time.LocalDateTime endDateTime = endDate.atTime(18, 0);
            
            maintenanceService.scheduleMaintenance(
                selectedHall.getHall().getId(),
                startDateTime,
                endDateTime,
                description,
                scheduler.getId()
            );
            
            UIComponents.showSuccess(dialog, "Maintenance scheduled successfully!");
            dialog.dispose();
            loadMaintenance();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private JPanel createDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // Year
        Integer[] years = new Integer[5];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) years[i] = currentYear + i;
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setFont(UIComponents.LABEL_FONT);
        panel.add(yearCombo);
        
        // Month
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthCombo.setFont(UIComponents.LABEL_FONT);
        panel.add(monthCombo);
        
        // Day
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;
        JComboBox<Integer> dayCombo = new JComboBox<>(days);
        dayCombo.setSelectedIndex(LocalDate.now().getDayOfMonth() - 1);
        dayCombo.setFont(UIComponents.LABEL_FONT);
        panel.add(dayCombo);
        
        return panel;
    }
    
    private LocalDate getDateFromPanel(JPanel datePanel) {
        Component[] components = datePanel.getComponents();
        JComboBox<Integer> yearCombo = (JComboBox<Integer>) components[0];
        JComboBox<String> monthCombo = (JComboBox<String>) components[1];
        JComboBox<Integer> dayCombo = (JComboBox<Integer>) components[2];
        
        int year = (Integer) yearCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1;
        int day = (Integer) dayCombo.getSelectedItem();
        
        return LocalDate.of(year, month, day);
    }
    
    private void startMaintenance() {
        int row = maintenanceTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a maintenance entry.");
            return;
        }
        
        String maintenanceId = tableModel.getValueAt(row, 0).toString();
        try {
            maintenanceService.startMaintenance(maintenanceId);
            loadMaintenance();
            UIComponents.showSuccess(this, "Maintenance started!");
        } catch (IllegalStateException e) {
            UIComponents.showError(this, e.getMessage());
        }
    }
    
    private void completeMaintenance() {
        int row = maintenanceTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a maintenance entry.");
            return;
        }
        
        String maintenanceId = tableModel.getValueAt(row, 0).toString();
        try {
            maintenanceService.completeMaintenance(maintenanceId);
            loadMaintenance();
            UIComponents.showSuccess(this, "Maintenance completed!");
        } catch (IllegalStateException e) {
            UIComponents.showError(this, e.getMessage());
        }
    }
    
    private void cancelMaintenance() {
        int row = maintenanceTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a maintenance entry.");
            return;
        }
        
        String maintenanceId = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this maintenance?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                maintenanceService.cancelMaintenance(maintenanceId);
                loadMaintenance();
                UIComponents.showSuccess(this, "Maintenance cancelled!");
            } catch (IllegalStateException e) {
                UIComponents.showError(this, e.getMessage());
            }
        }
    }
    
    // Helper class for hall combo box
    private static class HallItem {
        private Hall hall;
        
        public HallItem(Hall hall) {
            this.hall = hall;
        }
        
        public Hall getHall() {
            return hall;
        }
        
        @Override
        public String toString() {
            return hall.getName() + " (" + hall.getType().getDisplayName() + ")";
        }
    }
}
