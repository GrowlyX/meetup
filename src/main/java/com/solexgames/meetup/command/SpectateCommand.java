package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.lib.acf.annotation.Default;
import com.solexgames.lib.acf.annotation.Subcommand;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:45
 */

@CommandAlias("spectate|spec")
public class SpectateCommand extends BaseCommand {

	@Default
	public void onDefault(Player player) {
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final boolean spectating = gamePlayer.isSpectating();

		final Clickable clickable = new Clickable("");

		if (!spectating) {
			clickable.add(CC.GRAY + "Are you sure you want to spectate?\n", null, null, null);
			clickable.add(CC.GREEN + "[Click here to spectate]", null, "/spec confirm", ClickEvent.Action.RUN_COMMAND);
		} else {
			clickable.add(CC.GRAY + "Are you sure you want to stop spectating?\n", null, null, null);
			clickable.add(CC.RED + "[Click here to stop spectating]", null, "/spec confirm", ClickEvent.Action.RUN_COMMAND);
		}

		player.spigot().sendMessage(clickable.asComponents());
	}

	@Subcommand("confirm")
	public void onConfirm(Player player) {
		final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
		final boolean spectating = gamePlayer.isSpectating();

		if (Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
			player.sendMessage(CC.RED + "Error: You cannot stop spectating when there is a game running.");
			return;
		}

		if (!spectating) {
			Meetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "chose to watch", true);
		} else {
			if (Meetup.getInstance().getGameHandler().getGame().isState(GameState.STARTING)) {
				player.sendMessage(CC.RED + "Error: You cannot stop spectating when there is a game starting.");
				return;
			}

			Meetup.getInstance().getSpectatorHandler().removeSpectator(gamePlayer);
		}
	}
}
