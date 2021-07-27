package com.solexgames.meetup.handler;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
				.setDisplayName(CC.SEC + "Spectate Menu")
				.addLore(
						CC.SEC + "See a list of players",
						CC.SEC + "that you're able to",
						CC.SEC + "teleport to and spectate."
				)
				.create();
		this.navigationCompassItem = new ItemBuilder(Material.COMPASS)
				.setDisplayName(CC.AQUA + "Navigation Compass")
				.addLore(
						CC.PRI + "Left-Click: " + CC.SEC + "Teleport to the block you're looking at!",
						CC.PRI + "Right-Click: " + CC.SEC + "Teleport through walls!"
				)
				.create();
	}

	public void setSpectator(GamePlayer gamePlayer, String reason, boolean title) {
		final Player player = gamePlayer.getPlayer();
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();

		gameHandler.getRemaining().remove(gamePlayer);
		gameHandler.getSpectators().add(gamePlayer);

		Bukkit.getScheduler().runTask(Meetup.getInstance(), () -> {
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
				MeetupUtil.sendTitle(player, CC.B_RED + "DEAD", "You are now a spectator!", 0, 80, 20);
			}

			Bukkit.getOnlinePlayers().forEach(other -> {
				final GamePlayer otherPlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(other);
				final Scoreboard board = Meetup.getInstance().getScoreboardHandler().getAdapter().getScoreboard(other);

				Team ghostTeam = board.getTeam("ghost");

				if (ghostTeam == null) {
					ghostTeam = board.registerNewTeam("ghost");
					ghostTeam.setCanSeeFriendlyInvisibles(true);
				}

				if (!otherPlayer.isSpectating()) {
					other.hidePlayer(player);

					if (ghostTeam.hasEntry(player.getName())) {
						ghostTeam.removeEntry(player.getName());
					}
				} else {
					other.showPlayer(player);

					if (!ghostTeam.hasEntry(player.getName())) {
						ghostTeam.addEntry(player.getName());
					}
				}
			});

			if (reason != null && reason.equals("chose to watch")) {
				PaperLib.teleportAsync(player, Meetup.getInstance().getGameHandler().getSpawnLocation());
			}

			player.setPlayerListName(CC.GRAY + player.getName());

			player.getInventory().setItem(0, this.spectateMenuItem);
			player.getInventory().setItem(1, this.navigationCompassItem);

			player.updateInventory();
		});
	}

	public void removeSpectator(GamePlayer gamePlayer) {
		final Player player = gamePlayer.getPlayer();
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();

		MeetupUtil.resetPlayer(player);

		Bukkit.getScheduler().runTask(Meetup.getInstance(), () -> Bukkit.getOnlinePlayers().stream()
				.filter(online -> !online.canSee(player)).forEach(online -> online.showPlayer(player)));

		PaperLib.teleportAsync(player, gameHandler.getSpawnLocation());

		final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
		potPlayer.setupPlayerList();

		gamePlayer.getPlayer().sendMessage(CC.SEC + "You are no longer spectating the game.");

		gameHandler.getRemaining().add(gamePlayer);
		gameHandler.getSpectators().remove(gamePlayer);
	}
}
