package com.solexgames.meetup.task;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.GameState;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

public class GameCheckTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!UHCMeetup.getInstance().getGameHandler().getGame().isState(GameState.IN_GAME)) {
            return;
        }

        UHCMeetup.getInstance().getGameHandler().checkWinners();
    }
}
