package com.solexgames.uhc.state.impl;

import com.solexgames.uhc.cache.PlayerCache;
import com.solexgames.uhc.state.StateBasedModel;
import com.solexgames.uhc.state.impl.player.TempPlayer;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class Game extends PlayerCache<TempPlayer> implements StateBasedModel<GameState, TempPlayer> {

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
}
