package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCriteria;
import gov.sls.entity.ods.OdsCriteriaPK;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsCriteriaRepository extends EntityRepository<OdsCriteria, OdsCriteriaPK>, OdsCriteriaRepositoryCustom {

}
