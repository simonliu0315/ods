package gov.sls.ods.service;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCodes;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.ods.dto.Ods302eDto;
import gov.sls.ods.repository.OdsCodesRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;

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
public class Ods302eService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;

    @Autowired
    private OdsPackageTagRepository odsPackageTagRepository;

    @Autowired
    private OdsCodesRepository odsCodesRepository;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    /**
     * 依據條件查詢主題列表
     * 
     * @param pakcageName
     *            主題群組名稱(將會用like查詢)
     * @param tagList
     *            標籤清單
     * @param fileExtList
     *            檔案格式清單
     * @param orderByType
     *            0排序依據更新時間，1排序依據熱門排行
     * @return List<OdsGroup>主題清單
     */
    public List<Ods302eDto> findPackage(String packageName, List<String> tagList,
            List<String> fileExtList, int orderByType) {
        String userId = UserHolder.getUser().getId();
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(userId);
        log.info("UAA-UserId:" + userId);
        log.info("UAA-findResultList:" + findResultList.size());
        if (!CollectionUtils.isEmpty(findResultList)) {
            List<String> idList = new ArrayList<String>();
            for (Authority authority: findResultList) {
                idList.add(authority.getId());
                log.info("idList-id:" + authority.getId());
            }
            return odsPackageRepository.findPackage(packageName, tagList, fileExtList, orderByType, idList);
        } else {
            return new ArrayList<Ods302eDto>();
        }        
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
}
