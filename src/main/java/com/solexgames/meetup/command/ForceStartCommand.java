package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.lib.acf.annotation.CommandPermission;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 06/06/2021 01:04
 */
public class ForceStartCommand extends BaseCommand {

	@CommandAlias("forcestart|fs")
	@CommandPermission("game.command.forcestart")
	public void execute(CommandSender sender) {
		final GameHandler gameHandler = Meetup.getInstance().getGameHandler();
		final Game game = gameHandler.getGame();

		if (!gameHandler.isCanPlay()) {
			sender.sendMessage(ChatColor.RED + "The map's currently generating, sorry.");
			return;
		}

		if (!game.isState(GameState.STARTING)) {
			if (Bukkit.getOnlinePlayers().size() < 2) {
				sender.sendMessage(ChatColor.RED + "There must be at least two players online for you to force-start the game.");
				return;
			}

			gameHandler.handleStarting();
			game.setGameStartTime(10);

			Bukkit.broadcastMessage((sender instanceof Player ? ((Player) sender).getDisplayName() : CC.D_RED + "Console") + CC.SEC + " force-started the game.");
			return;
		}

		if (game.getGameStartTime() < 10) {
			sender.sendMessage(CC.RED + "Error: The game has already started, or is starting soon.");
			return;
		}

		game.setGameStartTime(10);
		Bukkit.broadcastMessage((sender instanceof Player ? ((Player) sender).getDisplayName() : CC.D_RED + "Console") + CC.SEC + " force-started the game.");
	}
}
