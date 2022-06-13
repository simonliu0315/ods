package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsPackageResourcePK;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageResourceRepository extends EntityRepository<OdsPackageResource, OdsPackageResourcePK>, OdsPackageResourceRepositoryCustom {
}
