package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsPackageResourcePK;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.entity.ods.OdsResource;
//import gov.sls.fms.dto.Fms421rDto;
import gov.sls.entity.ods.OdsResourceVersionPK;
import gov.sls.entity.ods.OdsWorkbook;
import gov.sls.entity.ods.OdsWorkbookPK;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods701e02Tab1FormBean;
import gov.sls.ods.dto.Ods701e05Tab1FormBean;
import gov.sls.ods.dto.Ods701eGrid1Dto;
import gov.sls.ods.dto.Ods701eGrid2Dto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.service.SqlEscapeService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;
import com.mysema.query.types.expr.BooleanExpression;

public class OdsResourceRepositoryImpl implements OdsResourceRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(OdsResourceRepositoryImpl.class);
    @Autowired
    private SqlExecutor sqlExecutor;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate slsJdbcTemplate;
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    protected String ODS_RESOURCE_PATH = "resource" + File.separator;
    
    protected String ODS_WORKBOOK_PATH = "workbook" + File.separator;
    
    @Override
    public List<Ods701eGrid1Dto> findAllRes(String name, String description, String workbookName, String viewName) {
        //String sql = "select * from ODS_RESOURCE";
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY RV.CREATED DESC) AS ROW_COUNT, "
              + "       R.ID                         AS ID, "
              + "       R.NAME                       AS NAME, "
              + "       R.DESCRIPTION                AS DESCRIPTION, "
              + "       DW.NAME AS WORKBOOK_NAME, "
              + "       DV.NAME AS VIEW_NAME, "
              + "       FORMAT, "
              + "       RV.CREATED                   AS MAX_RES_VER_CREATED, "
              + "       MAX_RES_VER, "
              + "       CASE DEL_MK "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS DEL_MK, "
              + "       STUFF("
              + "			(SELECT ',' + CONVERT(NVARCHAR(50), C.CATEGORY_ID) "
              + "			 FROM ODS_RESOURCE_CATEGORY C "
              + "			 WHERE RESOURCE_ID=RV.RESOURCE_ID FOR XML PATH('')), 1, 1, '') AS RES_CATEGORY "
              + "FROM   ODS_RESOURCE R "
              + "           LEFT OUTER JOIN ODS_DAN_WORKBOOK DW ON R.WORKBOOK_ID = DW.ID "
              + "           LEFT OUTER JOIN ODS_DAN_VIEW DV ON R.VIEW_ID = DV.ID "
              + "           AND DW.ID = DV.WORKBOOK_ID, "
              + "       ODS_RESOURCE_VERSION RV, "
              + "       (SELECT RESOURCE_ID, "
              + "               MAX(VER) AS MAX_RES_VER "
              + "        FROM   ODS_RESOURCE_VERSION "
              + "        GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
              + "WHERE  RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
              + "       AND RV.VER = MAX_RES_VER.MAX_RES_VER "
              + "       AND R.ID = RV.RESOURCE_ID " 
              + "       AND R.FORMAT <> 'common' " ;	//排除common resource
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        if (!Strings.isNullOrEmpty(name)) {
            sql = sql + "AND R.NAME like :name ";
            params.put("name", "%" + name + "%" );
        }
        if (!Strings.isNullOrEmpty(description)) {
            sql = sql + "AND R.DESCRIPTION like :description ";
            params.put("description", "%" + description + "%" );
        }
        if (!Strings.isNullOrEmpty(workbookName)) {
            sql = sql + "AND DW.NAME like :workbookName ";
            params.put("workbookName", "%" + workbookName + "%" );
        }
        if (!Strings.isNullOrEmpty(viewName)) {
            sql = sql + "AND DV.NAME like :viewName ";
            params.put("viewName", "%" + viewName + "%" );
        }

        

        
        List<OdsResource> res = sqlExecutor.queryForList(sql, params, OdsResource.class);
                
        for (OdsResource odsResource: res)
        {
            logger.debug("odsResource:" + ToStringBuilder.reflectionToString(odsResource));
        }
        
        return sqlExecutor.queryForList(sql, params, Ods701eGrid1Dto.class);
    }
    
    
    @Override
    public List<Ods701eGrid2Dto> findResDetailNDelByResId(String resId, String name, String description) {
        //String sql = "select * from ODS_RESOURCE";
        String sql =
                "SELECT RV.RESOURCE_ID, "
              + "       RV.NAME, "
              + "       RV.DESCRIPTION, "
              + "       RV.VER, "
              + "       RV.WORKBOOK_VER, "
              + "       RV.VERSION_DATETIME, "
              + "       CASE DEL_MK "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS DEL_MK, "
              + "       CASE TO_DATASTORE_SYNC "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS TO_DATASTORE_SYNC_GRID, "
              + "       RV.TO_DATASTORE_DATE AS TO_DATASTORE_DATE_GRID, "
              + "       CASE TO_DATASTORE_SUCCESS "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS TO_DATASTORE_SUCCESS_GRID "
              + "FROM   ODS_RESOURCE_VERSION RV "
              + "WHERE  RV.DEL_MK = 0 "
              + "       AND RV.VER > 0 "
              + "       AND RV.RESOURCE_ID = :resId ";
              if (!Strings.isNullOrEmpty(name)) {
                  sql = sql + "AND RV.NAME like :name ";
              }
              if (!Strings.isNullOrEmpty(description)) {
                  sql = sql + "AND RV.DESCRIPTION like :description ";
              }
              sql = sql + "ORDER  BY RV.VER DESC ";
        

        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", resId);
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", "%" +  name  + "%");
        }
        if (!Strings.isNullOrEmpty(description)) {
            params.put("description", "%" +  description  + "%");
        }
        
        List<Ods701eGrid2Dto> res = sqlExecutor.queryForList(sql, params, Ods701eGrid2Dto.class);
                
        for (Ods701eGrid2Dto odsResource: res)
        {
            logger.debug("odsResourceNDelDetail:" + ToStringBuilder.reflectionToString(odsResource));
        }
        
        return sqlExecutor.queryForList(sql, params, Ods701eGrid2Dto.class);
    }
    
    
    @Override
    public List<Ods701eGrid2Dto> findResDetailAllByResId(String resId) {
        //String sql = "select * from ODS_RESOURCE";
        String sql =
                "SELECT RV.RESOURCE_ID, "
              + "       RV.NAME, "
              + "       RV.DESCRIPTION, "
              + "       RV.VER, "
              + "       RV.WORKBOOK_VER, "
              + "       RV.VERSION_DATETIME, "
              + "       CASE DEL_MK "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS DEL_MK, "
              + "       CASE TO_DATASTORE_SYNC "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS TO_DATASTORE_SYNC_GRID, "
              + "       RV.TO_DATASTORE_DATE AS TO_DATASTORE_DATE_GRID, "
              + "       CASE TO_DATASTORE_SUCCESS "
              + "         WHEN '0' THEN 'N' "
              + "         WHEN '1' THEN 'Y' "
              + "       END AS TO_DATASTORE_SUCCESS_GRID "
              + "FROM   ODS_RESOURCE_VERSION RV "
              + "WHERE  RV.VER > 0 "
//              + "WHERE  RV.RESOURCE_ID = :resId "
              + "       AND RV.RESOURCE_ID = :resId "
              + "ORDER  BY RV.VER DESC ";
        

        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", resId);
        
        List<Ods701eGrid2Dto> res = sqlExecutor.queryForList(sql, params, Ods701eGrid2Dto.class);
                
        for (Ods701eGrid2Dto odsResource: res)
        {
            logger.debug("odsResourceAllDetail:" + ToStringBuilder.reflectionToString(odsResource));
        }
        
        return sqlExecutor.queryForList(sql, params, Ods701eGrid2Dto.class);
    }
    
    
    
    @Override
    public void createRes(OdsResource odsResource) {
        //String sql = "select * from ODS_RESOURCE";
        String sql =
                  "INSERT ODS_RESOURCE "
                + "       (ID, "
                + "        NAME, "
                + "        WORKBOOK_ID, "
                + "        VIEW_ID, "
                + "        FORMAT, "
                + "        DESCRIPTION, "
                + "        CREATED, "
                + "        UPDATED, "
                + "        CREATE_USER_ID, "
                + "        UPDATE_USER_ID) "
                + "VALUES (DEFAULT, "
                + "        :name, "
                + "        NULL, "
                + "        NULL, "
                + "        NULL, "
                + "        :description, "
                + "        :created, "
                + "        :updated, "
                + "        :createUserId, "
                + "        :updateUserId) ";

        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", odsResource.getName());
        params.put("description", odsResource.getDescription());
        params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd"));
        params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd"));
        params.put("createUserId", UserHolder.getUser().getId());
        params.put("updateUserId", UserHolder.getUser().getId());
        
        sqlExecutor.insert(sql, params);

    }    
    
    
    @Override
    public Map<String, Object> createRes(Ods701e02Tab1FormBean ods701e02Tab1FormBean, String fileExtension, String resType, String workbookId, String viewId, String workbookName) throws Exception{
        
        try{
            
            String workbookVer = null;
            String workbookUrl = "";
            
            if (workbookId != null)
            {
                String sql1 =
                        "SELECT * "
                        + "FROM   ODS_RESOURCE "
                        + "WHERE  ODS_RESOURCE.WORKBOOK_ID = :workbookId "
                        + "AND ODS_RESOURCE.VIEW_ID = :viewId "
                        + " AND ODS_RESOURCE.FORMAT = :format";
                Map<String, Object> params1 = new HashMap<String, Object>();
                params1.put("workbookId", workbookId);
                params1.put("viewId", viewId);
                params1.put("format", resType);		
                
                List<OdsResource> res = sqlExecutor.queryForList(sql1, params1, OdsResource.class);
                
                if (res.size() > 0)
                {
                    throw new Exception("素材及案例不可重複匯入");
                } 

                
                String sql6 =
                        "SELECT * "
                        + "FROM   ODS_WORKBOOK, "
                        + "       (SELECT ID, "
                        + "              MAX(VER) AS MAX_VER "
                        + "       FROM   ODS_WORKBOOK "
                        + "       GROUP  BY ID) AS MAX_WORKBOOK_VER "
                        + "WHERE  ODS_WORKBOOK.ID = MAX_WORKBOOK_VER.ID "
                        + "AND ODS_WORKBOOK.VER = MAX_WORKBOOK_VER.MAX_VER "
                        + "AND ODS_WORKBOOK.ID = :workbookId ";
                Map<String, Object> params6 = new HashMap<String, Object>();
                params6.put("workbookId", workbookId);
                
                List<OdsWorkbookPK> wbk = sqlExecutor.queryForList(sql6, params6, OdsWorkbookPK.class);
                
                if (wbk.size() > 0)
                {
                    workbookVer = String.valueOf(wbk.get(0).getVer());
                    
                } else {
                    workbookVer = "1";
                    
                    workbookUrl = getWorkbookResourcePath(workbookId, "twb", 1, "twbx");
                    
                    String sql7 =
                            "INSERT ODS_WORKBOOK  "
                             + "       (ID,  "
                             + "        VER,  "
                             + "        NAME,  "
                             + "        URL,  "
                             + "        CREATED,  "
                             + "        UPDATED,  "
                             + "        CREATE_USER_ID,  "
                             + "        UPDATE_USER_ID)  "
                             + "VALUES (:workbookId,  "
                             + "        1,  "
                             + "        :workbookName,  "
                             + "        :workbookUrl,  "
                             + "        :created, "
                             + "        :updated, "
                             + "        :createUserId, "
                             + "        :updateUserId) ";

          
                  
                  Map<String, Object> params7 = new HashMap<String, Object>();
                  params7.put("name", ods701e02Tab1FormBean.getName());
                  params7.put("workbookId", workbookId);
                  params7.put("workbookName", workbookName);
                  params7.put("workbookUrl", workbookUrl);
                  params7.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                  params7.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                  params7.put("createUserId", UserHolder.getUser().getId());
                  params7.put("updateUserId", UserHolder.getUser().getId());
                  
                  
                  sqlExecutor.insert(sql7, params7);
                    
                }
                               
            }
            
            
            
            
            String sql8 =
                    "SELECT * "
                    + "FROM   ODS_RESOURCE "
                    + "WHERE  ODS_RESOURCE.NAME = :name ";
            Map<String, Object> params8 = new HashMap<String, Object>();
            params8.put("name", ods701e02Tab1FormBean.getName());
            
            List<OdsResource> existRes = sqlExecutor.queryForList(sql8, params8, OdsResource.class);
            
            if (existRes.size() > 0)
            {
                throw new Exception("素材及案例名稱不可重覆");
            } 
            
            
            
            
            //String sql = "select * from ODS_RESOURCE";
            String sql =
                      "INSERT ODS_RESOURCE "
                    + "       (ID, "
                    + "        NAME, "
                    + "        WORKBOOK_ID, "
                    + "        VIEW_ID, "
                    + "        FORMAT, "
                    + "        DESCRIPTION, "
                    + "        CREATED, "
                    + "        UPDATED, "
                    + "        CREATE_USER_ID, "
                    + "        UPDATE_USER_ID) "
                    + "VALUES (DEFAULT, "
                    + "        :name, "
                    + "        :workbookId, "
                    + "        :viewId, "
                    + "        :format, "
                    + "        :description, "
                    + "        :created, "
                    + "        :updated, "
                    + "        :createUserId, "
                    + "        :updateUserId) ";
    
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", ods701e02Tab1FormBean.getName());
            params.put("workbookId", workbookId);
            params.put("viewId", viewId);
            params.put("format", resType);
            params.put("description", ods701e02Tab1FormBean.getDescription());
            params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("createUserId", UserHolder.getUser().getId());
            params.put("updateUserId", UserHolder.getUser().getId());
            
            
            sqlExecutor.insert(sql, params);
            
            

            
            
            
            String sql2 =
                    "SELECT R.ID "
                  + "FROM   ODS_RESOURCE R "
                  + "WHERE  R.NAME = :name ";
            
    
            
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("name", ods701e02Tab1FormBean.getName());
            
            List<OdsResource> res = sqlExecutor.queryForList(sql2, params2, OdsResource.class);
            String resId = res.get(0).getId();
            
            logger.debug("resId:" + ToStringBuilder.reflectionToString(resId));
            
            String url = getResourcePath(resId, resType, 1, fileExtension);
            
            
            String sql3 =
                    "INSERT ODS_RESOURCE_VERSION "
                    + "       (RESOURCE_ID, "
                    + "        VER, "
                    + "        VERSION_DATETIME, "
                    + "        WORKBOOK_VER, "
                    + "        URL, "
                    + "        DESCRIPTION, "
                    + "        NAME, "
                    + "        DEL_MK, "
                    + "        CREATED, "
                    + "        UPDATED, "
                    + "        CREATE_USER_ID, "
                    + "        UPDATE_USER_ID) "
                    + "VALUES (:resId, "
                    + "        1, "
                    + "        :versionDatetime, "
                    + "        :workbookVer, "
                    + "        :url, "
                    + "        :description + '_Ver1', "
                    + "        :name + '_Ver1', "
                    + "        0, "
                    + "        :created, "
                    + "        :updated, "
                    + "        :createUserId, "
                    + "        :updateUserId) ";
            
            Map<String, Object> params3 = new HashMap<String, Object>();
            params3.put("resId", resId);
            params3.put("versionDatetime", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params3.put("workbookVer", workbookVer);
            params3.put("url", url);
            params3.put("description", ods701e02Tab1FormBean.getDescription());
            params3.put("name", ods701e02Tab1FormBean.getName());
            params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params3.put("createUserId", UserHolder.getUser().getId());
            params3.put("updateUserId", UserHolder.getUser().getId());
            
            sqlExecutor.insert(sql3, params3);
            
            
            
            String sql4 = 
                    "INSERT ODS_RESOURCE_VERSION "
                    + "       (RESOURCE_ID, "
                    + "        VER, "
                    + "        VERSION_DATETIME, "
                    + "        WORKBOOK_VER, "
                    + "        URL, "
                    + "        DESCRIPTION, "
                    + "        NAME, "
                    + "        DEL_MK, "
                    + "        CREATED, "
                    + "        UPDATED, "
                    + "        CREATE_USER_ID, "
                    + "        UPDATE_USER_ID) "
                    + "SELECT RESOURCE_ID, "
                    + "       0, "
                    + "       VERSION_DATETIME, "
                    + "       WORKBOOK_VER, "
                    + "       URL, "
                    + "       DESCRIPTION, "
                    + "       NAME, "
                    + "       DEL_MK, "
                    + "       CREATED, "
                    + "       UPDATED, "
                    + "       CREATE_USER_ID, "
                    + "       UPDATE_USER_ID "
                    + "FROM   ODS_RESOURCE_VERSION "
                    + "WHERE  RESOURCE_ID = :resId "
                    + "       AND VER = 1 ";
    
            Map<String, Object> params4 = new HashMap<String, Object>();
            params4.put("resId", resId);
            
            sqlExecutor.insert(sql4, params4);
            
            
            
            for (OdsCategory selCategory : ods701e02Tab1FormBean.getSelCategory()){
                String sql5 =
                      "INSERT ODS_RESOURCE_CATEGORY "
                    + "       (RESOURCE_ID, "
                    + "        CATEGORY_ID, "
                    + "        CREATED, "
                    + "        UPDATED, "
                    + "        CREATE_USER_ID, "
                    + "        UPDATE_USER_ID) "
                    + "VALUES (:resId, "
                    + "        :categoryId, "
                    + "        :created, "
                    + "        :updated, "
                    + "        :createUserId, "
                    + "        :updateUserId) ";
        
        
                
                Map<String, Object> params5 = new HashMap<String, Object>();
                params5.put("resId", resId);
                params5.put("categoryId", selCategory.getId());
                params5.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params5.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params5.put("createUserId", UserHolder.getUser().getId());
                params5.put("updateUserId", UserHolder.getUser().getId());
            
                sqlExecutor.insert(sql5, params5);
                
     
            }
            
    
            
    
            Map<String, Object> createInfo = new HashMap<String, Object>();
            createInfo.put("resourceId", resId);
            createInfo.put("resourceVer", 1);
            createInfo.put("url", url);
            createInfo.put("workbookUrl", workbookUrl);
            
            return createInfo;
        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
        
    }    
    
    
    @Override
    public void createDsTempTbl(String resId, int ver, List<String> fileHeaderAry)  throws Exception{

        try {
            String sql8 =
                    "CREATE TABLE ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver + " "
                    + "(";
                    
                    for (String column : fileHeaderAry){
                        sql8 = sql8 + column +  " NVARCHAR(MAX),";
                    }
    
                    if (",".equals(sql8.substring(sql8.length() - 1, sql8.length()) ))
                    {
                        sql8 = sql8.substring(0, sql8.length() - 1);
                    }
                    
                    sql8 = sql8 + ") ";
            
            Map<String, Object> params8 = new HashMap<String, Object>();
    
            
            odsJdbcTemplate.update(sql8, params8);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
    }
    
    @Override
    public List<Boolean> insertDsTempTbl(String resId, int ver, List<Boolean> numericDataTypeAry, List<String> fileHeaderAry, List<List<String>> fileContentAryList) {

        
        if (fileContentAryList.size()>0 && fileContentAryList.get(0).size() > 0){
        
           String sql1 =
                    "INSERT INTO ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver + " "
                    + "(";
                    
                    for (String column : fileHeaderAry){
                        sql1 = sql1 + column +  ",";
                    }
    
                    if (",".equals(sql1.substring(sql1.length() - 1, sql1.length()) ))
                    {
                        sql1 = sql1.substring(0, sql1.length() - 1);
                    }
                    
                    sql1 = sql1 + ") VALUES ";
                    
                    for (List<String> fileContentAry : fileContentAryList) {
                        
                        sql1 = sql1 + "( ";
                        
                        //for (int i = 0; i < fileContentAry.size(); i ++){
                        for (int i = 0; i < fileHeaderAry.size(); i ++){

                            try {
                                /*sql1 = sql1 + "'" + fileContentAry.get(i).replaceAll("'", "''") +  "',"; 
                                
                                if (numericDataTypeAry.get(i) && !isNumeric(fileContentAry.get(i))){
                                    numericDataTypeAry.set(i, false);
                                }*/
                                
                                String origFileContent = fileContentAry.get(i);
                                
                                String newFileContent = numberFilter(origFileContent);
                                
                                /*if (numericDataTypeAry.get(i) && !isNumeric(newFileContent)){
                                    numericDataTypeAry.set(i, false);
                                    sql1 = sql1 + "'" + origFileContent.replaceAll("'", "''") +  "',";  // 多replaceAll("'", "''") 避免欄位值中有single quote
                                } else {
                                    sql1 = sql1 + "'" + newFileContent.replaceAll("'", "''") +  "',";  // 多replaceAll("'", "''") 避免欄位值中有single quote
                                }*/
                                
                                if(isEmpty(newFileContent)){
                                    sql1 = sql1 + "NULL,";
                                } else if (isNumeric(newFileContent)) {
                                    sql1 = sql1 + "'" + newFileContent.replaceAll("'", "''") +  "',";
                                    
                                } else {
                                    numericDataTypeAry.set(i, false);
                                    sql1 = sql1 + "'" + origFileContent.replaceAll("'", "''") +  "',"; 
                                }
                                
                            } catch (Exception e) {
                                sql1 = sql1 + "NULL,";  // 遇到index超出的錯誤，就給定NULL字串
                                numericDataTypeAry.set(i, false);  //當index超出，型態視為字串
                            }
                            

                        }
                        
                        if (",".equals(sql1.substring(sql1.length() - 1, sql1.length()) ))
                        {
                            sql1 = sql1.substring(0, sql1.length() - 1);
                        }
                        
                        sql1 = sql1 + "),";
                        
                    }
                    if (",".equals(sql1.substring(sql1.length() - 1, sql1.length()) ))
                    {
                        sql1 = sql1.substring(0, sql1.length() - 1);
                    }
                    
            
            Map<String, Object> params1 = new HashMap<String, Object>();
    
            
            odsJdbcTemplate.update(sql1, params1);
        
        }
        
        return numericDataTypeAry;
        
    }
    
    public String numberFilter(String origFileContent){

        String newFileContent = trimDoubleQuotes(origFileContent);
        
        newFileContent = trimPercentage(newFileContent);
        
        newFileContent = trimDollarSign(newFileContent);
        
        newFileContent = trimCommas(newFileContent);
        
        return newFileContent;
    }
    
    
    @Override
    public void createDsTbl(String resId, List<Boolean> numericDataTypeAry, List<String> fileHeaderAry) {

        
        String sql8 =
                "CREATE TABLE ODS_" + resId.replaceAll("-", "_") + " "
                + "(";
                
                sql8 = sql8 + "ODS_RESOURCE_VER INT  NOT NULL,";
        
                for (int i = 0; i < fileHeaderAry.size(); i ++){
                    if ( numericDataTypeAry.get(i) ){
                        sql8 = sql8 + fileHeaderAry.get(i) +  " NUMERIC(38,10),";
                    } else {
                        sql8 = sql8 + fileHeaderAry.get(i) +  " NVARCHAR(MAX),";
                    }
                    
                }

                /*if (",".equals(sql8.substring(sql8.length() - 1, sql8.length()) ))
                {
                    sql8 = sql8.substring(0, sql8.length() - 1);
                }*/
                
                sql8 = sql8 + ") ";
                
                sql8 = sql8 + " CREATE INDEX IDX_ODS_" + resId.replaceAll("-", "_") + "_ODS_RESOURCE_VER" + " ON ODS_" + resId.replaceAll("-", "_") + "( ODS_RESOURCE_VER )";
        
        Map<String, Object> params8 = new HashMap<String, Object>();

        
        odsJdbcTemplate.update(sql8, params8);
        
    }
    
    
    @Override
    public List<OdsResource> findDsTbl(String resId) throws Exception {
        
        String sql8 =
                "SELECT * FROM ODS_" + resId.replaceAll("-", "_");
        
        Map<String, Object> params8 = new HashMap<String, Object>();

        params8.put("resId", resId);
        
        return odsJdbcTemplate.query(sql8, params8, new BeanPropertyRowMapper<OdsResource>(
                OdsResource.class));
    }
    
    
    
    @Override
    public void insertDsTbl(String resId, int ver, List<String> fileHeaderAry) throws Exception {

        try{
            String sql =
                    "DELETE ODS_" + resId.replaceAll("-", "_") + " "
                    + "WHERE  ODS_RESOURCE_VER = " + ver ;
            Map<String, Object> params = new HashMap<String, Object>();
             
            odsJdbcTemplate.update(sql, params);
            
            
            String sql0 = 
                    
                    "INSERT ODS_" + resId.replaceAll("-", "_") + " "
                            + "(ODS_RESOURCE_VER, ";
            
            for (int i = 0; i < fileHeaderAry.size(); i ++){
    
                sql0 = sql0 + fileHeaderAry.get(i) +  ",";
            }
            
            if (",".equals(sql0.substring(sql0.length() - 1, sql0.length()) ))
            {
                sql0 = sql0.substring(0, sql0.length() - 1);
            }
            
            sql0 = sql0 + ") SELECT " + ver + ",";
            
            
            for (int i = 0; i < fileHeaderAry.size(); i ++){
    
                sql0 = sql0 + fileHeaderAry.get(i) +  ",";
            }
            
            if (",".equals(sql0.substring(sql0.length() - 1, sql0.length()) ))
            {
                sql0 = sql0.substring(0, sql0.length() - 1);
            }
            
            sql0 = sql0 + " FROM " + "ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver  + " ";
            
    
            Map<String, Object> params0 = new HashMap<String, Object>();
            
            odsJdbcTemplate.update(sql0, params0);
        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
        //若目前的ver若是maxVer時，要更改ver0的內容
        String sql3 =
                "SELECT MAX(ODS_RESOURCE_VER) AS VER "
              + "FROM ODS_" + resId.replaceAll("-", "_") ;
        
        Map<String, Object> params3 = new HashMap<String, Object>();
        List<OdsResourceVersionPK> res = odsJdbcTemplate.query(sql3, params3, new BeanPropertyRowMapper<OdsResourceVersionPK>(
                OdsResourceVersionPK.class));
        int maxVer = res.get(0).getVer();
        
        logger.debug("maxVer:" + ToStringBuilder.reflectionToString(maxVer));
        
        if(maxVer == ver)
        {
            String sql1 =
                    "DELETE ODS_" + resId.replaceAll("-", "_") + " "
                    + "WHERE  ODS_RESOURCE_VER = 0 " ;
            Map<String, Object> params1 = new HashMap<String, Object>();
             
            odsJdbcTemplate.update(sql1, params1);
            
            
            
            
            String sql2 = 
                    
                    "INSERT ODS_" + resId.replaceAll("-", "_") + " "
                            + "(ODS_RESOURCE_VER, ";
            
            for (int i = 0; i < fileHeaderAry.size(); i ++){

                sql2 = sql2 + fileHeaderAry.get(i) +  ",";
            }
            
            if (",".equals(sql2.substring(sql2.length() - 1, sql2.length()) ))
            {
                sql2 = sql2.substring(0, sql2.length() - 1);
            }
            
            sql2 = sql2 + ") SELECT 0,";
            
            
            for (int i = 0; i < fileHeaderAry.size(); i ++){

                sql2 = sql2 + fileHeaderAry.get(i) +  ",";
            }
            
            if (",".equals(sql2.substring(sql2.length() - 1, sql2.length()) ))
            {
                sql2 = sql2.substring(0, sql2.length() - 1);
            }
            
            sql2 = sql2 + " FROM " + "ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver  + " ";
            

            Map<String, Object> params2 = new HashMap<String, Object>();
            
            odsJdbcTemplate.update(sql2, params2);
        }
        
    }
    
    @Override
    public void dropDsTempTbl(String resId, int ver) {
       
        String sql8 =
                "DROP TABLE ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver + " ";
        
        Map<String, Object> params8 = new HashMap<String, Object>();
        
        odsJdbcTemplate.update(sql8, params8);
        
    }
    
    @Override
    public void updateResVerToDsInfo(String resId, int ver, String toDatastoreSync, String toDatastoreDate, String toDatastoreSuccess) {
        
        
      String sql =
                "UPDATE ODS_RESOURCE_VERSION SET "
              + "       TO_DATASTORE_SYNC = ";
              if (StringUtils.isEmpty(toDatastoreSync)) {
                  sql = sql  + "NULL, ";
              } else {
                  sql = sql  + ":toDatastoreSync, ";
              }
              sql = sql + "       TO_DATASTORE_DATE = ";
              if (StringUtils.isEmpty(toDatastoreDate)) {
                  sql = sql  + "NULL, ";
              } else {
                  sql = sql  + ":toDatastoreDate, ";
              }
              sql = sql + "       TO_DATASTORE_SUCCESS = ";
              if (StringUtils.isEmpty(toDatastoreSuccess)) {
                  sql = sql  + "NULL ";
              } else {
                  sql = sql  + ":toDatastoreSuccess ";
              }
//              + "       UPDATED = :updated, "
//              + "       UPDATE_USER_ID = :updateUserId "
              sql = sql  + "WHERE  RESOURCE_ID = :resId  AND (VER = :ver OR VER = 0)" ;
      
      
      
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("toDatastoreSync", toDatastoreSync);
      params.put("toDatastoreDate", toDatastoreDate);
      params.put("toDatastoreSuccess", toDatastoreSuccess);
//      params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));	//INSERT RV與檔案的資料寫到DS裡的時間不會相同，但卻是同一步驟，因此更新日期不變
//      params.put("updateUserId", UserHolder.getUser().getId());
      params.put("resId", resId);
      params.put("ver", ver);
      
      sqlExecutor.update(sql, params);
      
      
      
      
        
    }
    
    private boolean isEmpty(String s){
        return s == null || "".equals(s.trim());
    }
    
    private boolean isNumeric(String s) {  
        return s.matches("^(-?0+(\\.\\d+)?|-?([1-9][0-9]*)+(\\.\\d+)?)$");
        /*log.debug("test str:" + isNumeric("12345"));
        log.debug("test str:" + isNumeric("+12345"));
        log.debug("test str:" + isNumeric("-12345"));
        log.debug("test str:" + isNumeric("A"));
        log.debug("test str:" + isNumeric("1.2222222222"));
        log.debug("test str:" + isNumeric("3333333333.222.2222222"));
        log.debug("test str:" + isNumeric("-1.2222222222"));
        log.debug("test str:" + isNumeric("03333333333.2222222222"));
        log.debug("test str:" + isNumeric("0"));
        log.debug("test str:" + isNumeric("0123"));
        log.debug("test str:" + isNumeric("0.000"));
        log.debug("test str:" + isNumeric("0.001"));
        log.debug("test str:" + isNumeric("-0.001"));
        log.debug("test str:" + isNumeric("+0.001"));*/
    }  
    
    // "123" --> 123
    private static String trimDoubleQuotes(String text) {  
        int textLength = text.length();

        if (textLength >= 2 && text.charAt(0) == '"' && text.charAt(textLength - 1) == '"') {
            return text.substring(1, textLength - 1);
        } 
        return text;
    }  

    // 123% --> 123
    private static String trimPercentage(String text) {  
        int textLength = text.length();

        if (textLength >= 1 && text.charAt(textLength - 1) == '%') {
            return text.substring(0, textLength - 1);
        } 
        return text;
    } 
    
    // $123 --> 123  or  -$123 --> 123
    private static String trimDollarSign(String text) {  
        int textLength = text.length();

        if (textLength >= 1 && (text.charAt(0) == '$' || (text.charAt(0) == '-' && text.charAt(1) == '$'))) {
            return text.replaceFirst("\\$","");
        } 
        return text;
    } 
    
    // 1,234 --> 1234  but  123,4 --> 1234
    private static String trimCommas(String text) {  
        return text.replaceAll(",", "");
    } 
    

    private String getResourcePath(String resId, String resourceType, int ver, String fileExtension) {
        return 
        propertiesAccessor
        .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
        + ODS_RESOURCE_PATH
        + resId
        + File.separator
        + resourceType
        + File.separator
        + resId + "-" + ver + "." + fileExtension;
    }
    
    private String getWorkbookResourcePath(String workbookId, String resourceType, int ver, String fileExtension) {
        return 
        propertiesAccessor
        .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
        + ODS_WORKBOOK_PATH
        + workbookId
        + File.separator
        + resourceType
        + File.separator
        + workbookId + "-" + ver + "." + fileExtension;
    }
    
    
    @Override
    public void saveGrid1(List<Ods701eGrid1Dto> updOds701eGrid1Dtos) {
        //String sql = "select * from ODS_RESOURCE";
        
        for (Ods701eGrid1Dto updOds701eGrid1Dto : updOds701eGrid1Dtos){
            String sql =
                      "UPDATE ODS_RESOURCE "
                    + "SET    NAME = :name, "
                    + "       DESCRIPTION = :description, "
                    + "       UPDATED = :updated, "
                    + "       UPDATE_USER_ID = :updateUserId "
                    + "WHERE  ID = :id " ;
            
            
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", updOds701eGrid1Dto.getName());
            params.put("description", updOds701eGrid1Dto.getDescription());
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updateUserId", UserHolder.getUser().getId());
            params.put("id", updOds701eGrid1Dto.getId());
            
            sqlExecutor.update(sql, params);
            
            
            String sql2 =
                    "DELETE ODS_RESOURCE_CATEGORY "
                    + "WHERE  RESOURCE_ID = :id " ;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("id", updOds701eGrid1Dto.getId());
              
            sqlExecutor.update(sql2, params2);
            
            
            
           
            String categoryIdList = updOds701eGrid1Dto.getResCategory();

            if(categoryIdList != null && !"NULL".equals(categoryIdList))
            {
                
                if (",".equals(categoryIdList.substring(categoryIdList.length() - 1, categoryIdList.length()) ))
                {
                    categoryIdList = categoryIdList.substring(0, categoryIdList.length() - 1);
                }
                

                List<String> categoryIdListAry = new ArrayList<String>(Arrays.asList(categoryIdList.split(",")));

                for (String categoryId : categoryIdListAry){
                    String sql3 =
                          "INSERT ODS_RESOURCE_CATEGORY "
                        + "       (RESOURCE_ID, "
                        + "        CATEGORY_ID, "
                        + "        CREATED, "
                        + "        UPDATED, "
                        + "        CREATE_USER_ID, "
                        + "        UPDATE_USER_ID) "
                        + "VALUES (:resId, "
                        + "        :categoryId, "
                        + "        :created, "
                        + "        :updated, "
                        + "        :createUserId, "
                        + "        :updateUserId) ";
            
            
                    
                    Map<String, Object> params3 = new HashMap<String, Object>();
                    params3.put("resId", updOds701eGrid1Dto.getId());
                    params3.put("categoryId", categoryId);
                    params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params3.put("createUserId", UserHolder.getUser().getId());
                    params3.put("updateUserId", UserHolder.getUser().getId());
                
                    sqlExecutor.insert(sql3, params3);
                    

                }
                
             }
            
        }

    }    


    /*@Override
    public List<Fms421rDto> findByFyrRound(String fyr) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append("Select A.FYR, ");
        sql.append("A.MDAY, ");
        sql.append("ROUND(A.DAY_RSLT1 /100000000) DAY_RSLT1 , ");
        sql.append("ROUND(A.DAY_RSLT2 /100000000) DAY_RSLT2 , ");
        sql.append("ROUND(A.DAY_RSLT3 /100000000) DAY_RSLT3 , ");
        sql.append("ROUND(A.DAY_RSLT4 /100000000) DAY_RSLT4 , ");
        sql.append("ROUND(A.DAY_RSLT5 /100000000) DAY_RSLT5 , ");
        sql.append("ROUND(A.DAY_RSLT6 /100000000) DAY_RSLT6 , ");
        sql.append("ROUND(A.DAY_RSLT7 /100000000) DAY_RSLT7 , ");
        sql.append("ROUND(A.DAY_RSLT8 /100000000) DAY_RSLT8 , ");
        sql.append("ROUND(A.DAY_RSLT9 /100000000) DAY_RSLT9 , ");
        sql.append("ROUND(A.DAY_RSLT10/100000000) DAY_RSLT10, ");
        sql.append("ROUND(A.DAY_RSLT11/100000000) DAY_RSLT11, ");
        sql.append("ROUND(A.DAY_RSLT12/100000000) DAY_RSLT12 ");
        sql.append("From FMS421VA A ");
        sql.append("WHERE A.FYR =:fyr ");
        sql.append("order by A.MDAY ");
        params.put("fyr", fyr);
        return sqlExecutor.queryForList(sql, params, Fms421rDto.class);
    }

    @Override
    public List<Fms421rDto> findByFyr(String fyr) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append("Select A.FYR, ");
        sql.append("A.MDAY, ");
        sql.append("A.DAY_RSLT1 , ");
        sql.append("A.DAY_RSLT2 , ");
        sql.append("A.DAY_RSLT3 , ");
        sql.append("A.DAY_RSLT4 , ");
        sql.append("A.DAY_RSLT5 , ");
        sql.append("A.DAY_RSLT6 , ");
        sql.append("A.DAY_RSLT7 , ");
        sql.append("A.DAY_RSLT8 , ");
        sql.append("A.DAY_RSLT9 , ");
        sql.append("A.DAY_RSLT10, ");
        sql.append("A.DAY_RSLT11, ");
        sql.append("A.DAY_RSLT12 ");
        sql.append("From FMS421VA A ");
        sql.append("WHERE A.FYR =:fyr ");
        sql.append("order by A.MDAY ");
        params.put("fyr", fyr);
        return sqlExecutor.queryForList(sql, params, Fms421rDto.class);
    }

    private List<Fms421rDto> getTestData(String fyr) {
        logger.debug("Fms421rService test data Start");
        List<Fms421rDto> fms421rDtonewList = new ArrayList<Fms421rDto>();
        Random random = new Random((new Date()).getTime());
        for (int dayCnt = 1; dayCnt <= 31; dayCnt++) {
            if (random.nextBoolean()) {
                Fms421rDto newdto = new Fms421rDto();
                newdto.setFyr(fyr);
                newdto.setMday(Integer.toString(dayCnt));
                newdto.setDayRslt1((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt2((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt3((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt4((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt5((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt6((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt7((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt8((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt9((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt10((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt11((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                newdto.setDayRslt12((new BigDecimal(random.nextInt(10000)).multiply(new BigDecimal(100000000))));
                fms421rDtonewList.add(newdto);
                logger.debug("Fms421rService test data {}", newdto);
            }
        }
        return fms421rDtonewList;
    }
    
    private List<Fms421rDto> getTestReportData(String fyr) {
        logger.debug("Fms421rService test data Start");
        List<Fms421rDto> fms421rDtonewList = new ArrayList<Fms421rDto>();
        Random random = new Random((new Date()).getTime());
        for (int dayCnt = 1; dayCnt <= 31; dayCnt++) {
            if (random.nextBoolean()) {
                Fms421rDto newdto = new Fms421rDto();
                newdto.setFyr(fyr);
                newdto.setMday(Integer.toString(dayCnt));
                newdto.setDayRslt1(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt2(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt3(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt4(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt5(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt6(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt7(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt8(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt9(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt10(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt11(new BigDecimal(random.nextInt(10000)));
                newdto.setDayRslt12(new BigDecimal(random.nextInt(10000)));
                fms421rDtonewList.add(newdto);
                logger.debug("Fms421rService test data {}", newdto);
            }
        }
        return fms421rDtonewList;
    }

    @Override
    public void updateRsltByFyrAndmday(List<Fms421rDto> fms421rDtoList) {
        StringBuffer sql = new StringBuffer();
        sql.append("Insert into FMS421FA (FYR,MDAY,DAY_RSLT1,DAY_RSLT2,DAY_RSLT3,"
                + "DAY_RSLT4,DAY_RSLT5,DAY_RSLT6,DAY_RSLT7,DAY_RSLT8,DAY_RSLT9,"
                + "DAY_RSLT10,DAY_RSLT11,DAY_RSLT12,USERID,UPD_DATE_TIME)");
        sql.append("               values(:fyr,:mday,:dayRslt1,:dayRslt2,:dayRslt3,"
                + ":dayRslt4,:dayRslt5,:dayRslt6,:dayRslt7,:dayRslt8,:dayRslt9,"
                + ":dayRslt10,:dayRslt11,:dayRslt12,:userId,:updDateTime)");
        List<Map<String, ?>> paramList = new ArrayList<Map<String, ?>>();
        for (Fms421rDto dto : fms421rDtoList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dayRslt1", dto.getDayRslt1());
            map.put("dayRslt2", dto.getDayRslt2());
            map.put("dayRslt3", dto.getDayRslt3());
            map.put("dayRslt4", dto.getDayRslt4());
            map.put("dayRslt5", dto.getDayRslt5());
            map.put("dayRslt6", dto.getDayRslt6());
            map.put("dayRslt7", dto.getDayRslt7());
            map.put("dayRslt8", dto.getDayRslt8());
            map.put("dayRslt9", dto.getDayRslt9());
            map.put("dayRslt10", dto.getDayRslt10());
            map.put("dayRslt11", dto.getDayRslt11());
            map.put("dayRslt12", dto.getDayRslt12());
            map.put("fyr", dto.getFyr());
            map.put("mday", dto.getMday());
            map.put("userId", UserHolder.getUser().getId());
            map.put("updDateTime", LocalDateTimes.toMinguoString(LocalDateTime.now()));
            paramList.add(map);
            logger.debug("insert data :{}", dto.toString());
        }
        sqlExecutor.insert(sql, paramList);
    }

    @Override
    public void deleteByFyr(String fyr) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append("delete FMS421FA ");
        sql.append(" where fyr =:fyr ");
        params.put("fyr", fyr);
        sqlExecutor.delete(sql, params);
    }*/
    
    
    
    
    @Override
    public Map<String, Object> createResVersion(Ods701e05Tab1FormBean ods701e05Tab1FormBean, String fileExtension, String resType) {
        
        String sql2 =
                "SELECT MAX(VER) AS VER "
              + "FROM   ODS_RESOURCE_VERSION RV "
              + "WHERE  RV.RESOURCE_ID = :resId ";
        

        
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("resId", ods701e05Tab1FormBean.getResourceId());
        
        List<OdsResourceVersionPK> res = sqlExecutor.queryForList(sql2, params2, OdsResourceVersionPK.class);
        int maxVer = res.get(0).getVer();
        
        logger.debug("maxVer:" + ToStringBuilder.reflectionToString(maxVer));
        
        String url = getResourcePath(ods701e05Tab1FormBean.getResourceId(), resType, maxVer + 1, fileExtension);
        
        String sql3 =
                "INSERT ODS_RESOURCE_VERSION "
                + "       (RESOURCE_ID, "
                + "        VER, "
                + "        VERSION_DATETIME, "
                + "        WORKBOOK_VER, "
                + "        URL, "
                + "        DESCRIPTION, "
                + "        NAME, "
                + "        DEL_MK, "
                + "        CREATED, "
                + "        UPDATED, "
                + "        CREATE_USER_ID, "
                + "        UPDATE_USER_ID) "
                + "SELECT RV.RESOURCE_ID, "
                + "       VER + 1, "
                + "       :versionDatetime, "
                + "       WORKBOOK_VER, "
                + "       :url, "
                + "       :description, "
                + "       :name, "
                + "       0, "
                + "       :created, "
                + "       :updated, "
                + "       :createUserId, "
                + "       :updateUserId "
                + "FROM   ODS_RESOURCE_VERSION RV, "
                + "       (SELECT RESOURCE_ID, "
                + "               MAX(VER) AS MAX_RES_VER "
                + "        FROM   ODS_RESOURCE_VERSION "
                + "        GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                + "WHERE  RV.RESOURCE_ID = :resId "
                + "       AND VER = MAX_RES_VER.MAX_RES_VER "
                + "       AND RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID ";
        
        Map<String, Object> params3 = new HashMap<String, Object>();
        params3.put("versionDatetime", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params3.put("url", url);
        params3.put("description", ods701e05Tab1FormBean.getDescription());
        params3.put("name", ods701e05Tab1FormBean.getName());
        params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params3.put("createUserId", UserHolder.getUser().getId());
        params3.put("updateUserId", UserHolder.getUser().getId());
        params3.put("resId", ods701e05Tab1FormBean.getResourceId());
        
        sqlExecutor.insert(sql3, params3);
        
        
        String sql4 =
                "UPDATE RV0 "
                + "SET    RV0.VERSION_DATETIME = RV_MAX.VERSION_DATETIME, "
                + "       RV0.WORKBOOK_VER = RV_MAX.WORKBOOK_VER, "
                + "       RV0.URL = RV_MAX.URL, "
                + "       RV0.DESCRIPTION = RV_MAX.DESCRIPTION, "
                + "       RV0.NAME = RV_MAX.NAME, "
                + "       RV0.DEL_MK = RV_MAX.DEL_MK, "
                + "       RV0.CREATED = RV_MAX.CREATED, "
                + "       RV0.UPDATED = RV_MAX.UPDATED, "
                + "       RV0.CREATE_USER_ID = RV_MAX.CREATE_USER_ID, "
                + "       RV0.UPDATE_USER_ID = RV_MAX.UPDATE_USER_ID "
                + "FROM   ODS_RESOURCE_VERSION RV0 "
                + "       INNER JOIN ODS_RESOURCE_VERSION RV_MAX "
                + "               ON RV0.RESOURCE_ID = RV_MAX.RESOURCE_ID, "
                + "       (SELECT RESOURCE_ID, "
                + "               MAX(VER) AS MAX_VER "
                + "        FROM   ODS_RESOURCE_VERSION "
                + "        WHERE  DEL_MK <> 1 "
                + "        GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                + "WHERE  RV_MAX.RESOURCE_ID = :resId "
                + "       AND RV_MAX.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                + "       AND RV_MAX.VER = MAX_RES_VER.MAX_VER "
                + "       AND RV0.VER = '0' ";
        
        Map<String, Object> params4 = new HashMap<String, Object>();
        params4.put("resId", ods701e05Tab1FormBean.getResourceId());
        
        sqlExecutor.insert(sql4, params4);
        
        

        
        Map<String, Object> createInfo = new HashMap<String, Object>();
        createInfo.put("resourceId", ods701e05Tab1FormBean.getResourceId());
        createInfo.put("resourceVer", maxVer + 1);
        createInfo.put("url", url);
        
        return createInfo;
    }   
    
    
    @Override
    public void saveGrid2(List<Ods701eGrid2Dto> updOds701eGrid2Dtos) {
        //String sql = "select * from ODS_RESOURCE";
        
        for (Ods701eGrid2Dto updOds701eGrid2Dto : updOds701eGrid2Dtos){
            String sql =
                    "UPDATE ODS_RESOURCE_VERSION "
                    + "SET    NAME = :name, "
                    + "       DESCRIPTION = :description, "
                    + "       DEL_MK = (CASE WHEN :delMk = 'Y' THEN 1 ELSE 0 END), "
                    + "       UPDATED = :updated, "
                    + "       UPDATE_USER_ID = :updateUserId "
                    + "WHERE  RESOURCE_ID = :resourceId "
                    + "       AND ( VER = :ver "
                    + "              OR VER = (SELECT 0 "
                    + "                          FROM   ODS_RESOURCE_VERSION RV, "
                    + "                                 (SELECT RESOURCE_ID, "
                    + "                                         MAX(VER) AS MAX_VER "
                    + "                                  FROM   ODS_RESOURCE_VERSION "
                    + "                                  GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                    + "                          WHERE  RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                    + "                                 AND VER = MAX_RES_VER.MAX_VER "
                    + "                                 AND RV.RESOURCE_ID = "
                    + "                                     :resourceId "
                    + "                                 AND VER = :ver) ) " ;

    
    
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", updOds701eGrid2Dto.getName());
            params.put("description", updOds701eGrid2Dto.getDescription());
            params.put("delMk", updOds701eGrid2Dto.getDelMk());
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updateUserId", UserHolder.getUser().getId());
            params.put("resourceId", updOds701eGrid2Dto.getResourceId());
            params.put("ver", updOds701eGrid2Dto.getVer());
        
            sqlExecutor.update(sql, params);
            
            String sql4 =
                    "UPDATE RV0 "
                    + "SET    RV0.VERSION_DATETIME = RV_MAX.VERSION_DATETIME, "
                    + "       RV0.WORKBOOK_VER = RV_MAX.WORKBOOK_VER, "
                    + "       RV0.URL = RV_MAX.URL, "
                    + "       RV0.DESCRIPTION = RV_MAX.DESCRIPTION, "
                    + "       RV0.NAME = RV_MAX.NAME, "
                    + "       RV0.DEL_MK = RV_MAX.DEL_MK, "
                    + "       RV0.CREATED = RV_MAX.CREATED, "
                    + "       RV0.UPDATED = RV_MAX.UPDATED, "
                    + "       RV0.CREATE_USER_ID = RV_MAX.CREATE_USER_ID, "
                    + "       RV0.UPDATE_USER_ID = RV_MAX.UPDATE_USER_ID "
                    + "FROM   ODS_RESOURCE_VERSION RV0 "
                    + "       INNER JOIN ODS_RESOURCE_VERSION RV_MAX "
                    + "               ON RV0.RESOURCE_ID = RV_MAX.RESOURCE_ID, "
                    + "       (SELECT RESOURCE_ID, "
                    + "               MAX(VER) AS MAX_VER "
                    + "        FROM   ODS_RESOURCE_VERSION "
                    + "        WHERE  DEL_MK <> 1 "
                    + "        GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                    + "WHERE  RV_MAX.RESOURCE_ID = :resId "
                    + "       AND RV_MAX.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                    + "       AND RV_MAX.VER = MAX_RES_VER.MAX_VER "
                    + "       AND RV0.VER = '0' ";
            
            Map<String, Object> params4 = new HashMap<String, Object>();
            params4.put("resId", updOds701eGrid2Dto.getResourceId());
            
            sqlExecutor.insert(sql4, params4);
        }

    }
    
    @Override
    public Map<String, Object> saveFileRefreshUpload(Ods701eGrid2Dto ods701eGrid2Dto,
            String fileExtension, String resourceType){
        
        int ver = Integer.valueOf(ods701eGrid2Dto.getVer());
        
        String url = getResourcePath(ods701eGrid2Dto.getResourceId(), resourceType, ver, fileExtension);
        
        
        String sql =
                "UPDATE ODS_RESOURCE_VERSION "
                + "SET    URL = :url, "
                + "       VERSION_DATETIME = :versionDatetime, "
                + "       UPDATED = :updated, "
                + "       UPDATE_USER_ID = :updateUserId "
                + "WHERE  RESOURCE_ID = :resourceId "
                + "       AND ( VER = :ver "
                + "              OR VER = (SELECT 0 "
                + "                          FROM   ODS_RESOURCE_VERSION RV, "
                + "                                 (SELECT RESOURCE_ID, "
                + "                                         MAX(VER) AS MAX_VER "
                + "                                  FROM   ODS_RESOURCE_VERSION "
                + "                                  GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                + "                          WHERE  RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                + "                                 AND VER = MAX_RES_VER.MAX_VER "
                + "                                 AND RV.RESOURCE_ID = "
                + "                                     :resourceId "
                + "                                 AND VER = :ver) ) " ;



        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("url", url);
        params.put("versionDatetime", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params.put("updateUserId", UserHolder.getUser().getId());
        params.put("resourceId", ods701eGrid2Dto.getResourceId());
        params.put("ver", ods701eGrid2Dto.getVer());
    
        sqlExecutor.update(sql, params);
        
        
        
        String sql2 =
                "SELECT MAX(VER) AS VER "
              + "FROM   ODS_RESOURCE_VERSION RV "
              + "WHERE  RV.RESOURCE_ID = :resId ";
        
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("resId", ods701eGrid2Dto.getResourceId());
        
        List<OdsResourceVersionPK> res = sqlExecutor.queryForList(sql2, params2, OdsResourceVersionPK.class);
        int maxVer = res.get(0).getVer();
        
        logger.debug("maxVer:" + ToStringBuilder.reflectionToString(maxVer));
        
        boolean isMaxVer = false;
        if (ver == maxVer){
            isMaxVer = true;
        }
        
        
        Map<String, Object> updateInfo = new HashMap<String, Object>();
        updateInfo.put("url", url);
        updateInfo.put("isMaxVer", isMaxVer);
        
        return updateInfo;
    }
    
    
    
    @Override
    public Map<String, Object> saveFileRefreshImport(Ods701eGrid2Dto ods701eGrid2Dto, String fileExtension,
            String resourceType, String workbookId, String viewId){
           
           String sql6 =
                   "SELECT VER, URL "
                   + "FROM   ODS_WORKBOOK, "
                   + "       (SELECT ID, "
                   + "              MAX(VER) AS MAX_VER "
                   + "       FROM   ODS_WORKBOOK "
                   + "       GROUP  BY ID) AS MAX_WORKBOOK_VER "
                   + "WHERE  ODS_WORKBOOK.ID = MAX_WORKBOOK_VER.ID "
                   + "AND ODS_WORKBOOK.VER = MAX_WORKBOOK_VER.MAX_VER "
                   + "AND ODS_WORKBOOK.ID = :workbookId ";
           Map<String, Object> params6 = new HashMap<String, Object>();
           params6.put("workbookId", workbookId);
           
           List<OdsWorkbookPK> wbkPk = sqlExecutor.queryForList(sql6, params6, OdsWorkbookPK.class);
           
           String workbookVer = String.valueOf(wbkPk.get(0).getVer());
           
           List<OdsWorkbook> wbk = sqlExecutor.queryForList(sql6, params6, OdsWorkbook.class);
           
           String workbookUrl = wbk.get(0).getUrl();
           
        
        int ver = Integer.valueOf(ods701eGrid2Dto.getVer());
        
        String url = getResourcePath(ods701eGrid2Dto.getResourceId(), resourceType, ver, fileExtension);
        
        
        String sql =
                "UPDATE ODS_RESOURCE_VERSION "
                + "SET    URL = :url, "
                + "       WORKBOOK_VER = :workbookVer, "		
                + "       VERSION_DATETIME = :versionDatetime, "
                + "       UPDATED = :updated, "
                + "       UPDATE_USER_ID = :updateUserId "
                + "WHERE  RESOURCE_ID = :resourceId "
                + "       AND ( VER = :ver "
                + "              OR VER = (SELECT 0 "
                + "                          FROM   ODS_RESOURCE_VERSION RV, "
                + "                                 (SELECT RESOURCE_ID, "
                + "                                         MAX(VER) AS MAX_VER "
                + "                                  FROM   ODS_RESOURCE_VERSION "
                + "                                  GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                + "                          WHERE  RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                + "                                 AND VER = MAX_RES_VER.MAX_VER "
                + "                                 AND RV.RESOURCE_ID = "
                + "                                     :resourceId "
                + "                                 AND VER = :ver) ) " ;



        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("url", url);
        params.put("workbookVer", workbookVer);
        params.put("versionDatetime", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
        params.put("updateUserId", UserHolder.getUser().getId());
        params.put("resourceId", ods701eGrid2Dto.getResourceId());
        params.put("ver", ods701eGrid2Dto.getVer());
    
        sqlExecutor.update(sql, params);
        
        
        
        String sql2 =
                "SELECT MAX(VER) AS VER "
              + "FROM   ODS_RESOURCE_VERSION RV "
              + "WHERE  RV.RESOURCE_ID = :resId ";
        
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("resId", ods701eGrid2Dto.getResourceId());
        
        List<OdsResourceVersionPK> res = sqlExecutor.queryForList(sql2, params2, OdsResourceVersionPK.class);
        int maxVer = res.get(0).getVer();
        
        logger.debug("maxVer:" + ToStringBuilder.reflectionToString(maxVer));
        
        boolean isMaxVer = false;
        if (ver == maxVer){
            isMaxVer = true;
        }
        
        
        Map<String, Object> updateInfo = new HashMap<String, Object>();
        updateInfo.put("url", url);
        updateInfo.put("isMaxVer", isMaxVer);
        updateInfo.put("workbookUrl", workbookUrl);
        
        return updateInfo;
    }
    

    
    @Override
    public List<OdsPackageVersion> findRelatePackageInfoList(List<Ods701eGrid2Dto> updOds701eGrid2Dtos){
        
        List<OdsPackageVersionPK> relatePackageInfosList = new ArrayList<OdsPackageVersionPK>();
        
        List<OdsPackageVersion> packageVerIdList = new ArrayList<OdsPackageVersion>();
        
        //找出目前所修改的res+ver，其所有相對應的pkg id、ver。且當所修改的ver為該res的最新版，則連同ver 0相對應的pkg id、ver也要抓出
        for (Ods701eGrid2Dto updOds701eGrid2Dto : updOds701eGrid2Dtos){
            
            String sql =
                    "SELECT PR.PACKAGE_ID as PACKAGE_ID, PR.PACKAGE_VER as VER "
                  + "FROM   ODS_PACKAGE_RESOURCE PR "
                  + "WHERE  PR.RESOURCE_ID = :resourceId "
                  + "AND ( PR.RESOURCE_VER = :ver "
                  + "OR PR.RESOURCE_VER = (SELECT 0 "
                  + "                      FROM   ODS_RESOURCE_VERSION RV, "
                  + "                         (SELECT RESOURCE_ID, "
                  + "                                 MAX(VER) AS MAX_VER "
                  + "                          FROM   ODS_RESOURCE_VERSION "
                  + "                          GROUP  BY RESOURCE_ID) AS MAX_RES_VER "
                  + "                      WHERE  RV.RESOURCE_ID = MAX_RES_VER.RESOURCE_ID "
                  + "                      AND VER = MAX_RES_VER.MAX_VER "
                  + "                      AND RV.RESOURCE_ID = :resourceId "
                  + "                      AND VER = :ver ) )";


 
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("resourceId", updOds701eGrid2Dto.getResourceId());
            params.put("ver", updOds701eGrid2Dto.getVer());
        
            List<OdsPackageVersionPK> relatePackageInfo = sqlExecutor.queryForList(sql, params, OdsPackageVersionPK.class);
            
            relatePackageInfosList.addAll(relatePackageInfo);
        }
        
        /*
        // debug
        for (OdsPackageVersionPK relatePackageInfo : relatePackageInfosList)
        {

                logger.debug("PkgId:" + relatePackageInfo.getPackageId());
                logger.debug("PkgVer:" + relatePackageInfo.getVer());
                logger.debug("=============================");

        }
        */
        
        //pkg id、ver一樣的只保留一組，將重複的entity使用hash的方式進行剔除
        HashSet<OdsPackageVersionPK> h = new HashSet<OdsPackageVersionPK>(relatePackageInfosList);  
        relatePackageInfosList.clear();  
        relatePackageInfosList.addAll(h); 
        
        for (OdsPackageVersionPK relatePackageInfo : relatePackageInfosList)
        {

                /*logger.debug("PkgId:" + relatePackageInfo.getPackageId());
                logger.debug("PkgVer:" + relatePackageInfo.getVer());
                logger.debug("=============================");*/

                OdsPackageVersion odsPackageVersion = new OdsPackageVersion();
                odsPackageVersion.setId(relatePackageInfo);
                
                packageVerIdList.add(odsPackageVersion);
        }
        
        
        List<OdsPackageVersion> packageVerList = new ArrayList<OdsPackageVersion>();
        for (OdsPackageVersion packageVerId : packageVerIdList){
            
            String sql =
                    "SELECT PV.PACKAGE_ID as PACKAGE_ID, PV.VER as VER, PV.PATTERN as PATTERN "
                  + "FROM   ODS_PACKAGE_VERSION PV "
                  + "WHERE  PV.PACKAGE_ID = :packageId "
                  + "AND PV.VER = :ver ";

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("packageId", packageVerId.getId().getPackageId());
            params.put("ver", packageVerId.getId().getVer());
        
            List<OdsPackageVersion> packageInfo = sqlExecutor.queryForList(sql, params, OdsPackageVersion.class);
            
            packageVerList.addAll(packageInfo);
        }
        
        return packageVerList;
    }
    

    
    
    @Override
    public List<Ods703eTab2DialogDto> findResByNameAndCategory(String resName, String catId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("A.ID as resourceId, A.NAME as resName,A.DESCRIPTION as resDesc,")
               .append("A.FORMAT as format,")
               .append("(SELECT CAST(C.NAME + ', ' AS VARCHAR(MAX))")
               .append("FROM ODS_RESOURCE_CATEGORY B, ODS_CATEGORY C")
               .append("WHERE (A.ID = B.RESOURCE_ID")
               .append("AND C.ID = B.CATEGORY_ID")               
               .append(") FOR XML PATH ('')")
               .append(") as catName")
               .append("FROM ODS_RESOURCE A")
               .appendWhen(!Strings.isNullOrEmpty(catId), ", ODS_RESOURCE_CATEGORY D")
               .append("WHERE A.FORMAT <> 'common'")
               .appendWhen(!Strings.isNullOrEmpty(resName), "AND A.NAME LIKE :name", "%"+resName+"%")
               .appendWhen(!Strings.isNullOrEmpty(catId), "AND D.CATEGORY_ID = :catId", catId)
               .appendWhen(!Strings.isNullOrEmpty(catId), "AND A.ID = D.RESOURCE_ID")
               .append("ORDER BY A.NAME");

        Query query = builder.build();
        return sqlExecutor.queryForList(query, Ods703eTab2DialogDto.class);
    }
    
    @Override
    public List<Ods703eTab2DialogDto> findCommonRes() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("A.ID as resourceId, '0' as resourceVer, A.NAME as resName, A.DESCRIPTION as resDesc,")
               .append("A.FORMAT as format")
               .append("FROM ODS_RESOURCE A")
               .append("WHERE A.FORMAT = 'common'")
               .append("ORDER BY A.NAME");

        Query query = builder.build();
        return sqlExecutor.queryForList(query, Ods703eTab2DialogDto.class);
    }
    
    @Override
    public List<Map<String,Object>> findOds371Details(String reourdeId, String barCode
            , String invoiceDateS, String invoiceDateE, String hsnNm, String townNm
            , String cardTypeNm, String cardCodeNm) {
        String sql = "SELECT CONVERT(varchar(100),(發票日期),111) as invoiceDate, 縣市 as hsnNm"
                +", 鄉鎮市區 as townNm, 載具別 as cardTypeNm"
                +", 載具名稱 as cardCodeNm, 電子發票張數 as invoiceCount"
                +", 電子發票金額 as invoiceAmount, 捐贈發票張數 as dntCount"
                +", 捐贈發票金額 as dntAmount, 中獎發票張數 as prizeCount"
                +", 中獎獎項金額 as prizeAmount "
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE USER_UNIFY_ID = :barCode"
                +" AND 發票日期 between :invoiceDateS and :invoiceDateE ";
        if(hsnNm != null && !hsnNm.equals("")) {
            sql = sql+" AND 縣市 = :hsnNm ";
        }
        if(townNm != null && !townNm.equals("")) {
            sql = sql+" AND 鄉鎮市區 = :townNm ";
        }
        if(cardTypeNm != null && !cardTypeNm.equals("")) {
            sql = sql+" AND 載具別 = :cardTypeNm ";
        }
        if(cardCodeNm != null && !cardCodeNm.equals("")) {
            sql = sql+" AND 載具名稱 = :cardCodeNm ";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("barCode", barCode);
        params.put("invoiceDateS", invoiceDateS);
        params.put("invoiceDateE", invoiceDateE);
        params.put("hsnNm", hsnNm);
        params.put("townNm", townNm);
        params.put("cardTypeNm", cardTypeNm);
        params.put("cardCodeNm", cardCodeNm);
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }
    
    @Override
    public List<Map<String,Object>> findOds372Details(String reourdeId, String invoiceYmS
            , String invoiceYmE, String hsnNm, String townNm
            , String cardTypeNm, String cardCodeNm, String busiChiNm, String ban) {
        String sql = "SELECT 發票年月 as invoiceYm,行業別 as busiChiNm ,縣市 as hsnNm"
                +", 鄉鎮市區 as townNm, 載具類別名稱 as cardTypeNm"
                +", 載具名稱 as cardCodeNm, 電子發票中獎張數 as prizeCount"
                +", 電子發票中獎金額 as prizeAmount, 電子發票捐贈張數 as dntCount"
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 發票年月  between :invoiceYmS and :invoiceYmE "
                +" AND 社福團體統編  = :ban ";
        if(hsnNm != null && !hsnNm.equals("")) {
            sql = sql+" AND 縣市 = :hsnNm ";
        }
        if(townNm != null && !townNm.equals("")) {
            sql = sql+" AND 鄉鎮市區 = :townNm ";
        }
        if(cardTypeNm != null && !cardTypeNm.equals("")) {
            sql = sql+" AND 載具類別名稱 = :cardTypeNm ";
        }
        if(cardCodeNm != null && !cardCodeNm.equals("")) {
            sql = sql+" AND 載具名稱 = :cardCodeNm ";
        }
        if(busiChiNm != null && !busiChiNm.equals("")) {
            sql = sql+" AND 行業別  = :busiChiNm ";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("invoiceYmS", invoiceYmS);
        params.put("invoiceYmE", invoiceYmE);
        params.put("hsnNm", hsnNm);
        params.put("townNm", townNm);
        params.put("cardTypeNm", cardTypeNm);
        params.put("cardCodeNm", cardCodeNm);
        params.put("busiChiNm", busiChiNm);
        params.put("ban", ban);
        sqlEscapeService.escapeMsSql(sql);
        Log.info("findOds372Details sql:" + sql.toString());
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }
    
    @Override
    public List<Map<String,Object>> findOds373Details(String reourdeId, String invoiceYmS
            , String invoiceYmE, String hsnNm, String busiChiNm, String cardTypeNm){
        String sql = "SELECT 發票年月 as invoiceYm ";
        sql = sql+" , 縣市 as hsnNm ";
        sql = sql+" , 行業別 as busiChiNm ";
        sql = sql +" , 載具類別名稱 as cardTypeNm "
                + ", round(sum(電子發票中獎張數)/count(1),0) as prizeCount "
                + ", round(sum(電子發票中獎金額)/count(1),0) as prizeAmount "
                + ", round(sum(電子發票捐贈張數)/count(1),0) as dntCount "
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 發票年月  between :invoiceYmS and :invoiceYmE ";
        if(cardTypeNm != null && !cardTypeNm.equals("")) {
            sql = sql+" AND 載具類別名稱 = :cardTypeNm ";
        }
        if(hsnNm != null && !hsnNm.equals("")) {
            sql = sql+" AND 縣市 = :hsnNm ";
        }
        if(busiChiNm != null && !busiChiNm.equals("")) {
            sql = sql+" AND 行業別 = :busiChiNm ";
        }
        sql += "Group by 發票年月, 縣市 , 行業別 , 載具類別名稱";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("invoiceYmS", invoiceYmS);
        params.put("invoiceYmE", invoiceYmE);
        params.put("hsnNm", hsnNm);
        params.put("busiChiNm", busiChiNm);
        params.put("cardTypeNm", cardTypeNm);
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }
    
    @Override
    public List<Map<String,Object>> findOds374Details(String reourdeId, String invoiceDateS
            , String invoiceDateE, String ban){
        String sql = "SELECT CONVERT(varchar(100),(a.發票日期),111) as invoiceDate "
                +", round(sum(a.電子發票B2B開立張數) / count(distinct a.賣方統一編號),0) as b2bCrtAvgCount "
                +", round(sum(a.電子發票B2B銷售額) / count(distinct a.賣方統一編號),0) as b2bCrtAvgAmount "
                +", round(sum(a.電子發票B2C開立張數) / count(distinct a.賣方統一編號),0) as b2cCrtAvgCount "
                +", round(sum(a.電子發票B2C銷售額)  / count(distinct a.賣方統一編號),0) as b2cCrtAvgAmount "
                +" From ODS_"+reourdeId.replace("-", "_")+" a, "+vatTaxRgstStus()+" b "
                +" WHERE a.發票日期  between :invoiceDateS and :invoiceDateE "
                +" AND (b.營業人統編 = :ban or b.總機構營業人統編= :ban)"
                +" AND a.賣方統一編號 = b.營業人統編"
                +" GROUP BY a.發票日期";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("invoiceDateS", invoiceDateS);
        params.put("invoiceDateE", invoiceDateE);
        params.put("ban", ban);
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }
    
    @Override
    public List<Map<String,Object>> findOds375Details(String reourdeId, String invoiceDateS
            , String invoiceDateE, String hsnNm, String townNm, String invType, String bscd2Nm){
        String sql = "SELECT CONVERT(varchar(100),(發票日期),111) as invoiceDate, 縣市名稱 as hsnNm "
                +", 鄉鎮名稱 as townNm"
                +", round(電子發票B2B開立張數  / B2B營業人家數,0) as b2bCrtAvgCount"
                +", round(電子發票B2B銷售額  / B2B營業人家數,0) as b2bCrtAvgAmount"
                +", round(電子發票B2C開立張數  / B2C營業人家數,0) as b2cCrtAvgCount"
                +", round(電子發票B2C銷售額  / B2C營業人家數,0) as b2cCrtAvgAmount"
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 發票日期  between :invoiceDateS and :invoiceDateE "
                +" and 營業項目名稱 = :bscd2Nm ";
        if(hsnNm != null && !hsnNm.equals("")) {
            sql = sql+" AND 縣市名稱 = :hsnNm ";
        }
        if(townNm != null && !townNm.equals("")) {
            sql = sql+" AND 鄉鎮名稱 = :townNm ";
        }
        if ( StringUtils.equals(invType, "all") ) {
            sql += " and 營業人家數 > 5 ";
        } else if ( StringUtils.equals(invType, "B2B") ) {
            sql += " and B2B營業人家數 > 5 ";
        } else if ( StringUtils.equals(invType, "B2C") ) {
            sql += " and B2C營業人家數 > 5 ";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("invoiceDateS", invoiceDateS);
        params.put("invoiceDateE", invoiceDateE);
        params.put("bscd2Nm", bscd2Nm);
        params.put("hsnNm", hsnNm);
        params.put("townNm", townNm);
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }


    @Override
    public List<Map<String, Object>> findOds376Details(String reourdeId, String ban, String dataYrS,
            String dataYrE) {
        String sql = "SELECT CONVERT(varchar(100),(資料年度),111) as dataYr, "
                +" 營業淨利 as busiNetPf, "
                +" 營業收入淨額 as busiRvnuNet "
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 營業人統編 = :ban "
                +" and 資料年度  >= :dataYrS and 資料年度 <= :dataYrE ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ban", ban);
        params.put("dataYrS", StringUtils.substring(dataYrS, 0, 4)+"0101");
        params.put("dataYrE", StringUtils.substring(dataYrE, 0, 4)+"0101");
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }


    @Override
    public List<Map<String, Object>> findOds377Details(String reourdeId, String dataYrS,
            String dataYrE, String hsnNm, String townNm, String bscd2Nm) {
        String sql = "SELECT CONVERT(varchar(100),(資料年度),111) as dataYr, "
                +" 縣市名稱 as hsnNm, "
                +" 鄉鎮名稱 as townNm, "
                +" 營業項目名稱 as bscd2Nm, "
                +" round(營業淨利 / 營業人家數,0)  as busiNetPf, "
                +" round(營業收入淨額 / 營業人家數,0) as busiRvnuNet "
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 資料年度  between :dataYrS and :dataYrE "
                +" and 營業項目名稱 = :bscd2Nm "
                +" and 營業人家數 > 5 ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dataYrS", StringUtils.substring(dataYrS, 0, 4)+"0101");
        params.put("dataYrE", StringUtils.substring(dataYrE, 0, 4)+"0101");
        params.put("bscd2Nm", bscd2Nm);
        if ( StringUtils.isNotBlank(hsnNm) ) {
            sql += " and 縣市名稱 = :hsnNm ";
            params.put("hsnNm", hsnNm);
        }
        if ( StringUtils.isNotBlank(townNm) ) {
            sql += " and 鄉鎮名稱 = :townNm ";
            params.put("townNm", townNm);
        }
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }


    @Override
    public List<Map<String, Object>> findOds378Details(String reourdeId, String ban, String blYmS, String blYmE) {
        String sql = "SELECT CONVERT(varchar(100),(a.所屬年月),111) as blYm, "
                +" round(sum(a.銷項總計金額) / count(distinct a.營業人統編),0) as saleAvgAmt, "
                +" round(sum(a.發票申購總計張數) / count(distinct a.營業人統編),0) as avgCount "
                +" From ODS_"+reourdeId.replace("-", "_")+" a, "+vatTaxRgstStus()+" b "
                +" WHERE a.所屬年月  between :blYmS and :blYmE "
                +" AND (b.營業人統編 = :ban or b.總機構營業人統編= :ban) "
                +" AND a.營業人統編 = b.營業人統編"
                +" GROUP BY a.所屬年月";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ban",ban);
        params.put("blYmS", StringUtils.substring(blYmS, 0, 6)+"01");
        params.put("blYmE", StringUtils.substring(blYmE, 0, 6)+"01");
        System.out.println("#1993 ban="+ban);
        System.out.println("#1994 sql="+sql);
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }


    @Override
    public List<Map<String, Object>> findOds379Details(String reourdeId, String blYmS,
            String blYmE, String hsnNm, String townNm, String bscd2Nm) {
        String sql = "SELECT CONVERT(varchar(100),(所屬年月),111) as blYm, "
                +" 縣市名稱 as hsnNm, "
                +" 鄉鎮名稱 as townNm, "
                +" 營業項目名稱 as bscd2Nm, "
                +" round(銷項總計金額 / 營業人家數,0) as saleAvgAmt, "
                +" round(發票申購總計張數 / 營業人家數,0) as avgCount "
                +" From ODS_"+reourdeId.replace("-", "_")
                +" WHERE 所屬年月  between :blYmS and :blYmE "
                +" AND 營業項目名稱 = :bscd2Nm "
                +" AND 營業人家數 > 5 ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("blYmS", StringUtils.substring(blYmS, 0, 6)+"01");
        params.put("blYmE", StringUtils.substring(blYmE, 0, 6)+"01");
        params.put("bscd2Nm", bscd2Nm);
        if ( StringUtils.isNotBlank(hsnNm) ) {
            sql += " and 縣市名稱 = :hsnNm ";
            params.put("hsnNm", hsnNm);
        }
        if ( StringUtils.isNotBlank(townNm) ) {
            sql += " and 鄉鎮名稱 = :townNm ";
            params.put("townNm", townNm);
        }
        sqlEscapeService.escapeMsSql(sql);
        List<Map<String,Object>> restMap = odsJdbcTemplate.queryForList(sql, params);
        return restMap;
    }
    
    /**DAN送ODS營業稅營業人稅籍檔狀態
     * @return String table name
     */
    private String vatTaxRgstStus(){
        return "ODS_" + getDbName("O1502#D01@ALL$01").replaceAll("-", "_");
    }

    /** 依據viewId取得 resource ID
     * @param viewId viewId
     * @return String resource ID
     */
    public String getDbName(String viewId) {
        BooleanExpression criteria = QOdsResource.odsResource.viewId.eq(viewId);
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if (odsResource != null) {
            resourceId = odsResource.getId();
        }
        return resourceId;
    }
}
