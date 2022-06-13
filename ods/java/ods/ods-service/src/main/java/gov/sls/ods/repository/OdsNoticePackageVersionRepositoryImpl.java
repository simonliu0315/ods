package gov.sls.ods.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

@Slf4j
public class OdsNoticePackageVersionRepositoryImpl implements
        OdsNoticePackageVersionRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void noticePackageVersion(String packageId, int ver) {
        Query.Builder builder = Query.builder();
        builder.append("BEGIN TRAN")
                .append("UPDATE [ODS_NOTICE_PACKAGE_VERSION]")
                .append(" SET    PACKAGE_VER = :ver", ver)
                .append(" WHERE  PACKAGE_ID = :packageId", packageId)
                .append("IF @@ROWCOUNT = 0")
                .append("  BEGIN")
                .append("      INSERT [ODS_NOTICE_PACKAGE_VERSION]")
                .append("             (PACKAGE_ID,")
                .append("              PACKAGE_VER,")
                .append("              CREATED,")
                .append("              UPDATED,")
                .append("              CREATE_USER_ID,")
                .append("              UPDATE_USER_ID)")
                .append("      VALUES (:packageId,", packageId)
                .append("              :ver,", ver)
                .append("              :SYSTEMTIME,", new Date())
                .append("              :SYSTEMTIME,", new Date())
                .append("              :USERID,", "ODS772X")
                .append("              :USERID)", "ODS772X")
                .append("  END")
                .append(" COMMIT TRAN ");
        Query query = builder.build();
        log.debug("nPV p:" + packageId + " v:" + ver + " sql:" + query.getString());
        executor.update(query);
    }

    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_NOTICE_PACKAGE_VERSION")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        executor.delete(query);
    }
    
}
