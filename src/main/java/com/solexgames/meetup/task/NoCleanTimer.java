package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.TimeUtil;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * @author puugz
 * @since 01/06/2021 19:13
 */
public class NoCleanTimer extends BukkitRunnable {

	private final GamePlayer gamePlayer;

	@Getter
	private int time = 15;

	public NoCleanTimer(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.gamePlayer.setNoCleanTimer(this);
		this.gamePlayer.getPlayer().sendMessage(CC.GREEN + "You now have no clean timer.");

		// TODO: CC.RED + "Your no clean timer has expired due to hostile action."

		this.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		if (Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(this.time)) {
			this.gamePlayer.getPlayer().sendMessage(CC.RED + "No clean will expire in " + TimeUtil.secondsToRoundedTime(this.time) + ".");
		} else if (time == 0) {
			this.gamePlayer.getPlayer().sendMessage(CC.B_RED + "Your no clean timer has expired.");
			this.cancel();
			return;
		}
		this.time--;
	}

	private void stop(boolean hostileAction) {

	}
}