package HotelMgmt.GUI.Scheduler;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.HallType;
import HotelMgmt.services.HallServices;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Hall Management Frame for scheduler to add, edit, and manage halls.
 */
public class HallManagementFrame extends JPanel {
    
    private Scheduler scheduler;
    private SchedulerDashboard parent;
    private HallServices hallService;
    
    private JTable hallTable;
    private DefaultTableModel tableModel;
    
    public HallManagementFrame(Scheduler scheduler, SchedulerDashboard parent) {
        this.scheduler = scheduler;
        this.parent = parent;
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
        
        JLabel headerLabel = UIComponents.createTitleLabel("Hall Management");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JButton addBtn = UIComponents.createPrimaryButton("+ Add New Hall");
        addBtn.addActionListener(e -> showAddHallDialog());
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Hall table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] columns = {"Hall ID", "Name", "Type", "Capacity", "Price/Hour (RM)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hallTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(hallTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton editBtn = UIComponents.createPrimaryButton("Edit Hall");
        editBtn.addActionListener(e -> showEditHallDialog());
        buttonPanel.add(editBtn);
        
        JButton toggleBtn = UIComponents.createSecondaryButton("Toggle Availability");
        toggleBtn.addActionListener(e -> toggleHallAvailability());
        buttonPanel.add(toggleBtn);
        
        JButton deleteBtn = UIComponents.createDangerButton("Delete Hall");
        deleteBtn.addActionListener(e -> deleteHall());
        buttonPanel.add(deleteBtn);
        
        JButton refreshBtn = UIComponents.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadHalls());
        buttonPanel.add(refreshBtn);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        loadHalls();
    }
    
    private void loadHalls() {
        tableModel.setRowCount(0);
        List<Hall> halls = hallService.getAllHalls();
        
        for (Hall hall : halls) {
            tableModel.addRow(new Object[]{
                hall.getId(),
                hall.getName(),
                hall.getType().getDisplayName(),
                hall.getCapacity(),
                String.format("%.2f", hall.getPricePerHour()),
                hall.isAvailable() ? "Available" : "Unavailable"
            });
        }
    }
    
    private void showAddHallDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Hall", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UIComponents.createLabel("Hall Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = UIComponents.createTextField();
        panel.add(nameField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(UIComponents.createLabel("Hall Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<HallType> typeCombo = new JComboBox<>(HallType.values());
        typeCombo.setFont(UIComponents.LABEL_FONT);
        panel.add(typeCombo, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIComponents.createLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(50, 10, 1000, 10));
        capacitySpinner.setFont(UIComponents.LABEL_FONT);
        panel.add(capacitySpinner, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(UIComponents.createLabel("Price/Hour (RM):"), gbc);
        gbc.gridx = 1;
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(100.0, 10.0, 10000.0, 10.0));
        priceSpinner.setFont(UIComponents.LABEL_FONT);
        panel.add(priceSpinner, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(UIComponents.createLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = UIComponents.createTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = UIComponents.createPrimaryButton("Save");
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                UIComponents.showError(dialog, "Please enter hall name.");
                return;
            }
            
            HallType type = (HallType) typeCombo.getSelectedItem();
            int capacity = (Integer) capacitySpinner.getValue();
            double price = (Double) priceSpinner.getValue();
            String description = descArea.getText().trim();
            
            hallService.addHall(name, type, capacity, price, description);
            UIComponents.showSuccess(dialog, "Hall added successfully!");
            dialog.dispose();
            loadHalls();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showEditHallDialog() {
        int row = hallTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a hall to edit.");
            return;
        }
        
        String hallId = tableModel.getValueAt(row, 0).toString();
        Hall hall = hallService.getHallById(hallId).orElse(null);
        
        if (hall == null) {
            UIComponents.showError(this, "Hall not found.");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Hall", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UIComponents.createLabel("Hall Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = UIComponents.createTextField();
        nameField.setText(hall.getName());
        panel.add(nameField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(UIComponents.createLabel("Hall Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<HallType> typeCombo = new JComboBox<>(HallType.values());
        typeCombo.setSelectedItem(hall.getType());
        typeCombo.setFont(UIComponents.LABEL_FONT);
        panel.add(typeCombo, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIComponents.createLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(hall.getCapacity(), 10, 1000, 10));
        capacitySpinner.setFont(UIComponents.LABEL_FONT);
        panel.add(capacitySpinner, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(UIComponents.createLabel("Price/Hour (RM):"), gbc);
        gbc.gridx = 1;
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(hall.getPricePerHour(), 10.0, 10000.0, 10.0));
        priceSpinner.setFont(UIComponents.LABEL_FONT);
        panel.add(priceSpinner, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(UIComponents.createLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = UIComponents.createTextArea(3, 20);
        descArea.setText(hall.getDescription());
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = UIComponents.createPrimaryButton("Update");
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                UIComponents.showError(dialog, "Please enter hall name.");
                return;
            }
            
            hall.setName(name);
            hall.setType((HallType) typeCombo.getSelectedItem());
            hall.setCapacity((Integer) capacitySpinner.getValue());
            hall.setPricePerHour((Double) priceSpinner.getValue());
            hall.setDescription(descArea.getText().trim());
            
            hallService.updateHall(hall);
            UIComponents.showSuccess(dialog, "Hall updated successfully!");
            dialog.dispose();
            loadHalls();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void toggleHallAvailability() {
        int row = hallTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a hall.");
            return;
        }
        
        String hallId = tableModel.getValueAt(row, 0).toString();
        Hall hall = hallService.getHallById(hallId).orElse(null);
        
        if (hall != null) {
            hall.setAvailable(!hall.isAvailable());
            hallService.updateHall(hall);
            loadHalls();
            UIComponents.showSuccess(this, "Hall availability updated!");
        }
    }
    
    private void deleteHall() {
        int row = hallTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a hall to delete.");
            return;
        }
        
        String hallId = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this hall?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            hallService.deleteHall(hallId);
            loadHalls();
            UIComponents.showSuccess(this, "Hall deleted successfully!");
        }
    }
}
