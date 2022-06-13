package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods703eTab2Dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsPackageVersionMetadataRepositoryImpl implements OdsPackageVersionMetadataRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    @Override
    public void deleteByPackageIdAndPackageVer(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE").append("FROM ODS_PACKAGE_VERSION_METADATA")
                .append("WHERE PACKAGE_ID = :packageId", packageId)
                .append("AND PACKAGE_VER = :packageVer", packageVer);

        Query query = builder.build();
        executor.delete(query);
    }
    
    @Override
    public List<Ods703eTab2Dto> getPackageVersionMetatemplate(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT").append("A.ID metadataId, B.POSITIONS positions,")
                .append("B.DATA_TYPE dataType, B.DATA_KEY dataKey,")
                .append("C.DATA_VALUE dataValue").append("FROM ODS_METADATA A")
                .append("INNER JOIN ODS_METATEMPLATE B ON A.ID = B.METADATA_ID")
                .append("LEFT OUTER JOIN ODS_PACKAGE_VERSION_METADATA C ON A.ID = C.METADATA_ID")
                .append("AND B.DATA_KEY = C.DATA_KEY")
                .append("AND C.PACKAGE_ID = :packageId", Strings.emptyToNull(packageId))
                .append("AND C.PACKAGE_VER = :packageVer", packageVer)
                .append("WHERE A.IS_CHOOSE = 1 AND A.META_TYPE = '1'")
                .append("ORDER BY B.POSITIONS");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eTab2Dto.class);
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE").append("FROM ODS_PACKAGE_VERSION_METADATA")
                .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
}
