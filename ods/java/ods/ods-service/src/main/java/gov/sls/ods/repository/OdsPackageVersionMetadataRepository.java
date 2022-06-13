package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageVersionMetadata;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageVersionMetadataRepository extends EntityRepository<OdsPackageVersionMetadata, String>, OdsPackageVersionMetadataRepositoryCustom {
    List<OdsPackageVersionMetadata> findByPackageId(String packageId, int packageVer);
}
