package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsOrgCode;
import gov.sls.entity.ods.OdsOrgCodePK;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cht.commons.persistence.repositories.EntityRepository;

public interface OdsOrgCodeRepository extends EntityRepository<OdsOrgCode, OdsOrgCodePK>{

    @Modifying
    @Query(value="delete from OdsOrgCode")
    public void deleteAll();

}
