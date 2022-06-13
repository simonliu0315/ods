package gov.sls.ods.service;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.dto.Ods352eDataDto;
import gov.sls.ods.repository.OdsPackageVersionRepository;
import gov.sls.ods.repository.OdsUserPackageVersionClickRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cht.commons.security.Authority;

@Slf4j
@Service
public class Ods351eService {

    @Autowired
    private OdsUserPackageVersionClickRepository odsUserPackageVersionClickRepository;
    
    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    public List<Ods351eDataDto> getPopularUserPackageClick(){
        String userId = UserHolder.getUser().getId();
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(userId);
        log.info("UAA-UserId_351E:" + userId);
        log.info("UAA-findResultList_351E:" + findResultList.size());
        if (!CollectionUtils.isEmpty(findResultList)) {
            List<String> idList = new ArrayList<String>();
            for (Authority authority: findResultList) {
                idList.add(authority.getId());
                log.info("idList-id_351E:" + authority.getId());
            }
            return odsUserPackageVersionClickRepository.findPopularUserPackageClick(idList);
        } else {
            return new ArrayList<Ods351eDataDto>();
        }
    }
    
    public List<Ods351eDataDto> getLatestOdsPackageVersion(){
        String userId = UserHolder.getUser().getId();
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(userId);
        log.info("UAA-UserId_351E:" + userId);
        log.info("UAA-findResultList_351E:" + findResultList.size());
        if (!CollectionUtils.isEmpty(findResultList)) {
            List<String> idList = new ArrayList<String>();
            for (Authority authority: findResultList) {
                idList.add(authority.getId());
                log.info("idList-id_351E:" + authority.getId());
            }
            return odsPackageVersionRepository.findLatestOdsPackageVersion(idList);
        } else {
            return new ArrayList<Ods351eDataDto>();
        }
    }
}
