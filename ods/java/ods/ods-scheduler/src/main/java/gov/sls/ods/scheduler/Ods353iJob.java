/**
 * 
 */
package gov.sls.ods.scheduler;

import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsSolrControltable;
import gov.sls.ods.service.Ods353iService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 
 */
@Slf4j
@Service
public class Ods353iJob {

    @Autowired
    private Ods353iService service;

    @Scheduled(cron = "0 0 0 * * ?")
    // 每天零點零分
    // @Scheduled(fixedDelay = 60000)
    public void run() {
        log.debug("Ods353iJob Start Executing!");

        // null為失敗、1為成功、2為無更新資料
        String executeResult = "1";

        log.debug("Ods353iJob 新增一筆ODS_SOLR_CONTROLTABLE資料");
        OdsSolrControltable odsSolrControltable = service
                .createOdsSolrControltable();
        log.debug("Ods353iJob ODS_SOLR_CONTROLTABLE ID:"
                + odsSolrControltable.getId());

        log.debug("Ods353iJob 取得上一次處理成功時間, 用來作為計算本次處理的起始時間");
        Date preExecuteDate = service.getLastExecuteStartTime();
        log.debug("Ods353iJob preExecuteDate:" + preExecuteDate);
        log.debug("Ods353iJob 1. 依據preExecuteDate，取得需新增／更新索引之PACKAGE_VERSION LIST");
        // 1. 依據preExecuteDate，取得需新增／更新索引之PACKAGE_VERSION LIST
        List<OdsPackageVersion> updatePackageVersionList = service
                .getUpdatePackageVersion(preExecuteDate);

        if (null != updatePackageVersionList
                && !updatePackageVersionList.isEmpty()) {
            Set<String> packageIdSet = new HashSet<String>();
            for (OdsPackageVersion odsPackageVersion : updatePackageVersionList) {
                packageIdSet.add(odsPackageVersion.getId().getPackageId());
            }

            log.debug("Ods353iJob 2. 依據PACKAGE_VERSION LIST 之packageId取得ods_package_tags.TagName");
            // 2. 依據PACKAGE_VERSION LIST 之packageId取得ods_package_tags.TagName
            Map<String, List<String>> tagNameMap = service
                    .getTags(packageIdSet);

            // loop PACKAGE_VERSION LIST start
            for (OdsPackageVersion odsPackageVersion : updatePackageVersionList) {
                // 3. 取得詮釋資料
                // 4. 取得素材及案例
                // 5. 使用1~4資料建立索引
                log.debug("Ods353iJob 索引建立 packageId:"
                        + odsPackageVersion.getId().getPackageId() + " ver:"
                        + odsPackageVersion.getId().getVer());
                try {
                    service.saveIndex(odsPackageVersion, tagNameMap);
                } catch (Exception e) {
                    executeResult = null;
                    log.error("Ods353iJob 索引建立發生錯誤!packageId:"
                            + odsPackageVersion.getId().getPackageId()
                            + " ver:" + odsPackageVersion.getId().getVer());
                    log.error("", e);
                }
            }
            // loop end

            log.debug("Ods353iJob 6. 取得需刪除索引之PACKAGE_VERSION");
            // 6. 取得需刪除索引之PACKAGE_VERSION\
            List<OdsPackageVersion> delPackageVersion = service
                    .getDelPackageVersion(preExecuteDate);
            // 7. 刪除6的索引
            for (OdsPackageVersion odsPackageVersion : delPackageVersion) {
                try {
                    log.debug("Ods353iJob 刪除索引 packageId:"
                            + odsPackageVersion.getId().getPackageId()
                            + " ver:" + odsPackageVersion.getId().getVer());
                    service.deleteIndex(odsPackageVersion);
                } catch (Exception e) {
                    executeResult = null;
                    log.error("Ods353iJob 索引刪除發生錯誤!packageId:"
                            + odsPackageVersion.getId().getPackageId()
                            + " ver:" + odsPackageVersion.getId().getVer());
                }
            }
        } else {
            log.debug("Ods353iJob 目前無更新主題。");
            executeResult = "2";
        }

        log.debug("Ods353iJob 8. 更新執行結束時間並標示執行成功");
        // 8. 更新執行結束時間並標示執行成功
        service.saveOdsSolrControltable(executeResult, odsSolrControltable);
        log.debug("Ods353iJob Finished");
    }

}
