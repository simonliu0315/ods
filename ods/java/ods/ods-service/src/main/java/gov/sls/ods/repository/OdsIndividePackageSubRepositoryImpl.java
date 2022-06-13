package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.dto.Ods773xIndividualDto;
import gov.sls.ods.service.SqlEscapeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsIndividePackageSubRepositoryImpl implements OdsIndividePackageSubRepositoryCustom{
    
    @Autowired
    private SqlExecutor executor;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    
    @Override
    public List<Ods303eIndividualDto> getIndPackageSub(String packageCode, String userId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("P.ID as packageId, PS.USER_UNIFY_ID as userUnifyId")
               .append("FROM ODS_PACKAGE P")
               .append("INNER JOIN ODS_INDIVIDE_PACKAGE_SUB PS")
               .append("ON P.ID = PS.PACKAGE_ID")
               .append("WHERE")
               .append("P.CODE = :packageCode", packageCode)
               .append("AND PS.USER_ID = :userId", userId);

        Query query = builder.build();
        return executor.queryForList(query, Ods303eIndividualDto.class);
    }
    
    @Override
    public void deleteByUserUnifyIdPackageIdList(String userUnifyId, List<String> packageIdListAry){
        
        String sql1 =
                "DELETE ODS_INDIVIDE_PACKAGE_SUB "
              + "WHERE  USER_UNIFY_ID = :userUnifyId "
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
        params1.put("userUnifyId", userUnifyId);

        executor.delete(sql1, params1);
    }
    
    @Override
    public List<Ods773xIndividualDto> findNotifier() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
        .append("A.USER_UNIFY_ID, A.USER_ROLE, A.USER_ID, A.PACKAGE_ID, C.CODE")
        .append("FROM ODS_INDIVIDE_PACKAGE_SUB A")
        .append("INNER JOIN ODS_RESOURCE_INDIVID B")
        .append("ON A.USER_UNIFY_ID = B.USER_UNIFY_ID")
        .append("INNER JOIN ODS_PACKAGE C")
        .append("ON A.PACKAGE_ID = C.ID")
        .append("WHERE")
        .append("B.DAN_IMPORT_MK = 'Y'")
        .append("AND B.DAN_IMPORT_DATE < GETDATE()")
        .append("AND ISNULL(B.REPORT_NOTIFY_MK , 'N') = 'N'")
        .append("GROUP BY A.USER_UNIFY_ID, A.USER_ID, A.PACKAGE_ID, C.CODE, A.USER_ROLE");
//        builder.append("SELECT")
//               .append("A.USER_UNIFY_ID, A.USER_ID, A.PACKAGE_ID, C.CODE")
//               .append("FROM ODS_INDIVIDE_PACKAGE_SUB A")
//               .append("INNER JOIN ODS_RESOURCE_INDIVID B")
//               .append("ON A.USER_UNIFY_ID = B.USER_UNIFY_ID")
//               .append("INNER JOIN ODS_PACKAGE C")
//               .append("ON A.PACKAGE_ID = C.ID")
//               .append("WHERE")
//               .append("B.DAN_IMPORT_MK = 'Y'")
//               .append("AND B.DAN_IMPORT_DATE < GETDATE()")
//               .append("AND ISNULL(B.REPORT_NOTIFY_MK , 'N') = 'N'")
//               .append("GROUP BY A.USER_UNIFY_ID, A.USER_ID, A.PACKAGE_ID, C.CODE");

        Query query = builder.build();
        return executor.queryForList(query, Ods773xIndividualDto.class);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findUserUnifyIdByResourceId(String resourceId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
        .append("ODSIPS.USER_UNIFY_ID")
        .append("FROM ODS_PACKAGE_RESOURCE ODSPR, ODS_INDIVIDE_PACKAGE_SUB ODSIPS")
        .append("WHERE ODSPR.RESOURCE_ID =:resourceId", resourceId)
        .append("AND ODSIPS.PACKAGE_ID = ODSPR.PACKAGE_ID");

        Query query = builder.build();
        return executor.queryForList(query, String.class);
    }
}
