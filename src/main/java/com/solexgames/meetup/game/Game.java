package com.solexgames.meetup.game;

import com.solexgames.meetup.cache.PlayerCache;
import com.solexgames.meetup.state.StateBasedModel;
import com.solexgames.meetup.state.impl.player.TempPlayer;
import com.solexgames.meetup.util.CC;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class Game extends PlayerCache<TempPlayer> implements StateBasedModel<GameState, TempPlayer> {

    private int remaining;
    private int initial;
    private int border;

    private int gameStartTime = 60;
    private int borderTime = 120;
    private int endTime = 10;
    private int noCleanTime = 15;

    private boolean canStartGame;
    private boolean canAnnounce;
    private boolean generated;

    private String winner;

    private GameState state = GameState.WAITING;

    @Override
    public void start() {

    }

    @Override
    public void end(TempPlayer profile) {

    }

    @Override
    public void cleanup() {

    }

    public int getNextBorder() {
        switch (this.border) {
            case 100: return 75;
            case 75: return 50;
            case 50: return 25;
            default: return 10;
        }
    }

    public String getFormattedBorderStatus() {
        return this.borderTime > 0 && this.border != 10 ? CC.WHITE + " (" + CC.PRI + this.borderTime + CC.WHITE + ")" : "";
    }

    public int decrementBorderTime() {
        return this.borderTime--;
    }

    public boolean isState(GameState state) {
        return this.state.equals(state);
    }
}