package com.solexgames.meetup.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.json.JsonAppender;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.state.impl.TempPlayerState;
import org.bukkit.Bukkit;

/**
 * @author GrowlyX
 * @since 6/1/2021
 */

public class JedisUtil {

    public static String getServerUpdateJson() {
        final int remaining = (int) UHCMeetup.getInstance().getGameHandler().getGame().getPlayerTypeMap().values().stream()
                .filter(tempPlayer -> tempPlayer.getState().equals(TempPlayerState.ALIVE)).count();

        return new JsonAppender("MEETUP_SERVER_UPDATE")
                .put("SERVER_ID", CorePlugin.getInstance().getServerName())
                .put("GAME_STATE", UHCMeetup.getInstance().getGameHandler().getGame().getState().name())
                .put("REMAINING_PLAYERS", remaining)
                .put("MAX_PLAYERS", Bukkit.getMaxPlayers())
                .getAsJson();
    }

    public static String getServerOfflineJson() {
        return new JsonAppender("MEETUP_SERVER_OFFLINE")
                .put("SERVER_ID", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }
}
