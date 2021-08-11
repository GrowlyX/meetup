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
				element.add("Waiting for players");

				if (remaining.size() < gameHandler.getMinPlayers()) {
					final int more = gameHandler.getMinPlayers() - remaining.size();
					element.add(CC.PRI + more + CC.WHITE + " more player" + (more == 1 ? "" : "s") + ".");
				}
				break;
			case STARTING:
				element.add("The game will start in:");
				element.add(CC.PRI + (game.getGameStartTime() + 1));
				break;
			case IN_GAME:
				element.add("Border: " + CC.PRI + game.getNextBorder() + game.getFormattedBorderStatus());
				element.add("Remaining: " + CC.PRI + gameHandler.getRemaining().size() + "/" + gameHandler.getInitialPlayers());
				element.add("Your Ping: " + CC.PRI + this.getFormattedPing(PlayerUtil.getPing(player)));
				element.add("Kills: " + CC.PRI + gamePlayer.getGameKills());

				if (gamePlayer.getNoCleanTimer() != null) {
					element.add(CC.B_PRI + "Cooldowns:");
					element.add(CC.GRAY + " * " + CC.WHITE + "No Clean: " + CC.PRI + gamePlayer.getNoCleanTimer().getTime());

				}
				break;
			case ENDING:
				element.add(CC.B_PRI + "Top 3 kills:");
				gameHandler.getScoreboardEndingLines().forEach(element::add);
				break;
		}

		element.add("");
		element.add(CC.PRI + "pvp.bar");
		element.add(CC.GRAY + CC.S + "-------------------");

		return element;
	}

	private String getFormattedPing(int ping) {
		if (ping > 300) {
			return CC.D_RED + ping + " ms";
		} else if (ping > 150){
			return CC.PRI + ping + " ms";
		} else if (ping > 80){
			return CC.YELLOW + ping + " ms";
		} else {
			return CC.GREEN + ping + " ms";
		}
	}
}
