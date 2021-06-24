package com.solexgames.meetup.task.game;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class GameStartTask extends BukkitRunnable {

	public GameStartTask() {
		this.runTaskTimer(Meetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final Game game = Meetup.getInstance().getGameHandler().getGame();
		final int gameStartTime = game.getGameStartTime();

		if (Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
			Bukkit.broadcastMessage(CC.SEC + "The game will begin in " + CC.PRI + MeetupUtil.secondsToRoundedTime(gameStartTime) + CC.SEC + ".");

			if (Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(gameStartTime)) {
				this.sendTitle(CC.B_GREEN + gameStartTime, "Game is starting!");
				this.playSound(1F);
			}
		}

		if (gameStartTime == 0) {
			Bukkit.broadcastMessage(CC.SEC + "The game has commenced!");
			Meetup.getInstance().getGameHandler().handleGameStarted();

			this.cancel();
			this.sendTitle(CC.B_GREEN + "BEGIN", "Game has started!");
			this.playSound(2F);
			return;
		}

		game.setGameStartTime(gameStartTime - 1);
	}

	private void playSound(float pitch) {
		Meetup.getInstance().getGameHandler().getRemaining()
				.forEach(gamePlayer -> {
					final Player player = gamePlayer.getPlayer();

					player.playSound(player.getLocation(), Sound.NOTE_PLING, 5F, pitch);
				});
	}

	private void sendTitle(String title, String subTitle) {
		Meetup.getInstance().getGameHandler().getRemaining()
				.forEach(gamePlayer -> MeetupUtil.sendTitle(gamePlayer.getPlayer(), title, subTitle, 0, 20 * 5, 20));
	}
}
