package com.iti.cdr;

import org.bson.types.ObjectId;
import java.util.Date;

public class CDR {
    private ObjectId id;
    private String callId;
    private String callingNumber;
    private String calledNumber;
    private Date startTime;
    private Date endTime;
    private int duration; // in seconds
    private String callType; // VOICE, SMS, DATA
    private double charge;
    private String status; // SUCCESS, FAILED, BUSY
    private String imsi;
    private String imei;
    private String mcc;
    private String mnc;
    private String cellId;

    // Constructors
    public CDR() {}

    public CDR(String callId, String callingNumber, String calledNumber, 
              Date startTime, Date endTime, String callType, 
              double charge, String status) {
        this.callId = callId;
        this.callingNumber = callingNumber;
        this.calledNumber = calledNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = (int)((endTime.getTime() - startTime.getTime()) / 1000);
        this.callType = callType;
        this.charge = charge;
        this.status = status;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    // ... (add all other getters and setters)
}