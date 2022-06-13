package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsCodes;
import gov.sls.entity.ods.OdsCodesPK;

import java.util.List;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsCodesRepository extends EntityRepository<OdsCodes, OdsCodesPK> {
    public List<OdsCodes> findByCodeTypeAndValueNot(String codeType, String value);
}
