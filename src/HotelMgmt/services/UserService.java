package HotelMgmt.services;

import HotelMgmt.model.*;
import HotelMgmt.constants.*;
import HotelMgmt.repository.UserRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for User management operations.
 * Handles user CRUD operations and queries.
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService() {
        this.userRepository = UserRepository.getInstance();
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return userRepository.findAllCustomers();
    }
    
    /**
     * Get all schedulers
     */
    public List<Scheduler> getAllSchedulers() {
        return userRepository.findAllSchedulers();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Block a user
     */
    public void blockUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.BLOCKED);
            userRepository.update(user);
        }
    }
    
    /**
     * Unblock a user
     */
    public void unblockUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.ACTIVE);
            userRepository.update(user);
        }
    }
    
    /**
     * Delete a user
     */
    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }
    
    /**
     * Update user
     */
    public void updateUser(User user) {
        userRepository.update(user);
    }
    
    /**
     * Add a new scheduler
     */
    public Scheduler addScheduler(String name, String email, String password, String phone) {
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
     * Update scheduler
     */
    public void updateScheduler(Scheduler scheduler, String name, String email, String phone) {
        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        
        // Check if email is changed and new email already exists
        if (!scheduler.getEmail().equals(email) && userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        
        scheduler.setName(name);
        scheduler.setEmail(email);
        scheduler.setPhone(phone);
        userRepository.update(scheduler);
    }
    
    /**
     * Filter users by status
     */
    public List<User> filterByStatus(UserStatus status) {
        return userRepository.findAll().stream()
            .filter(u -> u.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Search users by name or email
     */
    public List<User> searchUsers(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return userRepository.findAll().stream()
            .filter(u -> u.getName().toLowerCase().contains(lowerKeyword) ||
                        u.getEmail().toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toList());
    }
    
    /**
     * Refresh user data
     */
    public void refresh() {
        userRepository.refresh();
    }
    
    /**
     * Activate a user
     */
    public void activateUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.ACTIVE);
            userRepository.update(user);
        }
    }
    
    /**
     * Deactivate a user
     */
    public void deactivateUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.INACTIVE);
            userRepository.update(user);
        }
    }
}
