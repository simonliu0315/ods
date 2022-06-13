package gov.sls.ods.repository;

import java.util.List;

import gov.sls.entity.ods.OdsUserFollowPackage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsUserFollowPackageRepository extends
        EntityRepository<OdsUserFollowPackage, String>, OdsUserFollowPackageRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM OdsUserFollowPackage WHERE "
            + "userId = :userId "
            + "AND packageId = :packageId ")
    void deleteByUserIdPackageId(
            @Param("userId") String userId, @Param("packageId") String packageId);
    
    @Query(value = "SELECT A FROM OdsUserFollowPackage A WHERE "
            + "userId = :userId "
            + "AND packageId = :packageId "
            + "AND resourceId IS NULL "
            + "AND resourceCriteriaId IS NULL ")
    List<OdsUserFollowPackage> getByUserIdPackageId(
            @Param("packageId") String packageId, @Param("userId") String userId);
}
