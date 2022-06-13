/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods771xService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 
 */
@Slf4j
@Service
public class Ods771xJob {

    @Autowired
    private Ods771xService service;

//    @Scheduled(fixedDelay = 60000)
    public void run() throws Exception {
        log.debug("Ods771xJob Start Executing!");

//        service.saveImportViews();
    }

}
