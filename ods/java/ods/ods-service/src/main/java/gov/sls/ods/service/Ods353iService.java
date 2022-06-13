package gov.sls.ods.service;

import gov.sls.commons.solr.document.SlsDocument;
import gov.sls.commons.solr.document.SlsMetadata;
import gov.sls.commons.solr.document.SlsResource;
import gov.sls.commons.solr.repository.SlsDocumentRepositoryCustom;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.entity.ods.OdsSolrControltable;
import gov.sls.ods.dto.PackageMetadataDto;
import gov.sls.ods.repository.OdsPackageMetadataRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;
import gov.sls.ods.repository.OdsSolrControltableRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods353iService {

    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;

    @Autowired
    private OdsResourceVersionRepository odsResourceVersionRepository;

    @Autowired
    private OdsPackageTagRepository odsPackageTagRepository;

    @Autowired
    private OdsSolrControltableRepository odsSolrControltableRepository;

    @Autowired
    private OdsPackageMetadataRepository odsPackageMetadataRepository;

    @Autowired
    private SlsDocumentRepositoryCustom slsDocumentRepository;

    /**
     * 新增一筆ODS_SOLR_CONTROLTABLE資料
     * 
     * @return OdsSolrControltable
     */
    public OdsSolrControltable createOdsSolrControltable() {
        // INSERT INTO ODS_SOLR_CONTROLTABLE(EXECUTE_START_DATE)VALUES(@nowDate)
        // 其中nowDate為系統當下時間，新增完成後取得該筆ODS_SOLR_CONTROLTABLE之ID，並寫入@solrId變數。
        OdsSolrControltable solrControltable = new OdsSolrControltable();
        solrControltable.setExecuteStartDate(new Date());
        odsSolrControltableRepository.create(solrControltable);
        log.debug("新增一筆ODS_SOLR_CONTROLTABLE資料");
        return solrControltable;
    }

    /**
     * 取得上一次處理成功時間, 用來作為計算本次處理的起始時間
     * 
     * @return lastExecuteStartTime 上一次處理成功時間
     */
    public Date getLastExecuteStartTime() {
        // select max(EXECUTE_START_DATE) as preExecuteDate
        // from ODS_SOLR_CONTROLTABLE
        // where execute_result = '1'
        return odsSolrControltableRepository.getPreExecuteDate();
    }

    /**
     * 依據preExecuteDate，取得需新增／更新索引之PACKAGE_VERSION LIST
     * 
     * @param preExecuteDate
     *            preExecuteDate
     * @return List<OdsPackageVersion>
     */
    public List<OdsPackageVersion> getUpdatePackageVersion(Date preExecuteDate) {
        return odsPackageVersionRepository.getReadyIndexPackageByPreExecuteDate(preExecuteDate);
    }

    /**
     * 依據PACKAGE_VERSION LIST 之packageId取得 ods_package_tags.TagName Map
     * 
     * @param packageIdSet
     *            Set<String>
     * @return Map<String, String> Key是packageId
     */
    public Map<String, List<String>> getTags(Set<String> packageIdSet) {
        List<OdsPackageTag> odsPackageTagsList = odsPackageTagRepository.findByIdIn(packageIdSet);
        Map<String, List<String>> tagNameMap = new HashMap<String, List<String>>();
        for (OdsPackageTag odsPackageTag : odsPackageTagsList) {
            String key = odsPackageTag.getPackageId();
            List<String> value = tagNameMap.get(key);
            if (null != value && !value.isEmpty()) {
                value = new ArrayList<String>();
            }
            value.add(odsPackageTag.getTagName());
            tagNameMap.put(key, value);
        }
        return tagNameMap;
    }

    /**
     * 建立/更新 索引
     * 
     * <pre>
     * 3. 取得詮釋資料 
     * 4. 取得素材及案例 
     * 5. 使用1~4資料建立索引
     * </pre>
     * 
     * @param odsPackageVersion
     *            odsPackageVersion
     * @param tagNameMap
     *            tagNameMap
     */
    public void saveIndex(OdsPackageVersion odsPackageVersion, Map<String, List<String>> tagNameMap) {

        String packageId = odsPackageVersion.getId().getPackageId();
        int ver = odsPackageVersion.getId().getVer();
        String key = packageId + "-" + ver;
        log.debug("buildIndex key:" + key);


        log.debug("取得ods_package_tags.TagName");
        // 2. 取得ods_package_tags.TagName
        List<String> packageTagList = tagNameMap.get(packageId);

        // 3. 取得詮釋資料
        log.debug("取得詮釋資料");
        List<PackageMetadataDto> findMetadataByPackageIdAndVer = odsPackageMetadataRepository
                .findMetadataByPackageIdAndVer(packageId, ver);
        List<SlsMetadata> metadataList = new ArrayList<SlsMetadata>();
        for (PackageMetadataDto packageMetadataDto : findMetadataByPackageIdAndVer) {
            metadataList.add(new SlsMetadata(packageMetadataDto.getDataKey(), packageMetadataDto
                    .getDataValue()));
        }

        // 4. 取得素材及案例
        log.debug("取得素材及案例");
        List<OdsResourceVersion> resourceByPackageIdAndVer = odsResourceVersionRepository
                .findResourceByPackageIdAndVer(packageId, ver);
        List<SlsResource> resourceList = new ArrayList<SlsResource>();
        for (OdsResourceVersion resource : resourceByPackageIdAndVer) {
            resourceList.add(new SlsResource(resource.getName(), resource.getDescription()));
        }
        
        //新增/更新索引
        SlsDocument slsDoc = new SlsDocument();
        slsDoc.setSlsId(key);
        slsDoc.setSource("ODS353I");
        slsDoc.setPackageName(odsPackageVersion.getName());
        slsDoc.setPackageDescriprion(odsPackageVersion.getDescription());
        slsDoc.setUrl("/ods-main/ODS303E/" + packageId + "/" + ver + "/");
        slsDoc.setPostDate(new Date());
        slsDoc.setPackageTagList(packageTagList);
        slsDoc.setMetadataList(metadataList);
        slsDoc.setResourceList(resourceList);
        log.info("save slsDocument:" + slsDoc.getSlsId() + "," + slsDoc.getSource());
        slsDocumentRepository.save(slsDoc);
        log.debug("新增/更新索引");
    }

    /**
     * 取得需刪除之PACKAGE_VERSION
     * 
     * @param preExecuteDate
     *            preExecuteDate
     * @return List<OdsPackageVersion>
     */
    public List<OdsPackageVersion> getDelPackageVersion(Date preExecuteDate) {
        // select
        // PACKAGE_ID, VER
        // from
        // ods_package_version
        // where
        // updated > @preExecuteDate
        // and ( del_mk = '1' or is_published <> '1' )
        return odsPackageVersionRepository.getDelIndexPackageByPreExecuteDate(preExecuteDate);
    }

    /**
     * 刪除索引
     * 
     * @param odsPackageVersion
     *            odsPackageVersion
     */
    public void deleteIndex(OdsPackageVersion odsPackageVersion) {

        String packageId = odsPackageVersion.getId().getPackageId();
        int ver = odsPackageVersion.getId().getVer();
        String key = packageId + "-" + ver;
        String source = "301";
        
        SlsDocument slsDoc = slsDocumentRepository.findBySlsIdAndSource(key, source);
        if (null != slsDoc) {
            slsDocumentRepository.delete(slsDoc);
            log.debug("delIndex id:" + key);
        } else {
            log.debug("delIndex id is not exist:" + key);
        }
    }

    /**
     * @param executeResult
     *            null為失敗、1為成功、2為無更新資料
     */
    public void saveOdsSolrControltable(String executeResult, OdsSolrControltable odsSolrControltable) {
        // 6.6.1 @executeEndDate：由系統時間寫入
        // 6.6.2 @executeResult：寫入'1'

        // UPDATE ODS_SOLR_CONTROLTABLE
        // SET
        // EXECUTE_END_DATE = now,
        // EXECUTE_RESULT = 1
        // WHERE
        // ID = @solrId
        odsSolrControltable.setExecuteEndDate(new Date());
        odsSolrControltable.setExecuteResult(executeResult);
        log.debug("SolrControltable save ExecuteResult:" + odsSolrControltable.getExecuteResult());
        odsSolrControltableRepository.save(odsSolrControltable);
    }
}
