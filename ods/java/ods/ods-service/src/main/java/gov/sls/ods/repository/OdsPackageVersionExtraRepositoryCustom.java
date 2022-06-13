package gov.sls.ods.repository;


public interface OdsPackageVersionExtraRepositoryCustom {

    public void deleteByPackageIdAndPackageVer(String packageId, int packageVer);
    
    public void deleteByPackageId(String packageId);
}
