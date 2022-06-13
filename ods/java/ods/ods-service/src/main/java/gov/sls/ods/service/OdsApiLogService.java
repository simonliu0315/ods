package gov.sls.ods.service;

import gov.sls.entity.ods.OdsApiLog;
import gov.sls.ods.repository.OdsApiLogRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdsApiLogService {

    @Autowired
    private OdsApiLogRepository odsApiLogRepository;

    /**
     * <pre>
     * methodId 清單：
     * 序號 001 https://sip.einvoice.nat.gov.tw:443/ods-main/ODS358I/packages?packageName={packageName}&packageTags={packageTags}&resourceFileExts={resourceFileExts}
     * 序號 002 https://sip.einvoice.nat.gov.tw:443/ods-main/ODS359I/package/versions/{packageId}
     * 序號 003 https://sip.einvoice.nat.gov.tw:443/ods-main/ODS359I/resources/{packageId}/{packageVer}
     * 序號 004 ODS371I code: O1501@ALL
     * 序號 005 ODS372I code: O1503@ALL
     * 序號 006 ODS373I code: O1503@ALL
     * 序號 007 ODS374I code: O1504@ALL
     * 序號 008 ODS375I code: O1504@ALL
     * 序號 009 ODS376I code: O1504@ALL
     * 序號 010 ODS377I code: O1504@ALL
     * 序號 011 ODS378I code: O1504@ALL
     * 序號 012 ODS379I code: O1504@ALL
     * </pre>
     * @param methodId    methodId   
     * @param ip          ip         
     * @param requestDate requestDate
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param request     request url
     * @param status      回傳訊息代碼     
     */
    public void createLog(String methodId, String ip, Date requestDate, String packageId,
            Integer packageVer, String request, Object status) {
        OdsApiLog odsApiLog = new OdsApiLog();
        odsApiLog.setMethodId(methodId);
        odsApiLog.setIp(ip);
        odsApiLog.setRequestDate(new SimpleDateFormat("yyyyMMdd").format(requestDate));
        odsApiLog.setPackageId(packageId);
        odsApiLog.setPackageVer(packageVer);
        odsApiLog.setCreated(new Date());
        odsApiLog.setRequest(request);
        odsApiLog.setStatus((String)status);
        odsApiLogRepository.create(odsApiLog);
    }
    

    /**
     * {@link OdsApiLogService#createLog(String, String, Date, String, Integer, String, Object)}
     * 
     * @param methodId    methodId   
     * @param ip          ip         
     * @param requestDate requestDate
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param request     request url
     * @param status      回傳訊息代碼     
     * @param appId      appId
     * @param userId userId     
     */
    public void createLog(String methodId, String ip, Date requestDate, String packageId,
            Integer packageVer, String request, Object status, String appId, String userId) {
        OdsApiLog odsApiLog = new OdsApiLog();
        odsApiLog.setMethodId(methodId);
        odsApiLog.setIp(ip);
        odsApiLog.setRequestDate(new SimpleDateFormat("yyyyMMdd").format(requestDate));
        odsApiLog.setPackageId(packageId);
        odsApiLog.setPackageVer(packageVer);
        odsApiLog.setCreated(new Date());
        odsApiLog.setRequest(request);
        odsApiLog.setStatus((String)status);
        
        odsApiLog.setAppId(appId);
        odsApiLog.setUserId(userId);
        
        odsApiLogRepository.create(odsApiLog);
    }
}
