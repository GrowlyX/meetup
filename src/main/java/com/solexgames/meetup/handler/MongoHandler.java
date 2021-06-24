package com.solexgames.meetup.handler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

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

        this.database = this.client.getDatabase("admin");
        this.playerCollection = this.database.getCollection("UHCMeetup");
    }
}
