package gov.sls.ods.service;

import gov.sls.commons.core.security.user.User;
import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.entity.ods.OdsUserPackageVersionClick;
import gov.sls.entity.ods.OdsUserPackageVersionDownload;
import gov.sls.entity.ods.OdsUserPackageVersionShare;
import gov.sls.ods.repository.OdsUserPackageRateRepository;
import gov.sls.ods.repository.OdsUserPackageVersionClickRepository;
import gov.sls.ods.repository.OdsUserPackageVersionDownloadRepository;
import gov.sls.ods.repository.OdsUserPackageVersionShareRepository;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods307eService {

    @Autowired
    private OdsUserPackageRateRepository odsUserPackageRateRepository;
    
    @Autowired
    private OdsUserPackageVersionShareRepository odsUserPackageVersionShareRepository;
    
    @Autowired
    private OdsUserPackageVersionDownloadRepository odsUserPackageVersionDownloadRepository;

    @Autowired
    private OdsUserPackageVersionClickRepository odsUserPackageVersionClickRepository;

    public int createRate(OdsUserPackageRate odsUserPackageRate, User user, String userRole) {
        OdsUserPackageRate odsUserPackageRateRet = null;
        //匿名使用者
        log.info("307service packageId:" + odsUserPackageRate.getPackageId());
        if ("00000".equals(odsUserPackageRate.getUserId())) {
            log.info("Process Anonymous Rating!!");
            log.info("Anonymous Rating packageId:" + odsUserPackageRate.getPackageId());
            odsUserPackageRate.setCreated(new Date());
            odsUserPackageRate.setCreateUserId("00000");
            odsUserPackageRate.setUpdated(new Date());
            odsUserPackageRate.setUpdateUserId("00000");
            odsUserPackageRate.setUserRole(userRole);
            odsUserPackageRateRet = odsUserPackageRateRepository.create(odsUserPackageRate);
        } else {            
            List<OdsUserPackageRate> uprList = odsUserPackageRateRepository
                    .findByPkgIdAndUserId(odsUserPackageRate.getPackageId(), odsUserPackageRate.getUserId());            
            if (!uprList.isEmpty()) {
                log.info("LoginUser Rating packageId(Rated):" + odsUserPackageRate.getPackageId());
                OdsUserPackageRate odsUserPackageRateQuery = uprList.get(0);
                odsUserPackageRateQuery.setPackageId(odsUserPackageRate.getPackageId());
                odsUserPackageRateQuery.setUserId(user.getId());
                odsUserPackageRateQuery.setRate(odsUserPackageRate.getRate());
                odsUserPackageRateQuery.setIpAddress(odsUserPackageRate.getIpAddress());
                odsUserPackageRateQuery.setUpdated(new Date());
                odsUserPackageRateQuery.setUpdateUserId(user.getId());
                odsUserPackageRateQuery.setUserRole(userRole);
                odsUserPackageRateRet = odsUserPackageRateRepository.save(odsUserPackageRateQuery);
            } else {
                log.info("LoginUser Rating packageId(New):" + odsUserPackageRate.getPackageId());
                odsUserPackageRate.setCreated(new Date());
                odsUserPackageRate.setCreateUserId(user.getId());
                odsUserPackageRate.setUpdated(new Date());
                odsUserPackageRate.setUpdateUserId(user.getId());
                odsUserPackageRate.setUserRole(userRole);
                odsUserPackageRateRet = odsUserPackageRateRepository.create(odsUserPackageRate);
            }
        }
        if (odsUserPackageRateRet == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public int createClick(OdsUserPackageVersionClick odsUserPackageVersionClick, User user) {

        OdsUserPackageVersionClick odsUserPackageVersionClickRet = null;
        odsUserPackageVersionClickRet = odsUserPackageVersionClickRepository
                .create(odsUserPackageVersionClick);
        if (odsUserPackageVersionClickRet == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public OdsUserPackageRate findRate(OdsUserPackageRate odsUserPackageRate, User user) {
        return odsUserPackageRateRepository.findOne(odsUserPackageRate.getId());
    }
    
    public OdsUserPackageRate findRateByPkgIdAndUserId(String packageId, String userId) {
        List<OdsUserPackageRate> uprList = odsUserPackageRateRepository.findByPkgIdAndUserId(packageId, userId);
        
        if (!uprList.isEmpty()) {
            return uprList.get(0);
        } else {
            return null;
        }
    }

    public int createShare(
            OdsUserPackageVersionShare odsUserPackageVersionShare, User user) {
        OdsUserPackageVersionShare odsUserPackageVersionShareRet = null;
        odsUserPackageVersionShareRet = odsUserPackageVersionShareRepository
                .create(odsUserPackageVersionShare);
        if (odsUserPackageVersionShareRet == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public int createDownload(
            OdsUserPackageVersionDownload odsUserPackageVersionDownload,
            User user) {
        OdsUserPackageVersionDownload odsUserPackageVersionDownloadRet = null;
        odsUserPackageVersionDownloadRet = odsUserPackageVersionDownloadRepository
                .create(odsUserPackageVersionDownload);
        if (odsUserPackageVersionDownloadRet == null) {
            return 0;
        } else {
            return 1;
        }
        
    }

}
