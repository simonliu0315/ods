package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageTag;

import java.util.Collection;
import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageTagRepository extends EntityRepository<OdsPackageTag, String>, OdsPackageTagRepositoryCustom {

    List<OdsPackageTag> findByPackageId(String packageId);

    public List<OdsPackageTag> findByIdIn(Collection<String> idList);
}
