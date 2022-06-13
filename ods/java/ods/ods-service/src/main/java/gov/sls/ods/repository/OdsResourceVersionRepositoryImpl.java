package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceVersion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsResourceVersionRepositoryImpl implements OdsResourceVersionRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    @Override
    public List<OdsResourceVersion> findResourceByPackageIdAndVer(String packageId, int ver) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT a.* ")
                .append("FROM ods_resource_version a ")
                .append("INNER JOIN ods_package_resource b ")
                .append("ON a.RESOURCE_ID = b.RESOURCE_ID AND a.ver = b.RESOURCE_VER ")
                .append("WHERE b.PACKAGE_ID = :packageId ", packageId)
                .append("AND b.PACKAGE_VER = :ver", ver);

        Query query = builder.build();
        return executor.queryForList(query, OdsResourceVersion.class);
    }

}
