package com.solexgames.meetup.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.NetworkServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.BungeeUtil;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author GrowlyX
 * @since 6/6/2021
 */

public class GameEndTask extends BukkitRunnable {

	public GameEndTask() {
		this.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

		if (game.getEndTime() <= 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			this.cancel();
			return;
		}

		if (game.getEndTime() == 5) {
			CorePlugin.getInstance().getServerSettings().setCanJoin(false);

			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.RED + "The server you were previously on is now down for:");
				player.sendMessage(game.getWinner() + CC.GREEN + " has won the game, thanks for playing!");

				BungeeUtil.sendToServer(player, this.getBestHub().getServerName(), CorePlugin.getInstance());
			}
		}

		game.setEndTime(game.getEndTime() - 1);
	}

	public NetworkServer getBestHub() {
		return CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
				.filter(Objects::nonNull)
				.filter(networkServer -> networkServer.getServerType().equals(NetworkServerType.HUB))
				.min(Comparator.comparingInt(server -> (int) + (long) server.getOnlinePlayers()))
				.orElse(null);
	}
}
