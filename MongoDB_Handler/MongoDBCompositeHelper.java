package com.iti.database.mongodb;

import java.lang.reflect.Field;
import org.bson.Document;

/**
 * Helper class for working with MongoDB composite objects
 */
public class MongoDBCompositeHelper {

    /**
     * Convert a MongoDB Document to a composite object
     */
    public static Object documentToComposite(Document doc, Class<?> compType) throws ReflectiveOperationException {
        if (doc == null) {
            return null;
        }
        
        Object compInstance = compType.getDeclaredConstructor().newInstance();
        Field[] compFields = compType.getDeclaredFields();
        
        for (Field field : compFields) {
            field.setAccessible(true);
            if (doc.containsKey(field.getName())) {
                Object value = doc.get(field.getName());
                if (value != null) {
                    // Handle nested documents if needed
                    if (value instanceof Document && MongoDBComposite.class.isAssignableFrom(field.getType())) {
                        value = documentToComposite((Document) value, field.getType());
                    }
                    field.set(compInstance, convertValueIfNeeded(value, field.getType()));
                }
            }
        }
        
        return compInstance;
    }
    
    /**
     * Convert a value to the appropriate type if needed
     */
    private static Object convertValueIfNeeded(Object value, Class<?> targetType) {
        // Handle primitive types
        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType == int.class || targetType == Integer.class) {
                return num.intValue();
            } else if (targetType == long.class || targetType == Long.class) {
                return num.longValue();
            } else if (targetType == double.class || targetType == Double.class) {
                return num.doubleValue();
            } else if (targetType == float.class || targetType == Float.class) {
                return num.floatValue();
            }
        }
        
        // Handle enum types
        if (targetType.isEnum() && value instanceof String) {
            return Enum.valueOf((Class<Enum>) targetType, (String) value);
        }
        
        return value;
    }
}
