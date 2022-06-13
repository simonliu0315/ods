package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsUserPackageVersionClick;
import gov.sls.ods.dto.OdsUserPackageVersionClickExt;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsUserPackageVersionClickRepository extends EntityRepository<OdsUserPackageVersionClick, String>, OdsUserPackageVersionClickRepositoryCustom {

    @Query(value = "SELECT A.packageId, A.packageVer, COUNT(*) as count FROM OdsUserPackageVersionClick A WHERE "            
            + "A.packageId = :packageId "
            + "AND A.packageVer = :packageVer "
            + "GROUP BY A.packageId, A.packageVer ")
    List<OdsUserPackageVersionClickExt> getGroupByPackageIdPackageVer(
            @Param("packageId") String packageId, @Param("packageVer") String packageVer);
}
