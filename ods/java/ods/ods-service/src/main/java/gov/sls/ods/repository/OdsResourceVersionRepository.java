package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.entity.ods.OdsResourceVersionPK;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsResourceVersionRepository extends
        EntityRepository<OdsResourceVersion, OdsResourceVersionPK>, OdsResourceVersionRepositoryCustom {
    @Query(value = "SELECT A FROM OdsResourceVersion A WHERE "
            + "id.resourceId = :resourceId AND delMk<>true AND id.ver<>0 ORDER BY ver DESC")
    List<OdsResourceVersion> findResourceVersionWithoutDelMk(@Param("resourceId") String resourceId);

    @Query(value = "SELECT B FROM OdsPackageVersion A, OdsPackageResource B WHERE "
            + "A.id.packageId = :packageId "
            + "AND A.id.packageId = B.id.packageId AND A.id.ver = B.id.packageVer "            
            + "AND B.id.resourceId = :resourceId "
            + "AND A.isPublished = 1 "
            + "AND B.id.resourceVer IN "
            + "( SELECT MAX(C.id.resourceVer) FROM OdsPackageResource C WHERE id.resourceId = :resourceId )"
            + "ORDER BY A.id.ver DESC")
    List<OdsPackageResource> findResourceVerLast(@Param("packageId") String packageId, @Param("resourceId") String resourceId);
}
