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
//public class AGI_1 extends BaseAgiScript {
//
//@Override
//public void service (AgiRequest ar, AgiChannel ac)throws AgiException{
//    answer();
//    streamFile("hello-world");
//    hangup();
//    throw new UnsupportedOperationException("NotSupported yet");
//}
//}
//public class AGI_1 {
//
//    public static void main(String[] args) {
//        AGIChannel agi = new AGIChannel();
//
//        try {
//            String msisdn = agi.getVariable("msisdn");
//
//            // Call REST service
//            URL url = new URL("http://localhost:8080/balance-api/balance?msisdn=" + msisdn);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            if (conn.getResponseCode() == 200) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String response = br.readLine();
//
//                // Parse JSON response
//                JSONObject json = new JSONObject(response);
//                double balance = json.getDouble("balance");
//
//                // Convert balance to speech
//                agi.exec("SayNumber", Double.toString(balance));
//            } else {
//                agi.exec("Playback", "invalid-number");
//            }
//
//        } catch (Exception e) {
//            agi.verbose("Error in AGI script: " + e.getMessage(), 1);
//        }
//    }
//}
//
//import org.asteriskjava.fastagi.AgiChannel;
//import org.asteriskjava.fastagi.AgiException;
//import org.asteriskjava.fastagi.AgiRequest;
//import org.asteriskjava.fastagi.BaseAgiScript;

//public class AGI_1 extends BaseAgiScript {
//
//    private static final String TEST_MSISDN = "20123456789";
//    private static final double TEST_BALANCE = 150.50;
//
//    @Override
//    public void service(AgiRequest request, AgiChannel channel)
//            throws AgiException {
//        try {
//            // Answer the call
//            answer();
//
//            // Speak welcome message using TTS
//            exec("Festival", "Welcome to balance inquiry service");
//
//            // Prompt for MSISDN
//            String msisdn = getData("silence/1", 5000, "Please enter your 10 digit phone number followed by pound", 10000 );

////            String msisdn  = getDigits("Please enter your 10 digit phone number", 
////                                    10000,  // timeout in ms
////                                    10);
//              String msisdn = getData(TEST_MSISDN, 0)
//              String msisdn = getData(TEST_MSISDN, 0, 0)
//            // Validate input
//            if (msisdn == null || msisdn.length() != 10 || !msisdn.matches("\\d+")) {
//                exec("Festival", "Invalid number entered");
//                hangup();
//                return;
//            }
//
//            // Check if test number matches
//            if (msisdn.equals(TEST_MSISDN)) {
//                exec("Festival", "Your current balance is");
//                exec("SayNumber", Double.toString(TEST_BALANCE));
//            } else {
//                exec("Festival", "Number not found in our system");
//            }
//
//            // Goodbye message
//            exec("Festival", "Thank you for using our service");
//
//        } catch (Exception e) {
//            verbose("Error in AGI script: " + e.getMessage(), 1);
//            exec("Festival", "We are experiencing technical difficulties");
//        } finally {
//            hangup();
//        }
//    }
//}
//package com.mycompany.agi_1;
//
//import org.asteriskjava.fastagi.AgiChannel;
//import org.asteriskjava.fastagi.AgiException;
//import org.asteriskjava.fastagi.AgiRequest;
//import org.asteriskjava.fastagi.BaseAgiScript;



public class AGI_1 extends BaseAgiScript {
    // Configuration - adjust these for your environment
    private static final String API_BASE_URL = "http://your-api-server:8080";
    private static final String BALANCE_ENDPOINT = "https://67ed776d4387d9117bbdba28.mockapi.io/api/v1/balance";
    private static final int TIMEOUT_MS = 3000; // 3 second timeout
    
    @Override
    public void service(AgiRequest request, AgiChannel channel) 
            throws AgiException {
        try {
            // 1. Answer the call
            answer();
            
            // 2. Play welcome message
            exec("Festival", "Welcome to balance inquiry service");
            
            // 3. Collect DTMF input (10 digits)
            exec("Festival", "Please enter your 10 digit phone number followed by pound");
            String msisdn = getData("silence/1", 10000, 15); // 5s timeout, max 10 digits
            System.out.println(msisdn);
            // 4. Validate input
            if (msisdn == null /*|| !msisdn.matches("\\d{15}")*/) {
                exec("Festival", "Invalid number format");
                hangup();
                return;
            }
            
            // 5. Query balance (replace with your actual lookup logic)
            double balance = fetchBalanceFromAPI(msisdn);
            
            // 6. Announce balance
            exec("Festival", "Your current balance is"+ Double.toString(balance)); 
            // 7. Goodbye
            exec("Festival", "Thank you for using our service");
            
        } catch (Exception e) {
            verbose("AGI Error: " + e.getMessage(), 1);
            exec("Festival", "System error occurred");
        } finally {
            hangup();
        }
    }
    
    // Replace this with your actual balance lookup logic
//    private double lookupBalance(String msisdn) {
//                  try {  URL url = new URL(BALANCE_ENDPOINT + msisdn);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        
//            conn.setRequestMethod("GET");
//                  
//        
//
//            if (conn.getResponseCode() == 200) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String response = br.readLine();
//
//                // Parse JSON response
//                //JSONObject json = new JSONObject(response);
//                //double balance = json.getDouble("balance");}
//            
//                    
//                    }
//                  catch (Exception e){
//                      
//                  }}
    /////////////////////////////
    private double fetchBalanceFromAPI(String msisdn) {
        HttpURLConnection conn = null;
        try {
            // Create connection
            URL url = new URL("https://67ed776d4387d9117bbdba28.mockapi.io/api/v1/balance");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
                // Parse JSON response (simple version)
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
        // 1. Remove all whitespace and square brackets
        String cleanJson = jsonResponse.replaceAll("[\\[\\]\\s]", "");
        
        // 2. Split into key-value pairs
        String[] pairs = cleanJson.split(",");
        
        // 3. Find the balance pair
        for (String pair : pairs) {
            if (pair.contains("\"balance\":")) {
                // 4. Extract the numeric value
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
    return -1; // Return -1 if extraction fails
}
}