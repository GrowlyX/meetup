package com.solexgames.meetup.listener;

import com.solexgames.meetup.UHCMeetup;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

/**
 * @author puugz
 * @since 01/06/2021 21:26
 */
public class SpectatorListener implements Listener {

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			if (isSpectator((Player) event.getTarget())) {
				event.setTarget(null);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			if (isSpectator((Player) event.getTarget())) {
				event.setTarget(null);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		if (event.getEntity() instanceof ItemFrame) {
			if (isSpectator(event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			if (isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (isSpectator((Player) event.getDamager())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			if (isSpectator((Player) event.getEntered())) {
				event.setCancelled(true);
			}
		}
	}

	public boolean isSpectator(Player player) {
		return UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player).isSpectating();
	}
}
