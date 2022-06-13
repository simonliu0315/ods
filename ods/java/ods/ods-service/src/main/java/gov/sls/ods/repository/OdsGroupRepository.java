package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsGroup;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsGroupRepository extends EntityRepository<OdsGroup, String>,
        OdsGroupRepositoryCustom {
    public OdsGroup findById(String id);

    @Query(value = "SELECT A FROM OdsGroup A, OdsGroupPackage B, OdsPackageVersion C WHERE "
            + "A.id = B.id.groupId " + "AND B.id.packageId = C.id.packageId "
            + "AND C.isPublished = 1 " + "AND A.id = :groupId ")
    public List<OdsGroup> getGroupInPublishPackage(@Param("groupId") String groupId);
}
