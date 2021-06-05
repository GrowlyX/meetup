package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 06/06/2021 01:04
 */
public class ForceStartCommand extends BaseCommand {

	@CommandAlias("forcestart|fs")
	@CommandPermission("uhcmeetup.command.forcestart")
	public void execute(CommandSender sender) {
		final GameHandler gameHandler = UHCMeetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		if (!game.isState(GameState.STARTING) || game.getGameStartTime() < 10) {
			sender.sendMessage(CC.RED + "I'm sorry, but you can't do this anymore.");
			return;
		}

		game.setGameStartTime(10);
		Bukkit.broadcastMessage((sender instanceof Player ? ((Player)sender).getDisplayName() : CC.D_RED + "Console") + CC.SEC + " force started the game.");
	}
}
