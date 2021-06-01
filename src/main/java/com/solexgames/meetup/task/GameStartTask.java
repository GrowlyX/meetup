package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.PlayerUtil;
import com.solexgames.meetup.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class GameStartTask extends BukkitRunnable {

	public GameStartTask() {
		this.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

		if (UHCMeetup.getInstance().getGameHandler().getRemainingPlayers().size() < UHCMeetup.getInstance().getGameHandler().getMinPlayers()) {
			game.setGameStartTime(60);
			// TODO: 6/1/2021 shutdown server and send game regeneration packet to redis
			this.cancel();
			// back to lobby?
			return;
		}
		final int gameStartTime = game.getGameStartTime();

		if (Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
			Bukkit.broadcastMessage(CC.SEC + "The game will begin in " + CC.PRI + TimeUtil.secondsToRoundedTime(gameStartTime) + CC.SEC + ".");
			if (Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
				this.sendTitle(CC.B_GREEN + gameStartTime, "Game is starting!");
			}
		}
		if (gameStartTime == 0) {
			this.sendTitle(CC.B_GREEN + "BEGIN", "Game has started!");

			UHCMeetup.getInstance().getGameHandler().getGame().start();

			this.cancel();
			return;
		}

		game.setGameStartTime(gameStartTime - 1);
	}

	private void sendTitle(String title, String subTitle) {
		Bukkit.getOnlinePlayers().stream()
				.map(player -> UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player))
				.filter(gamePlayer -> !gamePlayer.isSpectating())
				.forEach(gamePlayer -> PlayerUtil.sendTitle(gamePlayer.getPlayer(), title, subTitle, 20, 20 * 5, 20));
	}
}
