package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsLayout;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsLayoutRepository extends EntityRepository<OdsLayout, String> {
    List<OdsLayout> findByName(String name);
    List<OdsLayout> findByNameLike(String name);
}
