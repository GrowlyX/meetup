package com.solexgames.meetup.scenario.impl.listener;

import com.solexgames.meetup.Meetup;
import com.solexgames.meetup.player.GamePlayer;
import com.solexgames.meetup.scenario.impl.NoCleanScenario;
import com.solexgames.meetup.util.CC;
import com.solexgames.meetup.util.MeetupUtil;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

        Player damaging = null;

        if (event.getDamager() instanceof Projectile) {
            final Projectile source = (Projectile) event.getDamager();

            if (source.getShooter() instanceof Player) {
                damaging = (Player) source.getShooter();
            }
        }

        if (event.getDamager() instanceof Player) {
            damaging = (Player) event.getDamager();
        }

        if (damaging == null) {
            return;
        }

        final Player entity = (Player) event.getEntity();

        final GamePlayer gamePlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(entity);
        final GamePlayer damagingPlayer = Meetup.getInstance().getPlayerHandler().getByPlayer(damaging);

        if (damagingPlayer.isSpectating()) {
            return;
        }

        final NoCleanScenario noCleanScenario = Meetup.getInstance()
                .getScenario(NoCleanScenario.class);

        if (damagingPlayer.getNoCleanTimer() != null) {
            noCleanScenario.handleCancelNoClean(damagingPlayer);
        } else if (gamePlayer.getNoCleanTimer() != null) {
            damaging.sendMessage(entity.getDisplayName() + "'s " + CC.RED + "no clean timer expires in " + MeetupUtil.secondsToRoundedTime(gamePlayer.getNoCleanTimer().getTime()) + ".");
            event.setCancelled(true);
        }
    }
}
