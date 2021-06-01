package com.solexgames.meetup.handler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
        try {
            this.client = new MongoClient(new MongoClientURI(UHCMeetup.getInstance().getConfig().getString("mongodb.url")));
            this.database = this.client.getDatabase(UHCMeetup.getInstance().getConfig().getString("mongodb.database"));

            this.playerCollection = this.database.getCollection("UHCMeetup");
        } catch (Exception exception) {
            System.out.println("[MLGRush] Couldn't connect to the mongo database.");
            Bukkit.shutdown();
        }
    }
}
