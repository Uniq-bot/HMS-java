package HotelMgmt.repository;

import HotelMgmt.model.*;
import HotelMgmt.constants.UserRole;
import HotelMgmt.constants.UserStatus;
import HotelMgmt.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for User data management.
 * Handles CRUD operations for all user types (Customer, Scheduler, Admin, Manager).
 */
public class UserRepository implements Repository<User> {
    
    private static final String FILE_PATH = "data/users.txt";
    private List<User> users;
    private static UserRepository instance;
    
    private UserRepository() {
        this.users = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        users.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            User user = parseUser(line);
            if (user != null) {
                users.add(user);
            }
        }
    }
    
    private User parseUser(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        
        String roleStr = parts[4].trim();
        UserRole role = UserRole.fromString(roleStr);
        
        switch (role) {
            case CUSTOMER:
                return Customer.fromFileString(line);
            case SCHEDULER:
                return Scheduler.fromFileString(line);
            case ADMIN:
                return Administrator.fromFileString(line);
            case MANAGER:
                return Manager.fromFileString(line);
            default:
                return Customer.fromFileString(line);
        }
    }
    
    private void saveToFile() {
        List<String> lines = users.stream()
            .map(User::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(User entity) {
        users.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(User entity) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(entity.getId())) {
                users.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        users.removeIf(u -> u.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<User> findById(String id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }
    
    /**
     * Authenticate user
     */
    public Optional<User> authenticate(String email, String password) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && 
                        u.getPassword().equals(password) &&
                        u.isActive())
            .findFirst();
    }
    
    /**
     * Find all users by role
     */
    public List<User> findByRole(UserRole role) {
        return users.stream()
            .filter(u -> u.getRole() == role)
            .collect(Collectors.toList());
    }
    
    /**
     * Find all customers
     */
    public List<Customer> findAllCustomers() {
        return users.stream()
            .filter(u -> u instanceof Customer)
            .map(u -> (Customer) u)
            .collect(Collectors.toList());
    }
    
    /**
     * Find all schedulers
     */
    public List<Scheduler> findAllSchedulers() {
        return users.stream()
            .filter(u -> u instanceof Scheduler)
            .map(u -> (Scheduler) u)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return users.stream()
            .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
    
    /**
     * Block user
     */
    public void blockUser(String userId) {
        findById(userId).ifPresent(user -> {
            user.setStatus(UserStatus.BLOCKED);
            update(user);
        });
    }
    
    /**
     * Unblock user
     */
    public void unblockUser(String userId) {
        findById(userId).ifPresent(user -> {
            user.setStatus(UserStatus.ACTIVE);
            update(user);
        });
    }
}
