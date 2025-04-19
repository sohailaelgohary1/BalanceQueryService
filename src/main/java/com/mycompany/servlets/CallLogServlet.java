/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.servlets;




import com.google.gson.Gson;
import com.iti.database.MongoDB.MongoDB_Handler;
import com.mycompany.models.CallLog;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

@WebServlet("/CallLogServlet")
public class CallLogServlet extends HttpServlet {
    private final MongoDB_Handler mongoHandler = new MongoDB_Handler();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        CallLog callLog = gson.fromJson(reader, CallLog.class);

        mongoHandler.saveCallLog(callLog);

        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"CallLog saved successfully\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String callerParam = request.getParameter("caller");
        List<CallLog> logs;

        if (callerParam != null && !callerParam.isEmpty()) {
            logs = mongoHandler.searchCallLogsByCaller(callerParam);
        } else {
            logs = mongoHandler.getAllCallLogs();
            
        }

        String json = gson.toJson(logs);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}

