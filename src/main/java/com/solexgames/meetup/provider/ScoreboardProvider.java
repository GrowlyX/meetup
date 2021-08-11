package com.solexgames.meetup.provider;

import com.solexgames.core.util.PlayerUtil;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import io.github.nosequel.scoreboard.element.ScoreboardElement;
import io.github.nosequel.scoreboard.element.ScoreboardElementHandler;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author puugz
 * @since 18/06/2021 22:57
 */
public class ScoreboardProvider implements ScoreboardElementHandler {

	@Override
	public ScoreboardElement getElement(Player player) {
		final ScoreboardElement element = new ScoreboardElement();

		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		final List<GamePlayer> remaining = gameHandler.getRemaining();

		element.setTitle(CC.B_PRI + "UHC Meetup");
		element.add(CC.GRAY + CC.S + "-------------------");

		switch (game.getState()) {
			case WAITING:
				final int more = gameHandler.getMinPlayers() - remaining.size();

				element.add("Waiting for " + CC.PRI + more + CC.WHITE + " more player" + (more == 1 ? "" : "s"));
				element.add("to join the game.");
				break;
			case STARTING:
				element.add("The game will start in:");
				element.add(CC.PRI + (game.getGameStartTime() + 1));
				break;
			case IN_GAME:
				element.add("Border: " + CC.PRI + game.getNextBorder() + game.getFormattedBorderStatus());
				element.add("Players: " + CC.PRI + gameHandler.getRemaining().size());
				element.add("Ping: " + CC.PRI + PlayerUtil.getPing(player) + " ms");
				element.add("Kills: " + CC.PRI + gamePlayer.getGameKills());

				if (gamePlayer.getNoCleanTimer() != null) {
					element.add(CC.B_PRI + "Cooldowns:");
					element.add(CC.GRAY + " * " + CC.WHITE + "No Clean: " + CC.PRI + gamePlayer.getNoCleanTimer().getTime());
				}
				break;
		}

		element.add("");
		element.add(CC.PRI + "pvp.bar");
		element.add(CC.GRAY + CC.S + "-------------------");

		return element;
	}
}
