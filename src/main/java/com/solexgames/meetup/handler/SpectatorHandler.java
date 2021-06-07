package com.solexgames.meetup.handler;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.PlayerState;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author puugz
 * @since 01/06/2021 18:55
 */
public class SpectatorHandler {

	private final ItemStack spectateMenuItem;
	private final ItemStack navigationCompassItem;

	private final PotionEffect invisibilityEffect = new PotionEffect(
			PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false);

	public SpectatorHandler() {
		this.spectateMenuItem = new ItemBuilder(Material.ITEM_FRAME)
				.setDisplayName(CC.B_PRI + "Spectate Menu")
				.addLore(
						CC.GRAY + "See a list of players",
						CC.GRAY + "that you're able to",
						CC.GRAY + "teleport to and spectate."
				)
				.create();
		this.navigationCompassItem = new ItemBuilder(Material.COMPASS)
				.setDisplayName(CC.B_PRI + "Navigation Compass")
				.addLore(
						CC.GRAY + "Left-Click: " + CC.WHITE + "Teleport to the block you're looking at!",
						CC.GRAY + "Right-Click: " + CC.WHITE + "Teleport through walls!"
				)
				.create();
	}

	public void setSpectator(GamePlayer gamePlayer, String reason, boolean title) {
		final Player player = gamePlayer.getPlayer();

		Bukkit.getScheduler().runTask(UHCMeetup.getInstance(), () -> {
			player.setAllowFlight(true);
			player.setFlying(true);

			player.getInventory().clear();
			player.getInventory().setArmorContents(null);

			player.setGameMode(GameMode.CREATIVE);
			player.addPotionEffect(this.invisibilityEffect);

			if (reason != null) {
				gamePlayer.getPlayer().sendMessage(CC.SEC + "You're now a spectator: " + CC.RED + reason);
			}

			if (title) {
				PlayerUtil.sendTitle(player, CC.B_RED + "DEAD", "You are now a spectator!", 0, 80, 20);
			}

			Bukkit.getOnlinePlayers().forEach(other -> {
				final GamePlayer gamePlayer1 = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(other);

				if (!gamePlayer1.isSpectating()) {
					other.hidePlayer(player);
				}

				CorePlugin.getInstance().getNameTagManager().setupNameTag(player, other, ChatColor.GRAY);
			});

			player.getInventory().setItem(0, this.spectateMenuItem);
			player.getInventory().setItem(1, this.navigationCompassItem);

			player.updateInventory();
		});

		gamePlayer.setState(PlayerState.SPECTATING);
	}

	public void removeSpectator(GamePlayer gamePlayer) {
		final Player player = gamePlayer.getPlayer();

		PlayerUtil.resetPlayer(player);

		Bukkit.getScheduler().runTask(UHCMeetup.getInstance(), () -> Bukkit.getOnlinePlayers().stream()
				.filter(online -> !online.canSee(player)).forEach(online -> online.showPlayer(player)));

		gamePlayer.getPlayer().sendMessage(CC.SEC + "You are no longer spectating the game.");
		gamePlayer.setState(PlayerState.WAITING);
	}
}
