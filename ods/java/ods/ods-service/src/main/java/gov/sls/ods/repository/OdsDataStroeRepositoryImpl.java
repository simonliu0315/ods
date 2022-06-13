package gov.sls.ods.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cht.commons.persistence.query.Query;

@Slf4j
@Repository
public class OdsDataStroeRepositoryImpl implements OdsDataStroeRepository {
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;

    public List<Map<String, Object>> findDataStoreAllData(String resourceId, int resourceVer) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resource_ver", resourceVer);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * ");
        sql.append("FROM ODS_" + resourceId.replaceAll("-", "_") + " ");

        sql.append("WHERE ODS_RESOURCE_VER = :resource_ver ");
        log.debug(sql.toString());
        log.debug("param:" + params);
        return odsJdbcTemplate.queryForList(sql.toString(), params);
    }
    
    public List<Map<String, Object>> findOneDataStoreAllData(String resourceId, int resourceVer) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resource_ver", resourceVer);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT TOP 1 * ");
        sql.append("FROM ODS_" + resourceId.replaceAll("-", "_") + " ");

        sql.append("WHERE ODS_RESOURCE_VER = :resource_ver ");
        log.debug(sql.toString());
        log.debug("param:" + params);
        return odsJdbcTemplate.queryForList(sql.toString(), params);
    }

    public List<Map<String, Object>> findDataStoreData(String resourceId, int resourceVer,
            Map<String, Object> filters, String sort, String q, Set<String> columnSet) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resource_ver", resourceVer);
      
        Query.Builder builder = Query.builder();
        builder.append("SELECT * ");
        builder.append("FROM ODS_" + resourceId.replaceAll("-", "_") + " ");

        builder.append("WHERE ODS_RESOURCE_VER = :resource_ver", resourceVer);
        if (filters == null || filters.size() == 0) {

        } else {
            int i = 0;
            for (String key : filters.keySet()) {
//                builder.append("AND [" + key + "] = :" + key + " ", filters.get(key));
//                params.put(key, filters.get(key));
                builder.append("AND [" + key + "] = :p" + i + " ", filters.get(key));
                params.put("p" + i, filters.get(key));
                i++;
            }
        }
        if (StringUtils.isNotBlank(q)) {
            builder.append("AND ( ");
            int i = 0;
            for (String column : columnSet) {
                if (column.equals("ODS_RESOURCE_VER")) {
                    continue;
                }
                if (i == 0) {
                    builder.append("[" + column + "] LIKE :q ", "%" + q + "%");
                } else {
                    builder.append("OR [" + column + "] LIKE :q ", "%" + q + "%");
                }
                params.put("q", "%" + q + "%");
                i++;
            }
            builder.append(") ");
        }
        if (StringUtils.isNotBlank(sort)) {
            builder.append("ORDER BY " + sort.replaceAll("-", "_"));
        }        
        
        log.debug("param:" + params);        
        Query query = builder.build();
        log.debug(query.getString());
        return odsJdbcTemplate.queryForList(query.getString(), params);
    }

    /**
     * {@inheritDoc}
     */
    public int countDataStoreData(String resourceId, int resourceVer,
            Map<String, Object> filters, String sort, String q, Set<String> columnSet) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resource_ver", resourceVer);
      
        Query.Builder builder = Query.builder();
        builder.append("SELECT count(*) ");
        builder.append("FROM ODS_" + resourceId.replaceAll("-", "_") + " ");

        builder.append("WHERE ODS_RESOURCE_VER = :resource_ver", resourceVer);
        if (filters == null || filters.size() == 0) {

        } else {
            int i = 0;
            for (String key : filters.keySet()) {
//                builder.append("AND [" + key + "] = :" + key + " ", filters.get(key));
//                params.put(key, filters.get(key));
                builder.append("AND [" + key + "] = :p" + i + " ", filters.get(key));
                params.put("p" + i, filters.get(key));
                i++;
            }
        }
        if (StringUtils.isNotBlank(q)) {
            builder.append("AND ( ");
            int i = 0;
            for (String column : columnSet) {
                if (column.equals("ODS_RESOURCE_VER")) {
                    continue;
                }
                if (i == 0) {
                    builder.append("[" + column + "] LIKE :q ", "%" + q + "%");
                } else {
                    builder.append("OR [" + column + "] LIKE :q ", "%" + q + "%");
                }
                params.put("q", "%" + q + "%");
                i++;
            }
            builder.append(") ");
        }
        if (StringUtils.isNotBlank(sort)) {
            builder.append("ORDER BY " + sort.replaceAll("-", "_"));
        }        
        
        log.debug("param:" + params);        
        Query query = builder.build();
        log.debug(query.getString());
        return odsJdbcTemplate.queryForInt(query.getString(), params);
    }

    public List<Map<String, Object>> findDataStoreDataPaging(String resourceId, int resourceVer,
            Map<String, Object> filters, int offset, int limit, String sort, String q,
            Set<String> columnSet) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("resource_ver", resourceVer);               
        Query.Builder builder = Query.builder();
        builder.append("SELECT * FROM ( ");
        builder.append("SELECT ");

        builder.append("*, ROW_NUMBER() OVER (ORDER BY ODS_RESOURCE_VER) as ODS_RESOURCE_ROW ");
        builder.append("FROM ODS_" + resourceId.replaceAll("-", "_") + " ");
        builder.append("WHERE ODS_RESOURCE_VER = :resource_ver ", resourceVer);
        if (filters == null || filters.size() == 0) {

        } else {
            int i = 0;
            for (String key : filters.keySet()) {
//                builder.append("AND " + key + " = :" + key + " ", filters.get(key));
//                params.put(key, filters.get(key));
                builder.append("AND [" + key + "] = :p" + i + " ", filters.get(key));
                params.put("p" + i, filters.get(key));
                i++;
            }
        }
        if (StringUtils.isNotBlank(q)) {
            builder.append("AND ( ");
            int i = 0;
            for (String column : columnSet) {
                if (column.equals("ODS_RESOURCE_VER")) {
                    continue;
                }
                if (i == 0) {
                    builder.append("[" + column + "] LIKE :q ", "%" + q + "%");
                } else {
                    builder.append("OR [" + column + "] LIKE :q ", "%" + q + "%");
                }
                params.put("q", "%" + q + "%");
                i++;
            }
            builder.append(") ");
        }

        builder.append(") A ");

        builder.append("WHERE A.ODS_RESOURCE_ROW > " + offset + " ");
        builder.append("AND A.ODS_RESOURCE_ROW <= " + (offset + limit) + " ");
        if (StringUtils.isNotBlank(sort)) {
            builder.append("ORDER BY A." + sort);
        }
        Query query = builder.build();
        return odsJdbcTemplate.queryForList(query.getString(), params);
    }

    public List<Map<String, Object>> findDataStoreMetaData(String resourceId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", "ODS_" + resourceId.replaceAll("-", "_"));
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * ");
        sql.append("FROM INFORMATION_SCHEMA.COLUMNS IC ");
        sql.append("WHERE TABLE_NAME = :tableName");
        log.debug(sql.toString());
        log.debug("param:" + params);
        return odsJdbcTemplate.queryForList(sql.toString(), params);
    }

}
