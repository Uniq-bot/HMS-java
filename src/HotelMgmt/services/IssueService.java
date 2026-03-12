package HotelMgmt.services;

import HotelMgmt.model.Issue;
import HotelMgmt.model.Booking;
import HotelMgmt.constants.IssueStatus;
import HotelMgmt.repository.IssueRepository;
import HotelMgmt.repository.BookingRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Issue management operations.
 * Handles customer issues and complaints.
 */
public class IssueService {
    
    private final IssueRepository issueRepository;
    private final BookingRepository bookingRepository;
    
    public IssueService() {
        this.issueRepository = IssueRepository.getInstance();
        this.bookingRepository = BookingRepository.getInstance();
    }
    
    /**
     * Create a new issue
     */
    public Issue createIssue(String bookingId, String customerId, String customerName, 
                            String hallName, String description) {
        if (ValidationUtil.isEmpty(description)) {
            throw new IllegalArgumentException("Issue description is required.");
        }
        
        String issueId = IdGenerator.generateIssueId();
        Issue issue = new Issue(
            issueId,
            bookingId,
            customerId,
            customerName,
            hallName,
            ValidationUtil.sanitize(description)
        );
        
        issueRepository.save(issue);
        return issue;
    }
    
    /**
     * Update issue status
     */
    public void updateIssueStatus(String issueId, IssueStatus status) {
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setStatus(status);
            if (status == IssueStatus.DONE || status == IssueStatus.CLOSED) {
                issue.setResolvedAt(LocalDateTime.now());
            }
            issueRepository.update(issue);
        }
    }
    
    /**
     * Assign scheduler to issue
     */
    public void assignScheduler(String issueId, String schedulerId) {
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setAssignedSchedulerId(schedulerId);
            issue.setStatus(IssueStatus.IN_PROGRESS);
            issueRepository.update(issue);
        }
    }
    
    /**
     * Respond to an issue
     */
    public void respondToIssue(String issueId, String response) {
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setResponse(ValidationUtil.sanitize(response));
            issueRepository.update(issue);
        }
    }
    
    /**
     * Resolve an issue
     */
    public void resolveIssue(String issueId, String response) {
        Optional<Issue> issueOpt = issueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.resolve(ValidationUtil.sanitize(response));
            issueRepository.update(issue);
        }
    }
    
    /**
     * Close an issue
     */
    public void closeIssue(String issueId) {
        updateIssueStatus(issueId, IssueStatus.CLOSED);
    }
    
    /**
     * Cancel an issue
     */
    public void cancelIssue(String issueId) {
        updateIssueStatus(issueId, IssueStatus.CANCELLED);
    }
    
    /**
     * Get all issues
     */
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }
    
    /**
     * Get issue by ID
     */
    public Optional<Issue> getIssueById(String id) {
        return issueRepository.findById(id);
    }
    
    /**
     * Get issues by customer
     */
    public List<Issue> getIssuesByCustomer(String customerId) {
        return issueRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get open issues
     */
    public List<Issue> getOpenIssues() {
        return issueRepository.findOpen();
    }
    
    /**
     * Get issues by status
     */
    public List<Issue> getIssuesByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status);
    }
    
    /**
     * Get issues assigned to a scheduler
     */
    public List<Issue> getIssuesByScheduler(String schedulerId) {
        return issueRepository.findBySchedulerId(schedulerId);
    }
    
    /**
     * Delete an issue
     */
    public void deleteIssue(String issueId) {
        issueRepository.delete(issueId);
    }
    
    /**
     * Refresh issue data
     */
    public void refresh() {
        issueRepository.refresh();
    }
    
    /**
     * Get resolved issues (DONE or CLOSED)
     */
    public List<Issue> getResolvedIssues() {
        return issueRepository.findAll().stream()
            .filter(i -> i.getStatus() == IssueStatus.DONE || i.getStatus() == IssueStatus.CLOSED)
            .collect(java.util.stream.Collectors.toList());
    }
}
