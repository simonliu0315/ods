package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.dto.Ods707eGridDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsUserPackageVersionClickRepositoryImpl implements OdsUserPackageVersionClickRepositoryCustom {

    @Autowired
    private SqlExecutor executor;
    
    @Override
    public List<Ods707eGridDto> findClickCount(String packageId, String sDate, String eDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("COUNT(1) as clickCount")
               .append("FROM ODS_USER_PACKAGE_VERSION_CLICK A")
               .append("WHERE")
               .append("A.PACKAGE_ID = :packageId", packageId)
               .appendWhen((!Strings.isNullOrEmpty(sDate) || !Strings.isNullOrEmpty(eDate)), "AND")
               .appendWhen(!Strings.isNullOrEmpty(sDate), "A.CREATED >= :sDate", sDate)
               .appendWhen((!Strings.isNullOrEmpty(sDate) && !Strings.isNullOrEmpty(eDate)), "AND")
               .appendWhen(!Strings.isNullOrEmpty(eDate), "DATEADD(D, -1, A.CREATED) <= :eDate", eDate);

        Query query = builder.build();
        return executor.queryForList(query, Ods707eGridDto.class);
    }
    
    @Override
    public List<Ods351eDataDto> findPopularUserPackageClick(List<String> idList){
        StringBuilder sql = new StringBuilder();
        sql.append(" select top 3 ");
        sql.append("      t.name, t.imageFileName, t.description, t.packageId, ");
        sql.append("      t.clickCount, t.packageVer, t.type, t.code ");
        sql.append(" from ");
        sql.append("      (select ");
        sql.append("          a.name, a.image_url imageFileName,  ");
        sql.append("          a.description, a.id packageId, a.type, a.code, ");
        sql.append("          (select count(*) from   ");
        sql.append("              ODS_USER_PACKAGE_VERSION_CLICK b ");
        sql.append("              where a.id = b.package_id) clickCount, ");
        sql.append("          (select max(ver) from  ");
        sql.append("              ods_package_version c  ");
        sql.append("              where a.id = c.package_id and c.is_published = '1') packageVer ");
        sql.append("       from ");
        sql.append("          ods_package a) t");
        sql.append(" where ");
        sql.append("      packageVer is not null ");
        // 依使用者資料權查詢主題檔。
        if (!CollectionUtils.isEmpty(idList)) {
            sql.append(" AND packageId in (:idList)  ");
        }
        sql.append(" order by clickCount desc ");
        
        Map<String, Object> params = new HashMap<String, Object>();
        if (!CollectionUtils.isEmpty(idList)) {
            params.put("idList", idList);
        }
        
        return this.executor.queryForList(sql, params, Ods351eDataDto.class);
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_USER_PACKAGE_VERSION_CLICK")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    
}
