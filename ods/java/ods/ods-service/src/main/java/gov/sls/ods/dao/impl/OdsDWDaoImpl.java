package gov.sls.ods.dao.impl;

import gov.sls.ods.dao.OdsDWDao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OdsDWDaoImpl implements OdsDWDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("slsuaafrontJdbcTemplate")
    private NamedParameterJdbcTemplate slsuaafrontJdbcTemplate;

    @Override
    public List<Map<String, Object>> findDWHStageData(String tablename, Date start,
            Date end, String dateFeildName, String dbname) {
        StringBuffer sql =new StringBuffer(); 
        sql.append("select * from ").append(tablename);
        Map<String, Object> where = new HashMap<String, Object>();
        boolean whereCondition = false;
        boolean andCondition = false;
        if (null!=start && !StringUtils.isEmpty(dateFeildName)) {
            where.put("startdate", start);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" >= :startdate ");
            andCondition = true;
        }
        if (null!=end && !StringUtils.isEmpty(dateFeildName)) {
            where.put("enddate", end);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" < :enddate ");
            andCondition = true;
        }
        

        log.debug("Ods774xService findDWHStageData:" + sql.toString() + " dbname:" + dbname);
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ("SLSUAAFRONT".equals(dbname)) {
            ((JdbcTemplate)slsuaafrontJdbcTemplate.getJdbcOperations()).setFetchSize(1000);
            list = slsuaafrontJdbcTemplate.queryForList(sql.toString(), where);
        } else {
            ((JdbcTemplate)jdbcTemplate.getJdbcOperations()).setFetchSize(1000);
            list = jdbcTemplate.queryForList(sql.toString(), where);
        }
        
        return list;
    }

    @Override
    public List<Map<String, Object>> findDWHStageDataPage(String tablename, Date start,
            Date end, String dateFeildName, String dbname, int offset, int limit, String orderId) {

        StringBuffer sql =new StringBuffer(); 
        sql.append("select * from (")
        .append("SELECT ")
        .append("*, ROW_NUMBER() OVER (ORDER BY " + orderId + ") as SIPROWNUM FROM ")
        .append(tablename);
        
        Map<String, Object> where = new HashMap<String, Object>();
        boolean whereCondition = false;
        boolean andCondition = false;
        if (null!=start && !StringUtils.isEmpty(dateFeildName)) {
            where.put("startdate", start);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" >= :startdate ");
            andCondition = true;
        }
        if (null!=end && !StringUtils.isEmpty(dateFeildName)) {
            where.put("enddate", end);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" < :enddate ");
            andCondition = true;
        }
        sql.append(") A ")
        .append("WHERE A.SIPROWNUM > " + offset + " ")
        .append("AND A.SIPROWNUM <= " + (offset + limit) + " ");

        log.debug("Ods774xService findDWHStageDataPage:" + sql.toString() + " dbname:" + dbname);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ("SLSUAAFRONT".equals(dbname)) {
            list = slsuaafrontJdbcTemplate.queryForList(sql.toString(), where);
        } else {
            list = jdbcTemplate.queryForList(sql.toString(), where);
        }
        return list;
    }

    @Override
    public int countDWHStageData(String tablename, Date start,
            Date end, String dateFeildName, String dbname) {
        StringBuffer sql =new StringBuffer(); 
        sql.append("select count(1) from ").append(tablename);
        Map<String, Object> where = new HashMap<String, Object>();
        boolean whereCondition = false;
        boolean andCondition = false;
        if (null!=start && !StringUtils.isEmpty(dateFeildName)) {
            where.put("startdate", start);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" >= :startdate ");
            andCondition = true;
        }
        if (null!=end && !StringUtils.isEmpty(dateFeildName)) {
            where.put("enddate", end);
            if(!whereCondition){
                whereCondition = true;
                sql.append(" where ");
            }
            if(andCondition){
                sql.append(" and ");
            }
            sql.append(dateFeildName);
            sql.append(" < :enddate ");
            andCondition = true;
        }
        

        log.debug("Ods774xService countDWHStageData:" + sql.toString() + " dbname:" + dbname);
        int cnt=0;
        if ("SLSUAAFRONT".equals(dbname)) {
            cnt = slsuaafrontJdbcTemplate.queryForObject(sql.toString(), where, Integer.class);
        } else {
            cnt = jdbcTemplate.queryForObject(sql.toString(), where, Integer.class);
        }
        
        return cnt;
    }
}
