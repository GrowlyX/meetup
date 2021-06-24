package com.solexgames.meetup.menu;

import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
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
    public int getSize() {
        return 36;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Loadout";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

        gamePlayer.getLoadout().getInventoryLocationMap()
                .forEach((integer, itemStack) -> buttonMap.put(
                        integer >= 0 && integer <= 8 ? integer + 27 : integer - 9,
                        new ItemBuilder(itemStack).toButton()
                ));

        return buttonMap;
    }

    @Override
    public void onOpen(Player player) {
        player.getInventory().clear();

        while (player.getInventory().firstEmpty() != -1) {
            final int firstEmpty = player.getInventory().firstEmpty();

            player.getInventory().setItem(firstEmpty, LoadoutEditorMenu.RED_GLASS);
        }

        player.updateInventory();
        player.sendMessage(CC.GREEN + "You're now editing your loadout.");
    }

    @Override
    public void onClose(Player player) {
        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

        for (int i = 0; i <= 35; i++) {
            gamePlayer.getLoadout().getInventoryLocationMap()
                    .put(i >= 27 ? i - 27 : i + 9, player.getOpenInventory().getItem(i));
        }

        player.sendMessage(CC.SEC + "You've modified your loadout!");
        player.sendMessage(CC.I_GRAY + "If you need to reset your loadout try " + CC.I_YELLOW + "/resetloadout");

        player.getInventory().clear();
    }
}
