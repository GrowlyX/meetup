package com.solexgames.meetup.game;

import com.solexgames.core.util.Color;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.board.Board;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.player.PlayerState;
import com.solexgames.meetup.scenario.impl.NoCleanScenario;
import com.solexgames.meetup.scenario.impl.TimeBombScenario;
import com.solexgames.meetup.task.GameStartTask;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtils;
import com.solexgames.meetup.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GameListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		UHCMeetup.getInstance().getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, UHCMeetup.getInstance().getBoardManager().getAdapter()));
		UHCMeetup.getInstance().getPlayerHandler().insert(player.getUniqueId(), new GamePlayer(player, player.getName()));

		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
		final GameHandler gameHandler = UHCMeetup.getInstance().getGameHandler();

		switch (gameHandler.getGame().getState()) {
			case WAITING:
				final World lobbyWorld = Bukkit.getWorld("world"); // lobby

				player.teleport(new Location(lobbyWorld, 0, lobbyWorld.getHighestBlockYAt(0, 0) + 6, 0));

				gamePlayer.setState(PlayerState.WAITING);

				final int waiting = gameHandler.getRemainingPlayers().size();
				final int minPlayers = gameHandler.getMinPlayers();

				if (waiting >= minPlayers) {
					gameHandler.handleStarting();
				}
				else {
					final int more = minPlayers - waiting;
					Bukkit.broadcastMessage(CC.SEC + "The game requires " + CC.PRI + more + CC.SEC + " player" + (more == 1 ? "" : "s") + " to start.");
				}

				break;
			case STARTING:
				player.teleport(MeetupUtils.getScatterLocation());

				PlayerUtil.sitPlayer(player);

				// TODO: 05/06/2021 Load kit with the player's layout
				UHCMeetup.getInstance().getKitManager().handleItems(player);

				gamePlayer.setState(PlayerState.PLAYING);

				break;
			case IN_GAME:
				final World gameWorld = Bukkit.getWorld("meetup_game");

				player.sendMessage(CC.SEC + "You've been made a spectator as you've joined too late into the game.");
				player.teleport(new Location(gameWorld, 0, gameWorld.getHighestBlockYAt(0, 0) + 15, 0));

				UHCMeetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, null);

				gamePlayer.setState(PlayerState.PLAYING);
				break;
		}

		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

		UHCMeetup.getInstance().getBoardManager().getPlayerBoards().remove(player.getUniqueId());

		if (gamePlayer != null) {
			gamePlayer.savePlayerData(true);
		}

		event.setQuitMessage(null);
 	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Player killer = event.getEntity().getKiller();

		player.setHealth(20.0D);
		player.teleport(player.getLocation());

		final List<ItemStack> items = new ArrayList<>();

		Stream.of(player.getInventory().getArmorContents()).filter(stack -> stack != null && stack.getType() != Material.AIR).forEach(items::add);
		Stream.of(player.getInventory().getContents()).filter(stack -> stack != null && stack.getType() != Material.AIR).forEach(items::add);

		UHCMeetup.getInstance().getScenario(TimeBombScenario.class).handleTimeBomb(player, event.getDrops(), items);

		event.setDroppedExp(0);

		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

		gamePlayer.setDeaths(gamePlayer.getDeaths() + 1);

		UHCMeetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "died");

		if (killer != null) {
			final GamePlayer playerKiller = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(killer);

			playerKiller.setGameKills(playerKiller.getGameKills() + 1);
			playerKiller.setKills(playerKiller.getKills() + 1);

			UHCMeetup.getInstance().getScenario(NoCleanScenario.class).handleNoClean(playerKiller);
		}

		UHCMeetup.getInstance().getGameHandler().checkWinners();
	}

	@EventHandler
	public void onHorseSetup(CreatureSpawnEvent event) {
		if (event.getEntityType() != EntityType.HORSE || event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
			return;
		}
		final Horse horse = (Horse) event.getEntity();

		horse.setAdult();
		horse.setAgeLock(true);

		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

		horse.setTamed(true);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();

		if (item == null
				|| item.getType() != Material.GOLDEN_APPLE
				|| item.getItemMeta() == null
				|| !item.getItemMeta().hasDisplayName()
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(Color.translate("&6Golden Head"))) {
			return;
		}

		final Player player = event.getPlayer();

		player.removePotionEffect(PotionEffectType.REGENERATION);
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
	}

	@EventHandler
	public void onEntityDamageByEntityBow(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		final Player entity = (Player) event.getEntity();

		if (!(event.getDamager() instanceof Arrow)) return;

		final Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player)) return;

		final Player shooter = (Player) arrow.getShooter();

		if (entity.getName().equals(shooter.getName())) return;

		final double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

		if (health > 0.0D) {
			shooter.sendMessage(entity.getDisplayName() + CC.SEC + " is now at " + CC.PRI + health + "\u2764" + CC.SEC + ".");
		}
	}
}
