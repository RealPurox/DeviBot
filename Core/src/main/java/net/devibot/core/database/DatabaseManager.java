package net.devibot.core.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import net.devibot.core.Core;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Filter;

public class DatabaseManager {

    private MongoClient client;
    private MongoDatabase database;

    private static DatabaseManager instance;

    private List<Document> logs = new ArrayList<>();

    public static DatabaseManager getInstance() {
        return instance == null ? instance = new DatabaseManager() : instance;
    }

    /*public static void main(String[] args) {
        Core.setup();

        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Document document = new Document();
        document.put("name", "stevyb0t");
        document.put("discriminator", "0008");
        document.put("avatar", "/data/images/profile_pictures/191598787410526208.gif");

        Document ban = new Document();
        ban.put("active", true);
        ban.put("punisher", "222753093559910400");
        ban.put("time", 1547402441107L);
        ban.put("reason", "Homophobia");

        document.put("ban", ban);
        databaseManager.saveToDatabase("users", document, "191598787410526208");
        System.out.println("DONE");
    }*/

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
        return database.getCollection(collection).replaceOne(Filters.eq(key, value), document, new ReplaceOptions().upsert(true));
    }

    public UpdateResult saveToDatabase(String collection, Document document) {
        ObjectId objectId = new ObjectId();
        return database.getCollection(collection).replaceOne(Filters.eq("_id", objectId), document.append("_id", objectId), new ReplaceOptions().upsert(true));
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

    public List<Document> getDocuments(String key, String value, Map<String, String> filter, String collection) {
        FindIterable<Document> entry = database.getCollection(collection).find(Filters.eq(key, value));
        for (Map.Entry<String, String> filterEntry : filter.entrySet()) {
            entry = entry.filter(Filters.eq(filterEntry.getKey(), filterEntry.getValue()));
        }
        return entry.into(new ArrayList<>());
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
