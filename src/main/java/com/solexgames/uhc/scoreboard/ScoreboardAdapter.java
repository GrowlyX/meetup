package com.solexgames.uhc.scoreboard;

import com.solexgames.core.util.Color;
import io.github.nosequel.scoreboard.element.ScoreboardElement;
import io.github.nosequel.scoreboard.element.ScoreboardElementHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class ScoreboardAdapter implements ScoreboardElementHandler {

    /**
     * Get the scoreboard element of a player
     *
     * @param player the player
     * @return the element
     */
    @Override
    public ScoreboardElement getElement(Player player) {
        final ScoreboardElement element = new ScoreboardElement();

        element.add(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "UHC MEETUP");

        return element;
    }
}
