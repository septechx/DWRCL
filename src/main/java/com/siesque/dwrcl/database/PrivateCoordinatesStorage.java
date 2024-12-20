package com.siesque.dwrcl.database;

import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.util.List;

public class PrivateCoordinatesStorage {
    private static final Storage storage = new Storage("privateCoordinates.csv");

    public static String addCoordinate(String player, String name, String dim, BlockPos pos) throws IOException {
        return storage.addCoordinate(player, name, dim, pos);
    }

    public static boolean removeCoordinate(String player, String name) throws IOException {
        return storage.removeCoordinate(player, name);
    }

    public static List<String> getCoordinatesForPlayer(String player) throws IOException {
        return storage.read(player);
    }
}
