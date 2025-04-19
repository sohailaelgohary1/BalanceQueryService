/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.iti.database.MongoDB;


import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mycompany.models.CallLog;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDB_Handler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDB_Handler() {
        connect();
    }

    private void connect() {
        mongoClient = MongoClients.create("mongodb://localhost:27017"); // Or use env vars
        database = mongoClient.getDatabase("balance_query_db");
        collection = database.getCollection("call_logs");
    }

    public void saveCallLog(CallLog log) {
        Document doc = new Document("caller", log.getCaller())
                .append("callee", log.getCallee())
                .append("callStartTime", log.getCallStartTime())
                .append("callEndTime", log.getCallEndTime())
                .append("dtmfValue", log.getDtmfValue())
                .append("balanceReturned", log.getBalanceReturned())
                .append("duration", log.getDuration());

        collection.insertOne(doc);
    }

    public List<CallLog> getAllCallLogs() {
        List<CallLog> logs = new ArrayList<>();
        FindIterable<Document> docs = collection.find();

        for (Document doc : docs) {
            logs.add(documentToCallLog(doc));
        }
        return logs;
    }

    public List<CallLog> searchCallLogsByCaller(String caller) {
        List<CallLog> logs = new ArrayList<>();
        FindIterable<Document> docs = collection.find(Filters.eq("caller", caller));

        for (Document doc : docs) {
            logs.add(documentToCallLog(doc));
        }
        return logs;
    }

    private CallLog documentToCallLog(Document doc) {
        CallLog log = new CallLog();
        log.setCaller(doc.getString("caller"));
        log.setCallee(doc.getString("callee"));
        String var= (doc.get("callStartTime")).toString();
        String var2=(doc.get("callEndTime")).toString();
        System.out.println(var+" "+var2);
        log.setCallStartTime(var);
        log.setCallEndTime(var2);
        
        log.setDtmfValue(doc.getString("dtmfValue"));
        log.setBalanceReturned(doc.getString("balanceReturned"));
        log.setDuration(doc.getInteger("duration", 0));
       
        return log;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
