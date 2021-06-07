package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.player.PlayerState;
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
		final int gameStartTime = game.getGameStartTime();

		if (Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
			Bukkit.broadcastMessage(CC.SEC + "The game will begin in " + CC.PRI + TimeUtil.secondsToRoundedTime(gameStartTime) + CC.SEC + ".");

			if (Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
				this.sendTitle(CC.B_GREEN + gameStartTime, "Game is starting!");
			}
		}

		if (gameStartTime == 0) {
			Bukkit.broadcastMessage(CC.SEC + "The game has started, good luck!");
			UHCMeetup.getInstance().getGameHandler().handleStart();

			this.sendTitle(CC.B_GREEN + "BEGIN", "Game has started!");
			this.cancel();
			return;
		}

		game.setGameStartTime(gameStartTime - 1);
	}

	private void sendTitle(String title, String subTitle) {
		Bukkit.getOnlinePlayers().stream()
				.map(player -> UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player))
				.filter(gamePlayer -> !gamePlayer.getState().equals(PlayerState.SPECTATING))
				.forEach(gamePlayer -> PlayerUtil.sendTitle(gamePlayer.getPlayer(), title, subTitle, 0, 20 * 5, 20));
	}
}
