package com.siesque.dwrcl.database;

import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final Path STORAGE_DIR;
    private final Path storageFile;

    static {
        String workingDir = System.getProperty("user.dir");
        STORAGE_DIR = Paths.get(workingDir, "dwrcl");

        try {
            if (!Files.exists(STORAGE_DIR)) {
                Files.createDirectories(STORAGE_DIR);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }

    public Storage(String fileName) {
        this.storageFile = STORAGE_DIR.resolve(fileName);

        try {
            if (!Files.exists(storageFile)) {
                Files.createFile(storageFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize coordinate file: " + fileName, e);
        }
    }

    public Path getStorageFile() {
        return storageFile;
    }

    public static Path getStorageDir() {
        return STORAGE_DIR;
    }

    public List<String> read(String key) throws IOException {
        List<String> lines = Files.readAllLines(storageFile);
        List<String> keyValues = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(key)) {
                keyValues.add(line);
            }
        }

        return keyValues;
    }

    public String addCoordinate(String key, String name, String dim, BlockPos pos) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(storageFile, StandardOpenOption.APPEND)) {
            String toWrite = String.format("%s,%s,%s,%d,%d,%d%n", key, name, dim, pos.getX(), pos.getY(), pos.getZ());
            writer.write(toWrite);
            return toWrite;
        }
    }


    public boolean removeCoordinate(String player, String name) throws IOException {
        List<String> lines = Files.readAllLines(storageFile);
        List<String> updatedLines = new ArrayList<>();

        boolean found = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(player) && parts[1].equals(name)) {
                found = true; // Skip this line (remove the coordinate)
            } else {
                updatedLines.add(line); // Keep the other lines
            }
        }

        if (found) {
            Files.write(storageFile, updatedLines);
        }

        return found;
    }
}
