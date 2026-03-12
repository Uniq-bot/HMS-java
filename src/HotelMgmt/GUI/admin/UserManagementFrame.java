package HotelMgmt.GUI.admin;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;
import HotelMgmt.services.UserService;
import HotelMgmt.services.AuthServices;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * User Management Frame for admin to manage all users in the system.
 */
public class UserManagementFrame extends JPanel {
    
    private Administrator admin;
    private AdminDashboard parent;
    private UserService userService;
    private AuthServices authService;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;
    
    public UserManagementFrame(Administrator admin, AdminDashboard parent) {
        this.admin = admin;
        this.parent = parent;
        this.userService = new UserService();
        this.authService = new AuthServices();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        JLabel headerLabel = UIComponents.createTitleLabel("User Management");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        filterPanel.add(UIComponents.createLabel("Role:"));
        String[] roles = {"All", "Customer", "Scheduler", "Admin", "Manager"};
        roleFilter = UIComponents.createComboBox(roles);
        roleFilter.addActionListener(e -> loadUsers());
        filterPanel.add(roleFilter);
        
        filterPanel.add(UIComponents.createLabel("Status:"));
        String[] statuses = {"All", "Active", "Inactive", "Pending"};
        statusFilter = UIComponents.createComboBox(statuses);
        statusFilter.addActionListener(e -> loadUsers());
        filterPanel.add(statusFilter);
        
        JButton addBtn = UIComponents.createPrimaryButton("+ Add Staff");
        addBtn.addActionListener(e -> showAddStaffDialog());
        filterPanel.add(addBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] columns = {"User ID", "Name", "Email", "Phone", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(userTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewUserDetails());
        buttonPanel.add(viewBtn);
        
        JButton activateBtn = UIComponents.createSuccessButton("Activate");
        activateBtn.addActionListener(e -> activateUser());
        buttonPanel.add(activateBtn);
        
        JButton deactivateBtn = UIComponents.createDangerButton("Deactivate");
        deactivateBtn.addActionListener(e -> deactivateUser());
        buttonPanel.add(deactivateBtn);
        
        JButton deleteBtn = UIComponents.createDangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteUser());
        buttonPanel.add(deleteBtn);
        
        JButton refreshBtn = UIComponents.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsers());
        buttonPanel.add(refreshBtn);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        loadUsers();
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        String roleFilterValue = (String) roleFilter.getSelectedItem();
        String statusFilterValue = (String) statusFilter.getSelectedItem();
        
        List<User> users = userService.getAllUsers();
        
        for (User user : users) {
            if (matchesFilters(user, roleFilterValue, statusFilterValue)) {
                tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole().getDisplayName(),
                    user.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private boolean matchesFilters(User user, String role, String status) {
        boolean roleMatch = "All".equals(role) || 
            user.getRole().getDisplayName().equalsIgnoreCase(role);
        boolean statusMatch = "All".equals(status) || 
            user.getStatus().getDisplayName().equalsIgnoreCase(status);
        return roleMatch && statusMatch;
    }
    
    private void showAddStaffDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Staff", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UIComponents.createLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = UIComponents.createTextField();
        panel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(UIComponents.createLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = UIComponents.createTextField();
        panel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(UIComponents.createLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = UIComponents.createPasswordField();
        panel.add(passwordField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(UIComponents.createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = UIComponents.createTextField();
        panel.add(phoneField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(UIComponents.createLabel("Role:"), gbc);
        gbc.gridx = 1;
        String[] staffRoles = {"Scheduler", "Admin", "Manager"};
        JComboBox<String> roleCombo = UIComponents.createComboBox(staffRoles);
        panel.add(roleCombo, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = UIComponents.createPrimaryButton("Add Staff");
        JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, gbc);
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                UIComponents.showError(dialog, "Please fill all required fields.");
                return;
            }
            
            try {
                authService.registerScheduler(name, email, password, phone);
                UIComponents.showSuccess(dialog, "Staff added successfully!");
                dialog.dispose();
                loadUsers();
            } catch (IllegalArgumentException ex) {
                UIComponents.showError(dialog, ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void viewUserDetails() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a user to view.");
            return;
        }
        
        String userId = tableModel.getValueAt(row, 0).toString();
        User user = userService.getUserById(userId).orElse(null);
        
        if (user != null) {
            String details = String.format(
                "User ID: %s\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Role: %s\n" +
                "Status: %s",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getDisplayName(),
                user.getStatus().getDisplayName()
            );
            
            JOptionPane.showMessageDialog(this, details, "User Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void activateUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a user to activate.");
            return;
        }
        
        String userId = tableModel.getValueAt(row, 0).toString();
        userService.activateUser(userId);
        loadUsers();
        UIComponents.showSuccess(this, "User activated successfully!");
    }
    
    private void deactivateUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a user to deactivate.");
            return;
        }
        
        String userId = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate this user?",
            "Confirm Deactivate", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            userService.deactivateUser(userId);
            loadUsers();
            UIComponents.showSuccess(this, "User deactivated successfully!");
        }
    }
    
    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select a user to delete.");
            return;
        }
        
        String userId = tableModel.getValueAt(row, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete this user?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            userService.deleteUser(userId);
            loadUsers();
            UIComponents.showSuccess(this, "User deleted successfully!");
        }
    }
}
