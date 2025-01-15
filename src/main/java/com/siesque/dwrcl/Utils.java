package com.siesque.dwrcl;

import net.minecraft.util.math.BlockPos;

public class Utils {
    private static final String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String formatPosition(BlockPos position) {
        return position.getX() + ", " + position.getY() + ", " + position.getZ();
    }

    public static String formatCoordinate(String coordinate) {
        String[] coordinates = coordinate.split(",");
        return String.format("(%s) %s: %s, %s, %s", parseDimension(coordinates[2]), coordinates[1], coordinates[3], coordinates[4], coordinates[5]);
    }

    public static String parseDimension(String dim) {
        String processed = dim.equals("minecraft:the_nether") ? ":Nether" : dim;
        return capitalize(processed.split(":")[1]);
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty() || uppercaseLetters.contains(String.valueOf(input.charAt(0)))) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
