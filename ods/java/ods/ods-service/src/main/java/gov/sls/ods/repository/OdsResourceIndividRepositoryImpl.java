package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceIndivid;
import gov.sls.ods.dto.Ods303eIndividualDto;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

@Slf4j
public class OdsResourceIndividRepositoryImpl implements OdsResourceIndividRepositoryCustom{
    
    @Autowired
    private SqlExecutor executor;
    
    @Autowired
    private SqlPaginationHelper sqlPaginationHelper;
    
    @Override
    public Page<Ods303eIndividualDto> getResourceDate(String userUnifyId, Pageable pageable) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("DISTINCT ri.RESOURCE_DATE")
               .append("FROM ODS_PACKAGE P")
               .append("inner join ods_package_resource pr on p.id = pr.package_id")
               .append("inner join ODS_RESOURCE_INDIVID ri on ri.resource_id = pr.resource_id")
               .append("WHERE")
               .append("ri.DAN_IMPORT_Mk = 'Y'")
               .append("and ri.REPORT_NOTIFY_MK = 'Y'")
               .append("and ri.USER_UNIFY_ID = :userUnifyId", userUnifyId)
               .append("ORDER BY ri.RESOURCE_DATE");
        
        Query query = builder.build();
        log.info("Page SQL:" + query.getString());
        return sqlPaginationHelper.queryForPage(query, Ods303eIndividualDto.class, pageable);
    }
    
    @Override
    public List<OdsResourceIndivid> queryDanExportStatus(String userUnifyId, String resourceId){
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("*")
               .append("FROM ODS_RESOURCE_INDIVID ri")
               .append("WHERE")
               .append("ri.DAN_IMPORT_Mk = 'Y'")
               .append("and ri.USER_UNIFY_ID = :userUnifyId", userUnifyId)
               .append("and ri.RESOURCE_ID = :resourceId", resourceId)
               .append("ORDER BY ri.RESOURCE_DATE");
        
        Query query = builder.build();
        log.info("Page SQL:" + query.getString());
        return executor.queryForList(query, OdsResourceIndivid.class);
    }
    
    @Override
    public void updateNotifyMk(String userUnifyId, String mk) {
        Query.Builder builder = Query.builder();
        builder.append("UPDATE ODS_RESOURCE_INDIVID")
               .append("SET REPORT_NOTIFY_MK = :mk", mk)
               .append("WHERE USER_UNIFY_ID = :userUnifyId", userUnifyId)
               .append("AND DAN_IMPORT_MK = 'Y'")
               .append("AND DAN_IMPORT_DATE < GETDATE()")
               .append("AND ISNULL(REPORT_NOTIFY_MK , 'N') = 'N'");
    
        Query query = builder.build();
        executor.update(query);
    }
    
}
