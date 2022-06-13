package gov.sls.ods.repository;


public interface OdsNoticePackageVersionRepositoryCustom {
    
    /**尚未通知的主題版本更新於ODS_NOTICE_PACKAGE_VERSION，若是新的主題則新增
     * @param packageId packageId
     * @param ver ver
     */
    public void noticePackageVersion(String packageId, int ver);
    
    public void deleteByPackageId(String packageId);
}
