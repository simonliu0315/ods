package gov.sls.ods.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.cht.commons.persistence.query.Query;

@Component
public class SqlPaginationHelper {
    private static final Logger log = LoggerFactory.getLogger(SqlPaginationHelper.class);
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    public SqlPaginationHelper() {
        
    }
    
    public <T> Page<T> queryForPage(CharSequence sql, Class<T> clazz, Pageable pageable) {
        return queryForPage(Query.builder().append(sql).build(), clazz, pageable);
    }
    
    public <T> Page<T> queryForPage(CharSequence sql, Map<String, ?> parameters, 
        Class<T> clazz, Pageable pageable) {
        return queryForPage(Query.builder().append(sql, parameters).build(), clazz, pageable);
    }
    
    //Support MS SQL Server 2012 onward
    public <T> Page<T> queryForPage(Query query, Class<T> clazz, Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<T>(new ArrayList<T>(), pageable, 0);
        }
        String queryString = query.getString();
        String queryStringUpperCase = queryString.toUpperCase(); 
        int fromIdx = queryStringUpperCase.indexOf(" FROM ");   
        int orderIdx = queryStringUpperCase.lastIndexOf(" ORDER BY ");
        
        String countKeyWordTmp = queryStringUpperCase.substring(7, fromIdx);//between "SELECT " and "FROM"
        log.info("countKeyWordTmp1:" + countKeyWordTmp);
        int distinctIdx = countKeyWordTmp.indexOf("DISTINCT");
        if (distinctIdx >= 0) {
            countKeyWordTmp = countKeyWordTmp.substring(9);
        }
        log.info("countKeyWordTmp2:" + countKeyWordTmp);        
        int commaIdx = countKeyWordTmp.indexOf(",");
        int lastSpaceIdx = countKeyWordTmp.lastIndexOf(" ");
        String countKeyWord = "*";
        if (commaIdx >= 0) {
            countKeyWord = countKeyWordTmp.substring(0, commaIdx);
            int asIdx = countKeyWord.indexOf(" AS ");
            if (asIdx >= 0) {
                countKeyWord = countKeyWord.substring(0, asIdx);
            }
            countKeyWord = "DISTINCT " + countKeyWord;
        } else if (lastSpaceIdx >= 0) {
            countKeyWord = countKeyWordTmp.substring(0, lastSpaceIdx);
            int asIdx = countKeyWord.indexOf(" AS ");
            if (asIdx >= 0) {
                countKeyWord = countKeyWord.substring(0, asIdx);
            }
            if (!countKeyWord.contains("*") && !countKeyWord.isEmpty()) {
                countKeyWord = "DISTINCT " + countKeyWord;
            }
        } else {
            countKeyWord = countKeyWordTmp;
            int asIdx = countKeyWord.indexOf(" AS ");
            if (asIdx >= 0) {
                countKeyWord = countKeyWord.substring(0, asIdx);
            }
            if (!countKeyWord.contains("*")) {
                countKeyWord = "DISTINCT " + countKeyWord;
            }
        }
        log.info("countKeyWord:" + countKeyWord);
        String queryCountString = "SELECT COUNT(" + countKeyWord + ")";
        log.info("queryCountString3:" + queryCountString);
        if (orderIdx >= 0) {
            queryCountString += queryString.substring(fromIdx, orderIdx);
        } else {
            queryCountString += queryString.substring(fromIdx);
        }
        
        Map<String, Object> parameters = query.getParameters();
        
        boolean isString = clazz.equals(String.class);
        boolean isPrimitive = ClassUtils.isPrimitiveOrWrapper(clazz);
        
        logSqlExecute(queryCountString, parameters);
        
        long rowCount = this.jdbcTemplate.queryForObject(
            queryCountString, parameters, Long.class);
        long pageCount = rowCount / pageable.getPageSize();
        if (rowCount > pageable.getPageSize() * pageCount) {
            pageCount++;
        }
        
        if (pageable.getSort() != null) {            
            String orderby = extractSortSql(pageable.getSort());            
            if (orderIdx >= 0) {
                queryString = queryString.substring(0, orderIdx);
            }
            queryString += " " + orderby;
        }
        queryString += " OFFSET " + pageable.getOffset() + 
            " ROWS FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY";
        logSqlExecute(queryString, parameters);
        
        List<T> result = null;
        if ((isString) || (isPrimitive)) {
            result = this.jdbcTemplate.queryForList(queryString, parameters, clazz);
        } else {
            result = this.jdbcTemplate.query(queryString, parameters, 
                new BeanPropertyRowMapper<T>(clazz));
        }
        return new PageImpl<T>(result, pageable, rowCount);
    }
    
    private String extractSortSql(Sort sort) {
        String ret = "";
        if (sort == null) {
            return ret;
        }
        ret = "ORDER BY";
        Iterator<Sort.Order> orders = sort.iterator();
        while (orders.hasNext()) {
            Sort.Order order = orders.next();
            ret += " " + order.getProperty();
            if (order.getDirection() != null) {
                Sort.Direction d = order.getDirection();
                if (d.compareTo(Sort.Direction.ASC) == 0) {
                    ret += " ASC";
                } else {
                    ret += " DESC";
                }
            }
            if (orders.hasNext()) {
                ret += ",";
            }
        }
        return ret; 
        
    }
    
    private void logSqlExecute(CharSequence sql, Map<String, ?> parameters) {
        if (log.isDebugEnabled()) {
            log.debug("Executing SQL:{}", sql);
            if ((parameters == null) || (parameters.isEmpty())) {
                log.debug("With NO parameters!");
            } else {
                for (Map.Entry<String, ?> entry : parameters.entrySet()) {
                    log.debug("Param:{}, Value:{}", entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
