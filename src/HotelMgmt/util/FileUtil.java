package HotelMgmt.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations.
 * Handles reading, writing, and appending to text files.
 */
public class FileUtil {
    
    /**
     * Ensure file and directory exist
     */
    public static void ensureFileExists(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            System.err.println("Error creating file: " + filePath);
        }
    }

    /**
     * Read all lines from a file
     */
    public static List<String> read(String filePath) {
        ensureFileExists(filePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }
        return lines;
    }

    /**
     * Append a single line to a file
     */
    public static void append(String filePath, String line) {
        ensureFileExists(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath);
        }
    }

    /**
     * Write a list of lines to file (overwrite)
     */
    public static void write(String filePath, List<String> lines) {
        ensureFileExists(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
        }
    }
    
    /**
     * Delete a file
     */
    public static boolean delete(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filePath);
            return false;
        }
    }
    
    /**
     * Check if file exists
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}