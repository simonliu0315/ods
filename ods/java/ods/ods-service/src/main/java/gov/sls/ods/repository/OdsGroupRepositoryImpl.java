package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.ods.dto.Ods301eDto;
import gov.sls.ods.dto.Ods704eFormBean;
import gov.sls.ods.dto.Ods704eGrid1Dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;

public class OdsGroupRepositoryImpl implements OdsGroupRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OdsGroup> findGroup(String groupName, List<String> tagList,
            List<String> fileExtList, List<String> identityList, int orderByType, String userType) {

        Query.Builder builder = Query.builder();
        int firstConditionFlag = 0;// 用來判斷是否要加上AND
        builder.expandIterableParameters(true)
                .append("SELECT G.*")
                .append("FROM   ODS_GROUP G");

        // 1.2 關鍵字為條件查詢主題群組檔。
        if (StringUtils.isNotEmpty(groupName)) {
            builder.append("WHERE ");
            builder.appendWhen(StringUtils.isNotEmpty(groupName), "G.name like :groupName", "%"
                    + groupName + "%");
            firstConditionFlag = 1;
        }
        
        // 主題版本檔至少需要有一筆為publish才可
        if (1 == firstConditionFlag) {
            builder.append("AND");
        } else {
            builder.append("WHERE ");
            firstConditionFlag = 1;
        }
        builder.append("EXISTS(SELECT *")
                .append("      FROM   ODS_GROUP_PACKAGE GP,")
                .append("             ODS_PACKAGE_VERSION PV")
                .append("      WHERE  G.ID = GP.GROUP_ID")
                .append("         AND GP.PACKAGE_ID = PV.PACKAGE_ID")
                .append("         AND PV.IS_PUBLISHED = 'true'")
                .append(")");

        // 1.3 分類標籤為條件查詢主題群組檔
        if (!CollectionUtils.isEmpty(tagList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.append("EXISTS(SELECT *")
                    .append("      FROM   ODS_GROUP_PACKAGE GP")
                    .append("      WHERE  EXISTS (SELECT *")
                    .append("                     FROM   ODS_PACKAGE_TAGS T")
                    .append("                     WHERE  GP.PACKAGE_ID = T.PACKAGE_ID")
                    .appendWhen(!CollectionUtils.isEmpty(tagList), "AND T.TAG_NAME in (:tagList)",
                            tagList)
                    .append("                        AND G.ID = GP.GROUP_ID))");
        }
        // 1.4 檔案格式為條件查詢主題群組檔。
        if (!CollectionUtils.isEmpty(fileExtList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.append("EXISTS(SELECT *")
                    .append("      FROM  ODS_GROUP_PACKAGE GP")
                    .append("      WHERE EXISTS(SELECT *")
                    .append("                   FROM  ODS_PACKAGE_RESOURCE PR")
                    .append("                   WHERE EXISTS(SELECT *")
                    .append("                                FROM   ODS_RESOURCE R")
                    .append("                                WHERE  PR.RESOURCE_ID = R.ID")
                    .append("                                   AND GP.PACKAGE_ID = PR.PACKAGE_ID")
                    .append("                                   AND G.ID = GP.GROUP_ID")
                    .appendWhen(!CollectionUtils.isEmpty(fileExtList),
                            "AND R.FORMAT in (:fileExtList)", fileExtList)
                    .append("                           )))");
        }
        // 1.5 分眾推廣群為條件查詢主題群組檔。
        if (!CollectionUtils.isEmpty(identityList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.append("EXISTS(SELECT *")
                    .append("      FROM ODS_IDENTITY_GROUP IG")
                    .append("      WHERE EXISTS (SELECT *")
                    .append("                    FROM  ODS_IDENTITY I")
                    .append("                    WHERE IG.IDENTITY_ID = I.ID")
                    .appendWhen(!CollectionUtils.isEmpty(identityList),
                            "AND I.NAME in (:identityList)", identityList)
                    .append("                      AND G.ID = IG.GROUP_ID))");
        }

        //依所查詢之主題群組結果之分眾資料與使用者的分眾 進行排序
        builder.append("ORDER  BY (SELECT IDT_VW_PRIORITY.PRI")
                .append("                FROM   (SELECT G.id,")
                .append("                               IG.VIEW_PRIORITY PRI")
                .append("                        FROM   ods_group G LEFT JOIN ods_identity_group IG")
                .append("                               ON (G.ID = IG.GROUP_ID and ")
                .append("                                     IG.IDENTITY_ID = (Select ID from ods_identity WHERE NAME = ( CASE")
                .append("                                                                                                      WHEN 'N' = :userType THEN '消費者'", userType)
                .append("                                                                                                      WHEN 'B' = :userType THEN '營業人'", userType)
                .append("                                                                                                      WHEN 'A' = :userType THEN '專業代理人'", userType)
                .append("                                                                                                      WHEN 'O' = :userType THEN '外部機關'", userType)
                .append("                                                                                                      WHEN 'S' = :userType THEN '受捐贈機關或團體'", userType)
                .append("                                                                                                      WHEN 'G' = :userType THEN '政府機關'", userType)
                .append("                                                                                                  END) ")                                
                .append("                                                      )")
                .append("                                   )")
                .append("                         ) IDT_VW_PRIORITY ")
                .append("                WHERE  G.id = IDT_VW_PRIORITY.id) DESC");
        
        if (0 == orderByType) {
            // 依所查詢之主題群組結果各別的主題明細檔之 更新時間 進行排序
            builder.append(", (SELECT PV_MAX_CREATED.created")
                    .append("           FROM   (SELECT G.id,")
                    .append("                          Max(PV.created) CREATED")
                    .append("                   FROM   ods_group G,")
                    .append("                          ods_group_package GP,")
                    .append("                          ods_package P,")
                    .append("                          ods_package_version PV")
                    .append("                   WHERE  G.id = GP.group_id")
                    .append("                          AND GP.package_id = P.id")
                    .append("                          AND P.id = PV.package_id")
                    .append("                   GROUP  BY G.id) PV_MAX_CREATED")
                    .append("           WHERE  G.id = PV_MAX_CREATED.id) DESC");
        } else if (1 == orderByType) {
            // 依所查詢之主題群組結果各別的主題群組與主題關聯檔與使用者瀏覽主題版本計數檔的計數加總結果進行排序
            builder.append(", (SELECT PV_MAX_CLICK.CLICK")
                    .append("           FROM   (SELECT G.ID,")
                    .append("                          COUNT(UPVC.PACKAGE_ID) CLICK")
                    .append("                   FROM   ODS_GROUP G,")
                    .append("                          ODS_GROUP_PACKAGE GP,")
                    .append("                          ODS_PACKAGE P,")
                    .append("                          ODS_PACKAGE_VERSION PV")
                    .append("                          LEFT JOIN")
                    .append("                          ODS_USER_PACKAGE_VERSION_CLICK UPVC")
                    .append("                                 ON ( PV.PACKAGE_ID = UPVC.PACKAGE_ID")
                    .append("                                      AND PV.VER = UPVC.PACKAGE_VER )")
                    .append("                   WHERE  G.ID = GP.GROUP_ID")
                    .append("                          AND GP.PACKAGE_ID = P.ID")
                    .append("                          AND P.ID = PV.PACKAGE_ID")
                    .append("                   GROUP  BY G.ID) PV_MAX_CLICK")
                    .append("           WHERE  G.ID = PV_MAX_CLICK.ID) DESC");
        }
        Query query = builder.build();
        return executor.queryForList(query, OdsGroup.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ods301eDto> findPackagesByGroupId(String gorupId, List<String> idList) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT P.id, P.IMAGE_URL imageUrl, P.NAME name, P.DESCRIPTION, P.TYPE, P.CODE,")
               .append("(SELECT MAX(VER) FROM ODS_PACKAGE_VERSION PV WHERE PV.PACKAGE_ID = P.ID AND PV.IS_PUBLISHED = 'true') latestVer")
               .append("FROM ODS_GROUP_PACKAGE GP, ODS_PACKAGE P")
               .appendWhen(true, "WHERE GP.GROUP_ID =:groupId", gorupId)
               .append("AND GP.PACKAGE_ID = P.ID");
        // 依使用者資料權查詢主題檔。
        if (!CollectionUtils.isEmpty(idList)) {
            builder.appendWhen(!CollectionUtils.isEmpty(idList), "AND P.ID in (:idList)",
                    idList);
        }
        // 主題版本檔至少需要有一筆為publish才可
        builder.append("AND EXISTS(SELECT *")
                .append("      FROM ODS_PACKAGE_VERSION PV")
                .append("      WHERE P.ID = PV.PACKAGE_ID")
                .append("      AND PV.IS_PUBLISHED = 'true'")
                .append(")");
        Query query = builder.build();
        return executor.queryForList(query, Ods301eDto.class);
    }
    
    @Override
    public List<OdsGroup> findGroupByName(String name, String description){
        String sql =
                "SELECT "
              + " name , description , id , image_url "
              + "FROM   ODS_GROUP G ";
                if (!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(description)) {
                    sql = sql + "WHERE ";
                }
                if (!Strings.isNullOrEmpty(name)) {
                    sql = sql + "G.NAME like :name ";
                }
                if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(description)) {
                    sql = sql + "AND ";
                }
                if (!Strings.isNullOrEmpty(description)) {
                    sql = sql + "G.DESCRIPTION like :description ";
                }
              
              sql = sql + "ORDER  BY G.CREATED DESC ";
        

        Map<String, Object> params = new HashMap<String, Object>();
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", "%" +  name  + "%");
        }
        if (!Strings.isNullOrEmpty(description)) {
            params.put("description", "%" +  description  + "%");
        }
        
        List<OdsGroup> groups = executor.queryForList(sql, params, OdsGroup.class);

        
        return groups;
    }
    
    

    @Override
    public List<Ods704eGrid1Dto> findPackageByGroupId(String groupId) {
        
        String sql =
                "SELECT "
              + " b.name as PKG_GUP_NAME, b.description as PKG_GUP_DESCRIPTION, "
              + "b.id as PKG_GUP_PACKAGE_ID, b.image_url as PKG_GUP_IMAGE_URL "
              + "FROM   ODS_GROUP_PACKAGE a, ODS_PACKAGE b "
              + "WHERE  a.GROUP_ID = :groupId "
              + "   AND    a.PACKAGE_ID = b.ID "
              + "ORDER  BY b.NAME ";
        

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupId", groupId);

        
        List<Ods704eGrid1Dto> packages = executor.queryForList(sql, params, Ods704eGrid1Dto.class);
                
       return packages;
    }
    
    
    
    @Override
    public String createGroupByNameDesc(String name, String description, String fileExtension) throws Exception {

        try {
        
            //String sql = "select * from ODS_RESOURCE";
            String sql =
                      "INSERT ODS_GROUP "
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
            params.put("name", name);
            params.put("description", description);
            params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("createUserId", UserHolder.getUser().getId());
            params.put("updateUserId", UserHolder.getUser().getId());
            
            
            executor.insert(sql, params);
            
            

            
            String sql2 =
                    "SELECT G.ID "
                  + "FROM   ODS_GROUP G "
                  + "WHERE  G.NAME = :name "
                  + "ORDER BY G.CREATED DESC ";
            
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("name", name);
            
            List<OdsGroup> group = executor.queryForList(sql2, params2, OdsGroup.class);
            String groupId = group.get(0).getId();
            
            
            
            String sql3 =
                    "UPDATE ODS_GROUP "
                    + "SET IMAGE_URL = :imageUrl "
                  + "WHERE  ID = :groupId ";
            
            Map<String, Object> params3 = new HashMap<String, Object>();
            params3.put("imageUrl", groupId + ".png");
            params3.put("groupId", groupId);
            
            executor.update(sql3, params3);
            
            
            return groupId;
            
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
    }  
    
    
    
    public void updateGroupByNameDescIdtIdLstPkgIdLst(Ods704eFormBean ods704eFormBean) throws Exception{
        
        try{

            String sql1 =
                      "UPDATE ODS_GROUP "
                    + "SET     NAME = :name, "
                    + "        DESCRIPTION = :description, "
                    + "        UPDATED = :updated, "
                    + "        UPDATE_USER_ID = :updateUserId ";
            
            if(StringUtils.isNotEmpty(ods704eFormBean.getImageUrl())){
                sql1 = sql1 + "        ,IMAGE_URL = :imageUrl ";
            }
            
            sql1 = sql1 + "WHERE   ID = :id ";
    
            
            Map<String, Object> params1 = new HashMap<String, Object>();
            params1.put("name", ods704eFormBean.getName());
            params1.put("description", ods704eFormBean.getDescription());
            params1.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params1.put("updateUserId", UserHolder.getUser().getId());
            
            if(StringUtils.isNotEmpty(ods704eFormBean.getImageUrl())){
                params1.put("imageUrl", ods704eFormBean.getImageUrl());
            }
            
            params1.put("id", ods704eFormBean.getId());
            
            executor.update(sql1, params1);
            
            

            String sql2 =
                    "DELETE ODS_IDENTITY_GROUP "
                    + "WHERE  GROUP_ID = :id " ;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("id", ods704eFormBean.getId());
             
            executor.delete(sql2, params2);
            
            
            String chkIdentityIdList = ods704eFormBean.getChkIdentityIdList();
            
            if (!Strings.isNullOrEmpty(chkIdentityIdList))
            {
                List<String> identityIdListAry = new ArrayList<String>(Arrays.asList(chkIdentityIdList.split(",")));

                for (String identityId : identityIdListAry){
                    
                    String sql3 =
                            "INSERT ODS_IDENTITY_GROUP "
                          + "       (GROUP_ID, "
                          + "        IDENTITY_ID, "
                          + "        VIEW_PRIORITY, "
                          + "        CREATED, "
                          + "        UPDATED, "
                          + "        CREATE_USER_ID, "
                          + "        UPDATE_USER_ID) "
                          + "VALUES (:groupId, "
                          + "        :identityId, "
                          + "        1, "			//view_priority目前皆設置成1
                          + "        :created, "
                          + "        :updated, "
                          + "        :createUserId, "
                          + "        :updateUserId) ";

                  
                    Map<String, Object> params3 = new HashMap<String, Object>();
                    params3.put("groupId", ods704eFormBean.getId());
                    params3.put("identityId", identityId);
                    params3.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params3.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params3.put("createUserId", UserHolder.getUser().getId());
                    params3.put("updateUserId", UserHolder.getUser().getId());
                      
                      
                    executor.insert(sql3, params3);
                    
                }
            }
           
            
            String sql4 =
                    "DELETE ODS_GROUP_PACKAGE "
                    + "WHERE  GROUP_ID = :id " ;
            Map<String, Object> params4 = new HashMap<String, Object>();
            params4.put("id", ods704eFormBean.getId());
             
            executor.delete(sql4, params4);
            
            
            String selPackageIdList = ods704eFormBean.getSelPackageIdList();
            
            if (!Strings.isNullOrEmpty(selPackageIdList))
            {
                
                List<String> packageIdListAry = new ArrayList<String>(Arrays.asList(selPackageIdList.split(",")));
                
                for (String packageId : packageIdListAry){
                    
                    String sql =
                            "INSERT ODS_GROUP_PACKAGE "
                          + "       (GROUP_ID, "
                          + "        PACKAGE_ID, "
                          + "        CREATED, "
                          + "        UPDATED, "
                          + "        CREATE_USER_ID, "
                          + "        UPDATE_USER_ID) "
                          + "VALUES (:groupId, "
                          + "        :packageId, "
                          + "        :created, "
                          + "        :updated, "
                          + "        :createUserId, "
                          + "        :updateUserId) ";
    
                  
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("groupId", ods704eFormBean.getId());
                    params.put("packageId", packageId);
                    params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
                    params.put("createUserId", UserHolder.getUser().getId());
                    params.put("updateUserId", UserHolder.getUser().getId());
                      
                      
                    executor.insert(sql, params);
                }
            }

        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        
    }
    
    
    public void deleteGroupByGroupId(Ods704eFormBean ods704eFormBean) throws Exception{
        
        try{

            String sql1 =
                      "DELETE ODS_GROUP_PACKAGE "
                    + "WHERE   GROUP_ID = :id ";
    
            
            Map<String, Object> params1 = new HashMap<String, Object>();
            params1.put("id", ods704eFormBean.getId());
            
            executor.delete(sql1, params1);
           

            String sql2 =
                    "DELETE ODS_IDENTITY_GROUP "
                    + "WHERE  GROUP_ID = :id " ;
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("id", ods704eFormBean.getId());
             
            executor.delete(sql2, params2);
            
            
            String sql3 =
                    "DELETE ODS_GROUP "
                    + "WHERE  ID = :id " ;
            Map<String, Object> params3 = new HashMap<String, Object>();
            params3.put("id", ods704eFormBean.getId());
             
            executor.delete(sql3, params3);
            
        
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        
    }
    
}
