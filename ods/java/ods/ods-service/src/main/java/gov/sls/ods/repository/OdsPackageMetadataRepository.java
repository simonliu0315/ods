package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageMetadata;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageMetadataRepository extends EntityRepository<OdsPackageMetadata, String>, OdsPackageMetadataRepositoryCustom {
    List<OdsPackageMetadata> findByPackageId(String packageId);
}
