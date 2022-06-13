package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.Ods351eDataDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.PortalStat;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OdsPackageVersionRepositoryCustom {

    public List<PackageAndResourceDto> findPackageAndResource(String packageId, int packageVer, int rowPosition, int columnPosition);
    
    public void deletePackageVersionZero(String packageId);
    
    public List<PackageAndResourceDto> findPackageAndResourceAndLayout(String packageId, int packageVer);

    /**本週主題更新數統計
     * @return
     */
    public List<PortalStat> findPackageUpdateCount();
    /**本月主題版本更新數
     * @return
     */
    public List<PortalStat> findMonthPackageUpdateCount();
    
    public List<Ods351eDataDto> findLatestOdsPackageVersion(List<String> idList);
    
    public void deleteByPackageId(String packageId);

    /**
     * 取得需新增／更新索引之PACKAGE_VERSION
     * 
     * @param preExecuteDate
     *            上一次處理成功時間
     * @return List<OdsPackageVersion>
     */
    public List<OdsPackageVersion> getReadyIndexPackageByPreExecuteDate(Date preExecuteDate);
    

    /**
     * 取得需刪除之PACKAGE_VERSION
     * 
     * @param preExecuteDate
     *            上一次處理成功時間
     * @return List<OdsPackageVersion>
     */
    public List<OdsPackageVersion> getDelIndexPackageByPreExecuteDate(Date preExecuteDate);
    
    
    public List<OdsPackageVersion> getRssData();
}
