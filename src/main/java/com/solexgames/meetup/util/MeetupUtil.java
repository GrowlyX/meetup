package com.solexgames.meetup.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.NetworkServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.Color;
import com.solexgames.meetup.Meetup;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * @author puugz
 * @since 05/06/2021 12:31
 */

public class MeetupUtil {

	public static NetworkServer getBestHub() {
		return CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
				.filter(Objects::nonNull)
				.filter(networkServer -> networkServer.getServerType().equals(NetworkServerType.HUB) && !networkServer.getServerName().contains("ds"))
				.min(Comparator.comparingInt(server -> (int) + (long) server.getOnlinePlayers()))
				.orElse(null);
	}

	public static String millisToRoundedTime(long millis) {
		return DurationFormatUtils.formatDurationWords(millis, true, true);
	}

	public static String secondsToRoundedTime(int seconds) {
		return MeetupUtil.millisToRoundedTime(seconds * 1000L);
	}

	public static void sitPlayer(Player player) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;
		final Location location = player.getLocation();
		final EntityBat bat = new EntityBat(((CraftWorld) location.getWorld()).getHandle());

		bat.setLocation(location.getX(), location.getY() + 0.5, location.getZ(), 0, 0);
		bat.setInvisible(true);
		bat.setHealth(6);

		final PacketPlayOutSpawnEntityLiving spawnEntityPacket = new PacketPlayOutSpawnEntityLiving(bat);
		craftPlayer.getHandle().playerConnection.sendPacket(spawnEntityPacket);

		player.setMetadata("seated", new FixedMetadataValue(Meetup.getInstance(), bat.getId()));

		final PacketPlayOutAttachEntity sitPacket = new PacketPlayOutAttachEntity(0, craftPlayer.getHandle(), bat);
		craftPlayer.getHandle().playerConnection.sendPacket(sitPacket);

		Meetup.getInstance().getGameHandler().handleSetupHolo(player);
	}

	public static void unsitPlayer(Player player) {
		if (player.hasMetadata("seated")) {
			final CraftPlayer craftPlayer = (CraftPlayer) player;
			final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(player.getMetadata("seated").get(0).asInt());

			craftPlayer.getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void resetPlayer(Player player) {
		MeetupUtil.resetPlayer(player, false);
	}

	public static void resetPlayer(Player player, boolean resetHeldItemSlot) {
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
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

		if (resetHeldItemSlot) player.getInventory().setHeldItemSlot(0);
		player.updateInventory();
	}
	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stayTime, int fadeOut) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;

		final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(Color.translate("{\"text\": \"" + title + "\"}")), fadeIn, stayTime, fadeOut);
		final PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(Color.translate("{\"text\": \"" + subTitle + "\"}")), fadeIn, stayTime, fadeOut);

		craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
		craftPlayer.getHandle().playerConnection.sendPacket(subTitlePacket);
	}

	public static ItemStack getGoldenHead() {
		return new ItemBuilder(Material.GOLDEN_APPLE)
				.setDurability(0)
				.setDisplayName("&6Golden Head")
				.addLore(
						"&7Consume this special apple",
						"&7to receive health boosting effects!"
				)
				.create();
	}

	public static void deleteWorld() {
		final World world = Bukkit.getWorld("meetup_game");

		if (world != null) {
			Bukkit.getServer().unloadWorld(world, false);

			MeetupUtil.deleteFile(world.getWorldFolder());
		}
	}

	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				if (!deleteFile(subFile)) {
					return false;
				}
			}
		}

		return file.delete();
	}

	public static void respawnPlayer(PlayerDeathEvent event) {
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle").invoke(event.getEntity());
					Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

					Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

					Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
					minecraftServer.setAccessible(true);
					Object mcserver = minecraftServer.get(con);

					Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
					Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);

					moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskLater(Meetup.getInstance(), 2L);
	}

	public static Location getScatterLocation() {
		final Random r = new Random();

		final int x = r.nextInt(100 * 2) - 100;
		final int z = r.nextInt(100 * 2) - 100;

		final World world = Bukkit.getWorld("meetup_game");

		return new Location(world, x, world.getHighestBlockYAt(x, z) + 1, z);
	}
}
