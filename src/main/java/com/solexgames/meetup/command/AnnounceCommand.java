package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.util.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * @author GrowlyX
 * @since 6/5/2021
 */

public class AnnounceCommand extends BaseCommand {

    @CommandAlias("announce")
    @CommandPermission("uhcmeetup.command.announce")
    public void execute(Player player) {
        final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

        if (game.getState().equals(GameState.IN_GAME)) {
            player.sendMessage(CC.RED + "You cannot announce the game at this time.");
            return;
        }

        if (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L) > UHCMeetup.getInstance().getGameHandler().getLastAnnouncement()) {
            player.sendMessage(CC.RED + "You must wait 14 seconds as " + UHCMeetup.getInstance().getGameHandler().getLastAnnouncer() + CC.RED + " has already announced the game.");
            return;
        }

        UHCMeetup.getInstance().getGameHandler().setLastAnnouncement(System.currentTimeMillis());
        UHCMeetup.getInstance().getGameHandler().setLastAnnouncer(player.getDisplayName());

        final Clickable clickable = new Clickable("");

        clickable.add(
                CC.B_PRI + "UHC Meetup " + CC.B_GRAY + "» " + player.getDisplayName() + CC.SEC + " wants you to play! " + CC.B_GREEN + "[Connect]",
                CC.SEC + "Click to join " + CC.PRI + CorePlugin.getInstance().getServerName() + CC.SEC + "!\n\n" + CC.B_RED + "Warning: " + CC.RED + "This will switch your server!",
                "/join " + CorePlugin.getInstance().getServerName(),
                ClickEvent.Action.RUN_COMMAND
        );

        player.sendMessage(CC.GREEN + "You've announced the game.");

        RedisUtil.publishAsync(RedisUtil.sendClickable(clickable));
    }
}
