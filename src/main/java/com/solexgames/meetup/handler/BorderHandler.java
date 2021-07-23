package com.solexgames.meetup.handler;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * @author puugz
 * @since 24/06/2021 17:52
 */
@Getter
public class BorderHandler {

	@Getter
	private final List<Material> blockedWallBlocks = Arrays.asList(Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2,
			Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
			Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.DOUBLE_PLANT, Material.LONG_GRASS,
			Material.VINE, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.CACTUS, Material.DEAD_BUSH,
			Material.SUGAR_CANE_BLOCK, Material.ICE, Material.SNOW);

	public void setBorder(int border) {
		final Game game = Meetup.getInstance().getGameHandler().getGame();
		final World world = Bukkit.getWorld("meetup_game");

		game.setBorder(border);
		this.addBedrockBorder(world.getName(), border, 2);

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(border * 2);

		if (border == 10) {
			game.setBorderTime(-1);
		}

		this.handlePlayers(world, border);
	}

	private void handlePlayers(World world, int border) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equalsIgnoreCase("meetup_game")) {
				if (player.getLocation().getBlockX() > border) {
					this.handleEffects(player);
					PaperLib.teleportAsync(player, new Location(world, border - 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

					if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
						PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
					}
				}

				if (player.getLocation().getBlockZ() > border) {
					this.handleEffects(player);
					PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), border - 2));

					if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
						PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
					}
				}

				if (player.getLocation().getBlockX() < -border) {
					this.handleEffects(player);
					PaperLib.teleportAsync(player, new Location(world, -border + 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

					if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
						PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
					}
				}

				if (player.getLocation().getBlockZ() < -border) {
					this.handleEffects(player);
					PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), -border + 2));

					if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
						PaperLib.teleportAsync(player, new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
					}
				}
			}
		}
	}

	private void handleEffects(Player player) {
		player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 2, 2);
		player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 2.0f);

		player.sendMessage(ChatColor.RED + "You've been teleported to a valid location inside the world border.");
	}

	private void addBedrockBorder(String world, int radius, int blocksHigh) {
		for (int i = 0; i < blocksHigh; i++) {
			Bukkit.getScheduler().runTaskLater(Meetup.getInstance(), () -> this.addBedrockBorder(world, radius), i);
		}
	}

	private void figureOutBlockToMakeBedrock(String world, int x, int z) {
		Block block = Bukkit.getWorld(world).getHighestBlockAt(x, z);
		Block below = block.getRelative(BlockFace.DOWN);

		while (blockedWallBlocks.contains(below.getType()) && below.getY() > 1) {
			below = below.getRelative(BlockFace.DOWN);
		}

		below.getRelative(BlockFace.UP).setType(Material.BEDROCK);
	}

	private void addBedrockBorder(String world, int radius) {
		new BukkitRunnable() {
			private int counter = -radius - 1;
			private boolean phase1;
			private boolean phase2;
			private boolean phase3;

			@Override
			public void run() {
				if (!this.phase1) {
					final int maxCounter = this.counter + 500;
					final int x = -radius - 1;

					for (int z = this.counter; z <= radius && this.counter <= maxCounter; z++, this.counter++) {
						figureOutBlockToMakeBedrock(world, x, z);
					}

					if (this.counter >= radius) {
						this.counter = -radius - 1;
						this.phase1 = true;
					}
					return;
				}

				if (!this.phase2) {
					final int maxCounter = this.counter + 500;

					for (int z = this.counter; z <= radius && this.counter <= maxCounter; z++, this.counter++) {
						figureOutBlockToMakeBedrock(world, radius, z);
					}

					if (this.counter >= radius) {
						this.counter = -radius - 1;
						this.phase2 = true;
					}
					return;
				}

				if (!this.phase3) {
					final int maxCounter = this.counter + 500;
					final int z = -radius - 1;

					for (int x = this.counter; x <= radius && this.counter <= maxCounter; x++, this.counter++) {
						if (x == radius || x == -radius - 1) {
							continue;
						}

						figureOutBlockToMakeBedrock(world, x, z);
					}

					if (this.counter >= radius) {
						this.counter = -radius - 1;
						this.phase3 = true;
					}
					return;
				}

				final int maxCounter = this.counter + 500;

				for (int x = this.counter; x <= radius && this.counter <= maxCounter; x++, this.counter++) {
					if (x == radius || x == -radius - 1) {
						continue;
					}
					figureOutBlockToMakeBedrock(world, x, radius);
				}

				if (this.counter >= radius) {
					this.cancel();
				}
			}
		}.runTaskTimer(Meetup.getInstance(), 0L, 5L);
	}
}
