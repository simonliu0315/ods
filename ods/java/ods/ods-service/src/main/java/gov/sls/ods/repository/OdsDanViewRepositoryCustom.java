package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsDanView;

import java.util.List;


public interface OdsDanViewRepositoryCustom {
    public List<OdsDanView> findDanViewByDanWbkId(String danWbkId);
}
