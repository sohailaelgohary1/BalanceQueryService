/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BalanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String msisdn = request.getParameter("msisdn");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT balance FROM users WHERE msisdn = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, msisdn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("<h1>Balance for " + msisdn + ": " + rs.getDouble("balance") + "</h1>");
            } else {
                out.println("<h1>MSISDN not found</h1>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
