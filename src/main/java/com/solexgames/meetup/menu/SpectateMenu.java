package com.solexgames.meetup.menu;

import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author puugz
 * @since 01/06/2021 20:48
 */
public class SpectateMenu extends PaginatedMenu {

	public SpectateMenu() {
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
		final Map<Integer, Button> buttons = new HashMap<>();

		for (GamePlayer gamePlayer : UHCMeetup.getInstance().getGameHandler().getRemainingPlayers()) {
			buttons.put(buttons.size(), new PlayerButton(gamePlayer));
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class PlayerButton extends Button {

		private final GamePlayer gamePlayer;

		@Override
		public ItemStack getButtonItem(Player player) {
			final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			final SkullMeta meta = (SkullMeta) item.getItemMeta();

			meta.setOwner(this.gamePlayer.getName());
			meta.setDisplayName(this.gamePlayer.getPlayer().getDisplayName());
			meta.setLore(Collections.singletonList(CC.SEC + "Click to teleport to " + this.gamePlayer.getPlayer().getDisplayName() + CC.SEC + "."));

			item.setItemMeta(meta);

			return item;
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			final Player target = this.gamePlayer.getPlayer();

			if (target != null) {
				player.teleport(target);
			}
		}
	}
}
