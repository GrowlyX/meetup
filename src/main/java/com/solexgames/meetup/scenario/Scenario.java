package com.solexgames.meetup.scenario;

import com.solexgames.meetup.UHCMeetup;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author puugz
 * @since 05/06/2021 12:12
 */
public class Scenario {

	public Scenario() {
		if (this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, UHCMeetup.getInstance());
		}
	}
}
