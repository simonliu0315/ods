package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods779xService;

import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 個別化主題資料分析與隨選介接情境
 */
@Slf4j
@Service
public class Ods779xJob {

    @Autowired
    private Ods779xService service;
    
   //@Scheduled(cron = "0 0 8-23 ? * SAT") //每週六 08:00
   @Scheduled(cron = "0 0 08-23 ? * SUN") //每週日 15:00
   public void runB2cUserCarrierSumy() {
       log.info("Ods779xJob_runB2cUserCarrierSumy Start Executing!");
       long startTime = System.currentTimeMillis();

      service.runB2cUserCarrierSumy(Calendar.getInstance().getTime());
      
      log.info("Ods779xJob_runB2cUserCarrierSumy Finished!!Totle time:"
              + (System.currentTimeMillis() - startTime) + " ms");
   }
   
}
