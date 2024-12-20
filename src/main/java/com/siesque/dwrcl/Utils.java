package com.siesque.dwrcl;

import net.minecraft.util.math.BlockPos;

public class Utils {
    public static String formatPosition(BlockPos position) {
        return position.getX() + ", " + position.getY() + ", " + position.getZ();
    }

    public static String formatCoordinate(String coordinate) {
        String[] coordinates = coordinate.split(",");
        return String.format("(%s) %s: %s, %s, %s", parseDimension(coordinates[2]), coordinates[1], coordinates[3], coordinates[4], coordinates[5]);
    }

    public static String parseDimension(String dim) {
        return capitalize(dim.split(":")[1]);
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
