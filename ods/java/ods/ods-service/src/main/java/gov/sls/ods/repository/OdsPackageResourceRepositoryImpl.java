package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.ods.dto.Ods303eAnalysisDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsPackageResourceRepositoryImpl implements OdsPackageResourceRepositoryCustom {
    @Autowired
    private SqlExecutor executor;

    @Override
    public List<Ods703eTab2DialogDto> findPackResByIdAndVer(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("A.RESOURCE_ID as resourceId, A.RESOURCE_VER as resourceVer, A.RESOURCE_VER as ver,")
                .append("B.NAME as resName, B.DESCRIPTION as resDesc, B.FORMAT as format, B.UPDATED as versionDatetime")
                .append("FROM ODS_PACKAGE_RESOURCE A, ODS_RESOURCE B")
                .append("WHERE A.PACKAGE_ID = :packageId", packageId)
                .append("AND A.PACKAGE_VER = :packageVer", packageVer)
                .append("AND A.RESOURCE_ID = B.ID").append("ORDER BY B.NAME");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eTab2DialogDto.class);
    }

    @Override
    public void deletePackageResource(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
                .append("FROM ODS_PACKAGE_RESOURCE")
                .append("WHERE PACKAGE_ID = :packageId AND PACKAGE_VER = :packageVer", packageId,
                        packageVer);

        Query query = builder.build();
        executor.delete(query);
    }

    @Override
    public List<Ods303eAnalysisDto> findWorkbookByIdAndVer(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("DISTINCT b.WORKBOOK_ID, c.WORKBOOK_VER, d.NAME")
                .append("FROM ODS_PACKAGE_RESOURCE a")
                .append("inner join ODS_RESOURCE b on a.RESOURCE_ID = b.ID")
                .append("inner join ODS_RESOURCE_VERSION c on b.ID = c.RESOURCE_ID and a.RESOURCE_VER = c.VER")
                .append("inner join ODS_WORKBOOK d on b.WORKBOOK_ID = d.ID and c.WORKBOOK_VER = d.VER")
                .append("WHERE WORKBOOK_ID is not null")
                .append("AND WORKBOOK_VER is not null")
                .append("AND a.PACKAGE_ID = :packageId", packageId)
                .append("AND a.PACKAGE_VER = :packageVer", packageVer);                

        Query query = builder.build();
        return executor.queryForList(query, Ods303eAnalysisDto.class);
    }

    @Override
    public List<Ods303eAnalysisDto> findPackageByWorkbook(String workbookId, String workbookVer) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("DISTINCT a.PACKAGE_ID as packageId, a.PACKAGE_VER as packageVer, a.RESOURCE_ID as resourceId, a.RESOURCE_VER as resourceVer")
                .append("FROM ODS_PACKAGE_RESOURCE a")
                .append("inner join ODS_RESOURCE b on a.RESOURCE_ID = b.ID")
                .append("inner join ODS_RESOURCE_VERSION c on b.ID = c.RESOURCE_ID and a.RESOURCE_VER = c.VER")
                .append("inner join ODS_WORKBOOK d on b.WORKBOOK_ID = d.ID and c.WORKBOOK_VER = d.VER")
                .append("WHERE WORKBOOK_ID = :workbookId", workbookId)
                .append("AND WORKBOOK_VER = :workbookVer", workbookVer);                

        Query query = builder.build();
        return executor.queryForList(query, Ods303eAnalysisDto.class);
    }
    
    @Override
    public void insertPackageResource(OdsPackageResource odsPackageResource) {
        Query.Builder builder = Query.builder();
        builder.append("INSERT INTO ODS_PACKAGE_RESOURCE")
               .append("(PACKAGE_ID,PACKAGE_VER,RESOURCE_ID,RESOURCE_VER,")
               .append("CREATED,UPDATED,CREATE_USER_ID,UPDATE_USER_ID)")
               .append("VALUES(:packageId,:packageVer,", odsPackageResource.getId().getPackageId(), odsPackageResource.getId().getPackageVer())
               .append(":resourceId,:resourceVer,", odsPackageResource.getId().getResourceId(), odsPackageResource.getId().getResourceVer())
               .append(":created,:updated,", odsPackageResource.getCreated(), odsPackageResource.getUpdated())
               .append(":createUserId,:updateUserId)", odsPackageResource.getCreateUserId(), odsPackageResource.getUpdateUserId());

        Query query = builder.build();
        executor.insert(query);
    }

    @Override
    public List<OdsPackageResource> findPackageIdAndMaxPkgVer() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT PR.* ")
                .append("FROM   ODS_PACKAGE_RESOURCE PR, (SELECT PACKAGE_ID, ")
                .append("               MAX(VER) AS MAX_PKG_VER ")
                .append("        FROM   ODS_PACKAGE_VERSION ")
                .append("        GROUP  BY PACKAGE_ID) AS NOW_PKG ")
                .append("       LEFT JOIN (SELECT PACKAGE_ID, ")
                .append("                         MAX(PACKAGE_VER) AS MAX_PKG_VER ")
                .append("                  FROM   ODS_NOTICE_PACKAGE_VERSION ")
                .append("                  GROUP  BY PACKAGE_ID) AS NTC_PKG ")
                .append("              ON ( NOW_PKG.PACKAGE_ID = NTC_PKG.PACKAGE_ID ) ")
                .append("WHERE  (NOW_PKG.MAX_PKG_VER > NTC_PKG.MAX_PKG_VER ")
                .append("        OR NTC_PKG.PACKAGE_ID IS NULL) ")
                .append("        AND NOW_PKG.PACKAGE_ID = PR.PACKAGE_ID")
                .append("        AND NOW_PKG.MAX_PKG_VER = PR.PACKAGE_VER");
        Query query = builder.build();
        return executor.queryForList(query, OdsPackageResource.class);
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_PACKAGE_RESOURCE")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    
    @Override
    public List<Ods703eTab2DialogDto> findPackResInfoByIdAndVer(String packageId, int packageVer) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("A.RESOURCE_ID as resourceId, A.RESOURCE_VER as resourceVer,")
                .append("C.NAME as resName, C.DESCRIPTION as resDesc, B.FORMAT as format")
                .append("FROM ODS_PACKAGE_RESOURCE A, ODS_RESOURCE B, ODS_RESOURCE_VERSION C")
                .append("WHERE A.PACKAGE_ID = :packageId", packageId)
                .append("AND A.PACKAGE_VER = :packageVer", packageVer)
                .append("AND A.RESOURCE_ID = B.ID")
                .append("AND A.RESOURCE_ID = C.RESOURCE_ID")
                .append("AND A.RESOURCE_VER = C.VER")
                .append("ORDER BY C.NAME");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eTab2DialogDto.class);
    }
    
    @Override
    public List<Ods703eTab2DialogDto> findPackResInfoByIdAndVerAndViewId(String packageId, 
            int packageVer, String viewId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
                .append("A.RESOURCE_ID as resourceId, A.RESOURCE_VER as resourceVer,")
                .append("C.NAME as resName, C.DESCRIPTION as resDesc, B.FORMAT as format")
                .append("FROM ODS_PACKAGE_RESOURCE A, ODS_RESOURCE B, ODS_RESOURCE_VERSION C")
                .append("WHERE A.PACKAGE_ID = :packageId", packageId)
                .append("AND A.PACKAGE_VER = :packageVer", packageVer)
                .append("AND B.VIEW_ID = :viewId", viewId)
                .append("AND A.RESOURCE_ID = B.ID")
                .append("AND A.RESOURCE_ID = C.RESOURCE_ID")
                .append("AND A.RESOURCE_VER = C.VER")
                .append("ORDER BY C.NAME");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eTab2DialogDto.class);
    }
}
