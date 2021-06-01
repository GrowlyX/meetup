package com.solexgames.meetup.state.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
@RequiredArgsConstructor
public class TempPlayer {

    private TempPlayerState state;

    private int kills;

}
