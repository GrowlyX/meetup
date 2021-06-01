package com.solexgames.meetup.player;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.meetup.UHCMeetup;
import com.solexgames.meetup.factory.GsonFactory;
import com.solexgames.meetup.model.Loadout;
import com.solexgames.meetup.task.NoCleanTimer;
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

    private int gameKills;
    private int kills;
    private int deaths;
    private int played;

    private boolean spectating;
    private NoCleanTimer noCleanTimer;

    private Loadout loadout;

    public GamePlayer(Player player, String name) {
        this.player = player;
        this.name = name;

        this.loadPlayerData();
    }

    public void savePlayerData(boolean remove) {
        CompletableFuture.runAsync(() -> UHCMeetup.getInstance().getMongoHandler().getPlayerCollection().replaceOne(Filters.eq("uuid", this.player.getUniqueId().toString()), this.getDocument(), new ReplaceOptions().upsert(true)));

        if (remove) {
            UHCMeetup.getInstance().getPlayerHandler().remove(this.getPlayer().getUniqueId());
        }
    }

    public Document getDocument() {
        final Document document = new Document("_id", this.player.getUniqueId());

        document.put("uuid", this.player.getUniqueId().toString());
        document.put("name", this.name);

        document.put("kills", this.kills);
        document.put("deaths", this.deaths);
        document.put("played", this.played);

        document.put("layout", GsonFactory.getPrettyGson().toJson(this.loadout));

        return document;
    }

    private void loadPlayerData() {
        CompletableFuture.supplyAsync(() -> UHCMeetup.getInstance().getMongoHandler().getPlayerCollection().find(Filters.eq("uuid", this.player.getUniqueId().toString())).first())
                .thenAccept(document -> {
                    if (document == null) {
//                        this.layout = new Loadout(this.uuid);
//                        this.layout.setupDefaultInventory();

                        UHCMeetup.getInstance().getServer().getScheduler()
                                .runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> this.savePlayerData(false), 20L);
                    } else {
                        if (document.getInteger("kills") != null) {
                            this.kills = document.getInteger("kills");
                        }
                        if (document.getInteger("deaths") != null) {
                            this.deaths = document.getInteger("deaths");
                        }
                        if (document.getInteger("played") != null) {
                            this.played = document.getInteger("played");
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
