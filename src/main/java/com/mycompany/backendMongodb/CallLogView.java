/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.backendMongodb;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

public class CallLogView extends HttpServlet {

    // MongoDB connection setup
    private MongoCollection<Document> getCollection() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("balance_query_db");
        return database.getCollection("call_logs");
    }

    // Read all logs from MongoDB and convert to CallLog list
    private List<CallLog> getAllCallLogs() {
        List<CallLog> logs = new ArrayList<>();
        MongoCollection<Document> collection = getCollection();

        Bson sort = Sorts.descending("startTime");

        for (Document doc : collection.find().sort(sort)) {
            Date startTime = doc.getDate("startTime");
            Date endTime = doc.getDate("endTime");
            String msisdn = doc.getString("msisdn");
            String dtmf = doc.getString("dtmf");
            String balance = doc.getString("balance");

            CallLog log = new CallLog(startTime, endTime, msisdn, dtmf, balance);
            logs.add(log);
        }

        return logs;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<CallLog> logs = getAllCallLogs();

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Call Logs</h2>");
        response.getWriter().println("<table border='1'>");
        response.getWriter().println("<tr><th>Start Time</th><th>End Time</th><th>MSISDN</th><th>DTMF</th><th>Balance</th></tr>");

        for (CallLog log : logs) {
            response.getWriter().println("<tr>");
            response.getWriter().println("<td>" + log.getStartTime() + "</td>");
            response.getWriter().println("<td>" + log.getEndTime() + "</td>");
            response.getWriter().println("<td>" + log.getMsisdn() + "</td>");
            response.getWriter().println("<td>" + log.getDtmf() + "</td>");
            response.getWriter().println("<td>" + log.getBalance() + "</td>");
            response.getWriter().println("</tr>");
        }

        response.getWriter().println("</table>");
        response.getWriter().println("</body></html>");
    }
}
