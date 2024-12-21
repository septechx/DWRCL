package com.siesque.dwrcl;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siesque.dwrcl.commands.PrivateCoordinates;
import com.siesque.dwrcl.commands.TeamCoordinates;

public class DWRCoordinateLibrary implements DedicatedServerModInitializer {
	public static final String MOD_ID = "dwrcl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	PrivateCoordinates privateCoordinates = new PrivateCoordinates(LOGGER);
	TeamCoordinates teamCoordinates = new TeamCoordinates(LOGGER);

	public void onInitializeServer() {
		boolean hasDWRT = FabricLoader.getInstance().isModLoaded("dwrt");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("dwrcl")
					.executes(privateCoordinates::listCoordinates)
					.then(CommandManager.literal("add")
							.then(CommandManager.argument("name", StringArgumentType.string())
									.executes(privateCoordinates::addCoordinates)))
					.then(CommandManager.literal("remove")
							.then(CommandManager.argument("name", StringArgumentType.string())
									.executes(privateCoordinates::removeCoordinates)))
					.then(CommandManager.literal("list")
							.executes(privateCoordinates::listCoordinates))
					.then(CommandManager.literal("share")
							.then(CommandManager.argument("coordinate", StringArgumentType.string())
									.then(CommandManager.argument("name", StringArgumentType.string())
											.executes(privateCoordinates::shareCoordinates)))));

			if (hasDWRT) {
				dispatcher.register(CommandManager.literal("dwrcl")
						.then(CommandManager.literal("team")
								.executes(teamCoordinates::listCoordinates)
								.then(CommandManager.literal("add")
										.then(CommandManager.argument("name", StringArgumentType.string())
												.executes(teamCoordinates::addCoordinates)))
								.then(CommandManager.literal("remove")
										.then(CommandManager.argument("name", StringArgumentType.string())
												.executes(teamCoordinates::removeCoordinates)))
								.then(CommandManager.literal("list")
										.executes(teamCoordinates::listCoordinates))));
			}
		});
	}
}