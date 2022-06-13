package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCategory;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsCategoryRepository extends EntityRepository<OdsCategory, String>, OdsCategoryRepositoryCustom {

}
