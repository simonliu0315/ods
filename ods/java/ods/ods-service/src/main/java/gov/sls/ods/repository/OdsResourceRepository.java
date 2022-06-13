package gov.sls.ods.repository;

import java.util.List;

import gov.sls.entity.ods.OdsResource;
import gov.sls.ods.dto.Ods772CriteriaResourceDto;

import com.cht.commons.persistence.repositories.EntityRepository;


public interface OdsResourceRepository extends EntityRepository<OdsResource, String>, OdsResourceRepositoryCustom {

    /**
     * @param resourceIdList resourceIdList
     * @return List<OdsResource>
     */
    public List<OdsResource> findByIdIn(List<String> idList);
}
