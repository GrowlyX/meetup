package com.solexgames.meetup.util;

import com.solexgames.core.util.Color;
import com.solexgames.meetup.UHCMeetup;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@UtilityClass
public class PlayerUtil {

	public static void sitPlayer(Player player) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;
		final Location location = player.getLocation();
		final EntityBat bat = new EntityBat(((CraftWorld) location.getWorld()).getHandle());

		bat.setLocation(location.getX(), location.getY() + 0.5, location.getZ(), 0, 0);
		bat.setInvisible(true);
		bat.setHealth(6);

		final PacketPlayOutSpawnEntityLiving spawnEntityPacket = new PacketPlayOutSpawnEntityLiving(bat);
		craftPlayer.getHandle().playerConnection.sendPacket(spawnEntityPacket);

		player.setMetadata("seated", new FixedMetadataValue(UHCMeetup.getInstance(), bat.getId()));

		final PacketPlayOutAttachEntity sitPacket = new PacketPlayOutAttachEntity(0, craftPlayer.getHandle(), bat);
		craftPlayer.getHandle().playerConnection.sendPacket(sitPacket);
	}

	public void unsitPlayer(Player player) {
		if (player.hasMetadata("seated")) {
			final CraftPlayer craftPlayer = (CraftPlayer) player;
			final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(player.getMetadata("seated").get(0).asInt());

			craftPlayer.getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void resetPlayer(Player player) {
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);

		PlayerUtil.restorePlayer(player);
	}

	public static void restorePlayer(Player player) {
		player.getActivePotionEffects().clear();

		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0f);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setSaturation(20);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);

		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

		player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stayTime, int fadeOut) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;

		final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(Color.translate("{\"text\": \"" + title + "\"}")), fadeIn, stayTime, fadeOut);
		final PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(Color.translate("{\"text\": \"" + subTitle + "\"}")), fadeIn, stayTime, fadeOut);

		craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
		craftPlayer.getHandle().playerConnection.sendPacket(subTitlePacket);
	}
}
