package com.solexgames.meetup.listener;

import com.solexgames.core.util.Color;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.handler.DeathMessageHandler;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.scenario.impl.NoCleanScenario;
import com.solexgames.meetup.scenario.impl.TimeBombScenario;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
	public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
		Meetup.getInstance().getPlayerHandler().insert(event.getUniqueId(), new GamePlayer(event.getUniqueId(), event.getName()));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();

		Meetup.getInstance().getPlayerHandler().setupInventory(player);

		switch (gameHandler.getGame().getState()) {
			case WAITING:
				gameHandler.getRemaining().add(gamePlayer);

				player.teleport(gameHandler.getSpawnLocation());

				final int waiting = gameHandler.getRemaining().size();
				final int minPlayers = gameHandler.getMinPlayers();

				if (waiting >= minPlayers) {
					gameHandler.handleStarting();
				} else {
					final int more = minPlayers - waiting;
					Bukkit.broadcastMessage(CC.SEC + "The game requires " + CC.PRI + more + CC.SEC + " player" + (more == 1 ? "" : "s") + " to start.");
				}
				break;
			case STARTING:
				gameHandler.getRemaining().add(gamePlayer);

				player.teleport(MeetupUtil.getScatterLocation());
				MeetupUtil.sitPlayer(player);

				Meetup.getInstance().getKitHandler().handleItems(player);
				break;
			case IN_GAME:
				player.sendMessage(CC.SEC + "You've been made a spectator as you've joined too late into the game.");
				player.teleport(gameHandler.getMeetupSpectatorLocation());

				Meetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, null, false);
				break;
		}

		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();

		if (gamePlayer != null) {
			gamePlayer.savePlayerData(true);

			if (gamePlayer.isPlaying() && Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
				Bukkit.broadcastMessage(player.getDisplayName() + CC.SEC + " has disconnected and was disqualified.");

				Meetup.getInstance().getGameHandler().checkWinners();
			}

			gameHandler.getRemaining().remove(gamePlayer);
			gameHandler.getSpectators().remove(gamePlayer);
		}

		event.setQuitMessage(null);
 	}

	@EventHandler(
			priority = EventPriority.LOWEST,
			ignoreCancelled = true
	)
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Player killer = event.getEntity().getKiller();

		final DeathMessageHandler deathMessageHandler = Meetup.getInstance().getDeathMessageHandler();
		final CraftEntity craftKiller = deathMessageHandler.getKiller(player);

		event.setDeathMessage(deathMessageHandler.getDeathMessage(player, craftKiller));

		player.spigot().respawn();
		player.setHealth(20.0D);
		player.teleport(player.getLocation());

		final List<ItemStack> items = new ArrayList<>();

		Stream.of(player.getInventory().getArmorContents())
				.filter(stack -> stack != null && stack.getType() != Material.AIR)
				.forEach(items::add);
		Stream.of(player.getInventory().getContents())
				.filter(stack -> stack != null && stack.getType() != Material.AIR)
				.forEach(items::add);

		Meetup.getInstance().getScenario(TimeBombScenario.class)
				.handleTimeBomb(player, event.getDrops(), items);

		event.setDroppedExp(0);

		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

		gamePlayer.setDeaths(gamePlayer.getDeaths() + 1);

		if (killer != null) {
			final GamePlayer playerKiller = Meetup.getInstance().getPlayerHandler().getByPlayer(killer);

			playerKiller.setGameKills(playerKiller.getGameKills() + 1);
			playerKiller.setKills(playerKiller.getKills() + 1);

			Meetup.getInstance().getGameHandler().getKillTrackerMap().put(killer.getDisplayName(), playerKiller.getKills());

			Meetup.getInstance().getScenario(NoCleanScenario.class).handleNoClean(playerKiller);
		}

		Meetup.getInstance().getGameHandler().checkWinners();
		Meetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "died", true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		final Player player = (Player) event.getEntity();
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

		if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
			if (gamePlayer.isPlaying() && Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
				player.setHealth(0);
			} else {
				event.setCancelled(true);
				player.teleport(player.getWorld().getSpawnLocation());
			}
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();

		if (item == null || item.getType() != Material.GOLDEN_APPLE
				|| item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()
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
