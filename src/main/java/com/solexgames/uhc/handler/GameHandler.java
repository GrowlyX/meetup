package com.solexgames.uhc.handler;

import com.solexgames.uhc.state.impl.Game;
import com.solexgames.uhc.state.impl.GameState;
import lombok.Getter;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
public class GameHandler {

    private Game game;

    public void setupGame() {
        this.game = new Game();
    }

    public boolean isRunning() {
        return this.game.getState().equals(GameState.IN_GAME);
    }
}
