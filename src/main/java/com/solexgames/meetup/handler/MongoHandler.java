package com.solexgames.meetup.handler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.solexgames.core.CorePlugin;
import com.solexgames.meetup.UHCMeetup;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

/**
 * @author GrowlyX
 * @since 5/31/2021
 */

@Getter
@Setter
public class MongoHandler {

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> playerCollection;

    public MongoHandler() {
        this.client = CorePlugin.getInstance().getCoreDatabase().getClient();
        this.database = CorePlugin.getInstance().getCoreDatabase().getDatabase();

        this.playerCollection = this.database.getCollection("UHCMeetup");
    }
}
