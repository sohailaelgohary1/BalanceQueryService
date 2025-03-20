/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;



import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users")
public class UserManagement extends HttpServlet {
    
    // GET: View all users
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            out.println("<html><body><h2>Users List</h2><ul>");
            while (rs.next()) {
                out.println("<li>ID: " + rs.getInt("id") + ", MSISDN: " + rs.getString("msisdn") + 
                            ", Balance: " + rs.getBigDecimal("balance") + "</li>");
            }
            out.println("</ul></body></html>");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    // POST: Add a new user
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String msisdn = request.getParameter("msisdn");
        String balance = request.getParameter("balance");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (msisdn, balance) VALUES (?, ?)")) {
            pstmt.setString(1, msisdn);
            pstmt.setBigDecimal(2, new BigDecimal(balance));
            pstmt.executeUpdate();
            response.getWriter().println("User added successfully.");
        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // PUT: Update balance of an existing user
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String msisdn = request.getParameter("msisdn");
        String balance = request.getParameter("balance");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET balance = ? WHERE msisdn = ?")) {
            pstmt.setBigDecimal(1, new BigDecimal(balance));
            pstmt.setString(2, msisdn);
            int updatedRows = pstmt.executeUpdate();
            
            if (updatedRows > 0) {
                response.getWriter().println("Balance updated successfully.");
            } else {
                response.getWriter().println("User not found.");
            }
        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // DELETE: Remove a user
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String msisdn = request.getParameter("msisdn");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE msisdn = ?")) {
            pstmt.setString(1, msisdn);
            int deletedRows = pstmt.executeUpdate();

            if (deletedRows > 0) {
                response.getWriter().println("User deleted successfully.");
            } else {
                response.getWriter().println("User not found.");
            }
        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
