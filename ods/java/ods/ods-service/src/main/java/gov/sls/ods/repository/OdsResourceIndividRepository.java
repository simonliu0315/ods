package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceIndivid;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsResourceIndividRepository extends EntityRepository<OdsResourceIndivid, String>, OdsResourceIndividRepositoryCustom {
    
}
