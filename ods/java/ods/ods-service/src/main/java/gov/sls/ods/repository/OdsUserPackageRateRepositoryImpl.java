package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.dto.UserPackageRateAggregateDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsUserPackageRateRepositoryImpl implements OdsUserPackageRateRepositoryCustom {

    @Autowired
    private SqlExecutor executor;
    
    @Override
    public List<UserPackageRateAggregateDto> getAggregateByPackageId(String packageId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("ISNULL(AVG(CAST(RATE AS FLOAT)),0) rateAvg, ");
        sql.append("COUNT(USER_ID) rateCount ");        
        sql.append("FROM ");
        sql.append("ODS_USER_PACKAGE_RATE ");
        sql.append("WHERE ");
        sql.append("PACKAGE_ID = :packageId ");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("packageId", packageId);       
        return this.executor.queryForList(sql, param, UserPackageRateAggregateDto.class);
    }
    
    @Override
    public List<Ods707eGridDto> findAvgRateAneTotCount(String packageId, String sDate, String eDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("ISNULL(AVG(CAST(A.RATE AS FLOAT)),0) as avgRate,")
               .append("COUNT(1) as totCount")
               .append("FROM ODS_USER_PACKAGE_RATE A")
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
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_USER_PACKAGE_RATE")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    
    
    @Override
    public List<OdsUserPackageRate> findByPkgIdAndUserId(String packageId, String userId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("*")
               .append("FROM ODS_USER_PACKAGE_RATE")
               .append("WHERE")
               .append("PACKAGE_ID = :packageId", packageId)
               .append("AND USER_ID = :userId", userId);

        Query query = builder.build();
        return executor.queryForList(query, OdsUserPackageRate.class);
    }
}
