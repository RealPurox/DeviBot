package me.purox.devi.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.purox.devi.core.Devi;
import org.bson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private Devi devi;
    private MongoClient client;
    private MongoDatabase database;

    public DatabaseManager(Devi devi) {
        this.devi = devi;
    }

    public void connect() {
        String uri = devi.getSettings().getMongoToken();
        MongoClientURI clientURI = new MongoClientURI(uri);
        this.client = new MongoClient(clientURI);
        this.database = client.getDatabase(devi.getSettings().isDevBot() ? "devi_dev" : "devi");
    }

    public MongoClient getClient() {
        return client;
    }

    public UpdateResult saveToDatabase(String collection, Document document, String id) {
        return database.getCollection(collection).replaceOne(Filters.eq("_id", id), document.append("_id", id), new UpdateOptions().upsert(true));
    }

    public UpdateResult saveToDatabase(String collection, Document document) {
        String id = UUID.randomUUID().toString();
        return database.getCollection(collection).replaceOne(Filters.eq("_id", id), document.append("_id", id), new UpdateOptions().upsert(true));
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
        List<Document> documents = database.getCollection(collection).find(Filters.eq(key, value)).into(new ArrayList<>());
        if (documents == null) return new ArrayList<>();
        return documents;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
