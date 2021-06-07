package com.solexgames.meetup.handler;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.player.PlayerState;
import com.solexgames.meetup.task.BorderTask;
import com.solexgames.meetup.task.GameEndTask;
import com.solexgames.meetup.task.GameStartTask;
import com.solexgames.meetup.task.WorldGenTask;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtils;
import com.solexgames.meetup.util.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class GameHandler {

	private Game game = new Game();

	private final Set<Material> whitelistedBlocks = new HashSet<>();

	private final int minimumPlayers = 5;
	private boolean hasBeenBroadcasted = false;

	private long lastAnnouncement = 0L;
	private String lastAnnouncer;

	public void setupGame() {
		new WorldGenTask(this).runTaskTimer(UHCMeetup.getInstance(), 0L, 20L);
	}

	public List<GamePlayer> getRemainingPlayers() {
		return UHCMeetup.getInstance().getPlayerHandler().getPlayerTypeMap().values().stream()
				.filter(gamePlayer -> !gamePlayer.getState().equals(PlayerState.SPECTATING))
				.filter(gamePlayer -> gamePlayer.getPlayer() != null || gamePlayer.getPlayer().isOnline())
				.collect(Collectors.toList());
	}

	public List<GamePlayer> getSpectators() {
		return UHCMeetup.getInstance().getPlayerHandler().getPlayerTypeMap().values().stream()
				.filter(GamePlayer::isSpectating)
				.filter(gamePlayer -> gamePlayer.getPlayer() != null || gamePlayer.getPlayer().isOnline())
				.collect(Collectors.toList());
	}

	public void handleStart() {
		this.game.setState(GameState.IN_GAME);

		final List<GamePlayer> gamePlayers = this.getRemainingPlayers();

		gamePlayers.forEach(gamePlayer -> {
			final Player player = gamePlayer.getPlayer();
			gamePlayer.setPlayed(gamePlayer.getPlayed() + 1);

			PlayerUtil.unsitPlayer(player);
		});

		UHCMeetup.getInstance().getGameHandler().getSpectators()
				.forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(CC.SEC + "You've been made a spectator as you're not playing."));

		this.game.setRemaining(gamePlayers.size());
		this.game.setInitial(gamePlayers.size());

		new BorderTask();
	}

	public void handleStarting() {
		this.game.setState(GameState.STARTING);

		Bukkit.getScheduler().runTaskLater(UHCMeetup.getInstance(), () -> this.getRemainingPlayers().forEach(gamePlayer -> {
			final Player player = gamePlayer.getPlayer();

			gamePlayer.setState(PlayerState.PLAYING);

			Bukkit.getScheduler().runTask(UHCMeetup.getInstance(), () -> {
				PlayerUtil.resetPlayer(player);
				PlayerUtil.sitPlayer(player);

				// TODO: 05/06/2021 load kit with layout
				UHCMeetup.getInstance().getKitManager().handleItems(player);
			});

			player.teleport(MeetupUtils.getScatterLocation());
		}), 40L);

		new GameStartTask();
	}

	public void checkWinners() {
		if (this.game.isState(GameState.STARTING)) {
			return;
		}

		if (this.getRemainingPlayers().size() == 1 && !this.hasBeenBroadcasted) {
			this.getRemainingPlayers().forEach(this::selectWinner);
		}
	}

	private void selectWinner(GamePlayer winner) {
		this.hasBeenBroadcasted = true;

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(winner.getPlayer().getDisplayName() + CC.GREEN + " wins!");
		Bukkit.broadcastMessage("");

		winner.setWins(winner.getWins() + 1);

		this.game.setWinner(winner.getPlayer().getDisplayName());

		new GameEndTask();
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
					final Location location = new Location(Bukkit.getWorld("meetup_game"), x, 60, z);

					if (!location.getChunk().isLoaded()) {
						location.getWorld().loadChunk(x, z);
					}
				}
			}
		}, 100L);

		Bukkit.getScheduler().runTaskLater(UHCMeetup.getInstance(), () -> this.game.setGenerated(true), 200L);
	}
}
