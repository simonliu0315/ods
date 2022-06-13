package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.RSSItem;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods356iService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;

    public List<RSSItem> getRss() {
        List<RSSItem> rssItemList = odsPackageRepository.findRss();
        
        for (RSSItem rss : rssItemList) {
            //找出最大版本            
            String link = "";
            List<OdsPackageVersion> odsPackageVersionList = 
                    odsPackageVersionRepository.getPublishPackageVerMax(rss.getPackageId());
            if (!odsPackageVersionList.isEmpty()) {
                link = "/ods-main/ODS303E/" + rss.getPackageId() + "/" + odsPackageVersionList.get(0).getId().getVer() + "/";
            }
            rss.setLink(link);
        }
        return rssItemList;
    }
    
    public String findChannelTitle(String titlePrefix) {
        //String title = sipTreeNodeRepository.findOneBySid(treeNodeSid).getTitle();
        String title = "隨選式服務功能系統";
        if (Strings.isNullOrEmpty(title)) {
            return titlePrefix;
        } else {
            return titlePrefix + "_" + title;    
        }
    }
    
    public List<OdsPackageVersion> getRssData() {
        return odsPackageVersionRepository.getRssData();
    }
}
