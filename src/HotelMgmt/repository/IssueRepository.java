package HotelMgmt.repository;

import HotelMgmt.model.Issue;
import HotelMgmt.constants.IssueStatus;
import HotelMgmt.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Issue data management.
 * Handles CRUD operations for customer issues.
 */
public class IssueRepository implements Repository<Issue> {
    
    private static final String FILE_PATH = "data/issues.txt";
    private List<Issue> issues;
    private static IssueRepository instance;
    
    private IssueRepository() {
        this.issues = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized IssueRepository getInstance() {
        if (instance == null) {
            instance = new IssueRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        issues.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                Issue issue = Issue.fromFileString(line);
                if (issue != null) {
                    issues.add(issue);
                }
            } catch (Exception e) {
                System.err.println("Error parsing issue: " + line);
            }
        }
    }
    
    private void saveToFile() {
        List<String> lines = issues.stream()
            .map(Issue::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(Issue entity) {
        issues.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(Issue entity) {
        for (int i = 0; i < issues.size(); i++) {
            if (issues.get(i).getId().equals(entity.getId())) {
                issues.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        issues.removeIf(i -> i.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<Issue> findById(String id) {
        return issues.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<Issue> findAll() {
        return new ArrayList<>(issues);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find issues by customer ID
     */
    public List<Issue> findByCustomerId(String customerId) {
        return issues.stream()
            .filter(i -> i.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find issues by booking ID
     */
    public List<Issue> findByBookingId(String bookingId) {
        return issues.stream()
            .filter(i -> i.getBookingId().equals(bookingId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find issues by status
     */
    public List<Issue> findByStatus(IssueStatus status) {
        return issues.stream()
            .filter(i -> i.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Find open issues
     */
    public List<Issue> findOpen() {
        return issues.stream()
            .filter(i -> i.getStatus() == IssueStatus.OPEN || i.getStatus() == IssueStatus.IN_PROGRESS)
            .sorted(Comparator.comparing(Issue::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Find issues assigned to a scheduler
     */
    public List<Issue> findBySchedulerId(String schedulerId) {
        return issues.stream()
            .filter(i -> i.getAssignedSchedulerId().equals(schedulerId))
            .collect(Collectors.toList());
    }
}
