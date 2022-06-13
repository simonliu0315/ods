package gov.sls.ods.repository;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.dto.Ods706eGrid3Dto;
import gov.sls.ods.dto.Ods706eGrid4Dto;
import gov.sls.ods.dto.Ods706eTab1FormBean;
import gov.sls.ods.dto.Ods706eTab2Dto;
import gov.sls.ods.dto.Ods706eTab2FormBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.persistence.query.SqlExecutor;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;



public class OdsResourceCriteriaRepositoryImpl implements OdsResourceCriteriaRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(OdsResourceCriteriaRepositoryImpl.class);
    
    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Override
    public List<Ods706eGrid3Dto> findCriteriasByResId(String resId, String name, String description) {
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY RC.CREATED DESC) AS ROW_COUNT, "
              + " * "
              + "FROM   ODS_RESOURCE_CRITERIA RC "
              + "WHERE  RC.RESOURCE_ID = :resId ";
              if (!Strings.isNullOrEmpty(name)) {
                  sql = sql + "AND RC.NAME like :name ";
              }
              if (!Strings.isNullOrEmpty(description)) {
                  sql = sql + "AND RC.DESCRIPTION like :description ";
              }
              sql = sql + "ORDER  BY RC.CREATED DESC ";
        

        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", resId);
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", "%" +  name  + "%");
        }
        if (!Strings.isNullOrEmpty(description)) {
            params.put("description", "%" +  description  + "%");
        }
        
        List<Ods706eGrid3Dto> resourceCriterias = sqlExecutor.queryForList(sql, params, Ods706eGrid3Dto.class);
                
        for (Ods706eGrid3Dto resourceCriteria: resourceCriterias)
        {
            logger.debug("findCriteriasByResId:" + ToStringBuilder.reflectionToString(resourceCriteria));
        }
        
        return resourceCriterias;
        

    }
    
    
    @Override
    public List<Ods706eGrid4Dto> findCriteriaDetailByResIdCriId(String resId, String criId) {
        String sql =
                "SELECT ROW_NUMBER() "
              + "         OVER( "
              + "           ORDER BY C.CREATED DESC) AS ROW_COUNT, "
              + " CONDITION, "
              + " DATA_FIELD, "
              + " AGGREGATE_FUNC, "
              /*+ "       CASE AGGREGATE_FUNC "
              + "         WHEN 'SUM' THEN '加總' "
              + "         WHEN 'MAX' THEN '最大值' "
              + "         WHEN 'MIN' THEN '最小值' "
              + "         WHEN 'AVG' THEN '平均' "
              + "       END AS AGGREGATE_FUNC, "*/
              /*+ "       CASE OPERATOR "
              + "         WHEN '<' THEN '小於' "
              + "         WHEN '<=' THEN '小於等於' "
              + "         WHEN '==' THEN '等於' "
              + "         WHEN '>' THEN '大於' "
              + "         WHEN '>=' THEN '大於等於' "
              + "       END AS OPERATOR, "*/
              + " OPERATOR, "
              + " TARGET "
              + "FROM   ODS_CRITERIA C "
              + "WHERE  C.RESOURCE_ID = :resId "
              + "       AND C.RESOURCE_CRITERIA_ID = :criId "
              + "ORDER  BY C.CREATED DESC ";
        

        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", resId);
        params.put("criId", criId);
        
        List<Ods706eGrid4Dto> criteriaDetails = sqlExecutor.queryForList(sql, params, Ods706eGrid4Dto.class);
                
        for (Ods706eGrid4Dto criteriaDetail: criteriaDetails)
        {
            logger.debug("findCriteriaDetailByResIdCriId:" + ToStringBuilder.reflectionToString(criteriaDetail));
        }
        
        return criteriaDetails;
        

    }
    
    
    @Override
    public void createCriteriaByResId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        try{
                String sql =
                        "INSERT ODS_RESOURCE_CRITERIA "
                      + "       (RESOURCE_ID, "
                      + "        ID, "
                      + "        NAME, "
                      + "        DESCRIPTION, "
                      + "        CREATED, "
                      + "        UPDATED, "
                      + "        CREATE_USER_ID, "
                      + "        UPDATE_USER_ID) "
                      + "VALUES (:resId, "
                      + "        DEFAULT, "
                      + "        :name, "
                      + "        :description, "
                      + "        :created, "
                      + "        :updated, "
                      + "        :createUserId, "
                      + "        :updateUserId) ";
        
              
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("resId", ods706eTab1FormBean.getResId());
              params.put("name", ods706eTab1FormBean.getName());
              params.put("description", ods706eTab1FormBean.getDescription());
              params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("createUserId", UserHolder.getUser().getId());
              params.put("updateUserId", UserHolder.getUser().getId());
              
              sqlExecutor.insert(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    
    
    @Override
    public void updateCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        try{
                String sql =
                        "UPDATE ODS_RESOURCE_CRITERIA "
                      + "SET     NAME = :name, "
                      + "        DESCRIPTION = :description, "
                      + "        UPDATED = :updated, "
                      + "        UPDATE_USER_ID = :updateUserId "
                      + "WHERE  RESOURCE_ID = :resId "
                      + "AND ID = :criId ";
        
              
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("name", ods706eTab1FormBean.getName());
              params.put("description", ods706eTab1FormBean.getDescription());
              params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("updateUserId", UserHolder.getUser().getId());
              params.put("resId", ods706eTab1FormBean.getResId());
              params.put("criId", ods706eTab1FormBean.getCriId());

               sqlExecutor.update(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    
    @Override
    public void deleteCriteriaByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
        try{
              String sql1 =
                        "DELETE ODS_USER_FOLLOW_PACKAGE "
                      + "WHERE  RESOURCE_ID = :resId "
                      + "AND RESOURCE_CRITERIA_ID = :criId ";
              
              Map<String, Object> params1 = new HashMap<String, Object>();
              params1.put("resId", ods706eTab1FormBean.getResId());
              params1.put("criId", ods706eTab1FormBean.getCriId());

              sqlExecutor.delete(sql1, params1);
               
              
               
              String sql2 =
                       "DELETE ODS_CRITERIA "
                     + "WHERE  RESOURCE_ID = :resId "
                     + "AND RESOURCE_CRITERIA_ID = :criId ";
 
              Map<String, Object> params2 = new HashMap<String, Object>();
              params2.put("resId", ods706eTab1FormBean.getResId());
              params2.put("criId", ods706eTab1FormBean.getCriId());

              sqlExecutor.delete(sql2, params2);
             
              
              
              String sql3 =
                      "DELETE ODS_RESOURCE_CRITERIA "
                    + "WHERE  RESOURCE_ID = :resId "
                    + "AND ID = :criId ";
            
              Map<String, Object> params3 = new HashMap<String, Object>();
              params3.put("resId", ods706eTab1FormBean.getResId());
              params3.put("criId", ods706eTab1FormBean.getCriId());

              sqlExecutor.delete(sql3, params3);             
              
              
              
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    
    @Override
    public List<Ods706eTab2Dto> findDatasetColsByResId(String resId) {
        String sql =
                "SELECT COLUMN_NAME, DATA_TYPE "
              + "FROM   INFORMATION_SCHEMA.COLUMNS "
              + "WHERE  TABLE_NAME = :resId "
              + "AND DATA_TYPE = 'numeric' "
              + "ORDER BY ORDINAL_POSITION ";
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resId", "ODS_" + resId);
        
        List<Ods706eTab2Dto> datasetCols = odsJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<Ods706eTab2Dto>(
                Ods706eTab2Dto.class));
        
        for (Ods706eTab2Dto datasetCol: datasetCols)
        {
            logger.debug("findDatasetColsByResId:" + ToStringBuilder.reflectionToString(datasetCol));
        }
        
        return datasetCols;
        
        
    }

    
    @Override
    public void createCriteriaDetailByResIdCriId(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
        try{
                String sql =
                        "WITH X "
                      + "     AS (SELECT MAX(CONDITION) AS CONDITION, "
                      + "                1              AS R "
                      + "         FROM   ODS_CRITERIA "
                      + "         WHERE  RESOURCE_ID = :resId "
                      + "                AND RESOURCE_CRITERIA_ID = :criId "
                      + "         GROUP  BY RESOURCE_ID, "
                      + "                   RESOURCE_CRITERIA_ID "
                      + "         UNION "
                      + "         SELECT 0, "
                      + "                0 AS R) "
                      + "INSERT ODS_CRITERIA "
                      + "       (RESOURCE_ID, "
                      + "        RESOURCE_CRITERIA_ID, "
                      + "        CONDITION, "
                      + "        DATA_FIELD, "
                      + "        AGGREGATE_FUNC, "
                      + "        OPERATOR, "
                      + "        TARGET, "
                      + "        CREATED, "
                      + "        UPDATED, "
                      + "        CREATE_USER_ID, "
                      + "        UPDATE_USER_ID) "
                      + "SELECT :resId, "
                      + "       :criId, "
                      + "       CONDITION + 1, "
                      + "       :dataField, "
                      + "       :aggregateFunc, "
                      + "       :operator, "
                      + "       :target, "
                      + "       :created, "
                      + "       :updated, "
                      + "       :createUserId, "
                      + "       :updateUserId "
                      + "FROM   X "
                      + "WHERE  R = (SELECT MAX(R) "
                      + "            FROM   X) ";
        
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("resId", ods706eTab2FormBean.getResId());
              params.put("criId", ods706eTab2FormBean.getCriId());
              params.put("dataField", ods706eTab2FormBean.getDataField());
              params.put("aggregateFunc", ods706eTab2FormBean.getAggregateFunc());
              params.put("operator", ods706eTab2FormBean.getOperator());
              params.put("target", ods706eTab2FormBean.getTarget());
              params.put("created", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("createUserId", UserHolder.getUser().getId());
              params.put("updateUserId", UserHolder.getUser().getId());
              
              sqlExecutor.insert(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    
    @Override
    public void updateCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
            try{
                String sql =
                        "UPDATE ODS_CRITERIA "
                      + "SET     DATA_FIELD = :dataField, "
                      + "        AGGREGATE_FUNC = :aggregateFunc, "
                      + "        OPERATOR = :operator, "
                      + "        TARGET = :target, "
                      + "        UPDATED = :updated, "
                      + "        UPDATE_USER_ID = :updateUserId "
                      + "WHERE  RESOURCE_ID = :resId "
                      + "AND RESOURCE_CRITERIA_ID = :criId "
                      + "AND CONDITION = :condition ";
        
              
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("dataField", ods706eTab2FormBean.getDataField());
              params.put("aggregateFunc", ods706eTab2FormBean.getAggregateFunc());
              params.put("operator", ods706eTab2FormBean.getOperator());
              params.put("target", ods706eTab2FormBean.getTarget());
              params.put("updated", LocalDateTimes.toString(LocalDateTime.now(),"yyyy/MM/dd HH:mm:ss"));
              params.put("updateUserId", UserHolder.getUser().getId());
              params.put("resId", ods706eTab2FormBean.getResId());
              params.put("criId", ods706eTab2FormBean.getCriId());
              params.put("condition", String.valueOf(ods706eTab2FormBean.getCondition()));
    
               sqlExecutor.update(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    
    @Override
    public void deleteCriteriaDetailByResIdCriIdCond(Ods706eTab2FormBean ods706eTab2FormBean) throws Exception{
            try{
                String sql =
                        "DELETE ODS_CRITERIA "
                      + "WHERE  RESOURCE_ID = :resId "
                      + "AND RESOURCE_CRITERIA_ID = :criId "
                      + "AND CONDITION = :condition ";
        
              
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("resId", ods706eTab2FormBean.getResId());
              params.put("criId", ods706eTab2FormBean.getCriId());
              params.put("condition", String.valueOf(ods706eTab2FormBean.getCondition()));
    
               sqlExecutor.delete(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    @Override
    public void deleteCriteriaDetailByResIdCriId(Ods706eTab1FormBean ods706eTab1FormBean) throws Exception{
            try{
                String sql =
                        "DELETE ODS_CRITERIA "
                      + "WHERE  RESOURCE_ID = :resId "
                      + "AND RESOURCE_CRITERIA_ID = :criId ";
        
              
              Map<String, Object> params = new HashMap<String, Object>();
              params.put("resId", ods706eTab1FormBean.getResId());
              params.put("criId", ods706eTab1FormBean.getCriId());
    
               sqlExecutor.delete(sql, params);
              
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
