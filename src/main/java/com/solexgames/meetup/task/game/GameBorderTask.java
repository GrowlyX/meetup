package com.solexgames.meetup.task.game;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.handler.BorderHandler;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * @author puugz
 * @since 01/06/2021 22:21
 */
public class GameBorderTask extends BukkitRunnable {

	private final List<Integer> seconds = Arrays.asList(120, 60, 30, 15, 10, 5, 4, 3, 2, 1);

	public GameBorderTask() {
		this.runTaskTimer(Meetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final BorderHandler borderHandler = Meetup.getInstance().getBorderHandler();
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		if (game.getBorderTime() <= 0) {
			if (game.getNextBorder() == 10) {
				borderHandler.setBorder(10);
				this.cancel();
				return;
			}

			borderHandler.setBorder(game.getNextBorder());

			game.setBorderTime(120);
			game.setBorder(game.getNextBorder());
		} else if (this.seconds.contains(game.getBorderTime())) {
			Bukkit.broadcastMessage(CC.SEC + "The border will shrink to " + CC.PRI + game.getNextBorder() + CC.SEC + " in " + CC.PRI + MeetupUtil.secondsToRoundedTime(game.getBorderTime()) + CC.SEC + ".");
		}

		game.decrementBorderTime();
	}
}
