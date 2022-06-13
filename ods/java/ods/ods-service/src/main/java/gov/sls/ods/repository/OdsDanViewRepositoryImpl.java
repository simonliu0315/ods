package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsDanView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.SqlExecutor;
//import gov.sls.fms.dto.Fms421rDto;

public class OdsDanViewRepositoryImpl implements OdsDanViewRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(OdsDanViewRepositoryImpl.class);
    @Autowired
    private SqlExecutor sqlExecutor;
    
    @Override
    public List<OdsDanView> findDanViewByDanWbkId(String danWbkId) {
        //String sql = "select * from ODS_RESOURCE";
        Map<String, Object> params = new HashMap<String, Object>();
        
        String sql =
                "SELECT * "
                + "FROM   [ODS_DAN_VIEW] DV "
                + "WHERE  DV.WORKBOOK_ID = :danWbkId ";

        
        params.put("danWbkId", danWbkId);
        
        List<OdsDanView> danViews = sqlExecutor.queryForList(sql, params, OdsDanView.class);
                
        for (OdsDanView danView: danViews)
        {
            logger.debug("danView:" + ToStringBuilder.reflectionToString(danView));
        }
        
        return danViews;
    }
    
}
