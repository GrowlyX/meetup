package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.menu.LoadoutEditorMenu;
import com.solexgames.meetup.util.CC;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:44
 */
public class LoadoutCommand extends BaseCommand {

	@CommandAlias("loadout")
	public void execute(Player player) {
		new LoadoutEditorMenu().openMenu(player);
	}
}
