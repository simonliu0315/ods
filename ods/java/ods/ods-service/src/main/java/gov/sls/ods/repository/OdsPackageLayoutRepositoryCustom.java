package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackageLayout;


public interface OdsPackageLayoutRepositoryCustom {
    
    public void deletePackageLayout(String packageId, int packageVer);
    
    public void insertPackageLayout(OdsPackageLayout odsPackageLayout);
    
    public void deleteByPackageId(String packageId);
}
