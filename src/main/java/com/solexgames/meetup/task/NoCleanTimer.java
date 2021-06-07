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
		this.gamePlayer.getPlayer().sendMessage(CC.SEC + "You now have a no clean timer for " + CC.PRI + "15 seconds" + CC.SEC + ".");

		this.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		if (this.gamePlayer.getPlayer() == null) {
			this.cancel();
			return;
		}

		if (Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(this.time)) {
			this.gamePlayer.getPlayer().sendMessage(CC.RED + "Your no clean timer will expire in " + CC.YELLOW + TimeUtil.secondsToRoundedTime(this.time) + CC.RED + ".");
		} else if (time == 0) {
			if (this.gamePlayer.getPlayer() != null) {
				this.gamePlayer.getPlayer().sendMessage(CC.B_RED + "Your no clean timer has expired.");
				this.gamePlayer.setNoCleanTimer(null);
			}

			this.cancel();
			return;
		}

		this.time--;
	}
}
