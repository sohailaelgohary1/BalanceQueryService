package com.iti.cdr.service;

import com.iti.cdr.CDR;
import com.iti.cdr.repository.CDRRepository;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CDRGeneratorService {
    private final CDRRepository cdrRepository;
    private final Random random = new Random();

    public CDRGeneratorService(CDRRepository cdrRepository) {
        this.cdrRepository = cdrRepository;
    }

    public CDR generateRandomCDR() {
        String callId = UUID.randomUUID().toString();
        String callingNumber = generateRandomNumber();
        String calledNumber = generateRandomNumber();
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + (random.nextInt(3600) * 1000));
        String callType = random.nextBoolean() ? "VOICE" : "SMS";
        double charge = random.nextDouble() * 10;
        String status = random.nextBoolean() ? "SUCCESS" : "FAILED";
        
        CDR cdr = new CDR(callId, callingNumber, calledNumber, 
                         startTime, endTime, callType, charge, status);
        
        
        cdr.setImsi("IMSI" + random.nextInt(10000));
        cdr.setImei("IMEI" + random.nextInt(10000));
        cdr.setMcc(String.valueOf(200 + random.nextInt(100)));
        cdr.setMnc(String.valueOf(10 + random.nextInt(90)));
        cdr.setCellId(String.valueOf(random.nextInt(10000)));
        
        return cdr;
    }

    public void generateAndStoreCDRs(int count) {
        for (int i = 0; i < count; i++) {
            CDR cdr = generateRandomCDR();
            cdrRepository.insertCDR(cdr);
        }
    }

    private String generateRandomNumber() {
        return String.format("20%d%d%d%d%d", 
            random.nextInt(10), random.nextInt(10), 
            random.nextInt(10), random.nextInt(10), 
            random.nextInt(10));
    }
}
