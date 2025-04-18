/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.backendMongodb;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class LogCall extends HttpServlet {

    private MongoCollection<Document> getCollection() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("balance_query_db");
        return database.getCollection("call_logs");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String callingMSISDN = request.getParameter("msisdn");
            String dtmf = request.getParameter("dtmf");
            String balance = request.getParameter("balance");
            Date startTime = new Date();  

            Date endTime = new Date();    

            // Build MongoDB Document to insert
            Document doc = new Document("msisdn", callingMSISDN)
                    .append("dtmf", dtmf)
                    .append("balance", balance)
                    .append("startTime", startTime)
                    .append("endTime", endTime);

            // Insert into MongoDB
            getCollection().insertOne(doc);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Call log saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}

