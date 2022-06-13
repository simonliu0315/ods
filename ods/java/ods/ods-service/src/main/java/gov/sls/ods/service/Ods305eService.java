package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResourceCriteria;
import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.repository.OdsResourceCriteriaRepository;
import gov.sls.ods.repository.OdsUserFollowPackageRepository;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods305eService {

    @Autowired
    private OdsResourceCriteriaRepository odsResourceCriteriaRepository;

    @Autowired
    private OdsUserFollowPackageRepository odsUserFollowPackageRepository;

    public List<OdsResourceCriteria> getOdsResourceCriteria(String packageId, int packageVer) {
        return odsResourceCriteriaRepository.getResourceCriteriaInPackageResource(packageId,
                packageVer);
    }

    public List<OdsUserFollowPackage> getOdsUserFollowPackage(String packageId, int ver,
            String userId) {
        return odsUserFollowPackageRepository.getResourceCriteriaInPackageResource(packageId, ver,
                userId);
    }
    
    public void deleteUserFollowPackageByUserIdPackageId(String userId, String packageId) {
        odsUserFollowPackageRepository.deleteByUserIdPackageId(userId, packageId);
    }
    public void saveUserFollowPackage(OdsUserFollowPackage odsUserFollowPackage) {        
        odsUserFollowPackageRepository.save(odsUserFollowPackage);        
    }
    
    public List<OdsUserFollowPackage> getOdsUserFollowPackage(String packageId,
            String userId) {
        return odsUserFollowPackageRepository.getByUserIdPackageId(packageId,
                userId);
    }
}
