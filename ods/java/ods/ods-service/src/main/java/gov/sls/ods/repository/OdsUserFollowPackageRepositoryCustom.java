package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.dto.Ods352eDataDto;
import gov.sls.ods.dto.Ods707eGridDto;

import java.util.List;

public interface OdsUserFollowPackageRepositoryCustom {

    public List<OdsUserFollowPackage> getResourceCriteriaInPackageResource(String packageId,
            int ver, String userId);
    
    public List<Ods707eGridDto> findFollowCount(String packageId, String sDate, String eDate);

    /**取得訂閱 更新即通知 的 使用者
     * @return List<OdsUserFollowPackage>
     */
    public List<OdsUserFollowPackage> findUpdateFollowUser();

    /**取得訂閱門檻值 的 使用者
     * @return List<OdsUserFollowPackage>
     */
    public List<OdsUserFollowPackage> findCriteriaFollowUser();
    
    public void deleteByPackageId(String packageId);

    public List<Ods352eDataDto> getOdsUserFollowPackageByUser(String userId, boolean isPreview, List<String> idList);

    public void deleteUserFollowPackageByUserIdPackageIdList(String userId,
            List<String> packageIdListAry);
}
