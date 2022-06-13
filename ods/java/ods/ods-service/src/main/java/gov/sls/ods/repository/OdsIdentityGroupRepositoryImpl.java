package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.dto.Ods704eGrid1Dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;


public class OdsIdentityGroupRepositoryImpl implements OdsIdentityGroupRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(OdsIdentityGroupRepositoryImpl.class);
    @Autowired
    private SqlExecutor sqlExecutor;

    @Override
    public List<Ods704eGrid1Dto> findUnGroupPackageByNameSelPkg(String name, String selectedPkgList) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT "
              + " P.NAME as PKG_GUP_NAME, P.DESCRIPTION as PKG_GUP_DESCRIPTION, "
              + " P.ID as PKG_GUP_PACKAGE_ID, P.IMAGE_URL as PKG_GUP_IMAGE_URL "
              + "FROM   ODS_PACKAGE P "
              + "WHERE  1=1 ";
              if (!Strings.isNullOrEmpty(name)) {
                  sql = sql + "AND  P.NAME like :name ";
                  params.put("name", "%" +  name  + "%");
              }
              if ( !selectedPkgList.isEmpty() && ",".equals(selectedPkgList.substring(selectedPkgList.length() - 1, selectedPkgList.length()) ))
              {
                  selectedPkgList = selectedPkgList.substring(0, selectedPkgList.length() - 1);
                  List<String> selectedPkgListAry = new ArrayList<String>(Arrays.asList(selectedPkgList.split(",")));
                  
                  sql += "AND  P.ID NOT IN ( :selectedPkgIdAry )  ";
                  
                  params.put("selectedPkgIdAry", selectedPkgListAry);
              }
              
              sql = sql + "ORDER  BY P.NAME ";
        
        logger.debug("selectedPkgListselectedPkgList:" + ToStringBuilder.reflectionToString(selectedPkgList));
        List<Ods704eGrid1Dto> packages = sqlExecutor.queryForList(sql, params, Ods704eGrid1Dto.class);
                
        for (Ods704eGrid1Dto pkg: packages)
        {
            logger.debug("findUnGroupPackageByNameSelPkg:" + ToStringBuilder.reflectionToString(pkg));
        }
        
        return packages;
        
    }
    
    

    @Override
    public void createIdentityGroupByGupIdIdtIdList(String groupId, String chkIdentityIdList) {
        
        
        List<String> identityIdListAry = new ArrayList<String>(Arrays.asList(chkIdentityIdList.split(",")));
        
        for (String identityId : identityIdListAry){
            
            String sql =
                    "INSERT ODS_IDENTITY_GROUP "
                  + "       (GROUP_ID, "
                  + "        IDENTITY_ID, "
                  + "        VIEW_PRIORITY, "
                  + "        CREATED, "
                  + "        UPDATED, "
                  + "        CREATE_USER_ID, "
                  + "        UPDATE_USER_ID) "
                  + "VALUES (:groupId, "
                  + "        :identityId, "
                  + "        1, "			//view_priority目前皆設置成1
                  + "        :created, "
                  + "        :updated, "
                  + "        :createUserId, "
                  + "        :updateUserId) ";

          
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("groupId", groupId);
            params.put("identityId", identityId);
            params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
            params.put("createUserId", UserHolder.getUser().getId());
            params.put("updateUserId", UserHolder.getUser().getId());
              
              
            sqlExecutor.insert(sql, params);
        }

        
    }

}
