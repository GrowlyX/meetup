package com.solexgames.meetup.listener;

import com.solexgames.core.util.Color;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author puugz
 * @since 01/06/2021 23:00
 */
public class BorderListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
                && event.getFrom().getBlockY() == event.getTo().getBlockY())
            return;

        final Player player = event.getPlayer();
        final Game game = UHCMeetup.getInstance().getGameHandler().getGame();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (game.getState().equals(GameState.STARTING)) {
            if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                player.teleport(from);
                ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                return;
            }
        }

        int size = game.getBorder();
        final World world = player.getWorld();

        if (world.getName().equalsIgnoreCase("meetup_game")) {
            if (player.getLocation().getBlockX() > size) {
                this.handleEffects(player);
                player.teleport(new Location(world, size - 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                }
            }

            if (player.getLocation().getBlockZ() > size) {
                this.handleEffects(player);
                player.teleport(new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), size - 2));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                }
            }

            if (player.getLocation().getBlockX() < -size) {
                this.handleEffects(player);
                player.teleport(new Location(world, -size + 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                }
            }

            if (player.getLocation().getBlockZ() < -size) {
                this.handleEffects(player);
                player.teleport(new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), -size + 2));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                }
            }
        }
    }

    private void handleEffects(Player player) {
        player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 2, 2);
        player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 2.0f);
        player.sendMessage(ChatColor.RED + "You were shrunk in the border.");
    }
}
