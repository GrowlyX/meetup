package com.solexgames.meetup.task.game;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.BungeeUtil;
import com.solexgames.core.util.ExperienceUtil;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since 6/6/2021
 */

public class GameEndTask extends BukkitRunnable {

	public GameEndTask() {
		this.runTaskTimer(Meetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final Game game = Meetup.getInstance().getGameHandler().getGame();

		if (game.getEndTime() == 8) {
			CorePlugin.getInstance().getServerSettings().setCanJoin(false);

			for (Player player : Bukkit.getOnlinePlayers()) {
				final boolean winner = game.getWinnerId().equals(player.getUniqueId());

				ExperienceUtil.addExperience(player, winner ? 150 : 15);

				final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

				if (winner) {
					gamePlayer.setReRolls(gamePlayer.getReRolls() + 1);
					player.sendMessage(CC.SEC + "You've received " + CC.PRI + "1" + CC.SEC + " kit re-roll!");
				}
			}
		}

		if (game.getEndTime() == 5) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.RED + "The server you were previously on is now down for:");
				player.sendMessage(game.getWinner() + CC.GREEN + " has won the game, thanks for playing!");

				BungeeUtil.sendToServer(player, MeetupUtil.getBestHub().getServerName(), CorePlugin.getInstance());
			}
		}

		if (game.getEndTime() <= 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			this.cancel();
			return;
		}

		game.setEndTime(game.getEndTime() - 1);
	}
}
