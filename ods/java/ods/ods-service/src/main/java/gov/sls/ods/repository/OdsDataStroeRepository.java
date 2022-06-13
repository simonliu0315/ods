package gov.sls.ods.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OdsDataStroeRepository {

    public List<Map<String, Object>> findDataStoreAllData(String resourceId, int resourceVer);
    
    public List<Map<String, Object>> findDataStoreData(String resourceId, int resourceVer,
            Map<String, Object> filters, String sort, String q, Set<String> columnSet);
    
    public List<Map<String, Object>> findDataStoreDataPaging(String resourceId, int resourceVer,
            Map<String, Object> filters, int offset, int limit, String sort, String q, Set<String> columnSet);
    
    public List<Map<String, Object>> findDataStoreMetaData(String resourceId);
    

    /**分頁計算筆數
     * @param resourceId  resourceId 
     * @param resourceVer resourceVer
     * @param filters     filters    
     * @param sort        sort       
     * @param q           q          
     * @param columnSet   columnSet  
     * @return int
     */
    public int countDataStoreData(String resourceId, int resourceVer,
            Map<String, Object> filters, String sort, String q, Set<String> columnSet);
    

    /**分頁計算 只取一筆，用來查 column name的
     * @param resourceId  resourceId  
     * @param resourceVer resourceVer 
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findOneDataStoreAllData(String resourceId, int resourceVer);
}
