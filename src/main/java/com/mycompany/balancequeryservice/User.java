/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement  // Ensures the object can be serialized to JSON
public class User {
    private int id;
    private String msisdn;
    private BigDecimal balance;

    public User() {}

    public User(int id, String msisdn, BigDecimal balance) {
        this.id = id;
        this.msisdn = msisdn;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public BigDecimal getBalance() { return balance != null ? balance : BigDecimal.ZERO; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
