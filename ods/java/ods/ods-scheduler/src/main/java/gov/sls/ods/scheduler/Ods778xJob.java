package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods778xService;

import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.vfs2.FileSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * SFTP搬檔排程
 * SFTP for DAN TO ODS @Scheduled(cron = "0 15,45 * * * ?")
 * SFTP for ODS TO DAN @Scheduled(cron = "0 1,30 * * * ?")
 * </pre>
 */
@Slf4j
@Service
public class Ods778xJob {
    @Autowired
    private Ods778xService service;
    
    /**
     * SFTP for ODS TO DAN
     * @throws FileSystemException 
     */
//    @Scheduled(cron = "0 1,30 * * * ?")
     @Scheduled(fixedDelay = 30000)
    public void runUpload() {
        log.info("run  upload ");
        service.runUpload(Calendar.getInstance().getTime());
    }

    /**
     * SFTP for DAN TO ODS
     * @throws FileSystemException 
     */
    @Scheduled(cron = "0 15,45 * * * ?")
//    @Scheduled(fixedDelay = 45000)
    public void runDownload()  {
        service.runDownload();
    }


}
