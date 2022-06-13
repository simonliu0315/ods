package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods777xService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods777xJob {
    
    @Autowired
    private Ods777xService service;
    
    /**
     * DAN送ODS共用縣市鄉鎮狀態檔
     * 整檔
     * 每月 / 10日給檔
     */
    @Scheduled(cron = "0 5,35 1-20 10-25 * ?")
    public void runOdsOrgCode() {
        log.info("Ods777xJob_OdsOrgCode Start Executing!");
        long startTime = System.currentTimeMillis();
        
        service.runOdsOrgCode();
        
        log.info("Ods777xJob_OdsOrgCode Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
}
