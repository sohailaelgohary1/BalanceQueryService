package com.iti.cdr.repository;

import com.iti.cdr.CDR;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class CDRRepository {
    private final MongoCollection<Document> collection;

    public CDRRepository(MongoDatabase database) {
        this.collection = database.getCollection("cdrs");
        createIndexes();
    }

    private void createIndexes() {
        collection.createIndex(Indexes.ascending("callId"));
        collection.createIndex(Indexes.ascending("callingNumber"));
        collection.createIndex(Indexes.ascending("calledNumber"));
        collection.createIndex(Indexes.ascending("startTime"));
        collection.createIndex(Indexes.ascending("callType"));
        collection.createIndex(Indexes.compoundIndex(
            Indexes.ascending("callingNumber"),
            Indexes.ascending("startTime")
        ));
    }

    public String insertCDR(CDR cdr) {
        Document doc = new Document()
            .append("callId", cdr.getCallId())
            .append("callingNumber", cdr.getCallingNumber())
            .append("calledNumber", cdr.getCalledNumber())
            .append("startTime", cdr.getStartTime())
            .append("endTime", cdr.getEndTime())
            .append("duration", cdr.getDuration())
            .append("callType", cdr.getCallType())
            .append("charge", cdr.getCharge())
            .append("status", cdr.getStatus())
            .append("imsi", cdr.getImsi())
            .append("imei", cdr.getImei())
            .append("mcc", cdr.getMcc())
            .append("mnc", cdr.getMnc())
            .append("cellId", cdr.getCellId());

        collection.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }

    public List<CDR> findCDRsByCallingNumber(String callingNumber) {
        List<CDR> cdrs = new ArrayList<>();
        collection.find(eq("callingNumber", callingNumber))
            .forEach(doc -> cdrs.add(documentToCDR(doc)));
        return cdrs;
    }

    public List<CDR> findCDRsByDateRange(Date startDate, Date endDate) {
        List<CDR> cdrs = new ArrayList<>();
        collection.find(and(
            gte("startTime", startDate),
            lte("startTime", endDate)
        )).forEach(doc -> cdrs.add(documentToCDR(doc)));
        return cdrs;
    }

    private CDR documentToCDR(Document doc) {
        CDR cdr = new CDR();
        cdr.setId(doc.getObjectId("_id"));
        cdr.setCallId(doc.getString("callId"));
        cdr.setCallingNumber(doc.getString("callingNumber"));
        cdr.setCalledNumber(doc.getString("calledNumber"));
        cdr.setStartTime(doc.getDate("startTime"));
        cdr.setEndTime(doc.getDate("endTime"));
        cdr.setDuration(doc.getInteger("duration"));
        cdr.setCallType(doc.getString("callType"));
        cdr.setCharge(doc.getDouble("charge"));
        cdr.setStatus(doc.getString("status"));
        cdr.setImsi(doc.getString("imsi"));
        cdr.setImei(doc.getString("imei"));
        cdr.setMcc(doc.getString("mcc"));
        cdr.setMnc(doc.getString("mnc"));
        cdr.setCellId(doc.getString("cellId"));
        return cdr;
    }
}