package com.solexgames.meetup.handler;

import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.lib.commons.game.PlayerCache;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author puugz
 * @since 19/06/2021 22:31
 */
@Getter
public class PlayerHandler extends PlayerCache<GamePlayer> {

	private final ItemStack kitSelector;
	private final ItemStack backToLobby;

	public PlayerHandler() {
		this.kitSelector = new ItemBuilder(Material.BOOK)
				.setDisplayName(CC.PRI + "Kit Selector " + CC.GRAY + "(Right Click)")
				.create();
		this.backToLobby = new ItemBuilder(Material.BED)
				.setDisplayName(CC.RED + "Back to Lobby " + CC.GRAY + "(Right Click)")
				.create();
	}

	public void setupInventory(Player player) {
		MeetupUtil.resetPlayer(player, true);

		player.getInventory().setItem(0, this.kitSelector);
		player.getInventory().setItem(8, this.backToLobby);

		player.updateInventory();
	}
}
