package com.solexgames.meetup.scenario.impl;

import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.scenario.impl.listener.NoCleanListener;
import com.solexgames.meetup.task.NoCleanTimer;
import com.solexgames.meetup.util.CC;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

/**
 * @author puugz
 * @since 05/06/2021 12:13
 */
public class NoCleanScenario extends Scenario {

	public void handleNoClean(GamePlayer gamePlayer) {
		new NoCleanTimer(gamePlayer);
	}

	public void handleCancelNoClean(GamePlayer gamePlayer) {
		if (gamePlayer == null || gamePlayer.getNoCleanTimer() == null) {
			return;
		}

		gamePlayer.getNoCleanTimer().cancel();
		gamePlayer.setNoCleanTimer(null);

		gamePlayer.getPlayer().sendMessage(CC.RED + CC.BOLD + "Your no clean timer has expired due to hostile action.");
	}

	@Override
	public List<Listener> getListeners() {
		return Collections.singletonList(new NoCleanListener());
	}
}
