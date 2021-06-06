package com.solexgames.meetup.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.lib.commons.redis.json.JsonAppender;
import com.solexgames.meetup.UHCMeetup;
import org.bukkit.Bukkit;

/**
 * @author GrowlyX
 * @since 6/1/2021
 */

public class JedisUtil {

    public static String getServerUpdateJson() {
        final int remaining = UHCMeetup.getInstance().getGameHandler().getRemainingPlayers().size();

        return new JsonAppender("MEETUP_SERVER_UPDATE")
                .put("SERVER_ID", CorePlugin.getInstance().getServerName())
                .put("GAME_STATE", UHCMeetup.getInstance().getGameHandler().getGame().getState().name())
                .put("REMAINING_PLAYERS", remaining)
                .getAsJson();
    }

    public static String getServerOfflineJson() {
        return new JsonAppender("MEETUP_SERVER_OFFLINE")
                .put("SERVER_ID", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }
}
