package gov.sls.ods.web.rest;

import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods708eGridDto;
import gov.sls.ods.service.Ods774xService;
import gov.sls.ods.service.Ods775xService;
import gov.sls.ods.service.Ods776xService;
import gov.sls.ods.service.Ods777xService;
import gov.sls.ods.service.Ods778xService;
import gov.sls.ods.service.Ods779xService;
import gov.sls.ods.web.dto.Ods708eDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.vfs2.FileSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS708E/rest")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS708E')")
public class Ods708eResource {

    @Autowired
    private Ods774xService ods774xService;

    @Autowired
    private Ods775xService ods775xService;

    @Autowired
    private Ods776xService ods776xService;

    @Autowired
    private Ods777xService ods777xService;
    
    @Autowired
    private Ods778xService ods778xService;

    @Autowired
    private Ods779xService ods779xService;

    private final String[][] jobs = {
                                        {"Ods778xService", "runDownloadTableau", "SFTP下載Tableau檔案，起訖日不會用到"},
                                        {"Ods774xService", "findOdsIndividePackageSub", "OLTP 抽檔,抽取 OdsIndividePackageSub 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsUserFollowPackage", "OLTP 抽檔,抽取 OdsUserFollowPackage 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsPackage", "OLTP 抽檔,抽取 OdsPackage 資料，起訖日不會用到"},
                                        {"Ods774xService", "findOdsUserPackageRate", "OLTP 抽檔,抽取 OdsUserPackageRate 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsUserPackageVersionClick", "OLTP 抽檔,抽取 OdsUserPackageVersionClick 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsUserPackageVersionShare", "OLTP 抽檔,抽取 OdsUserPackageVersionShare 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsUserPackageVersionDownload", "OLTP 抽檔,抽取 OdsUserPackageVersionDownload 資料，起訖日必填"},
                                        {"Ods774xService", "findOdsUserResourceVersionDownload", "OLTP 抽檔,抽取 OdsUserResourceVersionDownload 資料，起訖日必填"},
                                        {"Ods774xService", "findSipUserSession", "OLTP 抽檔,抽取 SipUserSession 資料，起訖日必填"},
                                        {"Ods774xService", "findSipUserSurf", "OLTP 抽檔,抽取 SipUserSurf 資料，起訖日必填"},
                                        {"Ods774xService", "findUaaresource", "OLTP 抽檔,抽取 Uaaresource 資料，起訖日不會用到"},
                                        {"Ods774xService", "findOdsUserPackageNotify", "OLTP 抽檔,抽取 OdsUserPackageNotify 資料，起訖日必填"},
                                        {"Ods775xService", "runDanB2CXcaDntSumy", "dan to ods 受捐贈機關或團體轉檔，起訖日不會用到"},
                                        {"Ods776xService", "runEInvoBANSumy", "營業人開立電子發票彙總轉檔(會更新訂閱紀錄) 更新檔方式匯入(先刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEInvoBANSumyAll", "營業人開立電子發票彙總轉檔(會更新訂閱紀錄) 整檔方式匯入(全刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEAwardInvoBanSumy", "營業人電子發票中獎彙總轉檔，更新檔方式匯入(先刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEAwardInvoBanSumyAll", "營業人電子發票中獎彙總轉檔，整檔方式匯入(全刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEInvoHsnBSDCSumy", "營業人地區行業別開立電子發票彙總轉檔  更新檔方式匯入(先刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEInvoHsnBSDCSumyAll", "營業人地區行業別開立電子發票彙總轉檔  整檔方式匯入(全刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEBANSumy", "營業人資料彙整轉檔，只需要起日"},
                                        {"Ods776xService", "runEIdanpfsBanSumy", "營業人營所稅彙總轉檔，更新檔方式匯入(先刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEIdanpfsBanSumyAll", "營業人營所稅彙總轉檔，整檔方式匯入(全刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEIdanpfsDistrictSumy", "地區行業別營所稅彙總轉檔，更新檔方式匯入(先刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEIdanpfsDistrictSumyAll", "地區行業別營所稅彙總轉檔，整檔方式匯入(全刪再新增)，只需要起日"},
                                        {"Ods776xService", "runEIdanvatBanSumy", "營業人營業稅彙總轉檔，只需要起日"},
                                        {"Ods776xService", "runEIdanvatDistrictSumy", "地區行業別營業稅彙總轉檔，只需要起日"},
                                        {"Ods777xService", "runOdsOrgCode", "縣市鄉鎮市區代碼檔轉檔，起訖日不會用到"},
                                        {"Ods778xService", "runUpload", "SFTP上傳檔案，只需要起日，會傳psrdata\\ods\\odsdan\\yyyyMMdd裡面的東"},
                                        {"Ods778xService", "runDownload", "SFTP下載檔案，起訖日不會用到，會下載到psrdata\\ods\\danods"},
                                        {"Ods779xService", "runB2cUserCarrierSumy", "已歸戶載具分析轉檔，只需要起日"}
                                    };
    
    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods708eDto find(@RequestBody Ods708eDto ods708eDto, Alerter alerter) {
        List<Ods708eGridDto> ods708eGridDtoList = new ArrayList<Ods708eGridDto>();
        Calendar defaultStartTime=Calendar.getInstance();
        defaultStartTime.add(Calendar.MONTH, -1);
        defaultStartTime.set(Calendar.DATE, 1);
        Calendar defaultStopTime=Calendar.getInstance();
        defaultStopTime.set(Calendar.DATE, 1);
        for(String[] job:jobs) {
            Ods708eGridDto gdto = new Ods708eGridDto();
            gdto.setServiceName(job[0]);
            gdto.setMethodName(job[1]);
            gdto.setDesc(job[2]);
            gdto.setStartDate(defaultStartTime.getTime());
            gdto.setEndDate(defaultStopTime.getTime());
            ods708eGridDtoList.add(gdto);
        }
        
        Ods708eDto dto = new Ods708eDto();
        log.debug("List:"+ods708eGridDtoList.size());
        dto.setOds708eGridDtoList(ods708eGridDtoList);
        if (ods708eGridDtoList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }
    

    @RequestMapping(value = "/execute", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods708eDto execute(@RequestBody Ods708eDto ods708eDto, Alerter alerter) {
        String serviceName = ods708eDto.getServiceName();
        String methodName = ods708eDto.getMethodName();
        Date startDate = ods708eDto.getStartDate();
        Date endDate = ods708eDto.getEndDate();
        log.info("serviceName:"+serviceName);
        log.info("methodName:"+methodName);
        log.info("startDate:"+startDate);
        log.info("endDate:"+endDate);

        if("Ods774xService".equals(serviceName)) {
            if("findOdsIndividePackageSub".equals(methodName)) {
                ods774xService.findOdsIndividePackageSub(startDate, endDate);
            } else if("findOdsUserFollowPackage".equals(methodName)) {
                ods774xService.findOdsUserFollowPackage(startDate, endDate);
            } else if("findOdsPackage".equals(methodName)) {
                ods774xService.findOdsPackage();
            } else if("findOdsUserPackageRate".equals(methodName)) {
                ods774xService.findOdsUserPackageRate(startDate, endDate);
            } else if("findOdsUserPackageVersionClick".equals(methodName)) {
                ods774xService.findOdsUserPackageVersionClick(startDate, endDate);
            } else if("findOdsUserPackageVersionShare".equals(methodName)) {
                ods774xService.findOdsUserPackageVersionShare(startDate, endDate);
            } else if("findOdsUserPackageVersionDownload".equals(methodName)) {
                ods774xService.findOdsUserPackageVersionDownload(startDate, endDate);
            } else if("findOdsUserResourceVersionDownload".equals(methodName)) {
                ods774xService.findOdsUserResourceVersionDownload(startDate, endDate);
            } else if("findSipUserSession".equals(methodName)) {
                ods774xService.findSipUserSession(startDate, endDate);
            } else if("findSipUserSurf".equals(methodName)) {
                ods774xService.findSipUserSurf(startDate, endDate);
            } else if("findUaaresource".equals(methodName)) {
                ods774xService.findUaaresource();
            } else if("findOdsUserPackageNotify".equals(methodName)) {
                ods774xService.findOdsUserPackageNotify(startDate, endDate);
            }
        }
        if("Ods775xService".equals(serviceName)) {
            if("runDanB2CXcaDntSumy".equals(methodName)) {
                ods775xService.run();
            }
        }
        if("Ods776xService".equals(serviceName)) {
            if("runEInvoBANSumy".equals(methodName)) {
                ods776xService.runEInvoBANSumy(startDate);
            } else if("runEInvoBANSumyAll".equals(methodName)) {//整檔處理
                ods776xService.runEInvoBANSumyAll(startDate);
            } else if("runEAwardInvoBanSumy".equals(methodName)) {
                ods776xService.runEAwardInvoBanSumy(startDate);
            } else if("runEAwardInvoBanSumyAll".equals(methodName)) {//整檔處理
                ods776xService.runEAwardInvoBanSumyAll(startDate);
            } else if("runEInvoHsnBSDCSumy".equals(methodName)) {
                ods776xService.runEInvoHsnBSDCSumy(startDate);
            } else if("runEInvoHsnBSDCSumyAll".equals(methodName)) {//整檔處理
                ods776xService.runEInvoHsnBSDCSumyAll(startDate);
            } else if("runEBANSumy".equals(methodName)) {//整檔處理
                ods776xService.runEBANSumy(startDate);
            } else if("runEIdanpfsBanSumy".equals(methodName)) {
                ods776xService.runEIdanpfsBanSumy(startDate);
            } else if("runEIdanpfsBanSumyAll".equals(methodName)) {//整檔處理
                ods776xService.runEIdanpfsBanSumyAll(startDate);
            } else if("runEIdanpfsDistrictSumy".equals(methodName)) {
                ods776xService.runEIdanpfsDistrictSumy(startDate);
            } else if("runEIdanpfsDistrictSumyAll".equals(methodName)) {//整檔處理
                ods776xService.runEIdanpfsDistrictSumyAll(startDate);
            } else if("runEIdanvatBanSumy".equals(methodName)) {
                ods776xService.runEIdanvatBanSumy(startDate);
            } else if("runEIdanvatDistrictSumy".equals(methodName)) {
                ods776xService.runEIdanvatDistrictSumy(startDate);
            }
        }
        if("Ods777xService".equals(serviceName)) {
            if("runOdsOrgCode".equals(methodName)) {
                ods777xService.runOdsOrgCode();
            }
        }
        if("Ods779xService".equals(serviceName)) {
            if("runB2cUserCarrierSumy".equals(methodName)) {
                ods779xService.runB2cUserCarrierSumy(startDate);
            }
        }
        
        if("Ods778xService".equals(serviceName)) {
            if("runUpload".equals(methodName)) {
                    ods778xService.runUpload(startDate);
            }
            if("runDownload".equals(methodName)) {
                ods778xService.runDownload();
            }
            if("runDownloadTableau".equals(methodName)) {
                ods778xService.runDownloadTableau();
            }
        }
        
        alerter.success("啟動");
//        if (ods708eGridDtoList.isEmpty()) {
//            alerter.info(Messages.warning_notFound());
//        } else {
//            alerter.success(Messages.success_find());
//        }
        return ods708eDto;
    }

}
