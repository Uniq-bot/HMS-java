package HotelMgmt.services;

import HotelMgmt.model.Maintenance;
import HotelMgmt.model.Hall;
import HotelMgmt.constants.MaintenanceStatus;
import HotelMgmt.repository.MaintenanceRepository;
import HotelMgmt.repository.HallRepository;
import HotelMgmt.repository.BookingRepository;
import HotelMgmt.util.IdGenerator;
import HotelMgmt.util.DateUtil;
import HotelMgmt.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Maintenance management operations.
 * Handles maintenance scheduling for halls.
 */
public class MaintenanceService {
    
    private final MaintenanceRepository maintenanceRepository;
    private final HallRepository hallRepository;
    private final BookingRepository bookingRepository;
    
    public MaintenanceService() {
        this.maintenanceRepository = MaintenanceRepository.getInstance();
        this.hallRepository = HallRepository.getInstance();
        this.bookingRepository = BookingRepository.getInstance();
    }
    
    /**
     * Schedule maintenance for a hall
     */
    public Maintenance scheduleMaintenance(String hallId, LocalDateTime startDateTime, 
                                           LocalDateTime endDateTime, String remarks, 
                                           String schedulerId) {
        // Validate hall exists
        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (!hallOpt.isPresent()) {
            throw new IllegalArgumentException("Hall not found.");
        }
        Hall hall = hallOpt.get();
        
        // Validate time
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end times are required.");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        
        // Check for booking conflicts
        if (bookingRepository.hasConflict(hallId, startDateTime, endDateTime, null)) {
            throw new IllegalArgumentException("Cannot schedule maintenance. There are bookings during this period.");
        }
        
        // Check for maintenance conflicts
        if (maintenanceRepository.hasConflict(hallId, startDateTime, endDateTime, null)) {
            throw new IllegalArgumentException("Maintenance already scheduled for this period.");
        }
        
        String maintenanceId = IdGenerator.generateMaintenanceId();
        Maintenance maintenance = new Maintenance(
            maintenanceId,
            hallId,
            hall.getName(),
            startDateTime,
            endDateTime,
            ValidationUtil.sanitize(remarks),
            schedulerId
        );
        
        maintenanceRepository.save(maintenance);
        return maintenance;
    }
    
    /**
     * Update maintenance
     */
    public void updateMaintenance(String maintenanceId, LocalDateTime startDateTime, 
                                  LocalDateTime endDateTime, String remarks) {
        Optional<Maintenance> maintenanceOpt = maintenanceRepository.findById(maintenanceId);
        if (!maintenanceOpt.isPresent()) {
            throw new IllegalArgumentException("Maintenance record not found.");
        }
        
        Maintenance maintenance = maintenanceOpt.get();
        
        // Check for conflicts if time changed
        if (!maintenance.getStartDateTime().equals(startDateTime) || 
            !maintenance.getEndDateTime().equals(endDateTime)) {
            
            if (bookingRepository.hasConflict(maintenance.getHallId(), startDateTime, endDateTime, null)) {
                throw new IllegalArgumentException("Cannot reschedule. There are bookings during this period.");
            }
            
            if (maintenanceRepository.hasConflict(maintenance.getHallId(), startDateTime, endDateTime, maintenanceId)) {
                throw new IllegalArgumentException("Maintenance already scheduled for this period.");
            }
        }
        
        maintenance.setStartDateTime(startDateTime);
        maintenance.setEndDateTime(endDateTime);
        maintenance.setRemarks(ValidationUtil.sanitize(remarks));
        maintenanceRepository.update(maintenance);
    }
    
    /**
     * Update maintenance status
     */
    public void updateMaintenanceStatus(String maintenanceId, MaintenanceStatus status) {
        Optional<Maintenance> maintenanceOpt = maintenanceRepository.findById(maintenanceId);
        if (maintenanceOpt.isPresent()) {
            Maintenance maintenance = maintenanceOpt.get();
            maintenance.setStatus(status);
            maintenanceRepository.update(maintenance);
        }
    }
    
    /**
     * Cancel maintenance
     */
    public void cancelMaintenance(String maintenanceId) {
        updateMaintenanceStatus(maintenanceId, MaintenanceStatus.CANCELLED);
    }
    
    /**
     * Complete maintenance
     */
    public void completeMaintenance(String maintenanceId) {
        updateMaintenanceStatus(maintenanceId, MaintenanceStatus.COMPLETED);
    }
    
    /**
     * Start maintenance
     */
    public void startMaintenance(String maintenanceId) {
        updateMaintenanceStatus(maintenanceId, MaintenanceStatus.IN_PROGRESS);
    }
    
    /**
     * Get all maintenance records
     */
    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }
    
    /**
     * Get maintenance by ID
     */
    public Optional<Maintenance> getMaintenanceById(String id) {
        return maintenanceRepository.findById(id);
    }
    
    /**
     * Get maintenance for a hall
     */
    public List<Maintenance> getMaintenanceByHall(String hallId) {
        return maintenanceRepository.findByHallId(hallId);
    }
    
    /**
     * Get upcoming maintenance for a hall
     */
    public List<Maintenance> getUpcomingMaintenanceByHall(String hallId) {
        return maintenanceRepository.findUpcomingByHall(hallId);
    }
    
    /**
     * Get active maintenance
     */
    public List<Maintenance> getActiveMaintenance() {
        return maintenanceRepository.findActive();
    }
    
    /**
     * Get maintenance by scheduler
     */
    public List<Maintenance> getMaintenanceByScheduler(String schedulerId) {
        return maintenanceRepository.findBySchedulerId(schedulerId);
    }
    
    /**
     * Delete maintenance record
     */
    public void deleteMaintenance(String maintenanceId) {
        maintenanceRepository.delete(maintenanceId);
    }
    
    /**
     * Refresh maintenance data
     */
    public void refresh() {
        maintenanceRepository.refresh();
    }
    
    /**
     * Get pending maintenance (scheduled)
     */
    public List<Maintenance> getPendingMaintenance() {
        return maintenanceRepository.findAll().stream()
            .filter(m -> m.getStatus() == MaintenanceStatus.SCHEDULED)
            .collect(java.util.stream.Collectors.toList());
    }
}
