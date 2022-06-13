package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods359iService {

    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;

    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;

    
    public List<Map<String, Object>> getPackageVersionsResultList(String packageId) {

        List<OdsPackageVersion> pkgVerList = odsPackageVersionRepository
                .getPublishPackage(packageId);

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (OdsPackageVersion pkgVer : pkgVerList) {
            Map<String, Object> pkgVerMap = new LinkedHashMap<String, Object>();
            pkgVerMap.put("ver", pkgVer.getVer());
            pkgVerMap.put("name", pkgVer.getName());
            String desc = "";
            if (!Strings.isNullOrEmpty(pkgVer.getDescription())) {
                desc = pkgVer.getDescription();
            }
            pkgVerMap.put("description", desc);

            resultList.add(pkgVerMap);
        }

        return resultList;
    }

    public List<Map<String, Object>> getPackageResourcesResultList(String packageId,
            String packageVer, String urlBase) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            Map<String, Object> pkgResMap = new LinkedHashMap<String, Object>();            
            pkgResMap.put("id", pkgRes.getResourceId());
            pkgResMap.put("ver", pkgRes.getResourceVer());
            pkgResMap.put("name", pkgRes.getResName());
            String desc = "";
            if (!Strings.isNullOrEmpty(pkgRes.getResDesc())) {
                desc = pkgRes.getResDesc();
            }
            pkgResMap.put("description", desc);

            String fileType = "";
            if ("image".equals(pkgRes.getFormat())) {
                fileType = "png";
            } else if ("pdf".equals(pkgRes.getFormat())) {
                fileType = "pdf";
            } else if ("dataset".equals(pkgRes.getFormat())) {
                fileType = "csv";
            }
            pkgResMap.put("type", fileType);

            String url = urlBase + "ODS308E/download/" + packageId + "/" + packageVer + "/"
                    + pkgRes.getResourceId() + "/" + pkgRes.getResourceVer() + "/?fileType="
                    + fileType;
            pkgResMap.put("url", url);

            resultList.add(pkgResMap);
        }

        return resultList;
    }
    
    public List<Map<String, Object>> getResourcesDownloadResultList(String packageId,
            String packageVer, String urlBase) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            Map<String, Object> pkgResMap = new LinkedHashMap<String, Object>();
            
            String fileType = "";
            if ("image".equals(pkgRes.getFormat())) {
                fileType = "png";
            } else if ("pdf".equals(pkgRes.getFormat())) {
                fileType = "pdf";
            } else if ("dataset".equals(pkgRes.getFormat())) {
                fileType = "csv";
            }

            String url = urlBase + "ODS308E/download/" + packageId + "/" + packageVer + "/"
                    + pkgRes.getResourceId() + "/" + pkgRes.getResourceVer() + "/?fileType="
                    + fileType;
            pkgResMap.put("url", url);

            resultList.add(pkgResMap);
        }

        return resultList;
    }

}
