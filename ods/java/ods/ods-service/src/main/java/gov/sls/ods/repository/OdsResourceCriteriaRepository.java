package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceCriteria;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsResourceCriteriaRepository extends
        EntityRepository<OdsResourceCriteria, String> , OdsResourceCriteriaRepositoryCustom {

    List<OdsResourceCriteria> findByResourceIdAndName(String resourceId, String name);
    
    @Query(value = "SELECT A FROM OdsResourceCriteria A WHERE "
            + "EXISTS ( SELECT B.id.resourceId FROM OdsPackageResource B WHERE "
            + "B.id.resourceId = A.id.resourceId "
            + "AND B.id.packageId = :packageId "
            + "AND B.id.packageVer = :packageVer) ")
    List<OdsResourceCriteria> getResourceCriteriaInPackageResource(
            @Param("packageId") String packageId, @Param("packageVer") int ver);
}
