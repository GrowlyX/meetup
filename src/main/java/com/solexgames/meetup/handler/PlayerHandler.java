package com.solexgames.meetup.handler;

import com.solexgames.meetup.player.GamePlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 6/1/2021
 */

@Getter
public class PlayerHandler {

	private final Map<UUID, GamePlayer> playerTypeMap = new HashMap<>();

	public void insert(UUID uuid, GamePlayer profile) {
		this.playerTypeMap.put(uuid, profile);
	}

	public void remove(UUID uuid) {
		this.playerTypeMap.remove(uuid);
	}

	public GamePlayer getByPlayer(Player player) {
		return this.playerTypeMap.getOrDefault(player.getUniqueId(), null);
	}
}
