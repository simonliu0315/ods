package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageExtra;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageExtraRepository extends EntityRepository<OdsPackageExtra, String>, OdsPackageExtraRepositoryCustom {
    List<OdsPackageExtra> findByPackageId(String packageId);
    List<OdsPackageExtra> findByPackageIdOrderByPositionsAsc(String packageId);
}
