package com.siesque.dwrcl;

import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CoordinateStorage {
    private static final Path STORAGE_DIR;
    private static final Path COORDINATES_FILE;

    static {
        String workingDir = System.getProperty("user.dir");
        STORAGE_DIR = Paths.get(workingDir, "dwrcl");
        COORDINATES_FILE = STORAGE_DIR.resolve("coordinates.csv");

        try {
            if (!Files.exists(STORAGE_DIR)) {
                Files.createDirectories(STORAGE_DIR);
            }
            if (!Files.exists(COORDINATES_FILE)) {
                Files.createFile(COORDINATES_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize coordinate storage", e);
        }
    }

    public static void addCoordinate(String player, String name, BlockPos pos) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(COORDINATES_FILE, StandardOpenOption.APPEND)) {
            writer.write(String.format("%s,%s,%d,%d,%d%n", player, name, pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    public static boolean removeCoordinate(String player, String name) throws IOException {
        List<String> lines = Files.readAllLines(COORDINATES_FILE);
        List<String> updatedLines = new ArrayList<>();

        boolean found = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 5 && parts[0].equals(player) && parts[1].equals(name)) {
                found = true; // Skip this line (remove the coordinate)
            } else {
                updatedLines.add(line); // Keep the other lines
            }
        }

        if (found) {
            Files.write(COORDINATES_FILE, updatedLines);
        }

        return found;
    }

    public static List<String> getCoordinatesForPlayer(String player) throws IOException {
        List<String> lines = Files.readAllLines(COORDINATES_FILE);
        List<String> playerCoordinates = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 5 && parts[0].equals(player)) {
                playerCoordinates.add(line);
            }
        }

        return playerCoordinates;
    }
}
