/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;



public class User {
    private String msisdn;
    private double balance;

    public User() {}

    public User(String msisdn, double balance) {
        this.msisdn = msisdn;
        this.balance = balance;
    }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
