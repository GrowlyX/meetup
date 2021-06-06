package com.solexgames.meetup.game.border;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class Border {

	public Border(World world, int border) {
		final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

		game.setBorder(border);
		BorderHelper.addBedrockBorder(world.getName(), border, 2);

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(border * 2);

		if (border == 10) {
			game.setBorderTime(-1);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equalsIgnoreCase("meetup_game")) {
				if (player.getLocation().getBlockX() > border) {
					this.handleEffects(player);

					player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
				}

				if (player.getLocation().getBlockZ() > border) {
					this.handleEffects(player);

					player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
				}

				if (player.getLocation().getBlockX() < -border) {
					this.handleEffects(player);

					player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
				}

				if (player.getLocation().getBlockZ() < -border) {
					this.handleEffects(player);

					player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
				}
			}
		}
	}


	private void handleEffects(Player player) {
		player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 2, 2);
		player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 2.0f);

		player.sendMessage(ChatColor.RED + "You've been teleported to a valid location inside the world border.");
	}
}
