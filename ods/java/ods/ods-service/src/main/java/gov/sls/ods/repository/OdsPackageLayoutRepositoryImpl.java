package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageLayout;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsPackageLayoutRepositoryImpl implements OdsPackageLayoutRepositoryCustom {
    @Autowired
    private SqlExecutor executor;
   
    @Override
    public void deletePackageLayout(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_PACKAGE_LAYOUT")
               .append("WHERE PACKAGE_ID = :packageId AND PACKAGE_VER = :packageVer", packageId, packageVer);

        Query query = builder.build();
        executor.delete(query);
    }
    
    @Override
    public void insertPackageLayout(OdsPackageLayout odsPackageLayout) {
        Query.Builder builder = Query.builder();
        builder.append("INSERT INTO ODS_PACKAGE_LAYOUT")
               .append("(PACKAGE_ID,PACKAGE_VER,ROW_POSITION,COLUMN_POSITION,RESOURCE_ID,RESOURCE_VER,")
               .append("DESCRIPTION,CREATED,UPDATED,CREATE_USER_ID,UPDATE_USER_ID)")
               .append("VALUES(:packageId,:packageVer,", odsPackageLayout.getId().getPackageId(), odsPackageLayout.getId().getPackageVer())
               .append(":rowPosition,:columnPosition,", odsPackageLayout.getId().getRowPosition(), odsPackageLayout.getId().getColumnPosition())
               .append(":resourceId,:resourceVer,", odsPackageLayout.getResourceId(), odsPackageLayout.getResourceVer())
               .append(":description,:created,:updated,", odsPackageLayout.getDescription(), odsPackageLayout.getCreated(), odsPackageLayout.getUpdated())
               .append(":createUserId,:updateUserId)", odsPackageLayout.getCreateUserId(), odsPackageLayout.getUpdateUserId());

        Query query = builder.build();
        executor.insert(query);
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_PACKAGE_LAYOUT")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
}
