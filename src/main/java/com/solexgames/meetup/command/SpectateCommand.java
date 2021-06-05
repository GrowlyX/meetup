package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:45
 */
public class SpectateCommand extends BaseCommand {

	@CommandAlias("spectate|spec")
	public void execute(Player player) {
		final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
		final boolean spectating = gamePlayer.isSpectating();

		// on confirm
		if (true) {
			if (spectating) {
				UHCMeetup.getInstance().getSpectatorHandler().setSpectator(gamePlayer, "chose to watch", true);
			} else {
				UHCMeetup.getInstance().getSpectatorHandler().removeSpectator(gamePlayer);
			}

			return;
		}

		final Clickable clickable = new Clickable("");

		if (spectating) {
			clickable.add(CC.GRAY + "Are you sure you want to spectate? ", null, null, null);
			clickable.add(CC.GREEN + "[Click here to spectate]", null, "/spec", ClickEvent.Action.RUN_COMMAND);
		} else {
			clickable.add(CC.GRAY + "Are you sure you want to stop spectating? ", null, null, null);
			clickable.add(CC.RED + "[Click here to stop spectating]", null, "/spec", ClickEvent.Action.RUN_COMMAND);
		}

		player.spigot().sendMessage(clickable.asComponents());
	}
}
