package com.solexgames.meetup.handler;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.task.BorderTask;
import com.solexgames.meetup.task.GameStartTask;
import com.solexgames.meetup.task.WorldGenTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
public class GameHandler {

	private Game game;

	private final List<Material> whitelistedBlocks = new ArrayList<>();

	private int minPlayers = 2;

	// todo:
	// private final List<GamePlayer> remaining;
	// private final List<GamePlayer> spectators;

	public void setupGame() {
		this.game = new Game();
		new WorldGenTask(this).runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	public boolean isRunning() {
		return this.game.getState().equals(GameState.IN_GAME);
	}

	public List<GamePlayer> getRemainingPlayers() {
		return UHCMeetup.getInstance().getPlayerHandler().getPlayerTypeMap().values().stream()
				.filter(gamePlayer -> !gamePlayer.isSpectating())
				.collect(Collectors.toList());
	}

	public void begin() {
		this.game.setState(GameState.IN_GAME);

		final List<GamePlayer> gamePlayers = this.getRemainingPlayers();
		gamePlayers.forEach(gamePlayer -> gamePlayer.setPlayed(gamePlayer.getPlayed() + 1));

		this.game.setRemaining(gamePlayers.size());
		this.game.setInitial(gamePlayers.size());

		new BorderTask();
	}

	public void start() {
		this.game.setState(GameState.STARTING);
		new GameStartTask();
	}

	public void checkWinner() {

	}

	public void handleSetWhitelistedBlocks() {
		this.whitelistedBlocks.add(Material.LOG);
		this.whitelistedBlocks.add(Material.LOG_2);
		this.whitelistedBlocks.add(Material.WOOD);
		this.whitelistedBlocks.add(Material.LEAVES);
		this.whitelistedBlocks.add(Material.LEAVES_2);
		this.whitelistedBlocks.add(Material.WATER);
		this.whitelistedBlocks.add(Material.STATIONARY_WATER);
		this.whitelistedBlocks.add(Material.LAVA);
		this.whitelistedBlocks.add(Material.STATIONARY_LAVA);
		this.whitelistedBlocks.add(Material.LONG_GRASS);
		this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
		this.whitelistedBlocks.add(Material.COBBLESTONE);
		this.whitelistedBlocks.add(Material.CACTUS);
		this.whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
		this.whitelistedBlocks.add(Material.DOUBLE_PLANT);
		this.whitelistedBlocks.add(Material.OBSIDIAN);
		this.whitelistedBlocks.add(Material.SNOW);
		this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
		this.whitelistedBlocks.add(Material.RED_ROSE);
		this.whitelistedBlocks.add(Material.BROWN_MUSHROOM);
		this.whitelistedBlocks.add(Material.WEB);
		this.whitelistedBlocks.add(Material.ANVIL);
		this.whitelistedBlocks.add(Material.DEAD_BUSH);
		this.whitelistedBlocks.add(Material.RED_MUSHROOM);
		this.whitelistedBlocks.add(Material.HUGE_MUSHROOM_1);
		this.whitelistedBlocks.add(Material.HUGE_MUSHROOM_2);
	}

	public void handleLoadChunks() {
		Bukkit.getScheduler().runTaskLater(UHCMeetup.getInstance(), () -> {
			for (int x = -110; x < 110; x++) {
				for (int z = -110; z < 110; z++) {
					Location location = new Location(Bukkit.getWorld("meetupworld"), x, 60, z);

					if (!location.getChunk().isLoaded()) {
						location.getWorld().loadChunk(x, z);
					}
				}
			}
		}, 100L);

		Bukkit.getScheduler().runTaskLater(UHCMeetup.getInstance(), () -> this.game.setGenerated(true), 200L);
	}
}
