package gov.sls.ods.repository;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsPackageExtraRepositoryImpl implements OdsPackageExtraRepositoryCustom{
    
    @Autowired
    private SqlExecutor executor;

    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_PACKAGE_EXTRA")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
}
