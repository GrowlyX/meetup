package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.meetup.UHCMeetup;
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
		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
		final boolean spectating = gamePlayer.isSpectating();

		if (UHCMeetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
			player.sendMessage(CC.RED + "You can't do this anymore.");
			return;
		}

		final Clickable clickable = new Clickable("");

		if (!spectating) {
			clickable.add(CC.GRAY + "Are you sure you want to spectate? ", null, null, null);
			clickable.add(CC.GREEN + "[Click here to spectate]", null, "/spec confirm", ClickEvent.Action.RUN_COMMAND);
		} else {
			clickable.add(CC.GRAY + "Are you sure you want to stop spectating? ", null, null, null);
			clickable.add(CC.RED + "[Click here to stop spectating]", null, "/spec confirm", ClickEvent.Action.RUN_COMMAND);
		}

		player.spigot().sendMessage(clickable.asComponents());
	}

	@Subcommand("confirm")
	public void onConfirm(Player player) {
		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
		final boolean spectating = gamePlayer.isSpectating();

		if (!spectating) {
			UHCMeetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "chose to watch", true);
		} else {
			UHCMeetup.getInstance().getSpectatorHandler().removeSpectator(gamePlayer);
		}
	}
}
