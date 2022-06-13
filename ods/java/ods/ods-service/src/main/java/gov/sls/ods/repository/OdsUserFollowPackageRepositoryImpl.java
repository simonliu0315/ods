package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.dto.Ods352eDataDto;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.service.SqlEscapeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsUserFollowPackageRepositoryImpl implements OdsUserFollowPackageRepositoryCustom {

    @Autowired
    private SqlExecutor executor;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    

    @Override
    public List<OdsUserFollowPackage> getResourceCriteriaInPackageResource(String packageId,
            int ver, String userId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT *")
                .append("FROM ODS_USER_FOLLOW_PACKAGE UFP")
                .append("WHERE EXISTS ( SELECT *")
                .append("FROM ODS_RESOURCE_CRITERIA RC")
                .append("WHERE EXISTS ( SELECT RESOURCE_ID")
                .append("FROM ODS_PACKAGE_RESOURCE PR")
                .append("WHERE PR.RESOURCE_ID = RC.RESOURCE_ID")
                .append("AND PR.PACKAGE_ID = :packageId", packageId)
                .append("AND PR.PACKAGE_VER = :ver", ver)
                .append("AND UFP.PACKAGE_ID = PR.PACKAGE_ID")
                .append("AND UFP.RESOURCE_ID = RC.RESOURCE_ID")                
                .append("AND UFP.USER_ID = :userId))", userId);
        Query query = builder.build();
        return executor.queryForList(query, OdsUserFollowPackage.class);
    }
    
    @Override
    public List<Ods707eGridDto> findFollowCount(String packageId, String sDate, String eDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("COUNT(1) as followCount")
               .append("FROM ODS_USER_FOLLOW_PACKAGE A")
               .append("WHERE")
               .append("A.PACKAGE_ID = :packageId", packageId)
               .appendWhen((!Strings.isNullOrEmpty(sDate) || !Strings.isNullOrEmpty(eDate)), "AND")
               .appendWhen(!Strings.isNullOrEmpty(sDate), "A.CREATED >= :sDate", sDate)
               .appendWhen((!Strings.isNullOrEmpty(sDate) && !Strings.isNullOrEmpty(eDate)), "AND")
               .appendWhen(!Strings.isNullOrEmpty(eDate), "DATEADD(D, -1, A.CREATED)  <= :eDate", eDate);
        
        Query query = builder.build();
        return executor.queryForList(query, Ods707eGridDto.class);
    }

    @Override
    public List<OdsUserFollowPackage> findUpdateFollowUser() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT *")
                .append("FROM   ods_user_follow_package UFP") 
                .append("WHERE  EXISTS (SELECT NOW_PKG.package_id, ")
                .append("                      NOW_PKG.max_pkg_ver ")
                .append("               FROM   (SELECT package_id, ")
                .append("                              Max(ver) AS MAX_PKG_VER ")
                .append("                       FROM   ods_package_version ")
                .append("                       GROUP  BY package_id) AS NOW_PKG ")
                .append("                      LEFT JOIN (SELECT package_id, ")
                .append("                                        Max(package_ver) AS MAX_PKG_VER ")
                .append("                                 FROM ods_notice_package_version") 
                .append("                                 GROUP  BY package_id) AS NTC_PKG ")
                .append("                          ON ( NOW_PKG.package_id = NTC_PKG.package_id ) ")
                .append("               WHERE  ( NOW_PKG.max_pkg_ver > NTC_PKG.max_pkg_ver ")
                .append("                         OR NTC_PKG.package_id IS NULL ) ")
                .append("                      AND ( UFP.resource_id IS NULL ")
                .append("                            AND UFP.RESOURCE_CRITERIA_ID IS NULL ")
                .append("                            AND UFP.package_id = NOW_PKG.package_id )) ");
        Query query = builder.build();
        return executor.queryForList(query, OdsUserFollowPackage.class);
    }


    @Override
    public List<OdsUserFollowPackage> findCriteriaFollowUser() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT *")
                .append("FROM   ods_user_follow_package UFP") 
                .append("WHERE  EXISTS (SELECT NOW_PKG.package_id, ")
                .append("                      NOW_PKG.max_pkg_ver ")
                .append("               FROM   (SELECT package_id, ")
                .append("                              Max(ver) AS MAX_PKG_VER ")
                .append("                       FROM   ods_package_version ")
                .append("                       GROUP  BY package_id) AS NOW_PKG ")
                .append("                      LEFT JOIN (SELECT package_id, ")
                .append("                                        Max(package_ver) AS MAX_PKG_VER ")
                .append("                                 FROM ods_notice_package_version") 
                .append("                                 GROUP  BY package_id) AS NTC_PKG ")
                .append("                          ON ( NOW_PKG.package_id = NTC_PKG.package_id ) ")
                .append("               WHERE  ( NOW_PKG.max_pkg_ver > NTC_PKG.max_pkg_ver ")
                .append("                         OR NTC_PKG.package_id IS NULL ) ")
                .append("                      AND ( UFP.resource_id IS NOT NULL ")
                .append("                            AND UFP.RESOURCE_CRITERIA_ID IS NOT NULL ")
                .append("                            AND UFP.package_id = NOW_PKG.package_id )) ");
        Query query = builder.build();
        return executor.queryForList(query, OdsUserFollowPackage.class);
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_USER_FOLLOW_PACKAGE")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }

    


    @Override
    public List<Ods352eDataDto> getOdsUserFollowPackageByUser(String userId, boolean isPreview, List<String> idList) {
        String sql =
                "SELECT ";
                
        if(isPreview){
            sql = sql + "TOP 5 ";
        }
        
        sql = sql + "t.pkg_id, t.pkg_name, t.ufp_updated, t.type, t.code from ";
        sql = sql + "(SELECT B.id as pkg_id, B.name as pkg_name, convert(varchar, MAX(A.updated), 111) as ufp_updated, B.type, B.code "
            + "FROM Ods_User_Follow_Package A, Ods_Package B "
            + "WHERE A.user_Id = :userId AND A.package_Id = B.id ";        
        // 依使用者資料權查詢主題檔。
        if (!CollectionUtils.isEmpty(idList)) {
            sql = sql + "AND B.id in (:idList) ";
        }        
        sql = sql + "GROUP BY B.id, B.name, B.type, B.code ";
        sql = sql + "UNION ";
        sql = sql + "SELECT B.id as pkg_id, B.name as pkg_name, convert(varchar, MAX(C.updated), 111) as ufp_updated, B.type, B.code "
                + "FROM Ods_Package B, ODS_INDIVIDE_PACKAGE_SUB C "
                + "WHERE C.user_Id = :userId AND C.package_Id = B.id ";
        if (!CollectionUtils.isEmpty(idList)) {
            sql = sql + "AND B.id in (:idList) ";
        }
        sql = sql + "GROUP BY B.id, B.name, B.type, B.code) t ";
        sql = sql + "GROUP BY t.pkg_id, t.pkg_name, t.type, t.code, t.ufp_updated ";
        sql = sql + "ORDER BY MAX(t.ufp_updated) desc";
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        if (!CollectionUtils.isEmpty(idList)) {
            params.put("idList", idList);
        }
        params.put("userId", userId);
        if (!CollectionUtils.isEmpty(idList)) {
            params.put("idList", idList);
        }
        
        List<Ods352eDataDto> ods352eDtos = executor.queryForList(sql, params, Ods352eDataDto.class);

        return ods352eDtos;
    }
    
    
    @Override
    public void deleteUserFollowPackageByUserIdPackageIdList(String userId, List<String> packageIdListAry){
        
        String sql1 =
                "DELETE ODS_USER_FOLLOW_PACKAGE "
              + "WHERE  USER_ID = :userId "
              + "AND PACKAGE_ID IN ( ";
        
        for (String packageId : packageIdListAry){
            sql1 = sql1 + "'" + sqlEscapeService.escapeMsSql(packageId) + "',";
        }
        
        if (",".equals(sql1.substring(sql1.length() - 1, sql1.length()) ))
        {
            sql1 = sql1.substring(0, sql1.length() - 1);
        }
        
        sql1 = sql1 + " )";
      
        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("userId", userId);

        executor.delete(sql1, params1);
    }

}
