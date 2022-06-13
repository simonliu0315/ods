package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageDocument;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsPackageDocumentRepository extends EntityRepository<OdsPackageDocument, String> {

    @Query(value = "SELECT d FROM OdsPackageDocument d WHERE d.packageId = :packageId ")
    List<OdsPackageDocument> findByPackageId(@Param("packageId") String packageId);
    
}
