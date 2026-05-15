package com.medicalapp.common.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Demonstrates Encapsulation and File Handling.
 * This utility manages the low-level read/write operations to notepad files.
 */
public class FileStorageUtil<T> {
    private final String filePath;

    public FileStorageUtil(String fileName) {
        // Create data directory if not exists
        File dir = new File("data");
        if (!dir.exists()) dir.mkdir();
        this.filePath = "data/" + fileName;
    }

    public void writeToFile(List<T> data, Function<T, String> mapper) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T item : data) {
                writer.write(mapper.apply(item));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> readFromFile(Function<String, T> mapper) {
        List<T> data = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return data;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T item = mapper.apply(line);
                    if (item != null) {
                        data.add(item);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
