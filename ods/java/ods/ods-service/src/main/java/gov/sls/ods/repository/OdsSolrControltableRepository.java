package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsSolrControltable;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsSolrControltableRepository extends
        EntityRepository<OdsSolrControltable, String>, OdsSolrControltableRepositoryCustom {

}
