package gov.sls.ods.service;

import gov.sls.ods.dto.Ods352eDataDto;
import gov.sls.ods.repository.OdsIndividePackageSubRepository;
import gov.sls.ods.repository.OdsResourceCriteriaRepository;
import gov.sls.ods.repository.OdsUserFollowPackageRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cht.commons.security.Authority;

@Slf4j
@Service
public class Ods352eService {

    @Autowired
    private OdsResourceCriteriaRepository odsResourceCriteriaRepository;

    @Autowired
    private OdsUserFollowPackageRepository odsUserFollowPackageRepository;
    
    @Autowired
    private OdsIndividePackageSubRepository odsIndividePackageSubRepository;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    public List<Ods352eDataDto> getOdsUserFollowPackageByUser(String userId, boolean isPreview) {
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(userId);
        log.info("UAA-UserId_352E:" + userId);
        log.info("UAA-findResultList_352E:" + findResultList.size());
        if (!CollectionUtils.isEmpty(findResultList)) {
            List<String> idList = new ArrayList<String>();
            for (Authority authority: findResultList) {
                idList.add(authority.getId());
                log.info("idList-id_352E:" + authority.getId());
            }
            return odsUserFollowPackageRepository.getOdsUserFollowPackageByUser(
                    userId, isPreview, idList);
        } else {
            return new ArrayList<Ods352eDataDto>();
        }
    } 
    public void deleteUserFollowPackageByUserIdPackageIdList(String userId, String userUnifyId, String packageIdList) {
        
        if (",".equals(packageIdList.substring(packageIdList.length() - 1, packageIdList.length()) ))
        {
            packageIdList = packageIdList.substring(0, packageIdList.length() - 1);
        }
        
        List<String> packageIdListAry = new ArrayList<String>(Arrays.asList(packageIdList.split(",")));
        
        
        odsUserFollowPackageRepository.deleteUserFollowPackageByUserIdPackageIdList(userId, packageIdListAry);
        
        //刪除個人化網頁
        odsIndividePackageSubRepository.deleteByUserUnifyIdPackageIdList(userUnifyId, packageIdListAry);
    }
}
