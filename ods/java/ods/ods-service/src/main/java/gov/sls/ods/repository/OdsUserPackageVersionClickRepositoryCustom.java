package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.dto.Ods707eGridDto;

import java.util.List;

public interface OdsUserPackageVersionClickRepositoryCustom {
    
    public List<Ods707eGridDto> findClickCount(String packageId, String sDate, String eDate);
    
    public List<Ods351eDataDto> findPopularUserPackageClick(List<String> idList);
    
    public void deleteByPackageId(String packageId);
}
