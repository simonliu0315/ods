package gov.sls.ods.service;

import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsResourceIndivid;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.repository.OdsIndividePackageSubRepository;
import gov.sls.ods.repository.OdsResourceIndividRepository;
import gov.sls.ods.repository.OdsResourceRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class ImportService {
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;

    @Autowired
    private OdsIndividePackageSubRepository odsIndividePackageSubRepository;

    @Autowired
    private OdsResourceIndividRepository odsResourceIndividRepository;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    
    /**
     * 透過view ID取得Resource ID
     * @param viewid
     * @return resourceID
     */
    public String getResourceID(String viewid) {
        String resourceId="";
        BooleanExpression criteria= QOdsResource.odsResource.viewId.eq(viewid);
        OdsResource odsResource=odsResourceRepository.findOne(criteria);
        if (odsResource== null) {
            resourceId="";
        }else {
           resourceId= odsResource.getId();
        }
        return resourceId;
    }
    
    /**
     * 將要匯入的資料清單(datas)寫入resourceid對應的 table中
     * @param resourceId 要被寫入的table
     * @param field_count 與table對應的總欄位數
     * @param datas 資料清單
     * @return
     */
    public int[] createDataByResourceId(String resourceId, int field_count, List<Object[]> datas) {
        String tablename = getTableName(resourceId);
        String sql = "INSERT INTO " + tablename + " VALUES(?)";
        StringBuffer fields = new StringBuffer();
        for (int i = 0; i < field_count; i++) {
            fields.append(":Field_" + i + ",");
        }
        sql = sqlEscapeService.escapeMsSql(sql.replaceFirst("\\?",
                fields.substring(0, fields.length() - 1)));
        Map<String, ?>[] params = new HashMap[datas.size()];
        for (int j = 0; j < datas.size(); j++) {
            Map<String, Object> param = new HashMap<String, Object>();
            for (int i = 0; i < datas.get(j).length; i++) {
                param.put("Field_" + i, datas.get(j)[i]);
            }
            params[j] = param;
        }
        return odsJdbcTemplate.batchUpdate(sql, params);
    }
    
    /**
     * 刪除 resourceId table 所有資料
     * @param resourceId
     * @return 刪除筆數
     */
    public int deleteAllByResourceId(String resourceId) {
        String tablename=getTableName(resourceId);
        String sql=sqlEscapeService.escapeMsSql("DELETE FROM "+tablename);
        return odsJdbcTemplate.update(sql, new HashMap<String,Object>());
    }
    
    /**
     * 清空resourceId table 條件相合的資料
     * @param resourceId
     * @param wherecond 刪除條件
     * @return 刪除筆數
     */
    public int deleteByResourceId(String resourceId, String wherecond) {
        String tablename = getTableName(resourceId);
        String sql = sqlEscapeService.escapeMsSql(
                "DELETE FROM " + tablename + " Where " + wherecond).replaceAll("-", "'");
        return odsJdbcTemplate.update(sql, new HashMap<String, Object>());
    }
    
    /**
     * 組出table完整名稱
     * @param resourceId
     * @return
     */
    private String getTableName(String resourceId) {
        return "ODS_"+resourceId.replaceAll("-","_");
    }

    /**根據viewId 建立訂閱資料更新紀錄
     * @param viewId viewId
     */
    public void createOdsResourceIndivid(String viewId) {
        String resourceId = getResourceID(viewId);
        log.info("根據resourceId 建立訂閱資料更新紀錄");
        log.debug("resourceId:" + resourceId);
        log.debug("viewId:" + viewId);
        List<String> userUnifyIdList = odsIndividePackageSubRepository.findUserUnifyIdByResourceId(resourceId);
        if(CollectionUtils.isNotEmpty(userUnifyIdList)){
            List<OdsResourceIndivid> odsResourceIndividList = new ArrayList<OdsResourceIndivid>();
            Date now = Calendar.getInstance().getTime();
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(now);
            log.info("odsResourceIndividList size:" + odsResourceIndividList.size());
            for(String userUnifyId : userUnifyIdList){
                log.debug("userUnifyId:" +userUnifyId);
                OdsResourceIndivid odsResourceIndivid = new OdsResourceIndivid();
                odsResourceIndivid.setDanImportDate(now);
                odsResourceIndivid.setDanImportMk("Y");
                odsResourceIndivid.setResourceDate(dateStr);
                odsResourceIndivid.setResourceId(resourceId);
                odsResourceIndivid.setUserUnifyId(userUnifyId);
                odsResourceIndivid.setReportNotifyMk("N");
                odsResourceIndividList.add(odsResourceIndivid);
            }
            log.debug("save odsResourceIndividRepository");
            odsResourceIndividRepository.save(odsResourceIndividList);
        }else {
            log.info("odsResourceIndividList is empty");
        }
    }
}
