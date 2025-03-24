package com.mycompany.balancequeryservice;



/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class JakartaRestConfiguration extends Application {
}
