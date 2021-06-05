package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author puugz
 * @since 05/06/2021 12:40
 */
public class EndTask extends BukkitRunnable {

	public EndTask() {
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

		if (game.getEndTime() == 2) {
			Bukkit.getOnlinePlayers().forEach(player -> player.performCommand("hub"));
		}

		game.setEndTime(game.getEndTime() - 1);
	}
}
