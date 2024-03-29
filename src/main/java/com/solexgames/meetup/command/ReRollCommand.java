package com.solexgames.meetup.command;

import com.solexgames.lib.acf.BaseCommand;
import com.solexgames.lib.acf.ConditionFailedException;
import com.solexgames.lib.acf.annotation.CommandAlias;
import com.solexgames.lib.acf.annotation.CommandCompletion;
import com.solexgames.lib.acf.annotation.Default;
import com.solexgames.lib.acf.annotation.Subcommand;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.meetup.Meetup;
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
        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

        player.sendMessage(CC.SEC + "You currently have " + CC.PRI + gamePlayer.getReRolls() + CC.SEC + " re-roll credits.");
        player.sendMessage(CC.I_GRAY + "You can redeem these by using " + CC.I_YELLOW + "/rr redeem" + CC.I_GRAY + ".");
        player.sendMessage(CC.I_GRAY + "Purchase re-rolls by using " + CC.I_YELLOW + "/rr purchase" + CC.I_GRAY + ".");
    }

    @Subcommand("redeem")
    public void onRedeem(Player player) {
        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);

        if (gamePlayer.getReRolls() == 0) {
            throw new ConditionFailedException("You cannot redeem a kit re-roll when you have no credits.");
        }

        final Game game = Meetup.getInstance().getGameHandler().getGame();

        if (!game.isState(GameState.STARTING)) {
            throw new ConditionFailedException("You can only redeem re-roll credits when the game is starting.");
        }

        gamePlayer.setReRolls(gamePlayer.getReRolls() - 1);
        Meetup.getInstance().getKitHandler().handleItems(player);

        player.sendMessage(CC.GREEN + "You've used one re-roll credit to regen your kit.");
    }

    @Subcommand("purchase")
    @CommandCompletion("@purchasable")
    public void onPurchase(Player player, Integer amount) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(player);
        final int decrementBy = amount * 100;

        if (potPlayer.getExperience() < amount) {
            throw new ConditionFailedException("You do not have enough experience to purchase " + CC.SEC + amount + CC.RED + " re-rolls (" + decrementBy + " experience needed).");
        }

        gamePlayer.setReRolls(gamePlayer.getReRolls() + amount);
        potPlayer.setExperience(potPlayer.getExperience() - decrementBy);

        player.sendMessage(CC.SEC + "You've purchased " + CC.PRI + amount + CC.SEC + " re-rolls for " + CC.PRI + decrementBy + CC.SEC + " experience.");
    }
}
