/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods775xService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * dan to ods 受捐贈機關或團體轉檔 VIEW_ID="O1503#D01@ALL$01";
 */
@Slf4j
@Service
public class Ods775xJob {

    @Autowired
    private Ods775xService service;
    
   @Scheduled(cron = "0 0 1,3,5,7,9,12,15,18,21 15-25 * ?") //每月15號 01:00執行
//  @Scheduled(cron = "0 0 18 * * ?")
    public void run() { 
        log.info("Ods775xJob Start Executing!");
        long startTime= System.currentTimeMillis();
        
        service.run();
        
        log.info("Ods775xJob Finished!!Totle time:"+(System.currentTimeMillis()-startTime)+" ms");
    }
    
}
