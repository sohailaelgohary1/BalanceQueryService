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
    private static final int TIMEOUT_MS = 3000; 
    
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
            
           
            //exec("Festival", "Your current balance is"+ Double.toString(balance));
          
            exec("Festival", "Your current balance is"+ doubleToPounds(balance));
            
      
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
           
            URL url = new URL("http://localhost:8080/balancequery/api/balance/"+msisdn);
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
  public static String doubleToPounds(double num) {
        final String[] units = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
        };
        
        final String[] tens = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
        };
        
        final String[] scales = {
            "", "thousand", "million", "billion"
        };
        
        if (Math.abs(num) >= 1000000000000.0) {
            return "Too Much";
        }
        
        boolean isNegative = num < 0;
        if (isNegative) {
            num = -num;
        }
        
   
        long intPart = (long) num;
        double fractionalPart = num - intPart;
        int decimalPart = (int) Math.round(fractionalPart * 100);
        
        String result = "";
        
       
        if (intPart == 0 && decimalPart > 0) {
            if (decimalPart == 25) {
                return "quarter pound";
            } else if (decimalPart == 50 || decimalPart == 5) {
                return "half pound";
            } else {
                return decimalPart + " cents";
            }
        }
        
   
        if (intPart == 0) {
            result = "zero";
        } else {
            result = convertIntToWords(intPart, units, tens, scales);
        }
        result += " pound" + (intPart != 1 ? "s" : "");
        
       
        if (isNegative) {
            result = "negative " + result;
        }
        
     
        if (fractionalPart > 0) {
            if (decimalPart == 25) {
                result += " and quarter";
            } else if (decimalPart == 50 || decimalPart == 5) {
                result += " and half";
            } else if (decimalPart > 0) {
                result += " and " + decimalPart + " cent" + (decimalPart != 1 ? "s" : "");
            }
        }
        
        return result;
    }
    
  
    private static String convertIntToWords(long num, String[] units, String[] tens, String[] scales) {
        if (num == 0) {
            return "";
        }
        
        if (num < 20) {
            return units[(int) num];
        }
        
        if (num < 100) {
            return tens[(int) (num / 10)] + (num % 10 != 0 ? " " + units[(int) (num % 10)] : "");
        }
        
        if (num < 1000) {
            return units[(int) (num / 100)] + " hundred" + (num % 100 != 0 ? " " + convertIntToWords(num % 100, units, tens, scales) : "");
        }
        
        for (int i = 3; i >= 1; i--) {
            if (num >= Math.pow(1000, i)) {
                return convertIntToWords(num / (long) Math.pow(1000, i), units, tens, scales) + " " + scales[i] + 
                       (num % (long) Math.pow(1000, i) != 0 ? " " + convertIntToWords(num % (long) Math.pow(1000, i), units, tens, scales) : "");
            }
        }
        
        return "";
    }
    
}
