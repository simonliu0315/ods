package gov.sls.ods.repository;

import gov.sls.ods.dto.EPaper;
import gov.sls.ods.dto.Ods302eDto;
import gov.sls.ods.dto.Ods703eGridDto;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.dto.PackageInfo;
import gov.sls.ods.dto.PortalStat;
import gov.sls.ods.dto.RSSItem;

import java.util.Date;
import java.util.List;

public interface OdsPackageRepositoryCustom {

    public List<Ods703eGridDto> findPackageAndVersion(String name, String description);

    /**
     * 依據條件查詢主題列表
     * 
     * @param packageName
     *            主題名稱(將會用like查詢)
     * @param tagList
     *            標籤清單
     * @param fileExtList
     *            檔案格式清單
     * @param orderByType
     *            0排序依據更新時間，1排序依據熱門排行
     * @return List<Ods302eDto>主題清單
     */
    public List<Ods302eDto> findPackage(String packageName, List<String> tagList,
            List<String> fileExtList, int orderByType, List<String> idList);
    
    public List<Ods707eGridDto> findPackages(String name, String desc, String sDate, String eDate);
    
    public void updateImageUrl(String packageId);

    /**取得一週以來更新的主題數量
     * @return
     */
    public List<PortalStat> findPackageCount();
    /**取得所有主題數量
     * @return
     */
    public List<PortalStat> findAllPackageCount();
    
    public List<EPaper> findEPaper(Date preDate);
    
    public List<RSSItem> findRss();
    
    public List<PackageInfo> findPackageInfo();
}
