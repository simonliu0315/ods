package gov.sls.ods.repository;


public interface OdsGroupPackageRepositoryCustom {

    void createGroupPackageByGupIdPkgIdList(String groupId, String selPackageIdList);
    
    public void deleteByPackageId(String packageId);

}
