package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.TimeUtil;
import com.solexgames.meetup.game.border.Border;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * @author puugz
 * @since 01/06/2021 22:21
 */
public class BorderTask extends BukkitRunnable {

	private final List<Integer> seconds = Arrays.asList(120, 60, 30, 15, 10, 5, 4, 3, 2, 1);

	public BorderTask() {
		this.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		final GameHandler gameHandler = UHCMeetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		if (game.getBorderTime() <= 0) {
			if (game.getNextBorder() == 10) {
				new Border(Bukkit.getWorld("meetup_game"), game.getNextBorder());
				this.cancel();
				return;
			}

			game.setBorderTime(120);
			new Border(Bukkit.getWorld("meetup_game"), game.getNextBorder());
			game.setBorder(game.getNextBorder());
		} else if (this.seconds.contains(game.getBorderTime())) {
			Bukkit.broadcastMessage(CC.SEC + "The border will shrink to " + CC.PRI + game.getNextBorder() + CC.SEC + " in " + CC.PRI + TimeUtil.secondsToRoundedTime(game.getBorderTime()) + CC.SEC + ".");
		}
		game.decrementBorderTime();
	}
}
