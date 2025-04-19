package com.iti.database.MongoDB;
import  com.iti.database.psql.*;
import com.iti.database.ConditionBuilder;
import com.iti.database.DB_Condition;
import com.iti.database.DB_Handler;
import com.iti.database.mongodb.MongoDBComposite;
import com.iti.database.mongodb.MongoDBCompositeHelper;
import com.iti.database.mongodb.MongoDBEnum;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.*;

public class MongoDB_Handler implements DB_Handler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private String connectionString = "mongodb://localhost:27017";
    private String databaseName = "myDB";

    @Override
    public void connect() {
        try {
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to MongoDB", e);
        }
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            // Simple ping command to check connection
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public <T> T create(T entity) {
        MongoCollection<Document> collection = getCollection(entity.getClass());
        Document doc = convertToDocument(entity);
        
        // Handle MongoDB's _id field
        if (doc.get("_id") == null) {
            doc.append("_id", new ObjectId());
        }
        
        collection.insertOne(doc);
        return (T) convertToEntity(entity.getClass(), doc);
    }

    @Override
    public <T> T updateByValue(T entity, String whereColumn, DB_Condition condition, Object conditionValue) {
        MongoCollection<Document> collection = getCollection(entity.getClass());
        Bson filter = createFilter(whereColumn, condition, conditionValue);
        Document updateDoc = new Document("$set", convertToDocument(entity));
        
        UpdateResult result = collection.updateOne(filter, updateDoc);
        if (result.getModifiedCount() > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public <T> List<T> readAll(Class<T> entityClass) {
        MongoCollection<Document> collection = getCollection(entityClass);
        List<T> results = new ArrayList<>();
        
        for (Document doc : collection.find()) {
            results.add(convertToEntity(entityClass, doc));
        }
        
        return results;
    }

    @Override
    public <T> List<T> readByValue(Class<T> entityClass, String column, DB_Condition condition, Object value) {
        MongoCollection<Document> collection = getCollection(entityClass);
        Bson filter = createFilter(column, condition, value);
        List<T> results = new ArrayList<>();
        
        for (Document doc : collection.find(filter)) {
            results.add(convertToEntity(entityClass, doc));
        }
        
        return results;
    }

    @Override
    public boolean deleteByValue(Class<?> entityClass, String column, DB_Condition condition, Object value) {
        MongoCollection<Document> collection = getCollection(entityClass);
        Bson filter = createFilter(column, condition, value);
        
        DeleteResult result = collection.deleteMany(filter);
        return result.getDeletedCount() > 0;
    }

    @Override
    public void executeQuery(String query) {
        // MongoDB doesn't use SQL queries
        throw new UnsupportedOperationException("Raw query execution not directly supported in MongoDB handler");
    }

    @Override
    public List<Map<String, Object>> executeSelectQuery(String query) {
        // Not directly supported in MongoDB
        throw new UnsupportedOperationException("Raw query execution not directly supported in MongoDB handler");
    }

    @Override
    public <T> List<Map<String, Object>> joinTables(Class<T> leftTableClass, Class<T> rightTableClass, 
                                                   String joinColumn1, String joinColumn2) {
        // Implement using MongoDB $lookup aggregation
        MongoCollection<Document> leftCollection = getCollection(leftTableClass);
        String rightCollectionName = rightTableClass.getSimpleName();
        
        List<Document> pipeline = new ArrayList<>();
        
        // Create a $lookup stage for joining
        Document lookupStage = new Document("$lookup",
            new Document("from", rightCollectionName)
                .append("localField", joinColumn1)
                .append("foreignField", joinColumn2)
                .append("as", "joined")
        );
        
        pipeline.add(lookupStage);
        
        // Add unwind stage to flatten results
        Document unwindStage = new Document("$unwind", "$joined");
        pipeline.add(unwindStage);
        
        List<Map<String, Object>> results = new ArrayList<>();
        for (Document doc : leftCollection.aggregate(pipeline)) {
            Map<String, Object> resultMap = new HashMap<>();
            
            // Add all fields from the left collection
            for (String key : doc.keySet()) {
                if (!key.equals("joined")) {
                    resultMap.put(key, doc.get(key));
                }
            }
            
            // Add all fields from the right collection (joined)
            Document joinedDoc = (Document) doc.get("joined");
            for (String key : joinedDoc.keySet()) {
                resultMap.put(key, joinedDoc.get(key));
            }
            
            results.add(resultMap);
        }
        
        return results;
    }

    @Override
    public <T> List<T> readByValue(Class<T> entityClass, ConditionBuilder cdb) {
        MongoCollection<Document> collection = getCollection(entityClass);
        Bson filter = createFilterFromConditionBuilder(cdb);
        List<T> results = new ArrayList<>();
        
        for (Document doc : collection.find(filter)) {
            results.add(convertToEntity(entityClass, doc));
        }
        
        return results;
    }

    @Override
    public <T> T updateByValue(T entity, ConditionBuilder cdb) {
        MongoCollection<Document> collection = getCollection(entity.getClass());
        Bson filter = createFilterFromConditionBuilder(cdb);
        Document updateDoc = new Document("$set", convertToDocument(entity));
        
        UpdateResult result = collection.updateMany(filter, updateDoc);
        if (result.getModifiedCount() > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteByValue(Class<?> entityClass, ConditionBuilder cdb) {
        MongoCollection<Document> collection = getCollection(entityClass);
        Bson filter = createFilterFromConditionBuilder(cdb);
        
        DeleteResult result = collection.deleteMany(filter);
        return result.getDeletedCount() > 0;
    }

    // Helper methods
    private <T> MongoCollection<Document> getCollection(Class<T> entityClass) {
        return database.getCollection(entityClass.getSimpleName());
    }

    private Document convertToDocument(Object entity) {
        Document doc = new Document();
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value != null) {
                    // Handle special types
                    if (value instanceof MongoDBComposite) {
                        MongoDBComposite composite = (MongoDBComposite) value;
                        doc.append(field.getName(), composite.toDocument());
                    } else if (value instanceof MongoDBEnum) {
                        MongoDBEnum enumValue = (MongoDBEnum) value;
                        doc.append(field.getName(), enumValue.getEnumValue());
                    } else {
                        doc.append(field.getName(), value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to convert entity to document", e);
        }
        return doc;
    }

    private <T> T convertToEntity(Class<T> entityClass, Document doc) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = doc.get(field.getName());
                if (value != null) {
                    // Handle special types
                    if (MongoDBComposite.class.isAssignableFrom(field.getType()) && value instanceof Document) {
                        Object compositeInstance = MongoDBCompositeHelper.documentToComposite((Document) value, field.getType());
                        field.set(entity, compositeInstance);
                    } else if (field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), (String) value);
                        field.set(entity, enumValue);
                    } else {
                        field.set(entity, value);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert document to entity", e);
        }
    }

//    private Bson createFilter(String column, DB_Condition condition, Object value) {
//        MongoDB_Condition mongoCondition;
//        try {
//            mongoCondition = MongoDB_Condition.fromDB_Condition(condition);
//        } catch (IllegalArgumentException e) {
//            // Handle custom conditions that might not be in the enum
//            if (condition instanceof MongoDB_Condition) {
//                if (((MongoDB_Condition) condition) == MongoDB_Condition.ILIKE) {
//                    // For ILIKE, use regex with case insensitivity
//                    return regex(column, value.toString(), "i");
//                }
//            }
//            throw e;
//        }
//        return createFilter(column, mongoCondition, value);
//    }
    private Bson createFilter(String column, Object conditionObj, Object value) {
    if (conditionObj instanceof DB_Condition) {
        DB_Condition condition = (DB_Condition) conditionObj;
        MongoDB_Condition mongoCondition = MongoDB_Condition.fromDB_Condition(condition);
        return createMongoFilter(column, mongoCondition, value);
    } else if (conditionObj instanceof PSQL_Condition) {
        PSQL_Condition psqlCondition = (PSQL_Condition) conditionObj;
        if (psqlCondition == PSQL_Condition.ILIKE) {
            // For ILIKE, use regex with case insensitivity
            // Escape special regex characters in the value and convert % to .*
            String pattern = value.toString().replace(".", "\\.").replace("*", "\\*")
                                            .replace("+", "\\+").replace("?", "\\?")
                                            .replace("%", ".*");
            return regex(column, "^" + pattern + "$", "i");
        }
        throw new IllegalArgumentException("Unsupported PSQL_Condition: " + psqlCondition);
    }
    throw new IllegalArgumentException("Unsupported condition type: " + conditionObj.getClass().getName());
}

private Bson createMongoFilter(String column, MongoDB_Condition condition, Object value) {
    switch (condition) {
        case EQUALS:
            return eq(column, value);
        case NOT_EQUALS:
            return ne(column, value);
        case GREATER_THAN:
            return gt(column, value);
        case LESS_THAN:
            return lt(column, value);
        case GREATER_THAN_OR_EQUAL:
            return gte(column, value);
        case LESS_THAN_OR_EQUAL:
            return lte(column, value);
        case IN:
            return in(column, (Iterable<?>) value);
        case NOT_IN:
            return nin(column, (Iterable<?>) value);
        case EXISTS:
            return exists(column, (Boolean) value);
        case REGEX:
            return regex(column, value.toString());
        default:
            throw new IllegalArgumentException("Unsupported MongoDB condition: " + condition);
    }
}

    private Bson createFilter(String column, MongoDB_Condition condition, Object value) {
        switch (condition) {
            case EQUALS:
                return eq(column, value);
            case NOT_EQUALS:
                return ne(column, value);
            case GREATER_THAN:
                return gt(column, value);
            case LESS_THAN:
                return lt(column, value);
            case GREATER_THAN_OR_EQUAL:
                return gte(column, value);
            case LESS_THAN_OR_EQUAL:
                return lte(column, value);
            case IN:
                return in(column, (Iterable<?>) value);
            case NOT_IN:
                return nin(column, (Iterable<?>) value);
            case EXISTS:
                return exists(column, (Boolean) value);
            case REGEX:
                return regex(column, value.toString());
            default:
                throw new IllegalArgumentException("Unsupported MongoDB condition: " + condition);
        }
    }

    private Bson createFilterFromConditionBuilder(ConditionBuilder cdb) {
        // Convert ConditionBuilder to MongoDB filters
        List<Bson> filters = new ArrayList<>();
        
        // Implementation depends on ConditionBuilder implementation
        // For simplicity, assuming it builds a simple AND condition
        // You would need to adapt this based on your ConditionBuilder implementation
        
        return and(filters);
    }
}
