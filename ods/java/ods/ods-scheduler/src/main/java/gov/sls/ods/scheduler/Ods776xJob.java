/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.service.Ods776xService;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * runEInvoBANSumy開立電子發票彙總 更新檔方式匯入(先刪再新增)@Scheduled(cron = "0 0 3,11,19 3-25 * ?") //每月10號 03:00執行
 * runEInvoHsnBSDCSumy地區行業別開立電子發票彙總 更新檔方式匯入(先刪再新增)@Scheduled(cron = "0 0 1,5,9,10,15,19 3-25 * ?") //每月10號 01:00執行.
 * runEBANSumy營業人資料彙整總 整檔方式匯入(全刪再新增).@Scheduled(cron = "0 0 1,3,5,7,9,11,13,15,17,19,21 3-25 * ?") //每月10號 01:00執行
 * 
 * runEAwardInvoBanSumy營業人電子發票中獎彙總 整檔方式匯入(全刪再新增).@Scheduled(cron = "0 0 1,3,5,7,9,10,13,15,17,19,21 3-25 * ?") //每月15號 03:00執行
 * runEIdanpfsBanSumy營所稅營業人申報彙總 整檔方式匯入(全刪再新增) 每年 / 2/4, 6/4, 10/5 AM 00:00.@Scheduled(cron = "0 0 1 4-5 2,6,10 ?")
 * runEIdanpfsDistrictSumy營所稅營業人申報行政區彙總 整檔方式匯入(全刪再新增) 每年 / 10/10, 2/10, 6/10 AM 00:00.@Scheduled(cron = "0 0 1 5-25 2,6,10 ?")
 * 
 * 
 * runEIdanvatBanSumy營業稅營業人申報彙總 整檔 每月 / 5日 AM 00:00.@Scheduled(cron = "0 0 1 5-25 * ?")
 * runEIdanvatDistrictSumy營業稅營業人申報行政區彙總 整檔 每月 / 5日 AM 00:00.@Scheduled(cron = "0 0 2 5-25 * ?")
 * 
 * 
 * 
 */
@Slf4j
@Service
public class Ods776xJob {
    
    @Autowired
    private Ods776xService service;
    
    /**
     * 開立電子發票彙總 更新檔方式匯入(先刪再新增) 更新訂閱更新紀錄.
     */
   @Scheduled(cron = "0 0 3,11,19 3-25 * ?") //每月10號 03:00執行
    public void runEInvoBANSumy() {
         log.info("Ods776xJob_EInvoBANSumy Start Executing!");
         long startTime = System.currentTimeMillis();

        service.runEInvoBANSumy(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EInvoBANSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 地區行業別開立電子發票彙總 更新檔方式匯入(先刪再新增).
     */
   @Scheduled(cron = "0 0 1,5,9,10,15,19 3-25 * ?") //每月10號 01:00執行
    public void runEInvoHsnBSDCSumy() {
        log.info("Ods776xJob_EInvoHsnBSDCSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEInvoHsnBSDCSumy(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EInvoHsnBSDCSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
     
    /**
     * 營業人資料彙整總 整檔方式匯入(全刪再新增).
     */
   @Scheduled(cron = "0 0 1,3,5,7,9,11,13,15,17,19,21 3-25 * ?") //每月10號 01:00執行
    public void runEBANSumy() {
        log.info("Ods776xJob_EBANSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEBANSumy(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EBANSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");

    }
    
    /**
     * 營業人電子發票中獎彙總  整檔方式匯入(全刪再新增).
     */
   @Scheduled(cron = "0 0 1,3,5,7,9,10,13,15,17,19,21 3-25 * ?") //每月15號 03:00執行
    public void runEAwardInvoBanSumy() {
        log.info("Ods776xJob_EAwardInvoBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEAwardInvoBanSumyAll(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EAwardInvoBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * 營所稅營業人申報彙總  整檔方式匯入(全刪再新增) 每年 / 2/4, 6/4, 10/5 AM 00:00.
     */
   @Scheduled(cron = "0 0 1 4-5 2,6,10 ?")
    public void runEIdanpfsBanSumy() {
        log.info("Ods776xJob_EIdanpfsBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEIdanpfsBanSumyAll(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EIdanpfsBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
   /**
     * 營所稅營業人申報行政區彙總  整檔方式匯入(全刪再新增) 每年 / 10/10, 2/10, 6/10 AM 00:00.
     */
   @Scheduled(cron = "0 0 1 5-25 2,6,10 ?")
    public void runEIdanpfsDistrictSumy() {
        log.info("Ods776xJob_EIdanpfsDistrictSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEIdanpfsDistrictSumyAll(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EIdanpfsDistrictSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 營業稅營業人申報彙總 整檔 每月 / 5日 AM 04:00.
     */
   @Scheduled(cron = "0 0 4 5-25 * ?")
    public void runEIdanvatBanSumy() {
        log.info("Ods776xJob_EIdanvatBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEIdanvatBanSumy(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EIdanvatBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 營業稅營業人申報行政區彙總 整檔 每月 / 5日 AM 00:00.
     */
   @Scheduled(cron = "0 0 2 5-25 * ?")
    public void runEIdanvatDistrictSumy() {
        log.info("Ods776xJob_EIdanvatDistrictSumy Start Executing!");
        long startTime = System.currentTimeMillis();

        service.runEIdanvatDistrictSumy(Calendar.getInstance().getTime());
        
        log.info("Ods776xJob_EIdanvatDistrictSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
}
