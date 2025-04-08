package com.iti.cdr;

import com.iti.cdr.db.MongoDBManager;
import com.iti.cdr.repository.CDRRepository;
import com.iti.cdr.service.CDRGeneratorService;

public class CDRApplication {
    public static void main(String[] args) {
        try {
            
            MongoDatabase database = MongoDBManager.getDatabase();
            
            
            CDRRepository cdrRepository = new CDRRepository(database);
            CDRGeneratorService generator = new CDRGeneratorService(cdrRepository);
            
            generator.generateAndStoreCDRs(1000);
            
            System.out.println("CDR generation completed successfully!");
        } finally {
            MongoDBManager.close();
        }
    }
}
