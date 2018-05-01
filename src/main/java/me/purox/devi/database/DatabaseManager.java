package me.purox.devi.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import me.purox.devi.core.Devi;
import org.bson.Document;

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
        this.database = client.getDatabase("devi");
    }

    public MongoClient getClient() {
        return client;
    }

    public void saveToDatabase(String collection, Document document, String id) {
        database.getCollection(collection).replaceOne(Filters.eq("_id", id), document.append("_id", id), new UpdateOptions().upsert(true));
    }

    public void saveToDatabase(MongoDatabase db, String collection, Document document, String id) {
        db.getCollection(collection).replaceOne(Filters.eq("_id", id), document.append("_id", id), new UpdateOptions().upsert(true));
    }

    public Document getDocument(String id, String collection) {
        Document document = database.getCollection(collection).find(Filters.eq("_id", id)).first();
        if (document == null) return new Document();
        return document;
    }
}
