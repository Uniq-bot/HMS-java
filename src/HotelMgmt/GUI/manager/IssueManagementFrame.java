package HotelMgmt.GUI.manager;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.IssueStatus;
import HotelMgmt.services.IssueService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Issue Management Frame for manager to review and respond to customer issues.
 */
public class IssueManagementFrame extends JPanel {
    
    private Manager manager;
    private ManagerDashboard parent;
    private IssueService issueService;
    
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    
    public IssueManagementFrame(Manager manager, ManagerDashboard parent) {
        this.manager = manager;
        this.parent = parent;
        this.issueService = new IssueService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        JLabel headerLabel = UIComponents.createTitleLabel("Issue Management");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        filterPanel.add(UIComponents.createLabel("Status:"));
        String[] statuses = {"All", "Open", "In Progress", "Done", "Closed", "Cancelled"};
        statusFilter = UIComponents.createComboBox(statuses);
        statusFilter.addActionListener(e -> loadIssues());
        filterPanel.add(statusFilter);
        
        JButton refreshBtn = UIComponents.createPrimaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadIssues());
        filterPanel.add(refreshBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] columns = {"Issue ID", "Customer", "Hall", "Description", "Status", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issueTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(issueTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton viewBtn = UIComponents.createPrimaryButton("View Details");
        viewBtn.addActionListener(e -> viewIssueDetails());
        buttonPanel.add(viewBtn);
        
        JButton respondBtn = UIComponents.createSuccessButton("Respond");
        respondBtn.addActionListener(e -> respondToIssue());
        buttonPanel.add(respondBtn);
        
        JButton progressBtn = UIComponents.createSecondaryButton("Mark In Progress");
        progressBtn.addActionListener(e -> updateIssueStatus(IssueStatus.IN_PROGRESS));
        buttonPanel.add(progressBtn);
        
        JButton resolveBtn = UIComponents.createSuccessButton("Mark Done");
        resolveBtn.addActionListener(e -> updateIssueStatus(IssueStatus.DONE));
        buttonPanel.add(resolveBtn);
        
        JButton closeBtn = UIComponents.createSecondaryButton("Close Issue");
        closeBtn.addActionListener(e -> updateIssueStatus(IssueStatus.CLOSED));
        buttonPanel.add(closeBtn);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        
        loadIssues();
    }
    
    private void loadIssues() {
        tableModel.setRowCount(0);
        String statusFilterValue = (String) statusFilter.getSelectedItem();
        
        List<Issue> issues = issueService.getAllIssues();
        
        for (Issue issue : issues) {
            if (matchesFilter(issue, statusFilterValue)) {
                tableModel.addRow(new Object[]{
                    issue.getId(),
                    issue.getCustomerName(),
                    issue.getHallName(),
                    issue.getDescription().length() > 40 ? 
                        issue.getDescription().substring(0, 40) + "..." : issue.getDescription(),
                    issue.getStatus().getDisplayName(),
                    issue.getCreatedAt().format(formatter)
                });
            }
        }
    }
    
    private boolean matchesFilter(Issue issue, String status) {
        if ("All".equals(status)) return true;
        return issue.getStatus().getDisplayName().equalsIgnoreCase(status);
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
                "Customer: %s (%s)\n" +
                "Hall: %s\n" +
                "Status: %s\n" +
                "Created: %s\n\n" +
                "Description:\n%s\n\n" +
                "Response:\n%s",
                issue.getId(),
                issue.getBookingId(),
                issue.getCustomerName(),
                issue.getCustomerId(),
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
            scrollPane.setPreferredSize(new Dimension(450, 350));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Issue Details", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void respondToIssue() {
        int row = issueTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select an issue to respond to.");
            return;
        }
        
        String issueId = tableModel.getValueAt(row, 0).toString();
        Issue issue = issueService.getIssueById(issueId).orElse(null);
        
        if (issue != null) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Respond to Issue", true);
            dialog.setSize(500, 350);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout(0, 15));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Issue info
            JLabel infoLabel = new JLabel("<html><b>Issue:</b> " + issue.getId() + 
                "<br><b>Customer:</b> " + issue.getCustomerName() + 
                "<br><b>Hall:</b> " + issue.getHallName() + "</html>");
            infoLabel.setFont(UIComponents.LABEL_FONT);
            panel.add(infoLabel, BorderLayout.NORTH);
            
            // Response area
            JPanel responsePanel = new JPanel(new BorderLayout(0, 10));
            responsePanel.add(UIComponents.createLabel("Your Response:"), BorderLayout.NORTH);
            JTextArea responseArea = UIComponents.createTextArea(6, 30);
            responseArea.setText(issue.getResponse());
            JScrollPane scrollPane = new JScrollPane(responseArea);
            responsePanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(responsePanel, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton saveBtn = UIComponents.createPrimaryButton("Save Response");
            JButton cancelBtn = UIComponents.createSecondaryButton("Cancel");
            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            saveBtn.addActionListener(e -> {
                String response = responseArea.getText().trim();
                if (response.isEmpty()) {
                    UIComponents.showError(dialog, "Please enter a response.");
                    return;
                }
                
                issueService.respondToIssue(issueId, response);
                UIComponents.showSuccess(dialog, "Response saved successfully!");
                dialog.dispose();
                loadIssues();
            });
            
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            dialog.add(panel);
            dialog.setVisible(true);
        }
    }
    
    private void updateIssueStatus(IssueStatus newStatus) {
        int row = issueTable.getSelectedRow();
        if (row < 0) {
            UIComponents.showError(this, "Please select an issue.");
            return;
        }
        
        String issueId = tableModel.getValueAt(row, 0).toString();
        
        try {
            issueService.updateIssueStatus(issueId, newStatus);
            loadIssues();
            UIComponents.showSuccess(this, "Issue status updated to " + newStatus.getDisplayName());
        } catch (IllegalStateException e) {
            UIComponents.showError(this, e.getMessage());
        }
    }
}
