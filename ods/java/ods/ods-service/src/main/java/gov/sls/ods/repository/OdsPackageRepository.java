package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackage;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageRepository extends EntityRepository<OdsPackage, String>, OdsPackageRepositoryCustom {
    List<OdsPackage> findByName(String name);
    List<OdsPackage> findByNameLikeAndDescriptionLike(String name, String descripton);
    
    @Query(value = "SELECT A FROM OdsPackage A, OdsPackageVersion B WHERE "
            + "A.id = B.id.packageId "
            + "AND B.id.packageId = :packageId "
            + "AND B.isPublished = 1 ")
    public List<OdsPackage> getPublishPackage(@Param("packageId") String packageId);
    public List<OdsPackage> findByIdIn(List<String> idList);
    List<OdsPackage> findById(String packageId);
    
    @Query(value = "SELECT id FROM OdsPackage WHERE code = :code ")
    String findIdByCode(@Param("code") String code);
}
