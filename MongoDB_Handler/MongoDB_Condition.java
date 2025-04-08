package com.iti.database.MongoDB;
import com.iti.database.psql.*;

public enum MongoDB_Condition {
    EQUALS("$eq"),
    NOT_EQUALS("$ne"),
    GREATER_THAN("$gt"),
    LESS_THAN("$lt"),
    GREATER_THAN_OR_EQUAL("$gte"),
    LESS_THAN_OR_EQUAL("$lte"),
    IN("$in"),
    NOT_IN("$nin"),
    EXISTS("$exists"),
    REGEX("$regex"),
    TEXT("$text"),
    ELEM_MATCH("$elemMatch");

    static MongoDB_Condition fromDB_Condition(com.iti.database.DB_Condition condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private final String operator;

    MongoDB_Condition(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static MongoDB_Condition fromDB_Condition(DB_Condition condition) {
        switch (condition) {
            case EQUALS:
                return EQUALS;
            case NOT_EQUALS:
                return NOT_EQUALS;
            case GREATER_THAN:
                return GREATER_THAN;
            case LESS_THAN:
                return LESS_THAN;
            case GREATER_THAN_OR_EQUAL:
                return GREATER_THAN_OR_EQUAL;
            case LESS_THAN_OR_EQUAL:
                return LESS_THAN_OR_EQUAL;
            default:
                throw new IllegalArgumentException("Unsupported DB_Condition: " + condition);
        }
    }
    
    // Special method to handle PSQL_Condition cases
    public static MongoDB_Condition fromPSQL_Condition(PSQL_Condition condition) {
        if (condition == PSQL_Condition.ILIKE) {
            return REGEX; // Will need special handling when creating the filter
        }
        throw new IllegalArgumentException("Unsupported PSQL_Condition: " + condition);
    }
}