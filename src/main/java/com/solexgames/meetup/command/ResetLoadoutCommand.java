package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.util.CC;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:45
 */
public class ResetLoadoutCommand extends BaseCommand {

	@CommandAlias("resetloadout")
	public void execute(Player player) {
		UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player)
				.getLoadout().setupDefaultInventory();

		player.sendMessage(CC.GREEN + "You've reset your loadout.");
	}
}
