package gov.sls.ods.service;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsIndividePackageSub;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.repository.OdsIndividePackageSubRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods311eService {

    @Autowired
    private OdsIndividePackageSubRepository odsIndividePackageSubRepository;
    
    @Autowired
    private OdsPackageVersionRepository packageVersionRepos;
    
    @Autowired
    private OdsIndividePackageSubRepository individePackageSubRepos;

    public OdsIndividePackageSub createIndividePackageSub(String packageId, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        log.info("UserUnifyId:" + user.getBarCode());
        OdsIndividePackageSub odsIndividePackageSub = new OdsIndividePackageSub();
        odsIndividePackageSub.setPackageId(packageId);
        odsIndividePackageSub.setUserId(user.getId());
        odsIndividePackageSub.setUserUnifyId(user.getBarCode());
        odsIndividePackageSub.setIpAddress(ipAddress);        
        odsIndividePackageSub.setCreated(new Date());
        odsIndividePackageSub.setCreateUserId(user.getId());
        odsIndividePackageSub.setUpdated(new Date());
        odsIndividePackageSub.setUpdateUserId(user.getId());
        odsIndividePackageSub.setUserRole(String.valueOf(user.getRoles().get(0)));
        return odsIndividePackageSubRepository.create(odsIndividePackageSub);
    }
    
    public OdsPackageVersion getPackageVersion(String packageId, String packageVer) {
        OdsPackageVersionPK packageVersionPk = new OdsPackageVersionPK();
        packageVersionPk.setPackageId(packageId);
        packageVersionPk.setVer(Integer.parseInt(packageVer));
        
        return packageVersionRepos.findOne(packageVersionPk);
    }
    
    
    public List<Ods303eIndividualDto> getIndPackageSub(String packageCode, String userId) {
        
        return individePackageSubRepos.getIndPackageSub(packageCode, userId);
    }

}
