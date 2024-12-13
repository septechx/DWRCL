package com.siesque.dwrcl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DWRCoordinateLibrary implements ModInitializer {
	public static final String MOD_ID = "dwrcl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("dwrcl")
					.executes(DWRCoordinateLibrary::listCoordinates)
					.then(CommandManager.literal("add")
							.then(CommandManager.argument("name", StringArgumentType.string())
									.executes(DWRCoordinateLibrary::addCoordinates)))
					.then(CommandManager.literal("remove")
							.then(CommandManager.argument("name", StringArgumentType.string())
									.executes(DWRCoordinateLibrary::removeCoordinates)))
					.then(CommandManager.literal("list")
							.executes(DWRCoordinateLibrary::listCoordinates)));
		});
	}

	private static int addCoordinates(CommandContext<ServerCommandSource> context) {
		String name = StringArgumentType.getString(context, "name");
		ServerCommandSource source = context.getSource();

		if (source.getEntity() == null) {
			source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
			return 0;
		}

		String player = source.getEntity().getName().getString();
		BlockPos position = source.getEntity().getBlockPos();

		String formattedPosition = formatPosition(position);

		try {
			CoordinateStorage.addCoordinate(player, name, position);
			source.sendFeedback(() -> Text.literal("Added coordinates for: " + name + " at " + formattedPosition), false);
		} catch (IOException e) {
			LOGGER.error("Failed to add coordinates", e);
			source.sendFeedback(() -> Text.literal("An error occurred while saving coordinates."), false);
			return 0;
		}

		return 1;
	}

	private static int removeCoordinates(CommandContext<ServerCommandSource> context) {
		String name = StringArgumentType.getString(context, "name");
		ServerCommandSource source = context.getSource();

		if (source.getEntity() == null) {
			source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
			return 0;
		}

		String player = source.getEntity().getName().getString();

		try {
			boolean removed = CoordinateStorage.removeCoordinate(player, name);
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

	private static int listCoordinates(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();

		if (source.getEntity() == null) {
			source.sendFeedback(() -> Text.literal("This command must be run by an entity!"), false);
			return 0;
		}

		String player = source.getEntity().getName().getString();

		try {
			List<String> coordinates = CoordinateStorage.getCoordinatesForPlayer(player);
			if (coordinates.isEmpty()) {
				source.sendFeedback(() -> Text.literal("No coordinates found."), false);
			} else {
				source.sendFeedback(() -> Text.literal("Your Coordinates:"), false);
				for (String coordinate : coordinates) {
					source.sendFeedback(() -> Text.literal(formatCoordinate(coordinate)), false);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to list coordinates", e);
			source.sendFeedback(() -> Text.literal("An error occurred while listing coordinates."), false);
			return 0;
		}

		return 1;
	}

	private static String formatPosition(BlockPos position) {
		return position.getX() + ", " + position.getY() + ", " + position.getZ();
	}

	private static String formatCoordinate(String coordinate) {
		String[] coordinates = coordinate.split(",");
		return coordinates[1] + ": " + coordinates[2] + ", " + coordinates[3] + ", " + coordinates[4];
	}
}