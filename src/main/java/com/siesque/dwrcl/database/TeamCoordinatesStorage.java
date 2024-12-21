package com.siesque.dwrcl.database;

import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.util.List;

public class TeamCoordinatesStorage {
    private static final Storage storage = new Storage("teamCoordinates.csv");

    public static String addCoordinate(String team, String name, String dim, BlockPos pos) throws IOException {
        return storage.addCoordinate(team, name, dim, pos);
    }

    public static boolean removeCoordinate(String team, String name) throws IOException {
        return storage.removeCoordinate(team, name);
    }

    public static List<String> getCoordinatesForTeam(String team) throws IOException {
        return storage.read(team);
    }
}
