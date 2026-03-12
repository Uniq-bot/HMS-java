package HotelMgmt.GUI;

import HotelMgmt.model.Customer;
import HotelMgmt.services.AuthServices;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Registration Frame for new customer accounts.
 * Provides user registration with validation.
 */
public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private AuthServices authServices;

    public RegisterFrame() {
        authServices = new AuthServices();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Hall Booking Management System - Register");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIComponents.BACKGROUND_COLOR);

        // Left panel with branding
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(UIComponents.SUCCESS_COLOR);
        leftPanel.setPreferredSize(new Dimension(400, 650));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        JLabel brandLabel = new JLabel("Join Us Today!");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Create an account to start<br/>booking halls for your events</center></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 200));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(brandLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(tagline);
        leftPanel.add(Box.createVerticalGlue());

        // Registration Card
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420, 550));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("Create Account");
        title.setFont(UIComponents.TITLE_FONT);
        title.setForeground(UIComponents.TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fill in your details to register");
        subtitle.setFont(UIComponents.LABEL_FONT);
        subtitle.setForeground(UIComponents.TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name field
        JPanel namePanel = createFieldPanel("Full Name *");
        nameField = createStyledTextField();
        namePanel.add(nameField);

        // Email field
        JPanel emailPanel = createFieldPanel("Email Address *");
        emailField = createStyledTextField();
        emailPanel.add(emailField);

        // Phone field
        JPanel phonePanel = createFieldPanel("Phone Number");
        phoneField = createStyledTextField();
        phonePanel.add(phoneField);

        // Password field
        JPanel passwordPanel = createFieldPanel("Password *");
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordPanel.add(passwordField);

        // Confirm Password field
        JPanel confirmPanel = createFieldPanel("Confirm Password *");
        confirmPasswordField = new JPasswordField();
        styleTextField(confirmPasswordField);
        confirmPanel.add(confirmPasswordField);

        // Register button
        JButton registerButton = UIComponents.createSuccessButton("Register");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back button
        JButton backButton = UIComponents.createSecondaryButton("Back to Login");
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(namePanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(emailPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(phonePanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(passwordPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(confirmPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(registerButton);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(backButton);

        // Add panels
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UIComponents.BACKGROUND_COLOR);
        centerPanel.add(card);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);

        add(splitPane);

        // Event listeners
        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    private JPanel createFieldPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(UIComponents.TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setFont(UIComponents.LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIComponents.showError(this, "Please fill in all required fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIComponents.showError(this, "Passwords do not match.");
            confirmPasswordField.setText("");
            return;
        }

        try {
            Customer customer = authServices.registerCustomer(name, email, password, phone);
            UIComponents.showSuccess(this, "Registration successful! Please login with your credentials.");
            new LoginFrame();
            dispose();
        } catch (IllegalArgumentException e) {
            UIComponents.showError(this, e.getMessage());
        }
    }
}