package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
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
            player.sendMessage(CC.RED + "You cannot announce the game at this time.");
            return;
        }

        if (gameHandler.getLastAnnouncement() + TimeUnit.SECONDS.toMillis(15L) > System.currentTimeMillis() && gameHandler.getLastAnnouncer() != null && !player.isOp()) {
            player.sendMessage(CC.RED + "You must wait " + DurationFormatUtils.formatDurationWords(gameHandler.getLastAnnouncement() + TimeUnit.SECONDS.toMillis(15L) - System.currentTimeMillis(), true, true) + " as " + gameHandler.getLastAnnouncer() + CC.RED + " has already announced the game.");
            return;
        }

        gameHandler.setLastAnnouncement(System.currentTimeMillis());
        gameHandler.setLastAnnouncer(player.getDisplayName());

        player.sendMessage(CC.GREEN + "You've announced the game.");

        RedisUtil.publishAsync(RedisUtil.sendClickable(
                CC.B_PRI + "UHC Meetup " + CC.B_GRAY + "» " + player.getDisplayName() + CC.SEC + " wants you to play! " + CC.GREEN + "(Join)",
                CC.SEC + "Click to join " + CC.PRI + CorePlugin.getInstance().getServerName() + CC.SEC + "!\n\n" + CC.B_RED + "Warning: " + CC.RED + "This will switch your server!",
                "/join " + CorePlugin.getInstance().getServerName()
        ));
    }
}
