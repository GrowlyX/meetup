package com.solexgames.meetup.scenario.impl;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.task.NoCleanTimer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author puugz
 * @since 05/06/2021 12:13
 */
public class NoCleanScenario extends Scenario implements Listener {

	@EventHandler
	public void onPlayerEntityHit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDamager() instanceof Player)) return;

		final Player entity = (Player) event.getEntity();
		final Player damager = (Player) event.getDamager();

		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(entity);
		final GamePlayer damagerPlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(damager);

		if (damagerPlayer.getNoCleanTimer() != null) {
			this.handleCancelNoClean(damagerPlayer);
		}
		else if (gamePlayer.getNoCleanTimer() != null) {
			damager.sendMessage(entity.getDisplayName() + "'s " + CC.RED + "no clean timer expires in " + TimeUtil.secondsToRoundedTime(gamePlayer.getNoCleanTimer().getTime()) + ".");
		}
	}

	public void handleNoClean(GamePlayer gamePlayer) {
		new NoCleanTimer(gamePlayer);

		gamePlayer.getPlayer().sendMessage(CC.GREEN + "You now have no clean timer.");
	}

	public void handleCancelNoClean(GamePlayer gamePlayer) {
		if (gamePlayer == null || gamePlayer.getNoCleanTimer() == null) {
			return;
		}

		gamePlayer.getNoCleanTimer().cancel();
		gamePlayer.setNoCleanTimer(null);

		gamePlayer.getPlayer().sendMessage(CC.RED + "Your no clean timer has expired due to hostile action.");
	}
}
