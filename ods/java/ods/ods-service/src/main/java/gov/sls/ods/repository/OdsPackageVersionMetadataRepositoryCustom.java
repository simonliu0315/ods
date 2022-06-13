package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods703eTab2Dto;

import java.util.List;

public interface OdsPackageVersionMetadataRepositoryCustom {
    
    public void deleteByPackageIdAndPackageVer(String packageId, int packageVer);
    
    public List<Ods703eTab2Dto> getPackageVersionMetatemplate(String packageId, int packageVer);
    
    public void deleteByPackageId(String packageId);
}
