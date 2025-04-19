package com.iti.database.MongoDB;



import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class MongoDBManager {
    private final String uri = "mongodb://localhost:27017";
    private final String dbName = "balance_query";
    private final String collectionName = "call_logs";

    public MongoCollection<Document> getCollection() {
        MongoClient client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    public void insertCallLog(Document doc) {
        getCollection().insertOne(doc);
    }

    public List<Document> getAllLogs() {
        return getCollection().find().into(new ArrayList<>());
    }

    public List<Document> searchLogsByCaller(String caller) {
        return getCollection().find(Filters.eq("caller", caller)).into(new ArrayList<>());
    }
}
