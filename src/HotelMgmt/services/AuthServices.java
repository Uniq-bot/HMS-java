package HotelMgmt.services;

import HotelMgmt.model.*;
import HotelMgmt.constants.*;
import HotelMgmt.repository.UserRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.ValidationUtil;

import java.util.Optional;

/**
 * Service class for authentication and user registration.
 * Handles login, registration, and session management.
 */
public class AuthServices {
    
    private static User currentUser = null;
    private final UserRepository userRepository;
    
    public AuthServices() {
        this.userRepository = UserRepository.getInstance();
    }
    
    /**
     * Authenticate user with email and password
     */
    public User login(String email, String password) {
        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            return null;
        }
        
        // Hardcoded admin credentials
        if (email.equals("admin@hallbooking.com") && password.equals("admin123")) {
            currentUser = new Administrator("ADM001", "System Admin", "admin@hallbooking.com", "admin123", UserStatus.ACTIVE, "0123456789");
            return currentUser;
        }
        
        // Hardcoded manager credentials
        if (email.equals("manager@hallbooking.com") && password.equals("manager123")) {
            currentUser = new Manager("MGR001", "Operations Manager", "manager@hallbooking.com", "manager123", UserStatus.ACTIVE, "0123456790");
            return currentUser;
        }
        
        // Hardcoded scheduler credentials
        if (email.equals("scheduler@hallbooking.com") && password.equals("scheduler123")) {
            currentUser = new Scheduler("SCH001", "Hall Scheduler", "scheduler@hallbooking.com", "scheduler123", UserStatus.ACTIVE, "0123456791");
            return currentUser;
        }
        
        // Check database for other users
        Optional<User> user = userRepository.authenticate(email, password);
        if (user.isPresent()) {
            currentUser = user.get();
            return currentUser;
        }
        return null;
    }
    
    /**
     * Register a new customer
     */
    public Customer registerCustomer(String name, String email, String password, String phone) {
        // Validate inputs
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name. Name must be 2-100 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        
        String id = IdGenerator.generateCustomerId();
        Customer customer = new Customer(id, name, email, password, UserStatus.ACTIVE, phone, "");
        userRepository.save(customer);
        return customer;
    }
    
    /**
     * Register a new scheduler (admin only)
     */
    public Scheduler registerScheduler(String name, String email, String password, String phone) {
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        
        String id = IdGenerator.generateSchedulerId();
        Scheduler scheduler = new Scheduler(id, name, email, password, UserStatus.ACTIVE, phone);
        userRepository.save(scheduler);
        return scheduler;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Get currently logged in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Set current user (for session management)
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Update user profile
     */
    public void updateProfile(User user, String name, String phone) {
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name.");
        }
        user.setName(name);
        user.setPhone(phone);
        userRepository.update(user);
        if (currentUser != null && currentUser.getId().equals(user.getId())) {
            currentUser = user;
        }
    }
    
    /**
     * Change password
     */
    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 4 characters.");
        }
        user.setPassword(newPassword);
        userRepository.update(user);
    }
}
