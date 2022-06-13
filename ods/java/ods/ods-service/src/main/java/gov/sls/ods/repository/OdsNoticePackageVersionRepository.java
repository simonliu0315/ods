package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsNoticePackageVersion;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsNoticePackageVersionRepository extends
        EntityRepository<OdsNoticePackageVersion, OdsNoticePackageVersion>,
        OdsNoticePackageVersionRepositoryCustom {

}
