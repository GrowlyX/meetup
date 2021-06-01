package com.solexgames.uhc.state.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
@RequiredArgsConstructor
public class TempPlayer {

    private final Player player;
    private TempPlayerState state;

    private int kills;

}
