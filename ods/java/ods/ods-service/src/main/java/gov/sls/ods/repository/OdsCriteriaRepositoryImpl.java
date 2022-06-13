/**
 * 
 */
package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCriteria;
import gov.sls.ods.dto.Ods772CriteriaFollowUserDto;
import gov.sls.ods.dto.Ods772CriteriaResourceDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;

/**
 * 
 */
@Slf4j
public class OdsCriteriaRepositoryImpl implements OdsCriteriaRepositoryCustom {

    @Autowired
    private SqlExecutor executor;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;

    @Override
    public List<OdsCriteria> getCriteriaByResourceAndCriteria(String resourceId, String criteriaId) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT *")
                .append("FROM ODS_RESOURCE_CRITERIA RC")
                .append("WHERE RC.RESOURCE_ID = :resourceId", resourceId)
                .append("AND RC.ID = :criteriaId", criteriaId);
        Query query = builder.build();
        return executor.queryForList(query, OdsCriteria.class);
    }

    @Override
    public List<Ods772CriteriaFollowUserDto> checkCriteria(String resourceId, Integer ver, String criteriaId,
            String aggregateFunc, String dataField, String operator, String target) {
        String resourceIdrep = resourceId.replaceAll("-", "_");
        operator = operator.replaceAll("＞", ">");
        operator = operator.replaceAll("＜", "<");
        operator = operator.replaceAll("＝", "=");
        operator = operator.replaceAll("！", "!");
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT '"+resourceId+"' AS resourceId, ")
                .append(" '"+criteriaId+"' AS criteriaId, ")
                .append(" DS_F.ver, DS_F.aggFunc")
                .append(" FROM (SELECT DS.ODS_RESOURCE_VER ver, ")
                .append(aggregateFunc + " (DS.\"" + dataField + "\") AS aggFunc ")
                .append("      FROM ODS_" + resourceIdrep + " DS")
                .append("      WHERE ODS_RESOURCE_VER = :ver ")
                .append("      GROUP BY DS.ODS_RESOURCE_VER) AS DS_F")
                .append(" WHERE DS_F.aggFunc " + operator + " :target");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("target", target);
        params.put("ver", ver);
        log.info("sql:{}", builder.toString());
        log.info("ver:{}", ver);
        return odsJdbcTemplate.query(builder.toString(), params, new BeanPropertyRowMapper<Ods772CriteriaFollowUserDto>(Ods772CriteriaFollowUserDto.class));
    }

    @Override
    public List<Ods772CriteriaResourceDto> findByResourceIdIn(List<String> resourceIdList) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT RC.NAME, CRI.*,")
                .append("     (select max(ver) from ods_resource_version orv where orv.resource_id = rc.RESOURCE_ID) maxVer")
                .append("FROM ODS_RESOURCE_CRITERIA RC, ODS_CRITERIA CRI")
                .append("WHERE RC.RESOURCE_ID = CRI.RESOURCE_ID")
                .append("AND RC.ID = CRI.RESOURCE_CRITERIA_ID")
                .expandIterableParameters(true)
                .appendWhen(!CollectionUtils.isEmpty(resourceIdList),
                            "AND CRI.RESOURCE_ID in (:resourceIdList)", resourceIdList);
        Query query = builder.build();
        return executor.queryForList(query, Ods772CriteriaResourceDto.class);
    }
}
