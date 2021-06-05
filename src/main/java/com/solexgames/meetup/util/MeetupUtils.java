package com.solexgames.meetup.util;

import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Random;

/**
 * @author puugz
 * @since 05/06/2021 12:31
 */
public class MeetupUtils {

	public static ItemStack getGoldenHead() {
		return new ItemBuilder(Material.GOLDEN_APPLE)
				.setDurability(0)
				.setDisplayName("&6Golden Head").create();
	}

	public static void deleteWorld() {
		final World world = Bukkit.getWorld("meetup_game");

		if (world != null) {
			Bukkit.getServer().unloadWorld(world, false);

			deleteFile(world.getWorldFolder());
		}
	}

	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				if (!deleteFile(subfile)) {
					return false;
				}
			}
		}

		return file.delete();
	}

	public static Location getScatterLocation() {
		final Random r = new Random();

		final int x = r.nextInt(100 * 2) - 100;
		final int z = r.nextInt(100 * 2) - 100;

		final World world = Bukkit.getWorld("meetup_game");

		return new Location(world, x, world.getHighestBlockYAt(x, z) + 1, z);
	}
}
