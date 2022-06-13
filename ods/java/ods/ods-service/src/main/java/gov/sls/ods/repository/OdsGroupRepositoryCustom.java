package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsGroup;
import gov.sls.ods.dto.Ods301eDto;
import gov.sls.ods.dto.Ods704eFormBean;
import gov.sls.ods.dto.Ods704eGrid1Dto;

import java.util.List;

public interface OdsGroupRepositoryCustom {

    /**
     * 依據條件查詢主題群組列表
     * 
     * @param groupName
     *            主題群組名稱(將會用like查詢)
     * @param tagList
     *            標籤清單
     * @param fileExtList
     *            檔案格式清單
     * @param identityList
     *            分眾名稱清單
     * @param orderByType
     *            0排序依據更新時間，1排序依據熱門排行
     * @param userType 
     * @return List<OdsGroup>主題群組清單
     */
    public List<OdsGroup> findGroup(String groupName, List<String> tagList,
            List<String> fileExtList, List<String> identityList, int orderByType, String userType);

    /**依據groupId取得相關聯的packageList
     * @param gorupId gorupId
     * @return List<OdsPackage>
     */
    public List<Ods301eDto> findPackagesByGroupId(String gorupId, List<String> idList);
    
    public List<OdsGroup> findGroupByName(String name, String description);
    
    public List<Ods704eGrid1Dto> findPackageByGroupId(String groupId);

    public String createGroupByNameDesc(String name, String description, String fileExtension)
            throws Exception;

    public void updateGroupByNameDescIdtIdLstPkgIdLst(Ods704eFormBean ods704eDto) throws Exception;
    
    public void deleteGroupByGroupId(Ods704eFormBean ods704eDto) throws Exception;
}
