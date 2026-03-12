package HotelMgmt.GUI.manager;

import HotelMgmt.GUI.UIComponents;
import HotelMgmt.model.*;
import HotelMgmt.constants.HallType;
import HotelMgmt.services.BookingService;
import HotelMgmt.services.PaymentService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sales Report Frame for manager to view sales statistics and reports.
 */
public class SalesReportFrame extends JPanel {
    
    private Manager manager;
    private ManagerDashboard parent;
    private BookingService bookingService;
    private PaymentService paymentService;
    
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    public SalesReportFrame(Manager manager, ManagerDashboard parent) {
        this.manager = manager;
        this.parent = parent;
        this.bookingService = new BookingService();
        this.paymentService = new PaymentService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIComponents.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        JLabel headerLabel = UIComponents.createTitleLabel("Sales Reports");
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Period filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        filterPanel.add(UIComponents.createLabel("Period:"));
        String[] periods = {"All Time", "Today", "This Week", "This Month"};
        periodCombo = UIComponents.createComboBox(periods);
        periodCombo.addActionListener(e -> loadSales());
        filterPanel.add(periodCombo);
        
        JButton exportBtn = UIComponents.createPrimaryButton("Export Report");
        exportBtn.addActionListener(e -> exportReport());
        filterPanel.add(exportBtn);
        
        JButton refreshBtn = UIComponents.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadSales());
        filterPanel.add(refreshBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Sales table
        JPanel tablePanel = UIComponents.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        String[] columns = {"Payment ID", "Booking ID", "Customer", "Hall Type", "Amount (RM)", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = UIComponents.createTable(tableModel);
        JScrollPane scrollPane = UIComponents.createScrollPane(salesTable);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        loadSales();
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(UIComponents.BACKGROUND_COLOR);
        
        double totalRevenue = paymentService.getTotalRevenue();
        List<Payment> payments = paymentService.getAllPayments();
        int totalTransactions = payments.size();
        double avgTransaction = totalTransactions > 0 ? totalRevenue / totalTransactions : 0;
        
        // Group by hall type
        Map<String, Long> hallTypeCount = payments.stream()
            .collect(Collectors.groupingBy(p -> {
                Booking booking = bookingService.getBookingById(p.getBookingId()).orElse(null);
                return booking != null ? booking.getHallName() : "Unknown";
            }, Collectors.counting()));
        String topHall = hallTypeCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        
        panel.add(createSummaryCard("Total Revenue", String.format("RM %.2f", totalRevenue), UIComponents.SUCCESS_COLOR));
        panel.add(createSummaryCard("Transactions", String.valueOf(totalTransactions), UIComponents.PRIMARY_COLOR));
        panel.add(createSummaryCard("Avg Transaction", String.format("RM %.2f", avgTransaction), new Color(142, 68, 173)));
        panel.add(createSummaryCard("Top Hall", topHall, new Color(230, 126, 34)));
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = UIComponents.createCardPanel();
        card.setLayout(new GridLayout(2, 1));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIComponents.LABEL_FONT);
        titleLabel.setForeground(UIComponents.SECONDARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel);
        
        return card;
    }
    
    private void loadSales() {
        tableModel.setRowCount(0);
        String period = (String) periodCombo.getSelectedItem();
        
        List<Payment> payments = paymentService.getAllPayments();
        LocalDate today = LocalDate.now();
        
        for (Payment payment : payments) {
            LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
            
            boolean include = false;
            switch (period) {
                case "All Time":
                    include = true;
                    break;
                case "Today":
                    include = paymentDate.equals(today);
                    break;
                case "This Week":
                    include = paymentDate.isAfter(today.minusDays(7));
                    break;
                case "This Month":
                    include = paymentDate.getMonth() == today.getMonth() && 
                              paymentDate.getYear() == today.getYear();
                    break;
            }
            
            if (include) {
                Booking booking = bookingService.getBookingById(payment.getBookingId()).orElse(null);
                String customerName = booking != null ? booking.getCustomerName() : "Unknown";
                String hallName = booking != null ? booking.getHallName() : "Unknown";
                
                tableModel.addRow(new Object[]{
                    payment.getId(),
                    payment.getBookingId(),
                    customerName,
                    hallName,
                    String.format("%.2f", payment.getAmount()),
                    payment.getPaymentDate().format(formatter)
                });
            }
        }
    }
    
    private void exportReport() {
        StringBuilder report = new StringBuilder();
        report.append("HALL BOOKING SYSTEM - SALES REPORT\n");
        report.append("Generated: ").append(LocalDate.now().format(formatter)).append("\n");
        report.append("=".repeat(50)).append("\n\n");
        
        report.append("SUMMARY\n");
        report.append("-".repeat(30)).append("\n");
        report.append(String.format("Total Revenue: RM %.2f\n", paymentService.getTotalRevenue()));
        report.append(String.format("Total Transactions: %d\n\n", paymentService.getAllPayments().size()));
        
        report.append("TRANSACTION DETAILS\n");
        report.append("-".repeat(30)).append("\n");
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            report.append(String.format("Payment: %s | Booking: %s | Customer: %s | Amount: RM %s | Date: %s\n",
                tableModel.getValueAt(i, 0),
                tableModel.getValueAt(i, 1),
                tableModel.getValueAt(i, 2),
                tableModel.getValueAt(i, 4),
                tableModel.getValueAt(i, 5)
            ));
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Sales Report", JOptionPane.PLAIN_MESSAGE);
    }
}
