package com.mycompany.agi_1;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.asteriskjava.fastagi.*;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
/**
 *
 * @author kareem
 */

public class AGI_1 extends BaseAgiScript {
  
    private static final String API_BASE_URL = "http://your-api-server:8080";
    private static final String BALANCE_ENDPOINT = "https://67ed776d4387d9117bbdba28.mockapi.io/api/v1/balance";
    private static final int TIMEOUT_MS = 3000; // 3 second timeout
    
    @Override
    public void service(AgiRequest request, AgiChannel channel) 
            throws AgiException {
        try {
         
            answer();
            
         
            exec("Festival", "Welcome to balance inquiry service");
            
         
            exec("Festival", "Please enter your 10 digit phone number followed by pound");
            String msisdn = getData("silence/1", 10000, 15); 
            System.out.println(msisdn);
           
            if (msisdn == null ) {
                exec("Festival", "Invalid number format");
                hangup();
                return;
            }
            
            
            double balance = fetchBalanceFromAPI(msisdn);
            
           
            exec("Festival", "Your current balance is"+ Double.toString(balance)); 
      
            exec("Festival", "Thank you for using our service");
            
        } catch (Exception e) {
            verbose("AGI Error: " + e.getMessage(), 1);
            exec("Festival", "System error occurred");
        } finally {
            hangup();
        }
    }
    

    private double fetchBalanceFromAPI(String msisdn) {
        HttpURLConnection conn = null;
        try {
           
            URL url = new URL("https://67ed776d4387d9117bbdba28.mockapi.io/api/v1/balance");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
           
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
              
                return extractBalanceFromJson(response);
            } else {
                verbose("API Error: HTTP " + responseCode, 1);
                return -1;
            }
        } catch (Exception e) {
            try {
                verbose("API Call Failed: " + e.getMessage(), 1);
            } catch (AgiException ex) {
                Logger.getLogger(AGI_1.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    private double extractBalanceFromJson(String jsonResponse) {
    try {
      
        String cleanJson = jsonResponse.replaceAll("[\\[\\]\\s]", "");
        
     
        String[] pairs = cleanJson.split(",");
        
        
        for (String pair : pairs) {
            if (pair.contains("\"balance\":")) {
                
                String value = pair.split(":")[1].replaceAll("[^\\d.]", "");
                return Double.parseDouble(value);
            }
        }
    } catch (Exception e) {
        try {
            verbose("Balance extraction failed: " + e.getMessage(), 1);
        } catch (AgiException ex) {
            Logger.getLogger(AGI_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return -1;
}
}
