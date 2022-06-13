package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsOrgCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class OdsOrgCodeRepositoryImpl implements OdsOrgCodeRepositoryCustom {

    @Autowired
    private NamedParameterJdbcTemplate slsJdbcTemplate;
    
    @Override
    public List<Map<String, Object>> findHsn() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select HSN_CD as 縣市代號 , HSN_NM as 縣市名稱 ");
      sql.append(" from ODS_ORG_CODE ");
      sql.append(" GROUP BY HSN_CD, HSN_NM ");
      Map<String, Object> params = new HashMap<String, Object>();

      List<Map<String, Object>> restlist= slsJdbcTemplate.queryForList(sql.toString(), params);
      return restlist;
    }
    
    @Override
    public List<Map<String, Object>> findTown(String hsnCd) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select TOWN_CD as 鄉鎮代號 , TOWN_NM as 鄉鎮名稱 ");
      sql.append(" from ODS_ORG_CODE ");
      sql.append(" where HSN_CD = :hsnCd ");
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("hsnCd", hsnCd);
      List<Map<String, Object>> restlist= slsJdbcTemplate.queryForList(sql.toString(), params);
      return restlist;
    }

    @Override
    public String findHsnNameByCd(String hsnCd) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select HSN_NM ");
        sql.append(" from ODS_ORG_CODE ");
        sql.append(" where HSN_CD = :hsnCd ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("hsnCd", hsnCd);
        List<Map<String, Object>> restList= slsJdbcTemplate.queryForList(sql.toString(), params);
        return (String)restList.get(0).get("HSN_NM");
    }

    @Override
    public String findTownNameByCd(String hsnCd, String townCd) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select TOWN_NM ");
        sql.append(" from ODS_ORG_CODE ");
        sql.append(" where HSN_CD = :hsnCd and TOWN_CD = :townCd ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("hsnCd", hsnCd);
        params.put("townCd", townCd);
        Map<String, Object> rest= slsJdbcTemplate.queryForMap(sql.toString(), params);
        String townName = "";
        if(!rest.isEmpty()) {
            townName = (String)rest.get("TOWN_NM"); 
        }
        return townName;
    }

    @Override
    public void saveBatch(List<OdsOrgCode> entities) {
        String sql="insert into ODS_ORG_CODE values (:hsnCd,:hsnNm,:townCd,:townNm)";
        for(OdsOrgCode entity: entities) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("hsnCd", entity.getId().getHsnCd());
            params.put("hsnNm", entity.getHsnNm());
            params.put("townCd", entity.getId().getTownCd());
            params.put("townNm", entity.getTownNm());
            slsJdbcTemplate.update(sql, params);
        }
    }
    
}
