package com.solexgames.meetup.scoreboard;

import com.solexgames.core.util.PlayerUtil;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.board.Board;
import com.solexgames.meetup.board.BoardAdapter;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class ScoreboardAdapter implements BoardAdapter {

	@Override
	public List<String> getScoreboard(Player player, Board unused) {
		final List<String> board = new ArrayList<>();

		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
		final GameHandler gameHandler = UHCMeetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		board.add(CC.GRAY + CC.S + "--------------------");

		switch (game.getState()) {
			case WAITING:
				board.add("Waiting for players");

				final List<GamePlayer> remaining = gameHandler.getRemainingPlayers();

				if (remaining.size() < gameHandler.getMinimumPlayers()) {
					final int more = gameHandler.getMinimumPlayers() - remaining.size();
					board.add(CC.PRI + more + CC.RESET + " more player" + (more == 1 ? "" : "s") + ".");
				}
				break;
			case STARTING:
				board.add("The game will start in:");
				board.add(CC.PRI + game.getGameStartTime());
				break;
			case IN_GAME:
				board.add("Border: " + CC.PRI + game.getNextBorder() + game.getFormattedBorderStatus());
				board.add("Players: " + CC.PRI + gameHandler.getRemainingPlayers().size());
				board.add("Ping: " + CC.PRI + PlayerUtil.getPing(player) + " ms");
				board.add("Kills: " + CC.PRI + gamePlayer.getGameKills());

				if (gamePlayer.getNoCleanTimer() != null) {
					board.add("");
					board.add(CC.RED + "No Clean: " + CC.PRI + gamePlayer.getNoCleanTimer().getTime());
				}
				break;
		}

		board.add("");
		board.add(CC.PRI + "pvp.bar");
		board.add(CC.GRAY + CC.S + "--------------------");

		return board;
	}

	@Override
	public String getTitle(Player player) {
		return CC.B_PRI + "UHC Meetup";
	}

	@Override
	public long getInterval() {
		return 5;
	}

	@Override
	public void onScoreboardCreate(Player player, Scoreboard board) {
//        Team ghostTeam = board.getTeam("ghost");
//
//        if (ghostTeam == null) {
//        	ghostTeam = board.registerNewTeam("ghost");
//		}
	}

	@Override
	public void preLoop() {

	}
}
