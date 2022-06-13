package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods703eTab2Dto;
import gov.sls.ods.dto.PackageMetadataDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsPackageMetadataRepositoryImpl implements OdsPackageMetadataRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    @Override
    public List<PackageMetadataDto> getUnionPackageExtra(String packageId, int packageVer) {
        List<PackageMetadataDto> dto = new ArrayList<PackageMetadataDto>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("DATA_KEY, DATA_VALUE, DATA_TYPE ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_METADATA ");
        sql.append("WHERE ");
        sql.append("PACKAGE_ID = :packageId ");
        sql.append("ORDER BY POSITIONS ");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("packageId", packageId);

        dto.addAll(this.executor.queryForList(sql, param, PackageMetadataDto.class));
        sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("DATA_KEY, DATA_VALUE, DATA_TYPE ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_EXTRA ");
        sql.append("WHERE ");
        sql.append("PACKAGE_ID = :packageId ");
        sql.append("ORDER BY POSITIONS ");
        param = new HashMap<String, Object>();
        param.put("packageId", packageId);
        dto.addAll(this.executor.queryForList(sql, param, PackageMetadataDto.class));
        sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("DATA_KEY, DATA_VALUE, DATA_TYPE ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_VERSION_METADATA ");
        sql.append("WHERE ");
        sql.append("PACKAGE_ID = :packageId ");
        sql.append("AND PACKAGE_VER = :packageVer ");        
        param = new HashMap<String, Object>();
        param.put("packageId", packageId);
        param.put("packageVer", packageVer);
        dto.addAll(this.executor.queryForList(sql, param, PackageMetadataDto.class));
        sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("DATA_KEY, DATA_VALUE, DATA_TYPE ");
        sql.append("FROM ");
        sql.append("ODS_PACKAGE_VERSION_EXTRA ");
        sql.append("WHERE ");
        sql.append("PACKAGE_ID = :packageId ");
        sql.append("AND PACKAGE_VER = :packageVer ");
        param = new HashMap<String, Object>();
        param.put("packageId", packageId);
        param.put("packageVer", packageVer);
        dto.addAll(this.executor.queryForList(sql, param, PackageMetadataDto.class));
        return dto;
    }

    @Override
    public List<Ods703eTab2Dto> getPackageMetatemplate(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT").append("A.ID metadataId, B.POSITIONS positions,")
                .append("B.DATA_TYPE dataType, B.DATA_KEY dataKey,")
                .append("C.DATA_VALUE dataValue").append("FROM ODS_METADATA A")
                .append("INNER JOIN ODS_METATEMPLATE B ON A.ID = B.METADATA_ID")
                .append("LEFT OUTER JOIN ODS_PACKAGE_METADATA C ON A.ID = C.METADATA_ID")
                .append("AND B.DATA_KEY = C.DATA_KEY")
                .append("AND C.PACKAGE_ID = :packageId", Strings.emptyToNull(packageId))
                .append("WHERE A.IS_CHOOSE = 1 AND A.META_TYPE = '0'")
                .append("ORDER BY B.POSITIONS");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eTab2Dto.class);
    }

    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE").append("FROM ODS_PACKAGE_METADATA")
                .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    

    @Override
    public List<PackageMetadataDto> findMetadataByPackageIdAndVer(String packageId, int ver) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT DATA_KEY, DATA_VALUE")
               .append("FROM ODS_PACKAGE_METADATA")
               .append("WHERE PACKAGE_ID = :packageId", packageId)
               .append("UNION ALL")
               
               .append("SELECT DATA_KEY, DATA_VALUE")
               .append("FROM ODS_PACKAGE_EXTRA")
               .append("WHERE PACKAGE_ID = :packageId", packageId)
               .append("UNION ALL")
               
               .append("SELECT DATA_KEY, DATA_VALUE")
               .append("FROM ODS_PACKAGE_VERSION_METADATA")
               .append("WHERE PACKAGE_ID = :packageId", packageId)
               .append("AND PACKAGE_VER = :packageVer", ver)
               .append("UNION ALL")
               
               .append("SELECT DATA_KEY, DATA_VALUE")
               .append("FROM ODS_PACKAGE_VERSION_EXTRA")
               .append("WHERE PACKAGE_ID = :packageId", packageId)
               .append("AND PACKAGE_VER = :packageVer", ver)
               ;

        Query query = builder.build();
        return executor.queryForList(query, PackageMetadataDto.class);
    }
}
