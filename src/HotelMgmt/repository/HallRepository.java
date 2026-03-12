package HotelMgmt.repository;

import HotelMgmt.model.Hall;
import HotelMgmt.constants.HallType;
import HotelMgmt.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Hall data management.
 * Handles CRUD operations for halls.
 */
public class HallRepository implements Repository<Hall> {
    
    private static final String FILE_PATH = "data/halls.txt";
    private List<Hall> halls;
    private static HallRepository instance;
    
    private HallRepository() {
        this.halls = new ArrayList<>();
        loadFromFile();
    }
    
    public static synchronized HallRepository getInstance() {
        if (instance == null) {
            instance = new HallRepository();
        }
        return instance;
    }
    
    private void loadFromFile() {
        halls.clear();
        List<String> lines = FileUtil.read(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Hall hall = Hall.fromFileString(line);
            if (hall != null) {
                halls.add(hall);
            }
        }
    }
    
    private void saveToFile() {
        List<String> lines = halls.stream()
            .map(Hall::toFileString)
            .collect(Collectors.toList());
        FileUtil.write(FILE_PATH, lines);
    }
    
    @Override
    public void save(Hall entity) {
        halls.add(entity);
        saveToFile();
    }
    
    @Override
    public void update(Hall entity) {
        for (int i = 0; i < halls.size(); i++) {
            if (halls.get(i).getId().equals(entity.getId())) {
                halls.set(i, entity);
                break;
            }
        }
        saveToFile();
    }
    
    @Override
    public void delete(String id) {
        halls.removeIf(h -> h.getId().equals(id));
        saveToFile();
    }
    
    @Override
    public Optional<Hall> findById(String id) {
        return halls.stream()
            .filter(h -> h.getId().equals(id))
            .findFirst();
    }
    
    @Override
    public List<Hall> findAll() {
        return new ArrayList<>(halls);
    }
    
    @Override
    public void refresh() {
        loadFromFile();
    }
    
    /**
     * Find halls by type
     */
    public List<Hall> findByType(HallType type) {
        return halls.stream()
            .filter(h -> h.getType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Find all available halls
     */
    public List<Hall> findAvailable() {
        return halls.stream()
            .filter(Hall::isAvailable)
            .collect(Collectors.toList());
    }
    
    /**
     * Find halls by capacity (minimum)
     */
    public List<Hall> findByMinCapacity(int minCapacity) {
        return halls.stream()
            .filter(h -> h.getCapacity() >= minCapacity)
            .collect(Collectors.toList());
    }
    
    /**
     * Find halls by price range
     */
    public List<Hall> findByPriceRange(double minPrice, double maxPrice) {
        return halls.stream()
            .filter(h -> h.getPricePerHour() >= minPrice && h.getPricePerHour() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if hall name exists
     */
    public boolean nameExists(String name) {
        return halls.stream()
            .anyMatch(h -> h.getName().equalsIgnoreCase(name));
    }
}
