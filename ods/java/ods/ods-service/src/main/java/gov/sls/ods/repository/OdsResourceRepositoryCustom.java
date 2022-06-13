package gov.sls.ods.repository;


import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsPackageResourcePK;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsResource;
import gov.sls.ods.dto.Ods701e02Tab1FormBean;
import gov.sls.ods.dto.Ods701e05Tab1FormBean;
import gov.sls.ods.dto.Ods701eGrid1Dto;
import gov.sls.ods.dto.Ods701eGrid2Dto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OdsResourceRepositoryCustom {
    
    /**取得年度資料不換算單位為億元
     * @param fyr fyr
     * @return List<Fms421rDto>
     */
    //public List<Fms421rDto> findByFyr(String fyr);

    /**
     * @param fms421rDtoList fms421rDtoList
     */
    //public void updateRsltByFyrAndmday(List<Fms421rDto> fms421rDtoList);

    /**取得年度資料換算單位為億元
     * @param fyr fyr
     * @return List<Fms421rDto>
     */
    //public List<Fms421rDto> findByFyrRound(String fyr);

    /**刪除年度資料
     * @param name 
     * @param viewName 
     * @param workbookName 
     * @param fyr fyr
     */
    //public void deleteByFyr(String fyr);
    
    List<Ods701eGrid1Dto> findAllRes(String name, String description, String workbookName, String viewName);
    
    List<Ods701eGrid2Dto> findResDetailNDelByResId(String resId, String name, String description);
    
    List<Ods701eGrid2Dto> findResDetailAllByResId(String resId);
    
    void createRes(OdsResource odsResource);
    
    Map<String, Object> createRes(Ods701e02Tab1FormBean ods701e02Tab1FormBean, String fileExtension, String resType, String workbookId, String viewId, String workbookName) throws Exception;
    
    void saveGrid1(List<Ods701eGrid1Dto> updOds701eGrid1Dtos);
    
    //String createResVersion(Ods701e05Tab1FormBean ods701e05Tab1FormBean, String fileExtension, String resType);
    
    Map<String, Object> createResVersion(Ods701e05Tab1FormBean ods701e05Tab1FormBean, String fileExtension, String resType);
    
    void saveGrid2(List<Ods701eGrid2Dto> updOds701eGrid2Dtos);
    
    List<OdsPackageVersion>  findRelatePackageInfoList(List<Ods701eGrid2Dto> updOds701eGrid2Dtos);
    
    void createDsTempTbl(String resId, int ver, List<String> fileHeaderAry) throws Exception;
    
    List<Boolean> insertDsTempTbl(String resId, int ver, List<Boolean> numericDataTypeAry, List<String> fileHeaderAry, List<List<String>> fileContentAryList);
    
    void createDsTbl(String resId, List<Boolean> numericDataTypeAry, List<String> fileHeaderAry);
    
    void insertDsTbl(String resId, int ver, List<String> fileHeaderAry) throws Exception;
    
    void dropDsTempTbl(String resId, int ver);
    
    void updateResVerToDsInfo(String resId, int ver, String toDatastoreSync, String toDatastoreDate, String toDatastoreSuccess);
    
    List<Ods703eTab2DialogDto> findResByNameAndCategory(String resName, String catId);
    
    List<Ods703eTab2DialogDto> findCommonRes();
    
    Map<String, Object> saveFileRefreshUpload(Ods701eGrid2Dto ods701eGrid2Dto,
            String fileExtension, String resourceType);
    
    Map<String, Object> saveFileRefreshImport(
            Ods701eGrid2Dto ods701eGrid2Dto, String fileExtension,
            String resourceType, String workbookId, String viewId);
    
    List<OdsResource> findDsTbl(String resId) throws Exception;
    
    /**已歸戶載具分析
     * @param reourdeId    reourdeId   
     * @param barCode      barCode     
     * @param invoiceDateS invoiceDateS
     * @param invoiceDateE invoiceDateE
     * @param hsnNm        hsnNm       
     * @param townNm       townNm      
     * @param cardTypeNm   cardTypeNm  
     * @param cardCodeNm   cardCodeNm  
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds371Details(String reourdeId, String barCode
            , String invoiceDateS, String invoiceDateE, String hsnNm, String townNm
            , String cardTypeNm, String cardCodeNm);
    
    /**受捐贈機關或團體個別化主題-受捐贈機關或團體捐贈統計查詢
     * @param reourdeId  reourdeId  
     * @param invoiceYmS invoiceYmS 
     * @param invoiceYmE invoiceYmE 
     * @param hsnNm      hsnNm      
     * @param townNm     townNm     
     * @param busiChiNm  busiChiNm  
     * @param cardTypeNm cardTypeNm 
     * @param cardCodeNm cardCodeNm 
     * @param ban        ban        
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds372Details(String reourdeId, String invoiceYmS
            , String invoiceYmE, String hsnNm, String townNm, String busiChiNm
            , String cardTypeNm, String cardCodeNm, String ban);
    
    /**受捐贈機關或團體個別化主題-所有受捐贈機關或團體捐贈統計查詢
     * @param reourdeId  reourdeId  
     * @param invoiceYmS invoiceYmS 
     * @param invoiceYmE invoiceYmE 
     * @param hsnNm      hsnNm      
     * @param busiChiNm  busiChiNm  
     * @param cardTypeNm cardTypeNm 
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds373Details(String reourdeId, String invoiceYmS
            , String invoiceYmE, String hsnNm, String busiChiNm, String cardTypeNm);
    
    /**營業人個別化主題-營業人電子發票統計查詢
     * @param reourdeId    reourdeId    
     * @param invoiceDateS invoiceDateS 
     * @param invoiceDateE invoiceDateE 
     * @param ban          ban          
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds374Details(String reourdeId, String invoiceDateS
            , String invoiceDateE, String ban);
    
    /**營業人個別化主題-所有營業人電子發票統計查詢
     * @param reourdeId    reourdeId    
     * @param invoiceDateS invoiceDateS 
     * @param invoiceDateE invoiceDateE 
     * @param hsnNm        hsnNm        
     * @param townNm       townNm       
     * @param invType      invType      
     * @param bscd2Nm      bscd2Nm      
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds375Details(String reourdeId, String invoiceDateS
            , String invoiceDateE, String hsnNm, String townNm, String invType, String bscd2Nm);
    
    /**營業人個別化主題-營業人營所稅統計查詢
     * @param reourdeId reourdeId 
     * @param ban       ban       
     * @param dataYrS   dataYrS   
     * @param dataYrE   dataYrE   
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds376Details(String reourdeId, String ban, String dataYrS,String dataYrE);
    
    /**營業人個別化主題-所有營業人營所稅統計查詢
     * @param reourdeId reourdeId 
     * @param dataYrS   dataYrS   
     * @param dataYrE   dataYrE   
     * @param hsnNm     hsnNm     
     * @param townNm    townNm    
     * @param bscd2Nm   bscd2Nm   
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds377Details(String reourdeId, String dataYrS,String dataYrE,String hsnNm,String townNm, String bscd2Nm);
    
    /**營業人個別化主題-營業人營業稅統計查詢
     * @param reourdeId reourdeId 
     * @param ban       ban       
     * @param blYmS     blYmS     
     * @param blYmE     blYmE     
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds378Details(String reourdeId, String ban, String blYmS,String blYmE);
    
    /**營業人個別化主題-所有營業人營業稅統計查詢
     * @param reourdeId reourdeId 
     * @param blYmS     blYmS     
     * @param blYmE     blYmE     
     * @param hsnNm     hsnNm     
     * @param townNm    townNm    
     * @param bscd2Nm   bscd2Nm   
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> findOds379Details(String reourdeId, String blYmS,String blYmE, String hsnNm, String townNm, String bscd2Nm);
}
