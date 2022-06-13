package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.ods.dto.Ods702eDto;
import gov.sls.ods.dto.Ods702eGrid1Dto;
import gov.sls.ods.dto.Ods702eGrid2Dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;
//import gov.sls.fms.dto.Fms421rDto;

public class OdsCategoryRepositoryImpl implements OdsCategoryRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(OdsCategoryRepositoryImpl.class);
    @Autowired
    private SqlExecutor sqlExecutor;
    
    @Override
    public List<OdsCategory> findUnSelCategoryByResId(String resId) {
        //String sql = "select * from ODS_RESOURCE";
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT * "
                + "FROM   [ODS_CATEGORY] CA "
                + "WHERE  NOT EXISTS (SELECT CATEGORY_ID "
                + "                   FROM   [ODS_RESOURCE_CATEGORY] "
                + "                   WHERE  CA.ID = CATEGORY_ID "
                + "                          AND RESOURCE_ID  ";
        if("NULL".equals(resId))
        {
            sql += "                             IS NULL) ";
        } else {
            sql += "                             = :resId) ";
        }
        
        params.put("resId", resId);
        
        List<OdsCategory> categories = sqlExecutor.queryForList(sql, params, OdsCategory.class);
        
       for (OdsCategory odsCategory: categories)
        {
            logger.debug("unSelCategoryByResId:" + ToStringBuilder.reflectionToString(odsCategory));
        }
        
        return categories;
    }
    
    
    @Override
    public List<OdsCategory> findSelCategoryByResId(String resId) {
        //String sql = "select * from ODS_RESOURCE";
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT * "
                + "FROM   [ODS_CATEGORY] CA "
                + "WHERE  EXISTS (SELECT CATEGORY_ID "
                + "                   FROM   [ODS_RESOURCE_CATEGORY] "
                + "                   WHERE  CA.ID = CATEGORY_ID "
                + "                          AND RESOURCE_ID ";
        if("NULL".equals(resId))
        {
            sql += "                             IS NULL) ";
        } else {
            sql += "                             = :resId) ";
        }
        
        params.put("resId", resId);
        
        List<OdsCategory> categories = sqlExecutor.queryForList(sql, params, OdsCategory.class);
                
        for (OdsCategory odsCategory: categories)
        {
            logger.debug("selCategoryByResId:" + ToStringBuilder.reflectionToString(odsCategory));
        }
        
        return categories;
    }
    
    
    @Override
    public List<OdsCategory> findUnSelCategoryByCategoryId(String categoryIdList) {
        //String sql = "select * from ODS_RESOURCE";
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT * "
                + "FROM   [ODS_CATEGORY] CA ";

        if("NULL".equals(categoryIdList))
        {
        } else {
            if (",".equals(categoryIdList.substring(categoryIdList.length() - 1, categoryIdList.length()) ))
            {
                categoryIdList = categoryIdList.substring(0, categoryIdList.length() - 1);
            }
            List<String> categoryIdListAry = new ArrayList<String>(Arrays.asList(categoryIdList.split(",")));
            
            sql += "WHERE  CA.ID NOT IN ( :categoryId )  ";
            
            params.put("categoryId", categoryIdListAry);
        }
        
        
        
        List<OdsCategory> categories = sqlExecutor.queryForList(sql, params, OdsCategory.class);
        
       for (OdsCategory odsCategory: categories)
        {
            logger.debug("unSelCategoryByCategoryId:" + ToStringBuilder.reflectionToString(odsCategory));
        }
        
        return categories;
    }
    
    
    @Override
    public List<OdsCategory> findSelCategoryByCategoryId(String categoryIdList) {
        //String sql = "select * from ODS_RESOURCE";
        Map<String, Object> params = new HashMap<String, Object>();
        
        if("NULL".equals(categoryIdList))
        {
            return new ArrayList<OdsCategory>();
        } 

        
        if (",".equals(categoryIdList.substring(categoryIdList.length() - 1, categoryIdList.length()) ))
        {
            categoryIdList = categoryIdList.substring(0, categoryIdList.length() - 1);
        }
        
        //List<String> categoryIdListAry = new ArrayList<String>();
        List<String> categoryIdListAry = new ArrayList<String>(Arrays.asList(categoryIdList.split(",")));
        //categoryIdListAry.add("CF436682-14FF-49D2-BAB9-65F878A1102D");
        //categoryIdListAry.add("D24AEB7F-F592-487B-B7E8-9C9AF254705D");
        /*String sql =
                "SELECT * "
                + "FROM   [ODS_CATEGORY] CA "
                + "WHERE CA.ID IN ( :categoryId ) ";*/
        
        StringBuffer sql = new StringBuffer();
        sql.append("select * FROM   [ODS_CATEGORY] CA WHERE CA.ID IN ( :categoryId ) ");
        
        //logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX:" + categoryIdList);
        
        params.put("categoryId", categoryIdListAry);
        
        //List<OdsCategory> categories = sqlExecutor.queryForList(sql, params, OdsCategory.class);
        List<OdsCategory> categories = sqlExecutor.queryForList(sql.toString(), params, OdsCategory.class);
                
        for (OdsCategory odsCategory: categories)
        {
            logger.debug("selCategoryByCategoryId:" + ToStringBuilder.reflectionToString(odsCategory));
        }
        
        return categories;
    }
    
    
    
    @Override
    public List<Ods702eGrid2Dto> findCategoryByName(String name, String description) {
        
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY C.CREATED DESC ) AS CATEGORY_ROW_COUNT, "
              + " name as CATEGORY_NAME, description as CATEGORY_DESCRIPTION, id as CATEGORY_ID "
              + "FROM   ODS_CATEGORY C ";
              if (!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(description)) {
                  sql = sql + "WHERE ";
              }
              if (!Strings.isNullOrEmpty(name)) {
                  sql = sql + "C.NAME like :name ";
              }
              if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(description)) {
                  sql = sql + "AND ";
              }
              if (!Strings.isNullOrEmpty(description)) {
                  sql = sql + "C.DESCRIPTION like :description ";
              }
              
              
              sql = sql + "ORDER  BY C.CREATED DESC ";
        

        Map<String, Object> params = new HashMap<String, Object>();
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", "%" +  name  + "%");
        }
        if (!Strings.isNullOrEmpty(description)) {
            params.put("description", "%" +  description  + "%");
        }
        
        List<Ods702eGrid2Dto> categories = sqlExecutor.queryForList(sql, params, Ods702eGrid2Dto.class);
                
        for (Ods702eGrid2Dto category: categories)
        {
            logger.debug("findCategoryByName:" + ToStringBuilder.reflectionToString(category));
        }
        
        return categories;
    }

    
    @Override
    public List<Ods702eGrid1Dto> findResourceByCategoryId(String categoryId) {
        
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY b.NAME ) AS RES_CAT_ROW_COUNT, "
              + " b.name as RES_CAT_NAME, b.description as RES_CAT_DESCRIPTION, b.id as RES_CAT_RESOURCE_ID, "
              + " b.workbook_id as RES_CAT_WORKBOOK_ID, b.format as RES_CAT_FORMAT "
              + "FROM   ODS_RESOURCE_CATEGORY a, ODS_RESOURCE b "
              + "WHERE  a.CATEGORY_ID = :categoryId "
              + "   AND    a.RESOURCE_ID = b.ID "
              + "ORDER  BY b.NAME ";
        

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("categoryId", categoryId);

        
        List<Ods702eGrid1Dto> resources = sqlExecutor.queryForList(sql, params, Ods702eGrid1Dto.class);
                
        for (Ods702eGrid1Dto resource: resources)
        {
            logger.debug("findResourceByCategoryId:" + ToStringBuilder.reflectionToString(resource));
        }
        
        return resources;
    }
    
    
    
    @Override
    public List<Ods702eGrid1Dto> findUnCategoryResourceByNameSelRes(String name, String selectedResList) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY R.NAME ) AS CHOSE_RES_ROW_COUNT, "
              + " R.NAME as RES_CAT_NAME, R.DESCRIPTION as RES_CAT_DESCRIPTION, "
              + " R.ID as RES_CAT_RESOURCE_ID, R.WORKBOOK_ID as RES_CAT_WORKBOOK_ID, "
              + " R.FORMAT as RES_CAT_FORMAT, "
              + " V.URL as RES_CAT_ICON_URL "
              + "FROM   ODS_RESOURCE R, ODS_RESOURCE_VERSION V "
              + "WHERE  V.RESOURCE_ID = R.ID "
              + "AND V.VER = '0' "
              + "AND R.FORMAT <> 'common' ";
              if (!Strings.isNullOrEmpty(name)) {
                  sql = sql + "AND  R.NAME like :name ";
                  params.put("name", "%" +  name  + "%");
              }
              if ( !selectedResList.isEmpty() && ",".equals(selectedResList.substring(selectedResList.length() - 1, selectedResList.length()) ))
              {
                  selectedResList = selectedResList.substring(0, selectedResList.length() - 1);
                  List<String> selectedResListAry = new ArrayList<String>(Arrays.asList(selectedResList.split(",")));
                  
                  sql += "AND  R.ID NOT IN ( :selectedResIdAry )  ";
                  
                  params.put("selectedResIdAry", selectedResListAry);
              }
              
              sql = sql + "ORDER  BY R.NAME ";
        
        logger.debug("selectedResListselectedResList:" + ToStringBuilder.reflectionToString(selectedResList));
        List<Ods702eGrid1Dto> resources = sqlExecutor.queryForList(sql, params, Ods702eGrid1Dto.class);
                
        for (Ods702eGrid1Dto resource: resources)
        {
            logger.debug("findUnCategoryResourceByName:" + ToStringBuilder.reflectionToString(resource));
        }
        
        return resources;
        
    }
     

    
    @Override
    public String createCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception {

        try {
        
            //String sql = "select * from ODS_RESOURCE";
            String sql =
                      "INSERT ODS_CATEGORY "
                    + "       (ID, "
                    + "        NAME, "
                    + "        DESCRIPTION, "
                    + "        CREATED, "
                    + "        UPDATED, "
                    + "        CREATE_USER_ID, "
                    + "        UPDATE_USER_ID) "
                    + "VALUES (DEFAULT, "
                    + "        :name, "
                    + "        :description, "
                    + "        :created, "
                    + "        :updated, "
                    + "        :createUserId, "
                    + "        :updateUserId) ";
    
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", ods702eDto.getCategoryName());
            params.put("description", ods702eDto.getCategoryDescription());
            params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("createUserId", UserHolder.getUser().getId());
            params.put("updateUserId", UserHolder.getUser().getId());
            
            
            sqlExecutor.insert(sql, params);
            
            
    
            String sql2 =
                    "SELECT C.ID "
                  + "FROM   ODS_CATEGORY C "
                  + "WHERE  C.NAME = :name "
                  + "ORDER BY C.CREATED DESC ";
            
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("name", ods702eDto.getCategoryName());
            
            List<OdsCategory> category = sqlExecutor.queryForList(sql2, params2, OdsCategory.class);
            String categoryId = category.get(0).getId();
            
            logger.debug("categoryId:" + ToStringBuilder.reflectionToString(categoryId));
            
            
            List<Ods702eGrid1Dto> ods702eGrid1Dtos = ods702eDto.getGrid1();
            
            for (Ods702eGrid1Dto ods702eGrid1Dto: ods702eGrid1Dtos){
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
                params3.put("resId", ods702eGrid1Dto.getResCatResourceId());
                params3.put("categoryId", categoryId);
                params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params3.put("createUserId", UserHolder.getUser().getId());
                params3.put("updateUserId", UserHolder.getUser().getId());
            
                sqlExecutor.insert(sql3, params3);
                
     
            }
            
            return categoryId;
        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
    }  
    
    
    
    @Override
    public void updateCategoryByNameDescGrid1(Ods702eDto ods702eDto) throws Exception {
        try{

            String sql1 =
                      "UPDATE ODS_CATEGORY "
                    + "SET     NAME = :name, "
                    + "        DESCRIPTION = :description, "
                    + "        UPDATED = :updated, "
                    + "        UPDATE_USER_ID = :updateUserId "
                    + "WHERE   ID = :id ";
    
            
            Map<String, Object> params1 = new HashMap<String, Object>();
            params1.put("name", ods702eDto.getCategoryName());
            params1.put("description", ods702eDto.getCategoryDescription());
            params1.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params1.put("updateUserId", UserHolder.getUser().getId());
            params1.put("id", ods702eDto.getCategoryId());
            
            sqlExecutor.update(sql1, params1);
            
            

            String sql2 =
                    "DELETE ODS_RESOURCE_CATEGORY "
                    + "WHERE  CATEGORY_ID = :categoryId " ;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("categoryId", ods702eDto.getCategoryId());
             
            sqlExecutor.delete(sql2, params2);
            
            
            
            List<Ods702eGrid1Dto> ods702eGrid1Dtos = ods702eDto.getGrid1();
            
            for (Ods702eGrid1Dto ods702eGrid1Dto: ods702eGrid1Dtos){
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
                params3.put("resId", ods702eGrid1Dto.getResCatResourceId());
                params3.put("categoryId", ods702eDto.getCategoryId());
                params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                params3.put("createUserId", UserHolder.getUser().getId());
                params3.put("updateUserId", UserHolder.getUser().getId());
            
                sqlExecutor.insert(sql3, params3);
            }
        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        
    }  
    
    
    
    @Override
    public void deleteCategoryByCategoryId(Ods702eDto ods702eDto) throws Exception {
        
        try {

            String sql1 =
                    "DELETE ODS_RESOURCE_CATEGORY "
                    + "WHERE  CATEGORY_ID = :categoryId " ;
            Map<String, Object> params1 = new HashMap<String, Object>();
            params1.put("categoryId", ods702eDto.getCategoryId());
             
            sqlExecutor.delete(sql1, params1);
            
            

            String sql2 =
                    "DELETE ODS_CATEGORY "
                    + "WHERE  ID = :categoryId " ;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("categoryId", ods702eDto.getCategoryId());
             
            sqlExecutor.delete(sql2, params2);
            
         } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        
    }  
}
