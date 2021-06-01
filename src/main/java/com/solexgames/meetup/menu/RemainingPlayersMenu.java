package com.solexgames.meetup.menu;

import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author puugz
 * @since 01/06/2021 20:48
 */
public class RemainingPlayersMenu extends PaginatedMenu {

	public RemainingPlayersMenu() {
		super(27);
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		return null;
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return "Remaining Players";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		return null;
	}
}
