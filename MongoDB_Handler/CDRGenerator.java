package com.iti.generators;
import com.iti.database.DB_Handler;
import com.iti.database.psql.PSQL_Handler;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class CDRGenerator {
  
       
	   CDR cdr =  CDR();		
    public static void generateAndSaveCDRs(CDR cdr) {
		 dbHandler dbhandler = dbHandler();
         dbhandler.connect();
         
	    cdrr=dbhandler.create(cdr);
        
        dbhandler.disconnect();
    } 


 
}