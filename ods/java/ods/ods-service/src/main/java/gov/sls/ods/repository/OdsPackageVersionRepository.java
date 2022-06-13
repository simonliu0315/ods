package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageVersionRepository extends
        EntityRepository<OdsPackageVersion, OdsPackageVersionPK>, OdsPackageVersionRepositoryCustom {

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND isPublished = 1 AND id.ver <> 0 ORDER BY ver DESC")
    List<OdsPackageVersion> getPublishPackage(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND id.ver = :version AND isPublished = 1 ORDER BY id.ver DESC")
    List<OdsPackageVersion> getPublishPackageVer(@Param("packageId") String packageId,
            @Param("version") int version);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND id.ver IN "
            + "( SELECT MAX(B.id.ver) FROM OdsPackageVersion B WHERE id.packageId = :packageId AND isPublished = 1)")
    List<OdsPackageVersion> getPublishPackageVerLast(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND id.ver<>0 ORDER BY ver DESC")
    List<OdsPackageVersion> getPackageVersionAll(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND delMk<>true AND id.ver<>0 ORDER BY ver DESC")
    List<OdsPackageVersion> getPackageVersionWithoutDelMk(@Param("packageId") String packageId);

    @Query(value = "SELECT MAX(A.id.ver) FROM OdsPackageVersion A WHERE A.id.packageId = :packageId)")
    Integer getMaxPackageVersion(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND id.ver=0")
    List<OdsPackageVersion> getPackageVersionZero(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A WHERE "
            + "id.packageId = :packageId AND id.ver IN "
            + "( SELECT MAX(B.id.ver) FROM OdsPackageVersion B WHERE id.packageId = :packageId AND isPublished = true AND id.ver<>0)")
    List<OdsPackageVersion> getPublishPackageVerMax(@Param("packageId") String packageId);

    @Query(value = "SELECT A FROM OdsPackageVersion A, OdsPackageResource B WHERE "
            + "A.id.packageId = B.id.packageId AND A.id.ver = B.id.packageVer "            
            + "AND A.id.packageId = :packageId AND A.id.ver = :packageVer "
            + "AND B.id.resourceId = :resourceId AND B.id.resourceVer = :resourceVer "
            + "AND A.isPublished = 1 ORDER BY A.id.ver DESC")
    List<OdsPackageVersion> getPublishPackageVerResourceVer(@Param("packageId") String packageId,
            @Param("packageVer") int packageVer, @Param("resourceId") String resourceId,
            @Param("resourceVer") int resourceVer);
    
    @Query(value = "SELECT A FROM OdsPackageVersion A, OdsPackageResource B WHERE "
            + "A.id.packageId = B.id.packageId AND A.id.ver = B.id.packageVer "            
            + "AND B.id.resourceId = :resourceId AND B.id.resourceVer = :resourceVer "
            + "AND A.isPublished = 1 ORDER BY A.id.ver DESC")
    List<OdsPackageVersion> getPublishPackageVerByResourceVer(@Param("resourceId") String resourceId,
            @Param("resourceVer") int resourceVer);
}
