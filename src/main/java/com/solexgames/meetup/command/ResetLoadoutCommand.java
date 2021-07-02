package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.util.CC;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:45
 */
public class ResetLoadoutCommand extends BaseCommand {

	@CommandAlias("resetloadout")
	public void execute(Player player) {
		Meetup.getInstance().getPlayerHandler().getByPlayer(player)
				.getLoadout().setupDefaultInventory();

		player.sendMessage(CC.GREEN + "You've reset your loadout.");
	}
}
