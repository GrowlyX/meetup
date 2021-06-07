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

    @SerializedName("inventoryLayout")
    private final Map<Integer, ItemStack> inventoryLocationMap = new HashMap<>();

    public int getFromMaterial(Material material) {
        return this.inventoryLocationMap.entrySet().stream()
                .filter(itemStackIntegerEntry -> itemStackIntegerEntry.getValue().getType().equals(material))
                .map(Map.Entry::getKey).findFirst().orElse(-1);
    }

    public ItemStack getFromInteger(Integer integer) {
        return this.inventoryLocationMap.entrySet().stream()
                .filter(integerItemStackEntry -> integerItemStackEntry.getKey().equals(integer))
                .map(Map.Entry::getValue).findFirst().orElse(new ItemStack(Material.AIR));
    }

    public void setupDefaultInventory() {
        UHCMeetup.getInstance().getKitManager().getDefaultInventory()
                .forEach(this.inventoryLocationMap::put);
    }
}
