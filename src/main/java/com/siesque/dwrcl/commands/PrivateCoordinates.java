package com.siesque.dwrcl.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;

import com.siesque.dwrcl.Utils;
import com.siesque.dwrcl.database.PrivateCoordinatesStorage;

public class PrivateCoordinates {
    private final Logger LOGGER;

    public PrivateCoordinates(Logger logger) {
        this.LOGGER = logger;
    }

    public int addCoordinates(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        ServerCommandSource source = context.getSource();

        if (source.getEntity() == null) {
            source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
            return 0;
        }

        String player = source.getEntity().getName().getString();
        BlockPos position = source.getEntity().getBlockPos();
        String dim = source.getEntity().getWorld().getRegistryKey().getValue().toString();

        String formattedPosition = Utils.formatPosition(position);

        try {
            PrivateCoordinatesStorage.addCoordinate(player, name, dim, position);
            source.sendFeedback(() -> Text.literal("Added coordinates for: " + name + " at " + formattedPosition), false);
        } catch (IOException e) {
            LOGGER.error("Failed to add coordinates", e);
            source.sendFeedback(() -> Text.literal("An error occurred while saving coordinates."), false);
            return 0;
        }

        return 1;
    }

    public int removeCoordinates(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        ServerCommandSource source = context.getSource();

        if (source.getEntity() == null) {
            source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
            return 0;
        }

        String player = source.getEntity().getName().getString();

        try {
            boolean removed = PrivateCoordinatesStorage.removeCoordinate(player, name);
            if (removed) {
                source.sendFeedback(() -> Text.literal("Removed coordinates for: " + name), false);
            } else {
                source.sendFeedback(() -> Text.literal("No coordinates found for: " + name), false);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to remove coordinates", e);
            source.sendFeedback(() -> Text.literal("An error occurred while removing coordinates."), false);
            return 0;
        }

        return 1;
    }

    public int listCoordinates(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() == null) {
            source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
            return 0;
        }

        String player = source.getEntity().getName().getString();

        try {
            List<String> coordinates = PrivateCoordinatesStorage.getCoordinatesForPlayer(player);
            if (coordinates.isEmpty()) {
                source.sendFeedback(() -> Text.literal("No coordinates found."), false);
            } else {
                source.sendFeedback(() -> Text.literal("Your coordinates:"), false);
                for (String coordinate : coordinates) {
                    source.sendFeedback(() -> Text.literal(Utils.formatCoordinate(coordinate)), false);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to list coordinates", e);
            source.sendFeedback(() -> Text.literal("An error occurred while listing coordinates."), false);
            return 0;
        }

        return 1;
    }

    public int shareCoordinates(CommandContext<ServerCommandSource> context) {
        String coordinateName = StringArgumentType.getString(context, "coordinate");
        String player = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (source.getEntity() == null) {
            source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
            return 0;
        }

        String name = source.getEntity().getName().getString();

        try {
            List<String> coordinates = PrivateCoordinatesStorage.getCoordinatesForPlayer(name);
            String coordinateToShare = null;

            for (String coordinate : coordinates) {
                String[] parts = coordinate.split(",");

                if (parts[1].equals(coordinateName)) {
                    coordinateToShare = coordinate;
                }
            }

            if (coordinateToShare == null) {
                source.sendFeedback(() -> Text.literal("Coordinate not found"), false);
            } else {
                String[] parts = coordinateToShare.split(",");
                PrivateCoordinatesStorage.addCoordinate(
                        player,
                        coordinateName,
                        parts[2],
                        new BlockPos(
                                Integer.parseInt(parts[3]),
                                Integer.parseInt(parts[4]),
                                Integer.parseInt(parts[5])
                        )
                );
                source.sendFeedback(() -> Text.literal(String.format("Shared coordinate %s with %s", coordinateName, player)), false);
            }
        }  catch (IOException e) {
            LOGGER.error("Failed to share coordinates", e);
            source.sendFeedback(() -> Text.literal("An error occurred while sharing coordinates."), false);
            return 0;
        }

        return 1;
    }

    public int netherizeCoorinates(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        ServerCommandSource source = context.getSource();

        if (source.getEntity() == null) {
            source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
            return 0;
        }

        String player = source.getEntity().getName().getString();

        try {
            List<String> coordinates = PrivateCoordinatesStorage.getCoordinatesForPlayer(player);
            String netherCoordinate = null;

            for (String coordinate : coordinates) {
                String[] parts = coordinate.split(",");

                if (parts[1].equals(name)) {
                    netherCoordinate = coordinate;
                }
            }

            if (netherCoordinate == null) {
                source.sendFeedback(() -> Text.literal("Coordinate not found"), false);
            } else {
                String[] parts = netherCoordinate.split(",");
                BlockPos position = new BlockPos(
                        Integer.parseInt(parts[3]) / 8,
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]) / 8
                );
                PrivateCoordinatesStorage.addCoordinate(
                        player,
                        name,
                        "minecraft:nether",
                        position
                );
                source.sendFeedback(() -> Text.literal(String.format("Created nether coordinates for %s at (Nether) %s", name, Utils.formatPosition(position))), false);
            }
        }  catch (IOException e) {
            LOGGER.error("Failed to netherize coordinates", e);
            source.sendFeedback(() -> Text.literal("An error occurred while netherizing coordinates."), false);
            return 0;
        }

        return 1;
    }
}
