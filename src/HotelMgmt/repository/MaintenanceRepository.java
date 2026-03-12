package HotelMgmt.repository;

import HotelMgmt.model.Maintenance;
import HotelMgmt.constants.MaintenanceStatus;
import HotelMgmt.util.FileUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Maintenance data management.
 * Handles CRUD operations for maintenance schedules.
 */
public class MaintenanceRepository implements Repository<Maintenance> {
    
    private static final String FILE_PATH = "data/maintenance.txt";
    private List<Maintenance> maintenances;
    private static MaintenanceRepository instance;
    
    private MaintenanceRepository() {
        this.maintenances = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized MaintenanceRepository getInstance() {
        if (instance == null) {
            instance = new MaintenanceRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        maintenances.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                Maintenance maintenance = Maintenance.fromFileString(line);
                if (maintenance != null) {
                    maintenances.add(maintenance);
                }
            } catch (Exception e) {
                System.err.println("Error parsing maintenance: " + line);
            }
        }
    }
    
    private void saveToFile() {
        List<String> lines = maintenances.stream()
            .map(Maintenance::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(Maintenance entity) {
        maintenances.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(Maintenance entity) {
        for (int i = 0; i < maintenances.size(); i++) {
            if (maintenances.get(i).getId().equals(entity.getId())) {
                maintenances.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        maintenances.removeIf(m -> m.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<Maintenance> findById(String id) {
        return maintenances.stream()
            .filter(m -> m.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<Maintenance> findAll() {
        return new ArrayList<>(maintenances);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find maintenance by hall ID
     */
    public List<Maintenance> findByHallId(String hallId) {
        return maintenances.stream()
            .filter(m -> m.getHallId().equals(hallId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find maintenance by scheduler ID
     */
    public List<Maintenance> findBySchedulerId(String schedulerId) {
        return maintenances.stream()
            .filter(m -> m.getSchedulerId().equals(schedulerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Find active maintenance (scheduled or in progress)
     */
    public List<Maintenance> findActive() {
        return maintenances.stream()
            .filter(m -> m.getStatus() == MaintenanceStatus.SCHEDULED || 
                        m.getStatus() == MaintenanceStatus.IN_PROGRESS)
            .sorted(Comparator.comparing(Maintenance::getStartDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Find upcoming maintenance for a hall
     */
    public List<Maintenance> findUpcomingByHall(String hallId) {
        LocalDateTime now = LocalDateTime.now();
        return maintenances.stream()
            .filter(m -> m.getHallId().equals(hallId))
            .filter(m -> m.getStartDateTime().isAfter(now))
            .filter(m -> m.getStatus() == MaintenanceStatus.SCHEDULED)
            .sorted(Comparator.comparing(Maintenance::getStartDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Check for maintenance conflicts
     */
    public boolean hasConflict(String hallId, LocalDateTime start, LocalDateTime end, String excludeMaintenanceId) {
        return maintenances.stream()
            .filter(m -> m.getHallId().equals(hallId))
            .filter(m -> excludeMaintenanceId == null || !m.getId().equals(excludeMaintenanceId))
            .filter(m -> m.getStatus() == MaintenanceStatus.SCHEDULED || m.getStatus() == MaintenanceStatus.IN_PROGRESS)
            .anyMatch(m -> m.overlaps(start, end));
    }
}
