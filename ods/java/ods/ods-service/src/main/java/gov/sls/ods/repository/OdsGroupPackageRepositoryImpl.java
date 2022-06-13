package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;


public class OdsGroupPackageRepositoryImpl implements OdsGroupPackageRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(OdsGroupPackageRepositoryImpl.class);
    @Autowired
    private SqlExecutor sqlExecutor;
    

    @Override
    public void createGroupPackageByGupIdPkgIdList(String groupId, String selPackageIdList) {
        
        
        List<String> packageIdListAry = new ArrayList<String>(Arrays.asList(selPackageIdList.split(",")));
        
        for (String packageId : packageIdListAry){
            
            String sql =
                    "INSERT ODS_GROUP_PACKAGE "
                  + "       (GROUP_ID, "
                  + "        PACKAGE_ID, "
                  + "        CREATED, "
                  + "        UPDATED, "
                  + "        CREATE_USER_ID, "
                  + "        UPDATE_USER_ID) "
                  + "VALUES (:groupId, "
                  + "        :packageId, "
                  + "        :created, "
                  + "        :updated, "
                  + "        :createUserId, "
                  + "        :updateUserId) ";

          
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("groupId", groupId);
            params.put("packageId", packageId);
            params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("createUserId", UserHolder.getUser().getId());
            params.put("updateUserId", UserHolder.getUser().getId());
              
              
            sqlExecutor.insert(sql, params);
        }

        
    }
    
    @Override
    public void deleteByPackageId(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("DELETE")
               .append("FROM ODS_GROUP_PACKAGE")
               .append("WHERE PACKAGE_ID = :packageId", packageId);

        Query query = builder.build();
        sqlExecutor.delete(query);
    }
}
