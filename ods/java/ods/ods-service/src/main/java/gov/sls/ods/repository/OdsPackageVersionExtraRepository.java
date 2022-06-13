package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageVersionExtra;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageVersionExtraRepository extends EntityRepository<OdsPackageVersionExtra, String>, OdsPackageVersionExtraRepositoryCustom {
    List<OdsPackageVersionExtra> findByPackageIdAndPackageVerOrderByPositionsAsc(String packageId, int packageVer);
}
