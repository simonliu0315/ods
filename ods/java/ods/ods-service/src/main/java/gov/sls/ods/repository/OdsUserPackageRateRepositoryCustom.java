package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.dto.UserPackageRateAggregateDto;

import java.util.List;

public interface OdsUserPackageRateRepositoryCustom {
    
    public List<UserPackageRateAggregateDto> getAggregateByPackageId(String packageId);
    
    public List<Ods707eGridDto> findAvgRateAneTotCount(String packageId, String sDate, String eDate);
    
    public void deleteByPackageId(String packageId);
    
    public List<OdsUserPackageRate> findByPkgIdAndUserId(String packageId, String userId);
}
