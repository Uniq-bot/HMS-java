package HotelMgmt.util;


import java.io.*;
import java.util.*;

public class FileUtil {

    public static List<String> read(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("File read error.");
            System.out.println(e);
        }
        return lines;
    }

    public static void write(String path, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("File write error.");
        }
    }
}
