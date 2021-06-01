package com.solexgames.meetup.game;

import com.solexgames.core.util.Color;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Player killer = event.getEntity().getKiller();

		player.setHealth(20.0D);
		player.teleport(player.getLocation());

		// handle time bomb

		event.setDroppedExp(0);

		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

		gamePlayer.setDeaths(gamePlayer.getDeaths() + 1);

		UHCMeetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "died");

		if (killer != null) {
			final GamePlayer playerKiller = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(killer);

			playerKiller.setGameKills(playerKiller.getGameKills() + 1);
			playerKiller.setKills(playerKiller.getKills() + 1);
		}

		// check winners


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
