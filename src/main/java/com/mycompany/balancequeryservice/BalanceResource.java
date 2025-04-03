/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.balancequeryservice;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/balance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BalanceResource {
    
    private static final Logger LOGGER = Logger.getLogger(BalanceResource.class.getName());

    @GET
    public Response getAllUsers() {
        try {
            List<User> users = new ArrayList<>();
            String sql = "SELECT * FROM users";
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("msisdn"),
                        rs.getBigDecimal("balance")
                    ));
                }
            }
            return Response.ok(users, MediaType.APPLICATION_JSON).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Database error occurred\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
    }

    @POST
    public Response addUser(User user) {
        if (user.getMsisdn() == null || user.getMsisdn().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"MSISDN cannot be null or empty\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }

        String sql = "INSERT INTO users (msisdn, balance) VALUES (?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getMsisdn());
            stmt.setBigDecimal(2, user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1)); // Get generated ID
                    }
                }
                return Response.status(Response.Status.CREATED).entity(user).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                               .entity("{\"error\": \"User creation failed\"}")
                               .type(MediaType.APPLICATION_JSON)
                               .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Database error occurred\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
    }

    @PUT
    @Path("{id}")
    public Response updateBalance(@PathParam("id") int id, User user) {
        if (user.getBalance() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Balance cannot be null\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }

        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, user.getBalance());
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"User not found\"}")
                               .type(MediaType.APPLICATION_JSON)
                               .build();
            }
            return Response.ok("{\"message\": \"Balance updated successfully\"}", MediaType.APPLICATION_JSON).build();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating balance for user ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Database error occurred\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"User not found\"}")
                               .type(MediaType.APPLICATION_JSON)
                               .build();
            }
            return Response.ok("{\"message\": \"User deleted successfully\"}", MediaType.APPLICATION_JSON).build();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Database error occurred\"}")
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
    }
}
