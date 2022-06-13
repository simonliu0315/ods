/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.ods.service.Ods774xService;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * ods to dan 轉檔
 */
@Slf4j
@Service
public class Ods774xJob {

    @Autowired
    private Ods774xService service;

    public static void main(String[] args) {

        //取得今日
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(),
                Calendar.DATE);
        //取得七天前
        Date start = DateUtils.addDays(today, -7);
        System.out.println(today);
        System.out.println(start);
    }
    
    // inital
    //@Scheduled(cron = "0 30 11 12 1 ?")
    public void runInital() {
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 inital 資料");
        // OLTP 抽檔:
        // 抽取 #排程啟動日上月# 新增資料；
        // (Ex: 排程啟動日 = 20150802，則抽取 CREATED >= DATE '2015-07-01' AND CREATED <
        // DATE '2015-08-01')
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);


        String dateFormat = "yyyyMMdd";
        SimpleDateFormat formatter;
        Date satInit = null;
        try {
            formatter = new SimpleDateFormat(dateFormat);
            satInit = formatter.parse("20160109");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

       service.findOdsIndividePackageSub(null, satInit);
//        service.findOdsUserFollowPackage(null, end);
//        service.findOdsUserPackageRate(null, end);
        // service.findOdsUserPackageVersionClick(null, end);
        // service.findOdsUserPackageVersionShare(null, end);
//        service.findOdsUserPackageVersionDownload(null, end);
//        service.findOdsUserResourceVersionDownload(null, end);
//
//        service.findSipUserSession(null, end);
//        service.findSipUserSurf(null, end);
//        service.findOdsUserPackageNotify(null, end);
//
//        service.findUaaresource();
//        service.findOdsPackage();

        log.info("Ods774xJob Finished");
    }

    /**<pre>
     * 每月 2日 AM 00:00
     * 改為每周 / 六 AM 00:00
     * 
     *   CREATED >= #排程啟動日# - 7 --上週六 AND CREATED < #排程啟動日# --本周五
     *   (Ex: 排程啟動日(每周六)) = 20151226，則抽取 CREATED >= DATE '2015-12-19' AND CREATED < DATE '2015-12-25')
     * select * from ODS_INDIVIDE_PACKAGE_SUB where CREATED >= :startdate AND CREATED < :enddate
     * </pre>
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Scheduled(cron = "0 0 1 ? * SAT")
    // @Scheduled(fixedDelay = 60000)
    public void runOdsIndividePackageSub() {
        //取得今日
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(),
                Calendar.DATE);
        //取得七天前
        Date start = DateUtils.addDays(today, -7);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsIndividePackageSub 資料");
        service.findOdsIndividePackageSub(start, today);
        log.info("Ods774xJob 抽取 OdsIndividePackageSub 資料 Finished");
    }

    /**
     * 每月 2日 AM 00:00
     * 改為每周 / 六 AM 00:00
     * 整檔
     * select * from ODS_PACKAGE
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    @Scheduled(cron = "0 0 1 ? * SAT")
    // @Scheduled(fixedDelay = 60000)
    public void runOdsPackage() {
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsPackage 資料");
        service.findOdsPackage();
        log.info("Ods774xJob 抽取 OdsPackage 資料 Finished");
    }

    /**
     * select * from ODS_USER_FOLLOW_PACKAGE where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserFollowPackage() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserFollowPackage 資料");
        service.findOdsUserFollowPackage(start, end);
        log.info("Ods774xJob 抽取 OdsUserFollowPackage 資料 Finished");
    }

    /**
     * select * from ODS_USER_PACKAGE_RATE where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserPackageRate() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserPackageRate 資料");
        service.findOdsUserPackageRate(start, end);
        log.info("Ods774xJob 抽取 OdsUserPackageRate 資料 Finished");
    }

    /**
     * select * from ODS_USER_PACKAGE_VERSION_CLICK where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserPackageVersionClick() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserPackageVersionClick 資料");
        service.findOdsUserPackageVersionClick(start, end);
        log.info("Ods774xJob 抽取 OdsUserPackageVersionClick 資料 Finished");
    }

    /**
     * select * from ODS_USER_PACKAGE_VERSION_SHARE where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserPackageVersionShare() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserPackageVersionShare 資料");
        service.findOdsUserPackageVersionShare(start, end);
        log.info("Ods774xJob 抽取 OdsUserPackageVersionShare 資料 Finished");
    }

    /**
     * select * from ODS_USER_PACKAGE_VERSION_DOWNLOAD where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserPackageVersionDownload() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserPackageVersionDownload 資料");
        service.findOdsUserPackageVersionDownload(start, end);
        log.info("Ods774xJob 抽取 OdsUserPackageVersionDownload 資料 Finished");
    }

    /**
     * select * from ODS_USER_RESOURCE_VERSION_DOWNLOAD where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserResourceVersionDownload() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserResourceVersionDownload 資料");
        service.findOdsUserResourceVersionDownload(start, end);
        log.info("Ods774xJob 抽取 OdsUserResourceVersionDownload 資料 Finished");
    }

    /**
     * select * from SIP_USER_SESSION where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runSipUserSession() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 SipUserSession 資料");
        service.findSipUserSession(start, end);
        log.info("Ods774xJob 抽取 SipUserSession 資料 Finished");
    }

    /**
     * select * from SIP_USER_SURF where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runSipUserSurf() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 SipUserSurf 資料");
        service.findSipUserSurf(start, end);
        log.info("Ods774xJob 抽取 SipUserSurf 資料 Finished");
    }

    /**
     * select * from UAARESOURCE
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runUaaresource() {
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 Uaaresource 資料");
        service.findUaaresource();
        log.info("Ods774xJob 抽取 Uaaresource 資料 Finished");
    }

    /**
     * select * from ODS_USER_PACKAGE_NOTIFY where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    //@Scheduled(cron = "0 0 1 2 * ?")
    // @Scheduled(fixedDelay = 60000)
    // 移轉食品雲，內部主題轉檔用到的JOB取消
    public void runOdsUserPackageNotify() {
        Date end = DateUtils.ceiling(Calendar.getInstance().getTime(), Calendar.MONTH);
        end = DateUtils.addMonths(end, -1);
        Date start = DateUtils.addMonths(end, -1);
        log.info("Ods774xJob Start Executing!");
        log.info("Ods774xJob OLTP 抽檔,抽取 OdsUserPackageNotify 資料");
        service.findOdsUserPackageNotify(start, end);
        log.info("Ods774xJob 抽取 OdsUserPackageNotify 資料 Finished");
    }

}
