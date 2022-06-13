package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCriteria;
import gov.sls.ods.dto.Ods772CriteriaFollowUserDto;
import gov.sls.ods.dto.Ods772CriteriaResourceDto;

import java.util.List;


public interface OdsCriteriaRepositoryCustom{

    List<OdsCriteria> getCriteriaByResourceAndCriteria(String resourceId, String criteriaId);

    List<Ods772CriteriaFollowUserDto> checkCriteria(String resourceId, Integer maxVer, String criteriaId,
            String aggregateFunc, String dataField, String operator, String target);

    /**根據resource id list取得OdsCriteria 及 criteriaName
     * @param resourceIdList resourceIdList
     * @return List<Ods772CriteriaResourceDto>
     */
    public List<Ods772CriteriaResourceDto> findByResourceIdIn(List<String> resourceIdList);
}
