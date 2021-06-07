package com.solexgames.meetup.game;

import com.solexgames.meetup.util.CC;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GrowlyX
 * @since 5/31/2021
 *
 * Used for storing game data
 */

@Getter
@Setter
public class Game {

    private int border = 100;

    private int gameStartTime = 60;
    private int borderTime = 120;
    private int endTime = 10;

    private String winner;

    private GameState state = GameState.WAITING;

    public int getNextBorder() {
        return this.border == 10 ? 10 : this.border == 25 ? 10 : this.border - 25;
    }

    public String getFormattedBorderStatus() {
        return this.borderTime > 0 && this.border != 10 ? CC.WHITE + " (" + CC.PRI + this.borderTime + CC.WHITE + ")" : "";
    }

    public void decrementBorderTime() {
        this.borderTime--;
    }

    public boolean isState(GameState state) {
        return this.state.equals(state);
    }
}
