/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.backendMongodb;

/**
 *
 * @author selgohary
 */

import java.util.Date;

public class CallLog {
    private Date startTime;
    private Date endTime;
    private String msisdn;
    private String dtmf;
    private String balance;

    public CallLog() {
        // Empty constructor for MongoDB
    }

    public CallLog(Date startTime, Date endTime, String msisdn, String dtmf, String balance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.msisdn = msisdn;
        this.dtmf = dtmf;
        this.balance = balance;
    }

   
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public String getMsisdn() { return msisdn; }
    public String getDtmf() { return dtmf; }
    public String getBalance() { return balance; }

    
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public void setDtmf(String dtmf) { this.dtmf = dtmf; }
    public void setBalance(String balance) { this.balance = balance; }
}
