package com.solexgames.meetup.task.game;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.GameState;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class GameCheckTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!Meetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
            return;
        }

        Meetup.getInstance().getGameHandler().checkWinners();
    }
}
