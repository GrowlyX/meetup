package com.solexgames.meetup.handler;

import com.solexgames.core.util.StringUtil;
import com.solexgames.lib.commons.CommonLibsBukkit;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.task.game.GameBorderTask;
import com.solexgames.meetup.task.game.GameEndTask;
import com.solexgames.meetup.task.game.GameStartTask;
import com.solexgames.meetup.task.WorldGenTask;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.hologram.HologramFactory;
import me.lucko.helper.scheduler.threadlock.ServerThreadLock;
import me.lucko.helper.serialize.Position;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class GameHandler {

	private final Map<UUID, Hologram> statHologramMap = new HashMap<>();

	private Game game = new Game();

	private final List<GamePlayer> remaining = new ArrayList<>();
	private final List<GamePlayer> spectators = new ArrayList<>();
	private final Map<String, Integer> killTrackerMap = new HashMap<>();

	private Location spawnLocation;
	private Location meetupSpectatorLocation;

	private long lastAnnouncement;
	private String lastAnnouncer;

	private final int minPlayers = 5;

	private boolean hasEnded;
	private boolean canPlay;

	public void setupGame() {
		final World world = Bukkit.getWorld("world");
		this.spawnLocation = new Location(world, 0.5, world.getHighestBlockYAt(0, 0) + 5, 0.5);

		new WorldGenTask(this).runTaskTimer(Meetup.getInstance(), 0L, 20L);
	}

	public void checkWinners() {
		if (this.game.isState(GameState.IN_GAME) && this.remaining.size() == 1 && !this.hasEnded) {
			this.selectWinner(this.remaining.get(0));
		}
	}

	private void selectWinner(GamePlayer winner) {
		this.hasEnded = true;

		final List<String> topKills = new ArrayList<>();
		final List<Map.Entry<String, Integer>> sorted = this.killTrackerMap.entrySet().stream()
				.sorted(Comparator.comparingInt(entry -> -entry.getValue())).collect(Collectors.toList());

		for (int i = 0; i < Math.min(3, sorted.size()); i++) {
			final Map.Entry<String, Integer> entry = sorted.get(i);

			topKills.add(StringUtil.getCentered(CC.SEC + (i == 0 ? "1st" : i == 1 ? "2nd" : "3rd") + CC.GRAY + " - " + entry.getKey() + CC.GRAY + " - " + CC.PRI + entry.getValue()));
		}

		final List<String> messages = new ArrayList<>();

		messages.add(CC.GRAY + CC.S + StringUtils.repeat("-", 53));
		messages.add(StringUtil.getCentered(CC.PRI + CC.BOLD + "Meetup Game Results"));
		messages.add(StringUtil.getCentered(CC.GRAY + "Winner: " + winner.getPlayer().getDisplayName()));
		messages.add("");
		messages.add(StringUtil.getCentered(CC.PRI + CC.BOLD + "Top Kills"));
		messages.addAll(topKills);
		messages.add(CC.GRAY + CC.S + StringUtils.repeat("-", 53));

		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(messages.toArray(new String[0])));

		MeetupUtil.sendTitle(winner.getPlayer(), "&a&lWINNER", "You have won!", 0, 20 * 5, 20);

		winner.setWins(winner.getWins() + 1);

		this.game.setWinner(winner.getPlayer().getDisplayName());
		this.game.setWinnerId(winner.getUuid());

		new GameEndTask();
	}

	public void handleGameStarted() {
		this.game.setState(GameState.IN_GAME);

		this.getRemaining().forEach(gamePlayer -> {
			final Player player = gamePlayer.getPlayer();

			gamePlayer.setPlayed(gamePlayer.getPlayed() + 1);
			MeetupUtil.unsitPlayer(player);

			this.killTrackerMap.put(player.getDisplayName(), 0);
		});
		this.getSpectators().forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(CC.SEC + "You've been made a spectator as you're not playing."));
		this.clearHolograms();

		new GameBorderTask();
	}

	private void clearHolograms() {
		this.statHologramMap.forEach((player, hologram) -> {
			if (hologram != null) {
				hologram.despawn();
				hologram.closeAndReportException();
			}
		});
		this.statHologramMap.clear();
	}

	public void handleStarting() {
		this.game.setState(GameState.STARTING);

		final List<Player> remaining = this.remaining.stream().map(GamePlayer::getPlayer).collect(Collectors.toList());

		for (Player player : remaining) {
			for (Player player1 : remaining) {
				player.showPlayer(player1);
			}
		}

		Bukkit.getScheduler().runTaskLater(Meetup.getInstance(), () -> {
			this.getRemaining().forEach(gamePlayer -> {
				final Player player = gamePlayer.getPlayer();

				PaperLib.teleportAsync(player, MeetupUtil.getScatterLocation())
						.whenComplete((aBoolean, throwable) -> {
							MeetupUtil.callSync(() -> {
								MeetupUtil.resetPlayer(player);
								MeetupUtil.sitPlayer(player);

								Meetup.getInstance().getKitHandler().handleItems(player);
							});
						});
			});

			this.getSpectators().forEach(gamePlayer -> {
				final Player player = gamePlayer.getPlayer();
				final World gameWorld = Bukkit.getWorld("meetup_game");

				PaperLib.teleportAsync(player, new Location(gameWorld, 0.5, gameWorld.getHighestBlockYAt(0, 0) + 15, 0.5));

				player.sendMessage(CC.SEC + "You've been teleported the the arena as a spectator.");
			});
		}, 40L);

		new GameStartTask();
	}

	public void handleSetupHolo(Player player) {
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final HologramFactory hologramFactory = CommonLibsBukkit.getInstance().getHologramManager().getHologramFactory();

		final Location newLocation = player.getLocation().clone().add(0.0D, 2.15D, 2.15D);
		final Position position = Position.of(newLocation);
		final List<String> stringList = new ArrayList<>(Arrays.asList(
				CC.UB_PRI + "Your Stats",
				"Kills: " + gamePlayer.getKills(),
				"Deaths: " + gamePlayer.getDeaths(),
				"KDR: " + (gamePlayer.getKills() != 0 && gamePlayer.getDeaths() != 0 ? (gamePlayer.getKills() / gamePlayer.getDeaths()) : 0.0),
				"Game Wins: " + gamePlayer.getWins(),
				"Games Played: " + gamePlayer.getPlayed(),
				"Re-rolls: " + gamePlayer.getReRolls()
		));

		final Hologram hologram = hologramFactory.newHologram(position, stringList);
		MeetupUtil.callSync(hologram::spawn);

		this.statHologramMap.put(player.getUniqueId(), hologram);
	}

	public void handleLoadChunks() {
		Bukkit.getScheduler().runTaskLater(Meetup.getInstance(), () -> {
			for (int x = -110; x < 110; x++) {
				for (int z = -110; z < 110; z++) {
					final Location location = new Location(Bukkit.getWorld("meetup_game"), x, 60, z);

					if (!location.getChunk().isLoaded()) {
						location.getWorld().loadChunk(x, z);
					}
				}
			}

			final World meetupWorld = Bukkit.getWorld("meetup_game");
			final Location location = new Location(meetupWorld, 0.5D, meetupWorld.getHighestBlockYAt(0, 0) + 15, 0.5D);

			this.setMeetupSpectatorLocation(location);

			final BorderHandler borderHandler = Meetup.getInstance().getBorderHandler();
			borderHandler.setBorder(100);

			this.canPlay = true;
		}, 100L);
	}
}
