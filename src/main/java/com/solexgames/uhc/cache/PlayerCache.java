package com.solexgames.uhc.cache;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 6/1/2021
 */

public abstract class PlayerCache<T> {

    private final Map<Player, T> playerTypeMap = new HashMap<>();

    public void insert(Player player, T profile) {
        this.playerTypeMap.put(player, profile);
    }

    public void remove(Player player) {
        this.playerTypeMap.remove(player);
    }

    public T getByPlayer(Player player) {
        return this.playerTypeMap.getOrDefault(player, null);
    }

    public T getByUuid(UUID uuid) {
        return this.playerTypeMap.getOrDefault(Bukkit.getPlayer(uuid), null);
    }

    public T getByName(String name) {
        return this.playerTypeMap.getOrDefault(Bukkit.getPlayer(name), null);
    }
}
