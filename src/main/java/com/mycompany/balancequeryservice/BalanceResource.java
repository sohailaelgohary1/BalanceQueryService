/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;



import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/balance")
public class BalanceResource {

    @GET
    @Path("/{msisdn}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getBalance(@PathParam("msisdn") String msisdn) {
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT msisdn, balance FROM users WHERE msisdn = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, msisdn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(rs.getString("msisdn"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (user == null) {
            throw new NotFoundException("MSISDN not found");
        }

        return user;
    }
}
