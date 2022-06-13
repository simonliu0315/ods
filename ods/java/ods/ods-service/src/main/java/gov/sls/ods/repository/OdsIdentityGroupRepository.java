package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsIdentityGroup;
import gov.sls.entity.ods.OdsIdentityGroupPK;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsIdentityGroupRepository extends EntityRepository<OdsIdentityGroup, OdsIdentityGroupPK>,
OdsIdentityGroupRepositoryCustom {

}
