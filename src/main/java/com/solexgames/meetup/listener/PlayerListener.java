package com.solexgames.meetup.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.listener.custom.PreDisguiseEvent;
import com.solexgames.core.util.BungeeUtil;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.menu.SpectateMenu;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * @author puugz
 * @since 01/06/2021 21:26
 */
public class PlayerListener implements Listener {

	@EventHandler
	public void onPreDisguise(PreDisguiseEvent event) {
		final Game game = Meetup.getInstance().getGameHandler().getGame();

		if (game.isState(GameState.STARTING) || game.isState(GameState.IN_GAME)) {
			event.getPlayer().sendMessage(ChatColor.RED + "Error: You cannot disguise while the game is starting or has already started.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (this.shouldCancel(event.getPlayer())) event.setCancelled(true);
		if (!event.hasItem() || !event.getAction().name().contains("RIGHT")) return;

		switch (event.getItem().getType()) {
			case ITEM_FRAME:
				if (this.isSpectator(event.getPlayer())) {
					new SpectateMenu().openMenu(event.getPlayer());
				}
				break;
			case BED:
				BungeeUtil.sendToServer(event.getPlayer(), MeetupUtil.getBestHub().getServerName(), CorePlugin.getInstance());
				break;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (this.isSpectator((Player) event.getWhoClicked()) || Meetup.getInstance().getGameHandler().getGame().isState(GameState.WAITING)) event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (this.shouldCancel(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			if (this.shouldCancel((Player) event.getTarget())) {
				event.setTarget(null);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			if (this.shouldCancel((Player) event.getTarget())) {
				event.setTarget(null);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		if (event.getEntity() instanceof ItemFrame) {
			if (this.shouldCancel(event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (this.shouldCancel(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.CHEST)) {
			if (Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot break chests when they are in time bomb mode.");
			}
		}

		if (this.shouldCancel(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		final World gameWorld = Bukkit.getWorld("meetup_game");

		if (event.getBlock().getLocation().getY() >= gameWorld.getHighestBlockYAt(0, 0) + 30) {
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks higher than this point.");
			event.setCancelled(true);
			return;
		}

		if (this.shouldCancel(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			if (this.shouldCancel((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && this.shouldCancel((Player) event.getEntity())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && this.shouldCancel((Player) event.getDamager())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player && this.shouldCancel((Player) event.getEntered())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	public boolean shouldCancel(Player player) {
		return Meetup.getInstance().getPlayerHandler().getByPlayer(player).isSpectating()
				|| !Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME);
	}

	public boolean isSpectator(Player player) {
		return Meetup.getInstance().getPlayerHandler().getByPlayer(player).isSpectating();
	}
}
