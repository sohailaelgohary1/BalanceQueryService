package com.iti.database.MongoDB;

/**
 * Enum for standard database conditions
 */
public enum DB_Condition {
    EQUALS("="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<=");
    
    private final String operator;
    
    DB_Condition(String operator) {
        this.operator = operator;
    }
    
    public String getOperator() {
        return operator;
    }
}