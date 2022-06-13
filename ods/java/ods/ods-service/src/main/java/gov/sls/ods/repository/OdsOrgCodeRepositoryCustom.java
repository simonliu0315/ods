package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsOrgCode;

import java.util.List;
import java.util.Map;

public interface OdsOrgCodeRepositoryCustom {
    
    /**取得 縣市代號 ,縣市名稱 清單
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findHsn();
    
    
    /**取得 hsnCd下的 鄉鎮代號 , 鄉鎮名稱
     * @param hsnCd 縣市代號
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findTown(String hsnCd) ;
    
    /**
     * 取縣市名稱
     * @param hsnCd
     * @return
     */
    public String findHsnNameByCd(String hsnCd);

    /**
     * 取鄉鎮名稱
     * @param hsnCd
     * @return
     */
    public String findTownNameByCd(String hsnCd,String townCd);

    public void saveBatch(List<OdsOrgCode> entities);
    
}
