package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.game.border.Border;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
			return;
		}

		this.generateNewWorld();
	}

	private void generateNewWorld() {
		this.isGenerating = true;

		final WorldCreator worldCreator = new WorldCreator("meetup_game");
		worldCreator.generateStructures(false);

		try {
			this.world = Bukkit.createWorld(worldCreator);
		} catch (Exception ignored) {
			UHCMeetup.getInstance().getLogger().info("World NPE when trying to generate map.");
			UHCMeetup.getInstance().getServer().unloadWorld(this.world, false);

			this.deleteDirectory(new File("meetup_game"));

			this.isGenerating = false;
			return;
		}

		int waterCount = 0;

		UHCMeetup.getInstance().getLogger().info("Loaded a new world.");

		boolean flag = false;

		for (int i = -100; i <= 100; ++i) {
			boolean isInvalid = false;

			for (int j = -100; j <= 100; j++) {
				boolean isCenter = i >= -50 && i <= 50 && j >= -50 && j <= 50;

				if (isCenter) {
					final Block block = this.world.getHighestBlockAt(i, j).getLocation().add(0, -1, 0).getBlock();

					if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
						++waterCount;
					}
				}

				if (waterCount >= 2000) {
					UHCMeetup.getInstance().getLogger().info("Invalid center, too much water/lava. (" + waterCount + ")");
					isInvalid = true;
					break;
				}
			}

			if (isInvalid) {
				flag = true;
				break;
			}
		}

		if (flag) {
			Bukkit.getServer().unloadWorld(this.world, false);

			this.deleteDirectory(new File("meetup_game"));
			this.isGenerating = false;
			return;
		} else {
			Bukkit.getLogger().info("Found a good seed (" + this.world.getSeed() + ").");
			this.cancel();
		}

		final File lock = new File("meetup_game", "gen.lock");

		try {
			lock.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			return;
		}

		gameHandler.handleSetWhitelistedBlocks();
		gameHandler.handleLoadChunks();

		new Border(Bukkit.getWorld("meetup_game"), 100);

		UHCMeetup.getInstance().setWorldProperties();

		for (final Chunk c : this.world.getLoadedChunks()) {
			final int cx = c.getX() << 4;
			final int cz = c.getZ() << 4;

			for (int x = cx; x < cx + 16; x++) {
				for (int z = cz; z < cz + 16; z++) {
					for (int y = 0; y < 128; y++) {
						final Block potential50Y = this.world.getBlockAt(x, y, z);

						if (potential50Y.getY() == 50) {
							potential50Y.setType(Material.BEDROCK);
						}
					}
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
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}

		return (path.delete());
	}

	private void swapBiomes() {
		// Swap all biomes with other biomes
		this.setBiomeBase(Biome.OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.RIVER, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BEACH, Biome.TAIGA, 0);
		this.setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
		this.setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
		this.setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.ROOFED_FOREST, Biome.DESERT, 0);
		this.setBiomeBase(Biome.STONE_BEACH, Biome.PLAINS, 0);

		// Weird sub-biomes
		this.setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
		this.setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
		this.setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.DESERT, 128);

		// LIMITED threshold biomes
		this.setBiomeBase(Biome.FOREST_HILLS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.FOREST, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.PLAINS, 128);
		this.setBiomeBase(Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.BIRCH_FOREST_MOUNTAINS, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.TAIGA, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.TAIGA, Biome.SAVANNA, 128);
		this.setBiomeBase(Biome.TAIGA_HILLS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.TAIGA_MOUNTAINS, Biome.SAVANNA, 0);
		this.setBiomeBase(Biome.ICE_PLAINS, Biome.BIRCH_FOREST, 0);
		this.setBiomeBase(Biome.ICE_PLAINS, Biome.BIRCH_FOREST, 128);
		this.setBiomeBase(Biome.ICE_PLAINS_SPIKES, Biome.BIRCH_FOREST, 0);
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

		// DISALLOWED threshold biomes
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
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.FOREST, 0);
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.FOREST, 128);
		this.setBiomeBase(Biome.EXTREME_HILLS_PLUS_MOUNTAINS, Biome.FOREST, 0);
		this.setBiomeBase(Biome.FROZEN_OCEAN, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.FROZEN_RIVER, Biome.PLAINS, 0);
		this.setBiomeBase(Biome.ICE_MOUNTAINS, Biome.PLAINS, 0);
	}

	private void setBiomeBase(Biome from, Biome to, int plus) {
		BiomeBase.getBiomes()[(CraftBlock.biomeToBiomeBase(from).id + plus)] = CraftBlock.biomeToBiomeBase(to);
	}
}
