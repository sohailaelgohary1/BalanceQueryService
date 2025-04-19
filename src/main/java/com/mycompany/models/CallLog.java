/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.models;

import java.util.Date;

/**
 *
 * @author selgohary
 */

public class CallLog {
    private String caller;
    private String callee;
    private String callStartTime;
    private String callEndTime;
    private String dtmfValue;
    private String balanceReturned;
    private int duration;

    public CallLog() {}

    public CallLog(String caller, String callee, String callStartTime, String callEndTime,
                   String dtmfValue, String balanceReturned, int duration) {
        this.caller = caller;
        this.callee = callee;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
        this.dtmfValue = dtmfValue;
        this.balanceReturned = balanceReturned;
        this.duration = duration;
    }

    // Getters
    public String getCaller() { return caller; }
    public String getCallee() { return callee; }
    public String getCallStartTime() { return callStartTime; }
    public String getCallEndTime() { return callEndTime; }
    public String getDtmfValue() { return dtmfValue; }
    public String getBalanceReturned() { return balanceReturned; }
    public int getDuration() { return duration; }

    // Setters
    public void setCaller(String caller) { this.caller = caller; }
    public void setCallee(String callee) { this.callee = callee; }
    public void setCallStartTime(String callStartTime) { this.callStartTime = callStartTime; }
    public void setCallEndTime(String callEndTime) { this.callEndTime = callEndTime; }
    public void setDtmfValue(String dtmfValue) { this.dtmfValue = dtmfValue; }
    public void setBalanceReturned(String balanceReturned) { this.balanceReturned = balanceReturned; }
    public void setDuration(int duration) { this.duration = duration; }
}
