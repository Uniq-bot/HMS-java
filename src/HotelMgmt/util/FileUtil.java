package HotelMgmt.util;

import java.io.*;
import java.util.*;

public class FileUtil {

    public static List<String> read(String path) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("File read error: " + e.getMessage());
        }

        return lines;
    }
}
