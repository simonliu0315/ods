package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageLayout;
import gov.sls.entity.ods.OdsPackageLayoutPK;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageLayoutRepository extends EntityRepository<OdsPackageLayout, OdsPackageLayoutPK>, OdsPackageLayoutRepositoryCustom {

    List<OdsPackageLayout> findByIdPackageIdAndIdPackageVerOrderByIdRowPositionAscIdColumnPositionAsc(String packageId, int packageVer);
}
