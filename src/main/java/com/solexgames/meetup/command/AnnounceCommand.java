package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.ConditionFailedException;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.lib.acf.annotation.CommandPermission;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.handler.GameHandler;
import com.solexgames.meetup.util.CC;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * @author GrowlyX
 * @since 6/5/2021
 */

public class AnnounceCommand extends BaseCommand {

    @CommandAlias("announce|a|gamealert|gameannounce")
    @CommandPermission("game.command.announce")
    public void execute(Player player) {
        final GameHandler gameHandler = Meetup.getInstance().getGameHandler();
        final Game game = gameHandler.getGame();

        if (game.getState().equals(GameState.IN_GAME)) {
            throw new ConditionFailedException("You cannot announce the game while it's running.");
        }

        if (gameHandler.getLastAnnouncement() + TimeUnit.SECONDS.toMillis(15L) > System.currentTimeMillis() && gameHandler.getLastAnnouncer() != null && !player.isOp()) {
            throw new ConditionFailedException("You must wait " + DurationFormatUtils.formatDurationWords(gameHandler.getLastAnnouncement() + TimeUnit.SECONDS.toMillis(15L) - System.currentTimeMillis(), true, true) + " as " + gameHandler.getLastAnnouncer() + CC.RED + " has already announced the game.");
        }

        gameHandler.setLastAnnouncement(System.currentTimeMillis());
        gameHandler.setLastAnnouncer(player.getDisplayName());

        player.sendMessage(CC.GREEN + "You've announced the game.");

        RedisUtil.publishAsync(RedisUtil.sendClickable(
                CC.B_PRI + "Alert " + CC.B_GRAY + "» " + player.getDisplayName() + CC.SEC + " wants you to play " + CC.PRI + "UHC Meetup" + CC.SEC + "!" + CC.B_GREEN + " [Click to Connect]",
                CC.SEC + "Click to join " + CC.PRI + CorePlugin.getInstance().getServerName() + CC.SEC + "!\n\n" + CC.B_RED + "Warning: " + CC.RED + "This will switch your server!",
                "/join " + CorePlugin.getInstance().getServerName()
        ));
    }
}
