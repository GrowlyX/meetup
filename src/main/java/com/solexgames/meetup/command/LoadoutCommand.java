package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.meetup.menu.LoadoutEditorMenu;
import org.bukkit.entity.Player;

/**
 * @author puugz
 * @since 01/06/2021 18:44
 */
public class LoadoutCommand extends BaseCommand {

	@CommandAlias("loadout|loadout")
	public void execute(Player player) {
		new LoadoutEditorMenu().openMenu(player);
	}
}
