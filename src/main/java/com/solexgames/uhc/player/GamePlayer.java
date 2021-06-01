package com.solexgames.uhc.player;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.uhc.UHCPlugin;
import com.solexgames.uhc.factory.GsonFactory;
import com.solexgames.uhc.model.Loadout;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Long-term player profile where data is saved
 * <p>
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class GamePlayer {

    @SerializedName("_id")
    private final Player player;
    private final String name;

    private int kills;
    private int deaths;

    private Loadout loadout;

    public GamePlayer(Player player, String name) {
        this.player = player;
        this.name = name;

        this.loadPlayerData();
    }

    public void savePlayerData(boolean remove) {
        CompletableFuture.runAsync(() -> UHCPlugin.getInstance().getMongoHandler().getPlayerCollection().replaceOne(Filters.eq("uuid", this.player.getUniqueId().toString()), this.getDocument(), new ReplaceOptions().upsert(true)));

        if (remove) {
            UHCPlugin.getInstance().getPlayerHandler().remove(this.getPlayer());
        }
    }

    public Document getDocument() {
        final Document document = new Document("_id", this.player.getUniqueId());

        document.put("uuid", this.player.getUniqueId().toString());
        document.put("name", this.name);

        document.put("kills", this.kills);
        document.put("deaths", this.deaths);

        document.put("layout", GsonFactory.getPrettyGson().toJson(this.loadout));

        return document;
    }

    private void loadPlayerData() {
        CompletableFuture.supplyAsync(() -> UHCPlugin.getInstance().getMongoHandler().getPlayerCollection().find(Filters.eq("uuid", this.player.getUniqueId().toString())).first())
                .thenAccept(document -> {
                    if (document == null) {
//                        this.layout = new Loadout(this.uuid);
//                        this.layout.setupDefaultInventory();

                        UHCPlugin.getInstance().getServer().getScheduler()
                                .runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> this.savePlayerData(false), 20L);
                    } else {
                        if (document.getInteger("kills") != null) {
                            this.kills = document.getInteger("kills");
                        }
                        if (document.getInteger("deaths") != null) {
                            this.deaths = document.getInteger("deaths");
                        }
//                        if (document.getString("loadout") != null) {
//                            this.layout = GsonFactory.getPrettyGson().fromJson(document.getString("loadout"), Loadout.class);
//                        } else {
//                            this.layout = new Loadout(this.uuid);
//                        }
//
//                        this.layout.setupDefaultInventory();
                    }
                });
    }
}
