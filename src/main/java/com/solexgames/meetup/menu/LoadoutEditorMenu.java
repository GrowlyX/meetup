package com.solexgames.meetup.menu;

import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 6/6/2021
 */

public class LoadoutEditorMenu extends Menu {

    private static final ItemStack RED_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .setDurability(14)
            .setDisplayName(ChatColor.RED + "Do not touch!")
            .addLore(
                    "&7You shouldn't be able",
                    "&7to add items to your",
                    "&7own inventory while",
                    "&7editing loadouts!"
            )
            .create();

    @Override
    public String getTitle(Player player) {
        return "Editing Loadout";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

        for (int i = 0; i <= 36; i++) {
            final int finalSlot = i;

            buttonMap.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return gamePlayer.getLoadout().getFromInteger(finalSlot);
                }
            });
        }

        return buttonMap;
    }

//    @Override
//    public void onOpen(Player player) {
//        player.getInventory().clear();
//        player.sendMessage(Locale.LAYOUT_OPEN_EDITOR.formatLinesArray());
//
//        while (player.getInventory().firstEmpty() != -1) {
//            final int firstEmpty = player.getInventory().firstEmpty();
//
//            player.getInventory().setItem(firstEmpty, LoadoutEditorMenu.RED_GLASS);
//        }
//    }

//    @Override
//    public void onClose(Player player) {
//        final GamePlayer gamePlayer = CorePlugin.getInstance().getPlayerHandler().getByName(player.getName());
//
//        for (int i = 0; i <= 8; i++) {
//            gamePlayer.getLayout().getItemStacks()[i] = this.getInventory().getItem(i);
//        }
//
//        player.sendMessage(Locale.LAYOUT_MODIFIED.formatLinesArray());
//
//        CorePlugin.getInstance().getHotbarHandler().setupLobbyHotbar(player);
//    }
}
