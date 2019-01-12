package net.devibot.core.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import net.devibot.core.Core;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private MongoClient client;
    private MongoDatabase database;

    private static DatabaseManager instance;

    private List<Document> logs = new ArrayList<>();

    public static DatabaseManager getInstance() {
        return instance == null ? instance = new DatabaseManager() : instance;
    }

    public DatabaseManager() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applicationName("Devi")
                .applyConnectionString(new ConnectionString(Core.CONFIG.getMongoUrl()))
                .build();

        this.client = MongoClients.create(settings);
        this.database = client.getDatabase(Core.CONFIG.isDevMode() ? "development" : "master");

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pushLogs, 0, 15, TimeUnit.SECONDS);
    }

    public MongoClient getClient() {
        return client;
    }

    public UpdateResult saveToDatabase(String collection, Document document, String id) {
        return database.getCollection(collection).replaceOne(Filters.eq("_id", id), document.append("_id", id), new ReplaceOptions().upsert(true));
    }

    public UpdateResult saveToDatabase(String collection, Document document, String key, String value) {
        ObjectId objectId = new ObjectId();
        return database.getCollection(collection).replaceOne(Filters.eq(key, value), document.append("_id", objectId), new ReplaceOptions().upsert(true));
    }

    public UpdateResult saveToDatabase(String collection, Document document) {
        ObjectId objectId = new ObjectId();
        return database.getCollection(collection).replaceOne(Filters.eq("_id", objectId.toString()), document.append("_id", objectId.toString()), new ReplaceOptions().upsert(true));
    }

    public DeleteResult removeFromDatabase(String collection, String id) {
        return database.getCollection(collection).deleteOne(Filters.eq("_id", id));
    }

    public Document getDocument(String id, String collection) {
        Document document = database.getCollection(collection).find(Filters.eq("_id", id)).first();
        if (document == null) return new Document();
        return document;
    }

    public List<Document> getDocuments(String key, String value, String collection) {
        return database.getCollection(collection).find(Filters.eq(key, value)).into(new ArrayList<>());
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void addLog(Document document) {
        this.logs.add(document);
    }

    //used in shutdown hook
    public void pushLogs() {
        if (logs.isEmpty()) return;
        database.getCollection("logs").insertMany(logs);
        logs.clear();
    }
}
