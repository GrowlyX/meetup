package com.solexgames.meetup.menu;

import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import io.papermc.lib.PaperLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

        for (GamePlayer gamePlayer : Meetup.getInstance().getGameHandler().getRemaining()) {
            buttons.put(buttons.size(), new ItemBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setDisplayName(gamePlayer.getPlayer().getDisplayName())
                    .addLore(
                            CC.GRAY + "Health: " + CC.WHITE + this.fetchFancyPercentage(player.getHealth(), 20),
                            CC.GRAY + "Hunger: " + CC.WHITE + this.fetchFancyPercentage(player.getFoodLevel(), 20),
                            "",
                            CC.SEC + "[Click to teleport]"
                    )
                    .setOwner(gamePlayer.getName())
                    .toButton((clicker, clickType) -> {
                        final Player target = gamePlayer.getPlayer();

                        if (target != null) {
                            PaperLib.teleportAsync(player, target.getLocation()).whenComplete((aBoolean, throwable) -> {
                                player.sendMessage(CC.SEC + "You've teleported to " + target.getDisplayName() + CC.SEC + ".");
                            });
                        }
                    })
            );
        }

        return buttons;
    }

    public String fetchFancyPercentage(double amount, int total) {
        final double percentage = (amount * 100.0f) / total;

        return (percentage > 80 ? CC.GREEN + percentage :
                percentage > 60 ? CC.YELLOW + percentage :
                        percentage > 40 ? CC.RED + percentage :
                                CC.D_RED + percentage) + "%";
    }
}
