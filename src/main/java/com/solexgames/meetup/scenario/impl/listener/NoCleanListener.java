package com.solexgames.meetup.scenario.impl.listener;

import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.scenario.impl.NoCleanScenario;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author GrowlyX
 * @since 6/5/2021
 */

public class NoCleanListener implements Listener {

    @EventHandler
    public void onPlayerEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        final Player entity = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        final GamePlayer gamePlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(entity);
        final GamePlayer damagerPlayer = UHCMeetup.getInstance().getPlayerHandler().getByPlayer(damager);

        if (damagerPlayer.getNoCleanTimer() != null) {
            UHCMeetup.getInstance().getScenario(NoCleanScenario.class).handleCancelNoClean(damagerPlayer);
        } else if (gamePlayer.getNoCleanTimer() != null) {
            damager.sendMessage(entity.getDisplayName() + "'s " + CC.RED + "no clean timer expires in " + TimeUtil.secondsToRoundedTime(gamePlayer.getNoCleanTimer().getTime()) + ".");
        }
    }
}
