package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsGroupPackage;
import gov.sls.entity.ods.OdsGroupPackagePK;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsGroupPackageRepository extends EntityRepository<OdsGroupPackage, OdsGroupPackagePK>,
OdsGroupPackageRepositoryCustom {

}
