package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.entity.ods.OdsUserPackageNotify;
import gov.sls.ods.dto.Ods772CriteriaFollowUserDto;
import gov.sls.ods.dto.Ods772CriteriaResourceDto;
import gov.sls.ods.repository.OdsCriteriaRepository;
import gov.sls.ods.repository.OdsNoticePackageVersionRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsUserFollowPackageRepository;
import gov.sls.ods.repository.OdsUserPackageNotifyRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class Ods772xService {
    @Autowired
    private OdsCriteriaRepository odsCriteriaRepository;

    @Autowired
    private OdsUserFollowPackageRepository odsUserFollowPackageRepository;

    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;

    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    private OdsNoticePackageVersionRepository odsNoticePackageVersionRepository;

    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    @Autowired
    private OdsUserPackageNotifyRepository odsUserPackageNotifyRepository;
    
    /**
     * 1.取得尚未通知的主題
     * 
     * @return List<OdsPackageResource>
     */
    public List<OdsPackageResource> findNotyetNotifierPackage() {
        return odsPackageResourceRepository.findPackageIdAndMaxPkgVer();
    }

    /**
     * 製作odsResourceMap 給予後續製作通知內容email使用key為ResourceId
     * 
     * @param resourceIdList
     *            resourceIdList
     * @return Map<String, OdsResource>
     */
    public Map<String, OdsResource> findOdsResourceMap(List<String> resourceIdList) {
        List<OdsResource> odsResourceList = odsResourceRepository.findByIdIn(resourceIdList);
        Map<String, OdsResource> odsResourceMap = new HashMap<String, OdsResource>();
        for (OdsResource odsResource : odsResourceList) {
            odsResourceMap.put(odsResource.getId(), odsResource);
        }
        return odsResourceMap;
    }

    /**
     * 製作odsPackageMap 給予後續製作通知內容email使用key為packageId
     * 
     * @param packageIdList
     *            packageIdList
     * @return Map<String, OdsPackage>
     */
    public Map<String, OdsPackage> findOdsPackageMap(List<String> packageIdList) {
        List<OdsPackage> odsPackages = odsPackageRepository.findByIdIn(packageIdList);
        Map<String, OdsPackage> odsPackageMap = new HashMap<String, OdsPackage>();
        for (OdsPackage odsPackage : odsPackages) {
            odsPackageMap.put(odsPackage.getId(), odsPackage);
        }
        return odsPackageMap;
    }

    /**
     * 2.2.1找出有更新的主題之門檻List
     * 
     * @param resourceIdList
     *            resourceIdList
     * @return Map<String, List<Ods772CriteriaResourceDto>>
     *         key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
     */ 
    public Map<String, List<Ods772CriteriaResourceDto>> findOdsCriteriaMap(
            List<String> resourceIdList) {
        List<Ods772CriteriaResourceDto> odsCriteriaList = odsCriteriaRepository
                .findByResourceIdIn(resourceIdList);// 所有更新的案例的門檻資料List

        Map<String, List<Ods772CriteriaResourceDto>> odsCriteriaMap = new HashMap<String, List<Ods772CriteriaResourceDto>>();
        for (Ods772CriteriaResourceDto odsCriteria : odsCriteriaList) {
            // 判斷取得確認符合門檻的主題，list有值就代表符合門檻
            List<Ods772CriteriaFollowUserDto> checkCriteria2 = odsCriteriaRepository.checkCriteria(
                    odsCriteria.getResourceId(), odsCriteria.getMaxVer(), odsCriteria.getResourceCriteriaId(),
                    odsCriteria.getAggregateFunc(), odsCriteria.getDataField(),
                    odsCriteria.getOperator(), odsCriteria.getTarget());

            // 確認符合門檻的主題才加入List
            String resourceCriteriaKey = odsCriteria.getResourceId() + "$$"
                    + odsCriteria.getResourceCriteriaId();
            if (!CollectionUtils.isEmpty(checkCriteria2)) {
                // 製作OdsCriteriaMap 給予後續製作通知內容emial使用
                // key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
                if (odsCriteriaMap.containsKey(resourceCriteriaKey)) {
                    List<Ods772CriteriaResourceDto> odsCriteriaMapList = odsCriteriaMap
                            .get(resourceCriteriaKey);
                    odsCriteriaMapList.add(odsCriteria);
                } else {
                    List<Ods772CriteriaResourceDto> odsCriteriaMapList = new ArrayList<Ods772CriteriaResourceDto>();
                    odsCriteriaMapList.add(odsCriteria);
                    odsCriteriaMap.put(resourceCriteriaKey, odsCriteriaMapList);
                }
            }
        }
        return odsCriteriaMap;
    }

    /**
     * 取得需要通知的User list
     * 
     * @param odsCriteriaMap
     *            Map<String, List<Ods772CriteriaResourceDto>>
     *            key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
     * 
     * @return List<OdsUserFollowPackage> 訂閱未通知主題的使用者
     */
    public List<OdsUserFollowPackage> findNotifier(Map<String, List<Ods772CriteriaResourceDto>> odsCriteriaMap) {
        // 取得 更新即通知 的 使用者
        List<OdsUserFollowPackage> updateFollowUser = odsUserFollowPackageRepository
                .findUpdateFollowUser();

        // 找出符合門檻值的使用者
        List<OdsUserFollowPackage> criteriaFollowUser = new ArrayList<OdsUserFollowPackage>();// 符合門檻值需要通知的使用者List
        List<OdsUserFollowPackage> checkCriteriaFollowUser = odsUserFollowPackageRepository
                .findCriteriaFollowUser();// 有訂閱該主題的User
        // 判斷訂閱該主題的User 達到其 門檻值
        for (OdsUserFollowPackage ufp : checkCriteriaFollowUser) {
            // key為odsCriteria.getResourceId()+"$$"+odsCriteria.getCriteriaId()
            String resourceCriteriaKey = ufp.getResourceId() + "$$" + ufp.getResourceCriteriaId();
            log.info("user's OdsUserFollowPackage:{}", ufp);
            // 其門檻存在checkCriteria 則代表需要通知
            if (null != odsCriteriaMap.get(resourceCriteriaKey)) {
                criteriaFollowUser.add(ufp);
            }
        }
        updateFollowUser.addAll(criteriaFollowUser);
        return updateFollowUser;
    }

    /** 將這次於步驟1掃出來的尚未通知的主題版本更新於ODS_NOTICE_PACKAGE_VERSION，若是本次新的主題則新增。
     * @param packageResource Map<String, OdsPackageResource> 
     */
    public void noticePackageVersion(Map<String, OdsPackageResource> packageResource) {
        for (OdsPackageResource odsPackageResource : packageResource.values()) {
            odsNoticePackageVersionRepository.noticePackageVersion(
                    odsPackageResource.getPackageId(), odsPackageResource.getPackageVer());
        }
    }
    
    public void createUserPackageNotify(List<OdsUserFollowPackage> notifyList, Map<String, String> userideMail) {
        Date date = new Date();
        for (OdsUserFollowPackage notify : notifyList) {
            if (null != userideMail.get(notify.getUserId())) {
                OdsUserPackageNotify oupn = new OdsUserPackageNotify();
                oupn.setUserId(notify.getUserId());
                oupn.setPackageId(notify.getPackageId());
                oupn.setUserRole(notify.getUserRole());
                oupn.setEmail(userideMail.get(notify.getUserId()));
                oupn.setResourceId(notify.getResourceId());
                oupn.setResourceCriteriaId(notify.getResourceCriteriaId());
                oupn.setCreated(date);
                oupn.setCreateUserId("SYS");
                odsUserPackageNotifyRepository.create(oupn);
            }
            log.info(notify.getUserId() + " role:" + notify.getUserRole());
        }
    }
}
