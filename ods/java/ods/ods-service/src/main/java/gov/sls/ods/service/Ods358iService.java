package gov.sls.ods.service;

import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.Ods302eDto;
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
public class Ods358iService {

    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;

    public List<Map<String, Object>> getPackageResultList(List<Ods302eDto> odsPackages, String urlBase) {

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods302eDto pkg : odsPackages) {
            Map<String, Object> pkgMap = new LinkedHashMap<String, Object>();
            pkgMap.put("id", pkg.getId());
            pkgMap.put("name", pkg.getName());
            String desc = "";
            if (!Strings.isNullOrEmpty(pkg.getDescription())) {
                desc = pkg.getDescription();
            }
            pkgMap.put("description", desc);
            String url = "";
            List<OdsPackageVersion> odsPackageVersionList = odsPackageVersionRepository
                    .getPublishPackageVerMax(pkg.getId());
            if (!odsPackageVersionList.isEmpty()) {
                url = urlBase + "ODS303E/" + pkg.getId() + "/"
                        + odsPackageVersionList.get(0).getId().getVer() + "/";
            }
            pkgMap.put("url", url);
            String imageUrl = "";
            if (!Strings.isNullOrEmpty(pkg.getImageUrl())) {
                imageUrl = urlBase + "ODS308E/public/package/" + pkg.getId() + "/image/"
                        + pkg.getImageUrl();
            }
            pkgMap.put("imageUrl", imageUrl);

            resultList.add(pkgMap);
        }

        return resultList;
    }

}
