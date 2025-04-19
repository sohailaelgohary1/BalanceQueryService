package com.iti.database.mongodb;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bson.Document;

/**
 * Interface for MongoDB nested documents (equivalent to PostgreSQL composites)
 */
public interface MongoDBComposite {
    /**
     * Converts the composite object to a MongoDB Document
     */
    default Document toDocument() {
        Document doc = new Document();
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value != null) {
                    doc.append(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return doc;
    }
    
    /**
     * Gets all field values as a map
     */
    default Map<String, Object> getValues() {
        Map<String, Object> valuesMap = new LinkedHashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value != null) {
                    valuesMap.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return valuesMap;
    }
}
