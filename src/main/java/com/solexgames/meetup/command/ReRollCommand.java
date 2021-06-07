package com.solexgames.meetup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.game.Game;
import com.solexgames.meetup.game.GameState;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.util.CC;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 6/7/2021
 */

@CommandAlias("reroll|rr")
public class ReRollCommand extends BaseCommand {

    @Default
    public void onDefault(Player player) {
        final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

        player.sendMessage(CC.SEC + "You currently have " + CC.PRI + gamePlayer.getReRolls() + CC.SEC + " re-roll credits.");
        player.sendMessage(CC.I_GRAY + "You can redeem these by using " + CC.I_YELLOW + "/rr redeem" + CC.I_GRAY + ".");
        player.sendMessage(CC.I_GRAY + "Purchase re-rolls by using " + CC.I_YELLOW + "/rr purchase" + CC.I_GRAY + ".");
    }

    @Subcommand("redeem")
    public void onRedeem(Player player) {
        final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);

        if (gamePlayer.getReRolls() == 0) {
            player.sendMessage(CC.RED + "Error: You cannot redeem a kit re-roll when you have no credits.");
            return;
        }

        final Game game = UHCMeetup.getInstance().getGameHandler().getGame();

        if (!game.isState(GameState.STARTING)) {
            player.sendMessage(CC.RED + "Error: You can only redeem re-roll credits when a meetup is starting.");
            return;
        }

        gamePlayer.setReRolls(gamePlayer.getReRolls() - 1);
        UHCMeetup.getInstance().getKitManager().handleItems(player);

        player.sendMessage(CC.GREEN + "You've used one re-roll credit to regen your kit.");
    }

    @Subcommand("purchase")
    @CommandCompletion("@purchasable")
    public void onPurchase(Player player, Integer amount) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(player);
        final int decrementBy = amount * 100;

        if (potPlayer.getExperience() < amount) {
            player.sendMessage(CC.RED + "Error: You do not have enough experience to purchase " + CC.SEC + amount + CC.RED + " re-rolls (" + decrementBy + " experience needed).");
            return;
        }

        gamePlayer.setReRolls(gamePlayer.getReRolls() + amount);
        potPlayer.setExperience(potPlayer.getExperience() - decrementBy);

        player.sendMessage(CC.SEC + "You've purchased " + CC.PRI + amount + CC.SEC + " re-rolls for " + CC.PRI + decrementBy + CC.SEC + " experience.");
    }
}
