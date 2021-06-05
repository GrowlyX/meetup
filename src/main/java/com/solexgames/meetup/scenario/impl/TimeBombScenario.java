package com.solexgames.meetup.scenario.impl;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.scenario.Scenario;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * @author puugz
 * @since 05/06/2021 12:17
 */
public class TimeBombScenario extends Scenario {

	public void handleTimeBomb(Player player, List<ItemStack> drops, List<ItemStack> items) {
		if (drops != null) {
			drops.clear();
		}

		final Location where = player.getLocation();

		where.getBlock().setType(Material.CHEST);
		final Chest chest = (Chest) where.getBlock().getState();

		where.add(1, 0, 0).getBlock().setType(Material.CHEST);
		where.add(0, 1, 0).getBlock().setType(Material.AIR);
		where.add(1, 1, 0).getBlock().setType(Material.AIR);

		items.stream().filter(stack -> stack != null && stack.getType() != Material.AIR).forEach(stack -> chest.getInventory().addItem(stack));

		chest.getInventory().addItem(MeetupUtils.getGoldenHead());

		new BukkitRunnable() {
			private int time = 30;

			@Override
			public void run() {
				if (this.time == 0) {
					this.cancel();

					where.getWorld().createExplosion(where, 8f);

					Bukkit.broadcastMessage(player.getDisplayName() + "'s " + CC.SEC + "corpse has exploded!");
				}
				this.time--;
			}
		}.runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}
}