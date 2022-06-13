package gov.sls.ods.service;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCodes;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.entity.ods.OdsIdentity;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.ods.dto.Ods301eDto;
import gov.sls.ods.repository.OdsCodesRepository;
import gov.sls.ods.repository.OdsGroupRepository;
import gov.sls.ods.repository.OdsIdentityRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;
import gov.sls.ods.repository.OdsResourceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cht.commons.security.Authority;

@Slf4j
@Service
public class Ods301eService {

    @Autowired
    private OdsGroupRepository odsGroupRespository;

    @Autowired
    private OdsPackageTagRepository odsPackageTagRepository;

    @Autowired
    private OdsResourceRepository odsResourceRepository;

    @Autowired
    private OdsIdentityRepository odsIdentityRepository;
    @Autowired
    private OdsCodesRepository odsCodesRepository;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    /**
     * 依據條件查詢主題群組列表
     * 
     * @param groupName
     *            主題群組名稱(將會用like查詢)
     * @param tagList
     *            標籤清單
     * @param fileExtList
     *            檔案格式清單
     * @param identityList
     *            分眾名稱清單
     * @param orderByType
     *            0排序依據更新時間，1排序依據熱門排行
     * @param userType 
     * @return List<OdsGroup>主題群組清單
     */
    public List<OdsGroup> findGroups(String groupName, List<String> tagList,
            List<String> fileExtList, List<String> identityList, int orderByType, String userType) {
        return odsGroupRespository.findGroup(groupName, tagList, fileExtList, identityList,
                orderByType, userType);
    }

    /**
     * 取得所有tag name
     * 
     * @return List<String> tag清單
     */
    public List<String> findAllPackageTag() {
        List<OdsPackageTag> findAll = odsPackageTagRepository.findAll();
        Set<String> packageTagList = new HashSet<String>();
        for (OdsPackageTag odsPackageTag : findAll) {
            packageTagList.add(odsPackageTag.getTagName());
        }
        return new ArrayList<String>(packageTagList);
    }

    /**
     * 取得所有format name
     * 
     * @return List<String> 檔案格式清單
     */
    public List<String> findAllResourceFileExt() {
        // 查找OdsCodes, 格式為format排除value為common的共用代碼
        List<OdsCodes> findAll = odsCodesRepository.findByCodeTypeAndValueNot("FORMAT", "common");
        Set<String> fileExtList = new HashSet<String>();
        for (OdsCodes odsCodes : findAll) {
            //fileExtList.add(odsCodes.getValue());
            String chnName = "";
            if ("image".equals(odsCodes.getValue())) {
                chnName = "圖片";
            }
            if ("pdf".equals(odsCodes.getValue())) {
                chnName = "PDF檔";
            }
            if ("dataset".equals(odsCodes.getValue())) {
                chnName = "資料集";
            }
            fileExtList.add(chnName);
        }
        return new ArrayList<String>(fileExtList);
    }

    /**
     * 取得所有分眾 name
     * 
     * @return List<String> 分眾名稱清單
     */
    public List<String> findAllIdentity() {
        List<OdsIdentity> findAll = odsIdentityRepository.findAll();
        Set<String> identityList = new HashSet<String>();
        for (OdsIdentity odsIdentity : findAll) {
            identityList.add(odsIdentity.getName());
        }
        return new ArrayList<String>(identityList);

    }

    /**
     * 取得主題列表
     * 
     * @param gorupId
     *            群組ID
     * @return List<OdsPackage>
     */
    public List<Ods301eDto> findPackages(String gorupId) {
        String userId = UserHolder.getUser().getId();
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(userId);
        if (!CollectionUtils.isEmpty(findResultList)) {
            List<String> idList = new ArrayList<String>();
            for (Authority authority: findResultList) {
                idList.add(authority.getId());
            }
            return odsGroupRespository.findPackagesByGroupId(gorupId, idList);
        } else {
            return new ArrayList<Ods301eDto>();
        }
    }
    
    /**
     * 取得主題群組
     * 
     * @param gorupId
     *            群組ID
     * @return
     */
    public OdsGroup findGroup(String gorupId) {
        return odsGroupRespository.findById(gorupId);
    }
}
