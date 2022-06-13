package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsSolrControltable;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsSolrControltableRepositoryImpl implements OdsSolrControltableRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    @Override
    public Date getPreExecuteDate() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT MAX(EXECUTE_START_DATE) as executeStartDate")
            .append("FROM ODS_SOLR_CONTROLTABLE")
            .append("WHERE EXECUTE_RESULT = '1'")
            .append("OR EXECUTE_RESULT = '2'");

        Query query = builder.build();
        List<OdsSolrControltable> queryForList = executor.queryForList(query, OdsSolrControltable.class);
        Date maxDate = null;
        if (null != queryForList && !queryForList.isEmpty()) {
            maxDate = queryForList.get(0).getExecuteStartDate();
        }
        return maxDate;
    }

}
