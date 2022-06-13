package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.PortalStat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;


public class OdsPackageVersionRepositoryImpl implements OdsPackageVersionRepositoryCustom {

    @Autowired
    private SqlExecutor executor;
    
    @Override
    public List<PackageAndResourceDto> findPackageAndResource(String packageId, int packageVer, int rowPosition, int columnPosition) {
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("A.PACKAGE_ID, A.PACKAGE_VER, A.ROW_POSITION, ");
        sql.append("A.COLUMN_POSITION, A.RESOURCE_ID, ");
        sql.append("A.RESOURCE_VER, ");
        sql.append("A.DESCRIPTION, B.FORMAT, C.NAME, ");
        sql.append("C.DESCRIPTION as resourceDescription, ");
        sql.append("B.CURRENCY_COLS currencyCols ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_LAYOUT A ");
        sql.append("INNER JOIN ODS_RESOURCE B ON A.RESOURCE_ID = B.ID ");
        sql.append("INNER JOIN ODS_RESOURCE_VERSION C ON A.RESOURCE_ID = C.RESOURCE_ID ");
        sql.append("AND A.RESOURCE_VER = C.VER ");
        sql.append("WHERE ");
        sql.append("A.PACKAGE_ID = :packageId ");
        sql.append("AND A.PACKAGE_VER = :packageVer ");
        sql.append("AND A.ROW_POSITION = :rowPosition ");
        sql.append("AND A.COLUMN_POSITION = :columnPosition ");
        sql.append("ORDER BY ROW_POSITION, COLUMN_POSITION ");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("packageId", packageId);
        param.put("packageVer", packageVer);
        param.put("rowPosition", rowPosition);
        param.put("columnPosition", columnPosition);
        return this.executor.queryForList(sql, param, PackageAndResourceDto.class);
    }
    
    @Override
    public void deletePackageVersionZero(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_PACKAGE_VERSION")
               .append("WHERE PACKAGE_ID = :packageId AND VER=0", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    
    @Override
    public List<PackageAndResourceDto> findPackageAndResourceAndLayout(String packageId, int packageVer) {
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("A.PACKAGE_ID, A.PACKAGE_VER, A.ROW_POSITION, ");
        sql.append("A.COLUMN_POSITION, A.RESOURCE_ID, ");
        sql.append("A.RESOURCE_VER, ");
        sql.append("A.DESCRIPTION, B.FORMAT, C.NAME, ");
        sql.append("C.DESCRIPTION as resourceDescription ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_LAYOUT A ");
        sql.append("INNER JOIN ODS_RESOURCE B ON A.RESOURCE_ID = B.ID AND B.FORMAT <> 'common' ");
        sql.append("INNER JOIN ODS_RESOURCE_VERSION C ON A.RESOURCE_ID = C.RESOURCE_ID ");
        sql.append("AND A.RESOURCE_VER = C.VER ");
        sql.append("WHERE ");
        sql.append("A.PACKAGE_ID = :packageId ");
        sql.append("AND A.PACKAGE_VER = :packageVer ");      
        sql.append("ORDER BY ROW_POSITION, COLUMN_POSITION ");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("packageId", packageId);
        param.put("packageVer", packageVer);       
        return this.executor.queryForList(sql, param, PackageAndResourceDto.class);
    }

    @Override
    public List<PortalStat> findPackageUpdateCount() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("COUNT(1) AS number")
                .append("FROM ODS_PACKAGE_VERSION A")
                .append("WHERE A.UPDATED >= GETDATE()-6")
                .append("AND A.UPDATED <= GETDATE()")
                .append("AND IS_PUBLISHED = 'True' and DEL_MK = 'FALSE'");

        Query query = builder.build();
        return executor.queryForList(query, PortalStat.class);
    }
    @Override
    public List<PortalStat> findMonthPackageUpdateCount() {
       
        //取得本月第一日
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(),
                Calendar.DATE);
        Date monthFirstDay =DateUtils.setDays(today, 1);
        
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("COUNT(1) AS number")
                .append("FROM ODS_PACKAGE_VERSION A")
                .append("WHERE A.UPDATED >= :monthFirstDay", monthFirstDay)
                .append("AND A.UPDATED <= GETDATE()")
                .append("AND IS_PUBLISHED = 'True' and DEL_MK = 'FALSE'");
        Query query = builder.build();

        return this.executor.queryForList(query, PortalStat.class);
    }
    
    @Override
    public List<Ods351eDataDto> findLatestOdsPackageVersion(List<String> idList){
        StringBuilder sql = new StringBuilder();
        
        sql.append(" select ");
        sql.append("       t.name, t.imageFileName, t.description, t.packageId, ");
        sql.append("       t.packageVer, t.type, t.code ");
        sql.append(" from ");
        sql.append("       (select ");
        sql.append("              a.name, a.image_url imageFileName, ");
        sql.append("              a.description, a.id packageId, a.type, a.code, ");
        sql.append("              (select max(ver) from  ");
        sql.append("                   ods_package_version c ");
        sql.append("                   where a.id = c.package_id and c.is_published = '1') packageVer, ");
        sql.append("              (select max(created) from ");
        sql.append("                   ods_package_version c ");
        sql.append("                   where a.id = c.package_id and c.is_published = '1') maxCreated ");
        sql.append("       from ");
        sql.append("              ods_package a) t ");
        sql.append(" where ");
        sql.append("       packageVer is not null ");
        // 依使用者資料權查詢主題檔。
        if (!CollectionUtils.isEmpty(idList)) {
            sql.append(" AND packageId in (:idList)  ");
        }
        sql.append(" order by maxCreated desc ");
        
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
               .append("FROM ODS_PACKAGE_VERSION")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }

    @Override
    public List<OdsPackageVersion> getReadyIndexPackageByPreExecuteDate(Date preExecuteDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT opv.* FROM ODS_PACKAGE_VERSION opv ")
                .append("WHERE opv.del_Mk <> '1' and opv.is_Published = '1'")
                .appendWhen(
                        null != preExecuteDate,
                        "and (opv.updated > :preExecuteDate ", preExecuteDate)
                .appendWhen(
                        null != preExecuteDate,
                        " OR opv.package_Id IN ("
                                + "SELECT op.id FROM ODS_PACKAGE op WHERE op.updated > :preExecuteDate)"
                                + ") ", preExecuteDate);

        Query query = builder.build();
        return executor.queryForList(query, OdsPackageVersion.class);

    }

    @Override
    public List<OdsPackageVersion> getDelIndexPackageByPreExecuteDate(Date preExecuteDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT OPV.* FROM ODS_PACKAGE_VERSION OPV ")
                .append("WHERE ( OPV.del_mk = '1' or OPV.is_published <> '1' ) ")
                .appendWhen(null != preExecuteDate, "AND OPV.updated > :preExecuteDate ",
                        preExecuteDate);

        Query query = builder.build();
        return executor.queryForList(query, OdsPackageVersion.class);
    }
    
    @Override
    public List<OdsPackageVersion> getRssData() {
        /*Query.Builder builder = Query.builder();
        builder.append("SELECT * FROM ODS_PACKAGE A WHERE A.ID IN ")
                .append("    (SELECT DISTINCT PACKAGE_ID  ")
                .append("        FROM Ods_Package_Version B  ")
                .append("     WHERE B.is_Published = 'true' ")
                .append("     AND B.ver<>0 ")
                .append("     AND B.UPDATED >= :SYSDATE -6 ", new Date())
                .append("     AND B.UPDATED <= :SYSDATE )", new Date());*/
        
        Query.Builder builder = Query.builder();
        builder.append("SELECT * FROM ODS_PACKAGE_VERSION A, ")
                .append("(SELECT PACKAGE_ID, MAX(VER) as VER ")
                .append("    FROM ODS_PACKAGE_VERSION B   ")
                .append("WHERE B.is_Published = 'true' ")
                .append("     AND B.ver<>0 ")
                .append("     AND dateadd(d,6,B.UPDATED) >= :SYSDATE ", new Date())
                .append("     AND B.UPDATED <= :SYSDATE ", new Date())
                .append("GROUP BY PACKAGE_ID) MAX_VER_PKG ")
                .append("WHERE A.PACKAGE_ID = MAX_VER_PKG.PACKAGE_ID ")
                .append("     AND A.VER = MAX_VER_PKG.VER ");
                
        Query query = builder.build();
        return executor.queryForList(query, OdsPackageVersion.class);
    }
}
