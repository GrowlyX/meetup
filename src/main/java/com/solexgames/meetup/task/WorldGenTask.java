package com.solexgames.meetup.task;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.handler.GameHandler;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class WorldGenTask extends BukkitRunnable {

	private World world;

	private boolean isGenerating = false;
	private boolean hasGenerated = false;

	private final GameHandler gameHandler;
	private final long startMilli;

	public WorldGenTask(GameHandler gameHandler) {
		this.deleteDirectory(new File("meetup_game"));

		this.gameHandler = gameHandler;
		this.startMilli = System.currentTimeMillis();

		Logger.getGlobal().info("[UHCMeetup] Generation of the UHC Meetup world has started.");

		this.swapBiomes();
	}

	@Override
	public void run() {
		if (this.hasGenerated) {
			Logger.getGlobal().info("[UHCMeetup] Generation of the UHC Meetup world has finished.");
			Logger.getGlobal().info("[UHCMeetup] It took " + (System.currentTimeMillis() - this.startMilli) + "ms to generate the world.");

			this.cancel();
			return;
		}

		if (this.isGenerating) {
			Logger.getGlobal().info("[UHCMeetup] Currently generating the uhc meetup world...");
		}
	}

	private void generateNewWorld() {
		this.isGenerating = true;

		final WorldCreator worldCreator = new WorldCreator("meetup_game");
		worldCreator.generateStructures(false);

		try {
			this.world = Bukkit.createWorld(worldCreator);
		} catch (Exception ignored) {
			Meetup.getInstance().getLogger().info("World NPE when trying to generate map.");
			Meetup.getInstance().getServer().unloadWorld(this.world, false);

			this.deleteDirectory(new File("meetup_game"));

			this.isGenerating = false;
			return;
		}

		final File lock = new File("meetup_game", "gen.lock");

		try {
			lock.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.shutdown();
			return;
		}

		this.gameHandler.handleLoadChunks();

		new Border(Bukkit.getWorld("meetup_game"), 100);

		Meetup.getInstance().setWorldProperties();

		for (final Chunk chunk : this.world.getLoadedChunks()) {
			final int cx = chunk.getX() << 4;
			final int cz = chunk.getZ() << 4;

			for (int x = cx; x < cx + 16; x++) {
				for (int z = cz; z < cz + 16; z++) {
					this.world.getBlockAt(x, 50, z).setType(Material.BEDROCK);
				}
			}
		}

		this.hasGenerated = true;
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			final File[] files = path.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						this.deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}

		return (path.delete());
	}

	private void swapBiomes() {
		this.setBiomeBase(Biome.SMALL_MOUNTAINS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.MUSHROOM_ISLAND, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.MUSHROOM_SHORE, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.DESERT_MOUNTAINS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.DESERT_HILLS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.FLOWER_FOREST, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.SUNFLOWER_PLAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.RIVER, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BEACH, Biome.TAIGA, 0);
		this.setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
		this.setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
		this.setBiomeBase(Biome.JUNGLE_MOUNTAINS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.JUNGLE_EDGE_MOUNTAINS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.ROOFED_FOREST, Biome.DESERT, 0);
		this.setBiomeBase(Biome.STONE_BEACH, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
		this.setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
		this.setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.DESERT, 128);
		this.setBiomeBase(Biome.FOREST_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.TAIGA, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.TAIGA, Biome.SAVANNA, 128);
		this.setBiomeBase(Biome.TAIGA_HILLS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.TAIGA_MOUNTAINS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.ICE_PLAINS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.ICE_PLAINS, Biome.SAVANNA, 128);
		this.setBiomeBase(Biome.ICE_PLAINS_SPIKES, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.MEGA_SPRUCE_TAIGA, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MEGA_SPRUCE_TAIGA_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.MEGA_TAIGA_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.COLD_BEACH, Biome.DESERT, 0);
		this.setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.COLD_TAIGA_HILLS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.COLD_TAIGA_MOUNTAINS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.FOREST, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.ROOFED_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.MESA_BRYCE, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA_PLATEAU_FOREST, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA_PLATEAU_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.EXTREME_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.EXTREME_HILLS, Biome.DESERT, 128);
		this.setBiomeBase(Biome.EXTREME_HILLS_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.DESERT, 128);
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS_MOUNTAINS, Biome.DESERT, 0);
		this.setBiomeBase(Biome.FROZEN_OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.FROZEN_RIVER, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.ICE_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.SWAMPLAND, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.SWAMPLAND_MOUNTAINS, Biome.PLAINS, 0);

		Logger.getGlobal().info("[UHCMeetup] Finished biome swap for the UHC Meetup world.");
		Logger.getGlobal().info("[UHCMeetup] Starting world generation for the UHC Meetup world.");

		this.generateNewWorld();
	}

	private void setBiomeBase(Biome from, Biome to, int plus) {
		BiomeBase.getBiomes()[(CraftBlock.biomeToBiomeBase(from).id + plus)] = CraftBlock.biomeToBiomeBase(to);
	}
}
