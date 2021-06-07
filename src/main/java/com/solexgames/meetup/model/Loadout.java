package com.solexgames.meetup.model;

import com.google.gson.annotations.SerializedName;
import com.solexgames.meetup.UHCMeetup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@RequiredArgsConstructor
public class Loadout {

    @SerializedName("boundPlayer")
    private final UUID uuid;

    @SerializedName("meetupLoadout")
    private final Map<Integer, ItemStack> inventoryLocationMap = new HashMap<>();

    public void setupDefaultInventory() {
        UHCMeetup.getInstance().getKitManager().setupDefaultInventory(this.inventoryLocationMap);
    }

    public int getLocationOf(Material material) {
        return this.inventoryLocationMap.entrySet().stream()
                .filter(integerItemStackEntry -> integerItemStackEntry.getValue().getType().equals(material))
                .map(Map.Entry::getKey).findFirst().orElse(-1);
    }
}
