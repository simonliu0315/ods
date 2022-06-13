package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ads.Ads132fa;
import gov.sls.entity.ads.QAds132fa;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsDanView;
import gov.sls.entity.ods.OdsDanWorkbook;
import gov.sls.entity.ods.OdsIdentity;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.QOdsCategory;
import gov.sls.entity.ods.QOdsDanWorkbook;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods701e02Tab1FormBean;
import gov.sls.ods.dto.Ods701e05Tab1FormBean;
import gov.sls.ods.dto.Ods701eGrid1Dto;
import gov.sls.ods.dto.Ods701eGrid2Dto;
import gov.sls.ods.repository.Ads132faRepository;
import gov.sls.ods.repository.OdsCategoryRepository;
import gov.sls.ods.repository.OdsDanViewRepository;
import gov.sls.ods.repository.OdsDanWorkbookRepository;
import gov.sls.ods.repository.OdsIdentityRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.threeten.bp.LocalDateTime;

import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods701eService {

    @Autowired
    private Ads132faRepository ads132faRepository;
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    @Autowired
    private OdsCategoryRepository odsCategoryRepository;
    
    @Autowired
    private OdsResourceVersionRepository odsResourceVersionRepository;
    
    @Autowired
    private OdsDanWorkbookRepository odsDanWorkbookRepository;
    
    @Autowired
    private OdsIdentityRepository odsIdentityRepository;
    
    @Autowired
    private OdsDanViewRepository odsDanViewRepository;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    protected String ODS_PACKAGE_TEMPLATE_HTML = "package" + File.separator;
    protected String ODS_RESOURCE_PATH = "resource" + File.separator;
    
    protected Integer VERSION_ONE = new Integer(1);
    protected String SYNC_TRANSFER = "1";
    
    @Autowired
    Ods303eService ods303eService;
    
    @Autowired
    FileStore fileStore;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;

    public List<Ads132fa> findByPed(String ped) {
        BooleanExpression criteria = null;
        if (!Strings.isNullOrEmpty(ped)) {
            criteria = QAds132fa.ads132fa.ped.eq(ped);
        }
        return Lists
                .newArrayList(ads132faRepository.findAll(criteria, QAds132fa.ads132fa.ped.asc()));
    }
    
    public List<OdsIdentity> findIdentityAll() {
        //BooleanExpression criteria = null;

        /*return Lists
                .newArrayList(odsResourceRepository.findAll(criteria, QOdsResource.odsResource.id.asc()));*/
        return Lists
                .newArrayList(odsIdentityRepository.findAll());
    }
    
    public List<Ods701eGrid1Dto> findAll(String name, String description, String workbookName, String viewName) {
        //BooleanExpression criteria = null;

        /*return Lists
                .newArrayList(odsResourceRepository.findAll(criteria, QOdsResource.odsResource.id.asc()));*/
        return Lists
                .newArrayList(odsResourceRepository.findAllRes(name, description, workbookName, viewName));
    }
    
    /*public List<OdsResourceVersion> findResDetailByResId(String resId) {
        BooleanExpression criteria = null;
        if (!Strings.isNullOrEmpty(resId)) {
            criteria = QOdsResourceVersion.odsResourceVersion.id.resourceId.eq(resId);
        }
        return Lists
                .newArrayList(odsResourceVersionRepository.findAll(criteria, QOdsResourceVersion.odsResourceVersion.id.resourceId.asc()));
    }*/
    
    public List<Ods701eGrid2Dto> findResDetailNDelByResId(String resId, String name, String description) {
        //BooleanExpression criteria = null;

        /*return Lists
                .newArrayList(odsResourceRepository.findAll(criteria, QOdsResource.odsResource.id.asc()));*/
        return Lists
                .newArrayList(odsResourceRepository.findResDetailNDelByResId(resId, name, description));
    }   
    
    public List<Ods701eGrid2Dto> findResDetailAllByResId(String resId) {
        //BooleanExpression criteria = null;

        /*return Lists
                .newArrayList(odsResourceRepository.findAll(criteria, QOdsResource.odsResource.id.asc()));*/
        return Lists
                .newArrayList(odsResourceRepository.findResDetailAllByResId(resId));
    }   

    public void save(Ads132fa asb103fa) {
        ads132faRepository.save(asb103fa);
    }

    public void create(Ads132fa asb103fa) {
        ads132faRepository.create(asb103fa);
    }

    public void delete(String ped) {
        ads132faRepository.delete(ped);
    }

    
    public void create(OdsResource odsResource){
        odsResourceRepository.createRes(odsResource);
    }
    
    public void create(Ods701e02Tab1FormBean ods701e02Tab1FormBean){
        try {
            odsResourceRepository.createRes(ods701e02Tab1FormBean, "", "", "", "", "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }
     
    public void saveGrid1(List<Ods701eGrid1Dto> updOds701eGrid1Dtos){
        odsResourceRepository.saveGrid1(updOds701eGrid1Dtos);
    }
    
    public void create(Ods701e05Tab1FormBean ods701e05Tab1FormBean){
        odsResourceRepository.createResVersion(ods701e05Tab1FormBean, "", "");
    }
    public void saveGrid2(List<Ods701eGrid2Dto> updOds701eGrid2Dtos){
        odsResourceRepository.saveGrid2(updOds701eGrid2Dtos);
        
        List<OdsPackageVersion>  packageVerList = odsResourceRepository.findRelatePackageInfoList(updOds701eGrid2Dtos);
        
        for (int i = 0; i < packageVerList.size(); i++)
        {
            OdsPackageVersion odsPackageVersion = packageVerList.get(i);
            OdsPackageVersionPK odsPackageVersionPk = odsPackageVersion.getId();
            

            log.info("PkgId:" + odsPackageVersionPk.getPackageId());
            log.info("PkgVer:" + odsPackageVersionPk.getVer());
            if (!Strings.isNullOrEmpty(odsPackageVersion.getPattern())) {
                ods303eService.generateTemplate(odsPackageVersionPk.getPackageId(), odsPackageVersionPk.getVer());
            }
        }
        
    }
    
    
    public List<OdsCategory> findAllCategory() {
        BooleanExpression criteria = null;

        return Lists
                .newArrayList(odsCategoryRepository.findAll(criteria, QOdsCategory.odsCategory.name.asc()));
    }
    
    public List<OdsCategory> findUnSelCategoryByResId(String resId) {
        return odsCategoryRepository.findUnSelCategoryByResId(resId);
    }    
    
    public List<OdsCategory> findSelCategoryByResId(String resId) {
        return odsCategoryRepository.findSelCategoryByResId(resId);
    }   
    
    public List<OdsCategory> findUnSelCategoryByCategoryId(String categoryId) {
        return odsCategoryRepository.findUnSelCategoryByCategoryId(categoryId);
    }    
    
    public List<OdsCategory> findSelCategoryByCategoryId(String categoryId) {
        return odsCategoryRepository.findSelCategoryByCategoryId(categoryId);
    }   
    
    public List<OdsDanWorkbook> findDanWbkAll() {
        
        return Lists
                .newArrayList(odsDanWorkbookRepository.findAll(null, QOdsDanWorkbook.odsDanWorkbook.createdAt.desc()));
    }   

    public List<OdsDanView> findDanViewByDanWbkId(String danWbkId) {
        return odsDanViewRepository.findDanViewByDanWbkId(danWbkId);
    }   
    
    public double getWbkResFileSize(String wbkId, String viewId, String resType) {
        

        
        String fileWbkResUrl = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH)
                + wbkId
                + File.separator
                + viewId
                + File.separator
                + resType
                + File.separator
                + wbkId
                + '-'
                + viewId
                + '.' + getFileExtByResType(resType);
        
        /*File fromFile =                 
                   new File(fileWbkResUrl);*/
        File fromFile =                 
                fileStore.getFile(Locations.Persistent.ROOT, fileWbkResUrl);
        return fromFile.length();
        
    }  
    
    
    
    
    
    public void generateTemplate2(String packageId, int ver, InputStream multipartFile, String fileName) throws IOException {

        log.debug("ENVIRONMENT_SHARED_PATH:"
                + propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH));
        log.debug("ENVIRONMENT_PUBLIC_PATH:"
                + propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH));

        String htmlContent = "AAAA";
        
        //URI aaa = new URI("D:\\tac\\workspace\\tac\\tac-batch\\src\\main\\java\\gov\\fdc\\tac\\batch\\TAC238Controller.java");
        /*File fromFile =                 
                new File(propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                + ODS_PACKAGE_TEMPLATE_HTML
                + packageId
                + File.separator
                + "html"
                + File.separator
                + packageId
                + "-" + ver + ".html");*/
        
        /*File fromFile =                 
        new File("D:\\tac\\workspace\\tac\\tac-batch\\src\\main\\java\\gov\\fdc\\tac\\batch\\709025.jpg");



        log.debug("after htmlContent--->" + htmlContent);*/
        
        
        /*String theString = getStringFromInputStream(multipartFile);

        
            FileUtil.writeAsString(
                    new File(propertiesAccessor
                            .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                            + ODS_PACKAGE_TEMPLATE_HTML
                            + packageId
                            + File.separator
                            + "html"
                            + File.separator
                            + fileName), theString);*/
        
        
        File theString2 = stream2file(multipartFile);
        
        /*File toFile =                 
                new File(propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                + ODS_PACKAGE_TEMPLATE_HTML
                + packageId
                + File.separator
                + "html"
                + File.separator
                + fileName);*/
        
        File toFile = fileStore.getFile(Locations.Persistent.ROOT, propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                + ODS_PACKAGE_TEMPLATE_HTML
                + packageId
                + File.separator
                + "html"
                + File.separator
                + fileName);
        
        try{
        FileUtil.copyFile(theString2, toFile);
        }  catch (Exception e) {
            e.printStackTrace();
        }
                
            
            // FileUtil.writeAsString(new File("/stadat/ods/" + ODS_PACKAGE_TEMPLATE_HTML +
            // packageId + File.separator + packageId + "-" +ver + ".html"), htmlContent);
     
    }
    


     public static final String PREFIX = "stream2file";
     public static final String SUFFIX = ".tmp";

     public static File stream2file (InputStream in) throws IOException {
         final File tempFile = File.createTempFile(PREFIX, SUFFIX);
         tempFile.deleteOnExit();
         try (FileOutputStream out = new FileOutputStream(tempFile)) {
             IOUtils.copy(in, out);
         }
         return tempFile;
     }
     
     public Map<String, Object> create(String name, String description, String selCategoryIdList, InputStream multipartFileStream,
             String fileName, String fileContentType, String toDatastoreSync, String toDatastoreDate) throws Exception {
         
         Ods701e02Tab1FormBean ods701e02Tab1FormBean = new Ods701e02Tab1FormBean();
         
         ods701e02Tab1FormBean.setName(name);
         ods701e02Tab1FormBean.setDescription(description);
         
         List<OdsCategory> selCategory = new ArrayList<OdsCategory>();
         
         if (StringUtils.isNotEmpty(selCategoryIdList) && ",".equals(selCategoryIdList.substring(selCategoryIdList.length() - 1, selCategoryIdList.length()) ))
         {
             selCategoryIdList = selCategoryIdList.substring(0, selCategoryIdList.length() - 1);
         
             List<String> categoryIdListAry = new ArrayList<String>(Arrays.asList(selCategoryIdList.split(",")));
             
             for (String categoryId : categoryIdListAry){
                 OdsCategory odsCategory = new OdsCategory();
                 odsCategory.setId(categoryId);
                 selCategory.add(selCategory.size(), odsCategory);
             }
             
         }
         ods701e02Tab1FormBean.setSelCategory(selCategory);
         
         String fileExtension = getFileExtension(fileName);
         String resourceType = getResourceType(fileContentType, fileExtension);
       

         
         //資料建入DB
         Map<String, Object>  createInfo = odsResourceRepository.createRes(ods701e02Tab1FormBean, fileExtension, resourceType, null, null, null);

         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         byte[] buffer = new byte[1024];
         int len;
         while ((len = multipartFileStream.read(buffer)) > -1 ) {
             baos.write(buffer, 0, len);
         }
         baos.flush();
         
         //資料建入DATASTORE
/*         if("dataset".equals(resourceType)){
             InputStream streamToFile = new ByteArrayInputStream(baos.toByteArray()); 
             
             String today = LocalDateTimes.toString(LocalDateTime.now(),"yyyyMMdd");
             boolean isToDatastore;
             if ( SYNC_TRANSFER.equals(toDatastoreSync) || today.equals(toDatastoreDate))
             {
                 isToDatastore = true;
             } else {
                 isToDatastore = false;
             }
                 
             processDataset(createInfo.get("resourceId").toString(), (Integer) createInfo.get("resourceVer"), streamToFile, toDatastoreSync, toDatastoreDate, isToDatastore);
        }
 */        
         //資料建入實體檔案(必擺最後，否則前段出錯的話檔案還是會被新增進去)
         InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray());  
         generateFile(createInfo.get("url").toString(), streamToDatastore);
 
         InputStream streamToDatastoreVer0 = new ByteArrayInputStream(baos.toByteArray());  
         String ver0url = getResourcePath(createInfo.get("resourceId").toString(), resourceType, 0, fileExtension);
         generateFile(ver0url, streamToDatastoreVer0);
         
         Map<String, Object> toStoreMap = new HashMap<String, Object>();
         toStoreMap.put("resourceType", resourceType);
         toStoreMap.put("baos", baos);
         toStoreMap.put("toDatastoreSync", toDatastoreSync);
         toStoreMap.put("toDatastoreDate", toDatastoreDate);
         toStoreMap.put("resourceId", createInfo.get("resourceId"));
         toStoreMap.put("resourceVer", createInfo.get("resourceVer"));
         toStoreMap.put("filePath", createInfo.get("url").toString());
         toStoreMap.put("filePathVer0", ver0url);
         
         return toStoreMap;
         
     }
     
     public boolean isToDatastore(Map<String, Object> toStoreMap) throws Exception {
         
         boolean isToDatastore = false;
         
         String resourceType = (String) toStoreMap.get("resourceType");
         String toDatastoreSync = (String) toStoreMap.get("toDatastoreSync");
         String toDatastoreDate = (String) toStoreMap.get("toDatastoreDate");
         
         //資料建入DATASTORE
         if("dataset".equals(resourceType)){
             
             String today = LocalDateTimes.toString(LocalDateTime.now(),"yyyyMMdd");

             if ( SYNC_TRANSFER.equals(toDatastoreSync) || today.equals(toDatastoreDate))
             {
                 isToDatastore = true;
                 
                 //只要是勾選立即傳送或是日期為今天，一律視為要搬檔，因此將sync設為true、傳送日期設為今天
                 toStoreMap.put("toDatastoreSync", SYNC_TRANSFER);  
                 toStoreMap.put("toDatastoreDate", today);
             }
        } else {
            toStoreMap.put("toDatastoreSync", null);
            toStoreMap.put("toDatastoreDate", null);
        }
        
         return isToDatastore;
     }
     
     public void createDatasetToStore(Map<String, Object> toStoreMap) throws Exception {
         
         ByteArrayOutputStream baos = (ByteArrayOutputStream) toStoreMap.get("baos");
         String resourceId = (String) toStoreMap.get("resourceId");
         int resourceVer = (Integer) toStoreMap.get("resourceVer");
         String filePath = (String) toStoreMap.get("filePath");
         String filePathVer0 = (String) toStoreMap.get("filePathVer0");
         
         InputStream streamToFile = new ByteArrayInputStream(baos.toByteArray()); 
         
         datasetToDatastore(resourceId, resourceVer, streamToFile, filePath, filePathVer0);
         
     }
     
     public void updateToStoreResult(Map<String, Object> toStoreMap, String toDatastoreSuccess) throws Exception {
         
         String resourceId = (String) toStoreMap.get("resourceId");
         int resourceVer = (Integer) toStoreMap.get("resourceVer");
         String toDatastoreSync = (String) toStoreMap.get("toDatastoreSync");
         String toDatastoreDate = (String) toStoreMap.get("toDatastoreDate");
         
         odsResourceRepository.updateResVerToDsInfo(resourceId, resourceVer, toDatastoreSync, toDatastoreDate, toDatastoreSuccess);
         
     }
     
     private String getResourcePath(String resId, String resourceType, int ver, String fileExtension) {
         return 
         propertiesAccessor
         .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
         + ODS_RESOURCE_PATH
         + resId
         + File.separator
         + resourceType
         + File.separator
         + resId + "-" + ver + "." + fileExtension;
     }

     
     /*public void processDataset(String resId, int ver, InputStream multipartFileStream, String toDatastoreSync, String toDatastoreDate, boolean isToDatastore) throws Exception{

         String toDatastoreSuccess = "";
         
         if ( isToDatastore )
         {
             datasetToDatastore(resId, ver, multipartFileStream);
             odsResourceRepository.updateResVerToDsInfo(resId, ver, toDatastoreSync, toDatastoreDate, toDatastoreSuccess);
         } else {
             toDatastoreSuccess = "0";
             odsResourceRepository.updateResVerToDsInfo(resId, ver, toDatastoreSync, toDatastoreDate, toDatastoreSuccess);
         }
   
     }*/
     
     public void datasetToDatastore(String resId, int ver, InputStream multipartFileStream, String filePath, String filePathVer0) throws Exception{
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                 multipartFileStream,"utf-8"));
         String fileHeaderStr = bufferedReader.readLine();
         //移除BOM
         String UTF8_BOM = "\uFEFF";  
         if(fileHeaderStr.startsWith(UTF8_BOM)){
             fileHeaderStr = fileHeaderStr.replace(UTF8_BOM,"");
         }

         List<String> fileHeaderAry = new ArrayList<String>(Arrays.asList(fileHeaderStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")));
         List<String> fileHeaderAryWithQuote = new ArrayList<String>();
         
         for (String fileHeader : fileHeaderAry)
         {
              //當title的頭尾都沒有雙引號<此狀況會發生在欄位中有逗號的狀況>，就加雙引號，否則不用再加一次
             if(!"".equals(fileHeader) &&  !"\"".equals(fileHeader.substring(0,1)) && !",".equals(fileHeader.substring(fileHeader.length() - 1, fileHeader.length()) ))
             {
                 fileHeaderAryWithQuote.add("\"" + fileHeader + "\"");	 
             } else {
                 fileHeaderAryWithQuote.add( fileHeader );	 
             }
             
         }
         
         /*String s = "Sachin,,M,\"Maths,Science,English\",Need to improve in these subjects.";
         String[] splitted = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
         System.out.println(Arrays.toString(splitted));*/

         log.debug("Header:{" + fileHeaderAry + "}");
         log.debug("Header:{" + fileHeaderAryWithQuote + "}");

         //新增DS TEMP TABLE SCHEMA
         odsResourceRepository.createDsTempTbl(resId, ver, fileHeaderAryWithQuote);

         List<Boolean> numericDataTypeAry=new ArrayList<Boolean>(Arrays.asList(new Boolean[fileHeaderAryWithQuote.size()]));
         Collections.fill(numericDataTypeAry, new Boolean(true));
         

         //資料寫入DS TEMP TABLE，每1000筆執行一次insert statement
         List<String> fileContentAry = new ArrayList<String>();
         List<List<String>> fileContentAryList = new ArrayList<List<String>>();
         
         
         String fileContentStr = bufferedReader.readLine();
         int count = 1;
         while(!Strings.isNullOrEmpty(fileContentStr)){
             
             fileContentAry = Arrays.asList(fileContentStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
             
             fileContentAryList.add(fileContentAry);
             
             //log.debug("Record:{" + fileContentStr + "}");
             
             if (count == 1000)
             {
                 numericDataTypeAry = odsResourceRepository.insertDsTempTbl(resId, ver, numericDataTypeAry, fileHeaderAryWithQuote, fileContentAryList);
                 count = 0;
                 fileContentAryList = new ArrayList<List<String>>();
             }
             
             fileContentStr = bufferedReader.readLine();
             count++;
             
         }    
         numericDataTypeAry = odsResourceRepository.insertDsTempTbl(resId, ver, numericDataTypeAry, fileHeaderAryWithQuote, fileContentAryList);
         
         
         log.debug("is column numeric ary:{" + numericDataTypeAry + "}");
         
         try {
             List<OdsResource> odsResource = odsResourceRepository.findDsTbl(resId);

         }   catch (Exception e) {
             e.printStackTrace();
             log.debug("ds table is not exist, auto create");
             
             odsResourceRepository.createDsTbl(resId, numericDataTypeAry, fileHeaderAryWithQuote);
         }
         
         
         /*//CREATE DS TABLE，如果版本是ver1的話就要CREATE DS TABLE SCHEMA
         if (VERSION_ONE.equals(ver))
         {
             odsResourceRepository.createDsTbl(resId, numericDataTypeAry, fileHeaderAryWithQuote);

         }*/
                 
         //資料自DS TEMP TABLE寫入 DS TABLE
         try{
             odsResourceRepository.insertDsTbl(resId, ver, fileHeaderAryWithQuote);
         }  catch (Exception e) {
             throw new Exception(e.getMessage());
         } finally {
             
             //將replace後的資料重新寫到實體檔案中
             tableToCsv(resId, ver, fileHeaderAry, filePath, filePathVer0);
             //DROP DS TEMP TABLE
             odsResourceRepository.dropDsTempTbl(resId, ver);
         }

     }
     
     private void tableToCsv(String resId, int ver, List<String> fileHeaderAry, String filePath, String filePathVer0) throws IOException {
         Map<String, Object> where = new HashMap<String, Object>();
         
         List<Map<String, Object>> grid = odsJdbcTemplate.queryForList(
                 "SELECT * FROM "
                         + sqlEscapeService.escapeMsSql("ODS_TEMP_" + resId.replaceAll("-", "_") + "_" + ver + " "),
                         where);  
         

         ICsvMapWriter mapWriter = null;
         
         File toFile = fileStore.getFile(Locations.Persistent.ROOT, filePath); 
         
         try{

             //beanWriter = new CsvMapWriter(new FileWriter("D:/1.csv"), CsvPreference.STANDARD_PREFERENCE);	//no BOM, csv got error

             OutputStream os = new FileOutputStream(toFile);
             byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
             os.write(UTF8_BOM);
             os.flush();
             OutputStreamWriter out = new OutputStreamWriter(os,"UTF-8");
             
             mapWriter = new CsvMapWriter(out, CsvPreference.STANDARD_PREFERENCE);
             final String[] header = fileHeaderAry.toArray(new String[fileHeaderAry.size()]);
             // write the header
             mapWriter.writeHeader(header);
             
             for(Map<String, Object> anObj : grid){
                 mapWriter.write(anObj, header);                                
             }
             


             
         }finally{
             if( mapWriter != null ) {
                 mapWriter.close();
             }
             
             if(!Strings.isNullOrEmpty(filePathVer0)){
                 FileUtil.copyFile(toFile, fileStore.getFile(Locations.Persistent.ROOT, filePathVer0));
             }
         }
         
         
     }
     
     private String getFileExtension(String fileName){
         log.debug("input fileName{" + fileName + "}");
         
         int startIndex = fileName.lastIndexOf(46) + 1;
         int endIndex = fileName.length();
         String fileExtension = fileName.substring(startIndex, endIndex);
         
         if("jpg".equals(fileExtension) || "jpeg".equals(fileExtension) || "gif".equals(fileExtension)){
             fileExtension = "png";
             log.debug("change imageExtension{" + fileExtension + "} to PNG");
         }
         
         log.debug("output fileExtension{" + fileExtension + "}");
         
         return fileExtension;
     }
     
     private String getResourceType(String fileContentType, String fileExtension) throws IOException {
         log.debug("input fileContentType{" + fileContentType + "}");
         log.debug("input fileExtension{" + fileExtension + "}");
         
         //application/vnd.ms-excel
         //application/pdf
         //image/jpeg
         
         String resourceType = "";
         
         if("application/vnd.ms-excel".equals(fileContentType) || "csv".equals(fileExtension)) {
             resourceType = "dataset";
         } else if("application/pdf".equals(fileContentType) || "pdf".equals(fileExtension)){
             resourceType = "pdf";
         } else if("image/png".equals(fileContentType) || "png".equals(fileExtension) || "jpg".equals(fileExtension) || "jpeg".equals(fileExtension) || "gif".equals(fileExtension)){
             resourceType = "image";
         }
         
         return resourceType;
     }
     
     private String getFileExtByResType(String resourceType) {
         log.debug("input resourceType{" + resourceType + "}");
         
         //application/vnd.ms-excel
         //application/pdf
         //image/jpeg
         
         String fileExtension = "";
         
         if("dataset".equals(resourceType)) {
             fileExtension = "csv";
         } else if("pdf".equals(resourceType)){
             fileExtension = "pdf";
         } else if("image".equals(resourceType)){
             fileExtension = "png";
         }
         
         return fileExtension;
     }
     
     public void generateFile(String filePath, InputStream multipartFileStream) throws IOException {

         File fromFile = stream2file(multipartFileStream);
                 
         /*File toFile =                 
                 new File(filePath);*/
         
         File toFile = fileStore.getFile(Locations.Persistent.ROOT, filePath); 
         
         try{
         FileUtil.copyFile(fromFile, toFile);
         }  catch (Exception e) {
             e.printStackTrace();
         }

      
     }
     
     
     public Map<String, Object> create(String name, String description, String selCategoryIdList, String selImportInfoList, String toDatastoreSync, String toDatastoreDate) throws Exception {
         
         Ods701e02Tab1FormBean ods701e02Tab1FormBean = new Ods701e02Tab1FormBean();
         
         ods701e02Tab1FormBean.setName(name);
         ods701e02Tab1FormBean.setDescription(description);
         
         List<OdsCategory> selCategory = new ArrayList<OdsCategory>();
         
         if (StringUtils.isNotEmpty(selCategoryIdList) && ",".equals(selCategoryIdList.substring(selCategoryIdList.length() - 1, selCategoryIdList.length()) ))
         {
             selCategoryIdList = selCategoryIdList.substring(0, selCategoryIdList.length() - 1);
         
             List<String> categoryIdListAry = new ArrayList<String>(Arrays.asList(selCategoryIdList.split(",")));
             
             for (String categoryId : categoryIdListAry){
                 OdsCategory odsCategory = new OdsCategory();
                 odsCategory.setId(categoryId);
                 selCategory.add(selCategory.size(), odsCategory);
             }
             
         }
         ods701e02Tab1FormBean.setSelCategory(selCategory);
         
         
         List<String> importInfoListAry = new ArrayList<String>(Arrays.asList(selImportInfoList.split(",")));
         
         String workbookId = importInfoListAry.get(0);
         String viewId = importInfoListAry.get(1);
         String resourceType = importInfoListAry.get(2);
         String workbookName = importInfoListAry.get(3);
         String fileExtension = getFileExtByResType(resourceType);

         //資料建入DB
         Map<String, Object>  createInfo = odsResourceRepository.createRes(ods701e02Tab1FormBean, fileExtension, resourceType, workbookId, viewId, workbookName);
         
         log.debug("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"+createInfo.get("workbookUrl").toString());
         

         String fileUrl = propertiesAccessor
                 .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH)
                 + workbookId
                 + File.separator
                 + viewId
                 + File.separator
                 + resourceType
                 + File.separator
                 + workbookId + '-' + viewId
                 + '.' + fileExtension;
         
         /*File fromFile =                 
                    new File(fileUrl);*/
         
         File fromFile = fileStore.getFile(Locations.Persistent.ROOT, fileUrl); 
         
         InputStream multipartFileStream = null;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
             multipartFileStream = new FileInputStream(fromFile);             

             byte[] buffer = new byte[1024];
             int len;
             while ((len = multipartFileStream.read(buffer)) > -1 ) {
                 baos.write(buffer, 0, len);
             }
             baos.flush();
              
             //multipartFileStream.close();
         } finally {
             if (multipartFileStream != null) {
               safeClose(multipartFileStream);
             }
         }
                 
         
         //資料建入DATASTORE
         /*if("dataset".equals(resourceType)){
             InputStream streamToFile = new ByteArrayInputStream(baos.toByteArray());  
             
             String today = LocalDateTimes.toString(LocalDateTime.now(),"yyyyMMdd");
             boolean isToDatastore;
             if ( SYNC_TRANSFER.equals(toDatastoreSync) || today.equals(toDatastoreDate))
             {
                 isToDatastore = true;
             } else {
                 isToDatastore = false;
             }
                 
             processDataset(createInfo.get("resourceId").toString(), (Integer) createInfo.get("resourceVer"), streamToFile, toDatastoreSync, toDatastoreDate, isToDatastore);
        }*/
         
         //資料建入實體檔案(必擺最後，否則前段出錯的話檔案還是會被新增進去)
         InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray());  
         generateFile(createInfo.get("url").toString(), streamToDatastore);
         
         InputStream streamToDatastoreVer0 = new ByteArrayInputStream(baos.toByteArray());  
         String ver0url = getResourcePath(createInfo.get("resourceId").toString(), resourceType, 0, fileExtension);
         generateFile(ver0url, streamToDatastoreVer0);
         
         
         //當有wbkUrl，代表本次上傳的是新的wbk，產生檔案
         if(!"".equals(createInfo.get("workbookUrl").toString()))
         {
             String fileWorkbookUrl = propertiesAccessor
                     .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH)
                     + workbookId
                     + File.separator
                     + "twb"
                     + File.separator
                     + workbookId
                     + '.' + "twbx";
             
             /*File fromFileWorkbook =                 
                        new File(fileWorkbookUrl);*/
             
             File fromFileWorkbook = fileStore.getFile(Locations.Persistent.ROOT, fileWorkbookUrl); 
             
             InputStream workbookFileStream = null;
             try {
                 workbookFileStream = new FileInputStream(fromFileWorkbook);
                 generateFile(createInfo.get("workbookUrl").toString(), workbookFileStream);
             } finally {
                 if (workbookFileStream != null) {
                   safeClose(workbookFileStream);
                 }
             }
         }
         
         
         Map<String, Object> toStoreMap = new HashMap<String, Object>();
         toStoreMap.put("resourceType", resourceType);
         toStoreMap.put("baos", baos);
         toStoreMap.put("toDatastoreSync", toDatastoreSync);
         toStoreMap.put("toDatastoreDate", toDatastoreDate);
         toStoreMap.put("resourceId", createInfo.get("resourceId"));
         toStoreMap.put("resourceVer", createInfo.get("resourceVer"));
         toStoreMap.put("filePath", createInfo.get("url").toString());
         toStoreMap.put("filePathVer0", ver0url);
         
         return toStoreMap;
     }
     
     
    public Map<String, Object> createResdetail(String resourceId, String name, String description, InputStream multipartFileStream, String fileName, String fileContentType, String toDatastoreSync, String toDatastoreDate) throws Exception {
         
         Ods701e05Tab1FormBean ods701e05Tab1FormBean = new Ods701e05Tab1FormBean();
         
         ods701e05Tab1FormBean.setResourceId(resourceId);
         ods701e05Tab1FormBean.setName(name);
         ods701e05Tab1FormBean.setDescription(description);
         

         
         
         String fileExtension = getFileExtension(fileName);
         String resourceType = getResourceType(fileContentType, fileExtension);
         
         
         //資料建入DB
          
         Map<String, Object>  createInfo = odsResourceRepository.createResVersion(ods701e05Tab1FormBean, fileExtension, resourceType);

         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         byte[] buffer = new byte[1024];
         int len;
         while ((len = multipartFileStream.read(buffer)) > -1 ) {
             baos.write(buffer, 0, len);
         }
         baos.flush();
         
         //資料建入DATASTORE
/*         if("dataset".equals(resourceType)){
             InputStream streamToFile = new ByteArrayInputStream(baos.toByteArray());  
             
             String today = LocalDateTimes.toString(LocalDateTime.now(),"yyyyMMdd");
             boolean isToDatastore;
             if ( SYNC_TRANSFER.equals(toDatastoreSync) || today.equals(toDatastoreDate))
             {
                 isToDatastore = true;
             } else {
                 isToDatastore = false;
             }
                 
             processDataset(createInfo.get("resourceId").toString(), (Integer) createInfo.get("resourceVer"), streamToFile, toDatastoreSync, toDatastoreDate, isToDatastore);
         }
         */
         
         //資料建入實體檔案(必擺最後，否則前段出錯的話檔案還是會被新增進去)
         InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray()); 
         generateFile(createInfo.get("url").toString(), streamToDatastore);
         
         InputStream streamToDatastoreVer0 = new ByteArrayInputStream(baos.toByteArray());  
         String ver0url = getResourcePath(createInfo.get("resourceId").toString(), resourceType, 0, fileExtension);
         generateFile(ver0url, streamToDatastoreVer0);
         
         
       
//         String filePath = odsResourceRepository.createResVersion(ods701e05Tab1FormBean, fileExtension, resourceType);
//         generateFile(filePath, multipartFileStream);
         
         Map<String, Object> toStoreMap = new HashMap<String, Object>();
         toStoreMap.put("resourceType", resourceType);
         toStoreMap.put("baos", baos);
         toStoreMap.put("toDatastoreSync", toDatastoreSync);
         toStoreMap.put("toDatastoreDate", toDatastoreDate);
         toStoreMap.put("resourceId", createInfo.get("resourceId"));
         toStoreMap.put("resourceVer", createInfo.get("resourceVer"));
         toStoreMap.put("filePath", createInfo.get("url").toString());
         toStoreMap.put("filePathVer0", ver0url);
         
         return toStoreMap;
     }

    
    public List<OdsDanWorkbook> findDanWbkByName(String wbkName) {
        //return odsDanWorkbookRepository.findDanWbkByName(wbkName);
        
        BooleanExpression criteria = null;
        if (!Strings.isNullOrEmpty(wbkName)) {
            criteria = QOdsDanWorkbook.odsDanWorkbook.name.like("%" + wbkName + "%");
        }
        return Lists
                .newArrayList(odsDanWorkbookRepository.findAll(criteria, QOdsDanWorkbook.odsDanWorkbook.createdAt.desc()));
    }   
    
    
    public Map<String, Object> saveFileRefreshUpload(String resourceId, String ver, 
            InputStream multipartFileStream, String fileName, String fileContentType, String toDatastoreSync, String toDatastoreDate) throws Exception {
        
        
        Ods701eGrid2Dto ods701eGrid2Dto = new Ods701eGrid2Dto();
        ods701eGrid2Dto.setResourceId(resourceId);
        ods701eGrid2Dto.setVer(ver);
        
        String fileExtension = getFileExtension(fileName);
        String resourceType = getResourceType(fileContentType, fileExtension);
        
        
        //修正DB的resource url
        Map<String, Object> updateInfo = odsResourceRepository.saveFileRefreshUpload(ods701eGrid2Dto, fileExtension, resourceType);

        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = multipartFileStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        
        //資料建入實體檔案
        InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray()); 
        generateFile(updateInfo.get("url").toString(), streamToDatastore);

        String ver0url = "";
        if ((boolean)updateInfo.get("isMaxVer"))
        {
            InputStream streamToDatastoreVer0 = new ByteArrayInputStream(baos.toByteArray());  
            ver0url = getResourcePath(resourceId, resourceType, 0, fileExtension);
            generateFile(ver0url, streamToDatastoreVer0);
        }

        
        Map<String, Object> toStoreMap = new HashMap<String, Object>();
        toStoreMap.put("resourceType", resourceType);
        toStoreMap.put("baos", baos);
        toStoreMap.put("toDatastoreSync", toDatastoreSync);
        toStoreMap.put("toDatastoreDate", toDatastoreDate);
        toStoreMap.put("resourceId", resourceId);
        toStoreMap.put("resourceVer", Integer.valueOf(ver));
        toStoreMap.put("filePath", updateInfo.get("url").toString());
        toStoreMap.put("filePathVer0", ver0url);
        
        return toStoreMap;
    }
    
    
    
    public Map<String, Object> saveFileRefreshImport(String resourceId, String ver, String toDatastoreSync, String toDatastoreDate) throws Exception {
        
        
        BooleanExpression criteria = QOdsResource.odsResource.id.eq(resourceId);
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        
        String workbookId = odsResource.getWorkbookId();
        String viewId = odsResource.getViewId();
        String resourceType = odsResource.getFormat();
        String fileExtension = getFileExtByResType(resourceType);
        
        
        Ods701eGrid2Dto ods701eGrid2Dto = new Ods701eGrid2Dto();
        ods701eGrid2Dto.setResourceId(resourceId);
        ods701eGrid2Dto.setVer(ver);
        
        //資料建入DB
        Map<String, Object> updateInfo = odsResourceRepository.saveFileRefreshImport(ods701eGrid2Dto, fileExtension, resourceType, workbookId, viewId);

        log.debug("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"+updateInfo.get("workbookUrl").toString());
        

        String fileUrl = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH)
                + workbookId
                + File.separator
                + viewId
                + File.separator
                + resourceType
                + File.separator
                + workbookId + '-' + viewId
                + '.' + fileExtension;
        
        /*File fromFile =                 
                   new File(fileUrl);*/
        
        File fromFile = fileStore.getFile(Locations.Persistent.ROOT, fileUrl); 
        
        InputStream multipartFileStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            multipartFileStream = new FileInputStream(fromFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = multipartFileStream.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
             
            //multipartFileStream.close();
        } finally {
            if (multipartFileStream != null) {
              safeClose(multipartFileStream);
            }
        }
        
        
        
        
        //資料建入實體檔案(必擺最後，否則前段出錯的話檔案還是會被新增進去)
        InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray());  
        generateFile(updateInfo.get("url").toString(), streamToDatastore);
        
        String ver0url = "";
        if ((boolean)updateInfo.get("isMaxVer"))
        {
            InputStream streamToDatastoreVer0 = new ByteArrayInputStream(baos.toByteArray());  
            ver0url = getResourcePath(resourceId, resourceType, 0, fileExtension);
            generateFile(ver0url, streamToDatastoreVer0);
        }

        //wbk資料建入實體檔案
        String fileWorkbookUrl = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH)
                + workbookId
                + File.separator
                + "twb"
                + File.separator
                + workbookId
                + '.' + "twbx";
        
        /*File fromFileWorkbook =                 
                   new File(fileWorkbookUrl);*/
        
        File fromFileWorkbook = fileStore.getFile(Locations.Persistent.ROOT, fileWorkbookUrl); 
        
        InputStream workbookFileStream = null;
        try {
            workbookFileStream = new FileInputStream(fromFileWorkbook);
            generateFile(updateInfo.get("workbookUrl").toString(), workbookFileStream);
        } finally {
            if (workbookFileStream != null) {
              safeClose(workbookFileStream);
            }
        }
        
        Map<String, Object> toStoreMap = new HashMap<String, Object>();
        toStoreMap.put("resourceType", resourceType);
        toStoreMap.put("baos", baos);
        toStoreMap.put("toDatastoreSync", toDatastoreSync);
        toStoreMap.put("toDatastoreDate", toDatastoreDate);
        toStoreMap.put("resourceId", resourceId);
        toStoreMap.put("resourceVer", Integer.valueOf(ver));
        toStoreMap.put("filePath", updateInfo.get("url").toString());
        toStoreMap.put("filePathVer0", ver0url);
        
        return toStoreMap;
    }
    
    public static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

}
