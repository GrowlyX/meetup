package com.solexgames.meetup.handler;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.apache.commons.lang.WordUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author puugz
 * @since 18/06/2021 23:35
 */
public class DeathMessageHandler {

	public String getDeathMessage(LivingEntity entity, Entity killer) {
		String output = this.getEntityName(entity) + CC.SEC;

		if (entity.getLastDamageCause() != null) {
			final String killerName = this.getEntityName(killer);

			switch (entity.getLastDamageCause().getCause()) {
				case BLOCK_EXPLOSION: case ENTITY_EXPLOSION:
					output += " was blown to smithereens";
					break;
				case CONTACT:
					output += " was pricked to death";
					break;
				case DROWNING:
					if (killer != null) {
						output += " drowned while fighting " + killerName;
					} else {
						output += " drowned";
					}
					break;
				case ENTITY_ATTACK:
					if (killer != null) {
						output += " was slain by " + killerName;

						if (killer instanceof Player) {
							final ItemStack hand = ((Player) killer).getItemInHand();
							final String handString = hand == null ? "their fists" :
									hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() ? hand.getItemMeta().getDisplayName()
											: WordUtils.capitalizeFully(hand.getType().name().replace("_", " "));

							output += CC.SEC + " using " + CC.RED + handString;
						}
					}
					break;
				case FALL:
					if (killer != null) {
						output += " hit the ground too hard thanks to " + killerName;
					} else {
						output += " hit the ground too hard";
					}
					break;
				case FALLING_BLOCK:
					break;
				case FIRE_TICK: case FIRE:
					if (killer != null) {
						output += " burned to death thanks to " + killerName;
					} else {
						output += " burned to death";
					}
					break;
				case LAVA:
					if (killer != null) {
						output += " tried to swim in lava while fighting " + killerName;
					} else {
						output += " tried to swim in lava";
					}
					break;
				case MAGIC:
					output += " died";
					break;
				case MELTING:
					output += " died of melting";
					break;
				case POISON:
					output += " was poisoned";
					break;
				case LIGHTNING:
					output += " was struck by lightning";
					break;
				case PROJECTILE:
					if (killer != null) {
						output += " was shot to death by " + killerName;
					}
					break;
				case STARVATION:
					output += " starved to death";
					break;
				case SUFFOCATION:
					output += " suffocated in a wall";
					break;
				case SUICIDE:
					output += " committed suicide";
					break;
				case THORNS:
					output += " died whilst trying to kill " + killerName;
					break;
				case VOID:
					if (killer != null) {
						output += " fell into the void thanks to " + killerName;
					} else {
						output += " fell into the void";
					}
					break;
				case WITHER:
					output += " withered away";
					break;
				case CUSTOM:
					output += " died ";
					break;
			}

		} else {
			output += " died";
		}

		return output + CC.SEC + ".";
	}

	private String getEntityName(Entity entity) {
		if (entity == null) {
			return "";
		}

		String output;

		if (entity instanceof Player) {
			final Player player = (Player) entity;
			final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

			output = player.getDisplayName() + " " + CC.GRAY + "[" + CC.RED + gamePlayer.getGameKills() + CC.GRAY + "]";
		} else {
			final String entityName = entity.getCustomName() != null ? entity.getCustomName() : entity.getType().name();

			output = CC.SEC + "a " + CC.RED + WordUtils.capitalizeFully(entityName.replace("_", ""));
		}

		return output;
	}

	public CraftEntity getKiller(Player player) {
		EntityLiving lastAttacker = ((CraftPlayer) player).getHandle().lastDamager;
		return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
	}
}
