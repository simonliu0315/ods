package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * dan to ods 營業人個別化主題 VIEWID
 * O1502#D02@ALL$01 資料集營業人開立電子發票彙總
 * O1502#D03@ALL$01 資料集營業人電子發票中獎彙總
 * O1502#D04@ALL$01 資料集營業人地區行業別開立電子發票彙總
 * O1502#D01@ALL$01 資料集營業人資料彙整
 * O1502#D05@ALL$01 資料集營業人營所稅彙總
 * O1502#D06@ALL$01 資料集地區行業別營所稅彙總
 * O1502#D07@ALL$01 資料集營業人營業稅彙總
 * O1502#D08@ALL$01 資料集地區行業別營業稅彙總
 */
@Slf4j
@Service
public class Ods776xService {

    private final static String FILENAME_PREFIX = "EIDW-DAN.DAN_%s.%s";
    
    private final static int MAX_BATCH = 1000;

    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private final static String START_MARK = "\\|#";

    private final static String END_MARK = "#\\|";

    private final static String SPLIT_MARK = "\\*\\|";
    
    private final static String SUFFIX=".TXT";

    // 開立電子發票彙總
    private final static String EInvoBANSumy_VIEW_ID = "O1502#D02@ALL$01";
    private final static String EInvoBANSumy_TABLENAME = "EIN_INVO_BAN_SUMY";
    private final static int EInvoBANSumy_FIELD_COUNT = 7;

    // 地區行業別開立電子發票彙總
    private final static String EInvoHsnBSDCSumy_VIEW_ID = "O1502#D04@ALL$01";
    private final static String EInvoHsnBSDCSumy_TABLENAME = "EIN_INVO_DISTRICT_SUMY";
    private final static int EInvoHsnBSDCSumy_FIELD_COUNT = 18;

    // 營業人資料彙整總
    private final static String EBANSumy_VIEW_ID = "O1502#D01@ALL$01";
    private final static String EBANSumy_TABLENAME = "VAT_TAX_RGST_STUS";
    private final static int EBANSumy_FIELD_COUNT = 17;

    // 營業人電子發票中獎彙總
    private final static String EAwardInvoBanSumy_VIEW_ID = "O1502#D03@ALL$01";
    private final static String EAwardInvoBanSumy_TABLENAME = "AWARD_INVO_BAN_SUMY";
    private final static int EAwardInvoBanSumy_FIELD_COUNT = 5;

    // 營所稅營業人申報彙總
    private final static String EIdanpfsBanSumy_VIEW_ID = "O1502#D05@ALL$01";
    private final static String EIdanpfsBanSumy_TABLENAME = "DAN_PFS_FLG_BAN_SUMY";
    private final static int EIdanpfsBanSumy_FIELD_COUNT = 5;
    
    // 營所稅營業人申報行政區彙總
    private final static String EIdanpfsDistrictSumy_VIEW_ID = "O1502#D06@ALL$01";
    private final static String EIdanpfsDistrictSumy_TABLENAME = "DAN_PFS_FLG_DISTRICT_SUMY";
    private final static int EIdanpfsDistrictSumy_FIELD_COUNT = 14;
    
    // 營業稅營業人申報彙總
    private final static String EIdanvatBanSumy_VIEW_ID = "O1502#D07@ALL$01";
    private final static String EIdanvatBanSumy_TABLENAME = "DAN_VAT_FLG_BAN_SUMY";
    private final static int EIdanvatBanSumy_FIELD_COUNT = 5;
    
    // 營業稅營業人申報行政區彙總
    private final static String EIdanvatDistrictSumy_VIEW_ID = "O1502#D08@ALL$01";
    private final static String EIdanvatDistrictSumy_TABLENAME = "DAN_VAT_FLG_DISTRICT_SUMY";
    private final static int EIdanvatDistrictSumy_FIELD_COUNT = 14;
    
    @Autowired
    private ImportService service;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;

    private String getFullPath() {
        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "danods").getAbsolutePath();
        return targetPath;
    }
    
    private String getBakPath() {
        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "danods" + File.separator + "bak").getAbsolutePath();
        return targetPath;
    }
    
    /**
     * 取得要被匯入的檔案
     * @param tablename
     * @return
     */
    private File getUploadFile(String tablename,Date runDate) {
        String date = "10";
        if (tablename.equals(EAwardInvoBanSumy_TABLENAME)) {
            date = "15";
        }
        String datetime = new SimpleDateFormat("yyyyMM").format(runDate)
                + date;
        final String prefix = String.format(FILENAME_PREFIX, tablename, datetime);
        File[] fileList = new File(getFullPath()).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().startsWith(prefix) && pathname.getName()
                        .endsWith(SUFFIX));
            }
        });
        File result_file = null;

        if (fileList != null) {
            for (File file : fileList) {
                result_file = file;
            }
        }
        return result_file;
    }
    
    /**
     * 開立電子發票彙總
     * 更新檔方式匯入(先刪再新增)
     * 會更新訂閱紀錄
     */
    @Async
    public void runEInvoBANSumy(Date runDate) {
        File uploadFile = getUploadFile(EInvoBANSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EInvoBANSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());
            
            StringBuffer deletesql = getDeleteSqlByDate(path, EInvoBANSumy_FIELD_COUNT,
                    "ODS_RESOURCE_VER =1 AND 發票日期  in(", "-");
            service.deleteByResourceId(resourceId, deletesql.toString());

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EInvoBANSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEInvoBANSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId, EInvoBANSumy_FIELD_COUNT,
                                    datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EInvoBANSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
            updateOdsResourceIndivid(EAwardInvoBanSumy_VIEW_ID);//更新訂閱更新紀錄
        } else {
            log.info("File is not Exist!!");
        }
    }

    /**
     * 開立電子發票彙總
     * 整檔方式匯入(全刪再新增)
     * 會更新訂閱紀錄
     */
    @Async
    public void runEInvoBANSumyAll(Date runDate) {
        File uploadFile = getUploadFile(EInvoBANSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EInvoBANSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            service.deleteAllByResourceId(resourceId);

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EInvoBANSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEInvoBANSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId, EInvoBANSumy_FIELD_COUNT,
                                    datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EInvoBANSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
            updateOdsResourceIndivid(EAwardInvoBanSumy_VIEW_ID);//更新訂閱更新紀錄
        } else {
            log.info("File is not Exist!!");
        }
    }
    /**
     * 地區行業別開立電子發票彙總
     * 整檔方式匯入(全刪再新增)
     */
    @Async
    public void runEInvoHsnBSDCSumyAll(Date runDate) {
        log.info("Ods776xJob_EInvoHsnBSDCSumyAll Start Executing!");
        long startTime = System.currentTimeMillis();
        File uploadFile = getUploadFile(EInvoHsnBSDCSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EInvoHsnBSDCSumy_VIEW_ID);

            Path path = Paths.get(uploadFile.getPath());

            service.deleteAllByResourceId(resourceId);

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EInvoHsnBSDCSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEInvoHsnBSDCSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EInvoHsnBSDCSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EInvoHsnBSDCSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EInvoHsnBSDCSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 地區行業別開立電子發票彙總
     * 更新檔方式匯入(先刪再新增)
     */
    @Async
    public void runEInvoHsnBSDCSumy(Date runDate) {
        log.info("Ods776xJob_EInvoHsnBSDCSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        File uploadFile = getUploadFile(EInvoHsnBSDCSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EInvoHsnBSDCSumy_VIEW_ID);

            Path path = Paths.get(uploadFile.getPath());

            StringBuffer deletesql = getDeleteSqlByDate(path, EInvoHsnBSDCSumy_FIELD_COUNT,
                    "ODS_RESOURCE_VER =1 AND 發票日期  in(", "-");
            service.deleteByResourceId(resourceId, deletesql.toString());

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EInvoHsnBSDCSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEInvoHsnBSDCSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EInvoHsnBSDCSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EInvoHsnBSDCSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EInvoHsnBSDCSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
     
    /**
     * 營業人資料彙整總
     * 整檔方式匯入(全刪再新增)
     */
    @Async
    public void runEBANSumy(Date runDate) {
        log.info("Ods776xJob_EBANSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        File uploadFile = getUploadFile(EBANSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EBANSumy_VIEW_ID);

            service.deleteAllByResourceId(resourceId);

            List<Object[]> datas = new ArrayList<Object[]>();
            Path path = Paths.get(uploadFile.getPath());
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EBANSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEBANSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId, EBANSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EBANSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EBANSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");

    }
    /**
     * 營業人電子發票中獎彙總
     * 更新檔方式匯入(先刪再新增)
     */
    @Async
    public void runEAwardInvoBanSumy(Date runDate) {
        log.info("Ods776xJob_EAwardInvoBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        File uploadFile = getUploadFile(EAwardInvoBanSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EAwardInvoBanSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            StringBuffer deletesql = getDeleteSqlByDate(path, EAwardInvoBanSumy_FIELD_COUNT,
                    "ODS_RESOURCE_VER =1 AND 發票歸屬期別年月  in(", "-");
            service.deleteByResourceId(resourceId, deletesql.toString());

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EAwardInvoBanSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEAwardInvoBanSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EAwardInvoBanSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EAwardInvoBanSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EAwardInvoBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * 營業人電子發票中獎彙總
     * 整檔方式匯入(全刪再新增)
     */
    @Async
    public void runEAwardInvoBanSumyAll(Date runDate) {
        log.info("Ods776xJob_EAwardInvoBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        File uploadFile = getUploadFile(EAwardInvoBanSumy_TABLENAME,runDate);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EAwardInvoBanSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            service.deleteAllByResourceId(resourceId);

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EAwardInvoBanSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEAwardInvoBanSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EAwardInvoBanSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EAwardInvoBanSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EAwardInvoBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    /**
     * 更新訂閱更新紀錄
     * @param viewId viewId
     */
    private void updateOdsResourceIndivid(String viewId) {
        log.info("updateOdsResourceIndivid Start Executing!");
        service.createOdsResourceIndivid(viewId);
        log.info("updateOdsResourceIndivid finished!");
    }
    
    /**
     * 將檔案移到指定的位置
     * @param path
     */
    private void removeFile(Path path) {
        File file = new File(getBakPath());
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            Files.move(path, Paths.get(getBakPath() +File.separator+ path.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 組出刪除資料的條件語法，主要依發票日來刪除資料
     * @param path 檔案路徑
     * @param field_count 檔案內欄位總數 
     * @param profixsql 刪除sql的前段內容
     * @param separation 日期的分格符號
     * @return
     */
    private StringBuffer getDeleteSqlByDate(Path path, int field_count, String profixsql,
            String separation) {
        Set<String> invodateSet = new HashSet<>();
        try (Scanner scanner = new Scanner(path, ENCODING.name())) {
            while (scanner.hasNextLine()) {
                String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                for (String contents : groups) {
                    String[] content = contents.split(SPLIT_MARK);
                    if (content.length != field_count - 1)
                        continue;
                    invodateSet.add(content[0]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuffer deletesql = new StringBuffer(profixsql);
        Iterator<String> it = invodateSet.iterator();
        while (it.hasNext()) {
            deletesql.append(separation + it.next().substring(0,8) + separation + ",");
        }
        deletesql.replace(deletesql.length() - 1, deletesql.length(), ")");
        log.info(deletesql.toString());
        return deletesql;
    }
    /**
     * 將取得的Content轉為物件
     * 開立電子發票彙總
     * @param content
     * @return
     */
    private Object[] mapEInvoBANSumyStringToObject(String[] content) {
        Object[] objs = new Object[EInvoBANSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1] = new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//發票日期
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//賣方統一編號
        objs[3] = new BigDecimal(content[2]);//電子發票B2B開立張數
        objs[4] = new BigDecimal(content[3]);//電子發票B2B銷售額
        objs[5] = new BigDecimal(content[4]);//電子發票B2C開立張數
        objs[6] = new BigDecimal(content[5]);//電子發票B2C銷售額
        return objs;
    }
    
    /**
     * 將取得的Content轉為物件
     * 地區行業別開立電子發票彙總
     * @param content
     * @return
     */
    private Object[] mapEInvoHsnBSDCSumyStringToObject(String[] content) {
        Object[] objs = new Object[EInvoHsnBSDCSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1] = new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//發票日期
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//總局代號
        objs[3] = content[2];//縣市代號
        objs[4] = content[3];//縣市名稱
        objs[5] = content[4];//鄉鎮代號
        objs[6] = content[5];//鄉鎮名稱
        objs[7] = content[6];//分局稽徵所代號
        objs[8] = content[7];//分局稽徵所名稱
        objs[9] = content[8];//營業項目代號
        objs[10] = content[9];//營業項目名稱
        objs[11] = new BigDecimal(content[10]);//電子發票B2B開立張數
        objs[12] = new BigDecimal(content[11]);//電子發票B2B銷售額
        objs[13] = new BigDecimal(content[12]);//電子發票B2C開立張數
        objs[14] = new BigDecimal(content[13]);//電子發票B2C銷售額
        objs[15] = new BigDecimal(content[14]);//營業人家數
        objs[16] = new BigDecimal(content[15]);//B2B營業人家數
        objs[17] = new BigDecimal(content[16]);//B2C營業人家數
        return objs;
    }
    
    /**
     * 將取得的Content轉為物件
     * 營業人資料彙整總
     * @param content
     * @return
     */
    private Object[] mapEBANSumyStringToObject(String[] content) {
        Object[] objs = new Object[EBANSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        objs[1] = content[0];//營業人統編
        objs[2] = content[1];//總機構營業人統編
        objs[3] = content[2];//總局代號
        objs[4] = content[3];//縣市代號
        objs[5] = content[4];//縣市名稱
        objs[6] = content[5];//鄉鎮代號
        objs[7] = content[6];//鄉鎮名稱
        objs[8] = content[7];//分局稽徵所代號
        objs[9] = content[8];//分局稽徵所名稱
        objs[10] = content[9];//營業項目代號
        objs[11] = content[10];//營業項目名稱
        objs[12] = content[11];//營業項目代號 (2碼)
        objs[13] = content[12];//營業項目名稱 (2碼)
        objs[14] = content[13];//營業項目代號 (4碼)
        objs[15] = content[14];//營業項目名稱 (4碼)
        objs[16] = new BigDecimal(content[15]);//資本額
        return objs;
    }
    
    /**
     * 將取得的Content轉為物件
     * 營業人電子發票中獎彙總
     * @param content
     * @return
     */
    private Object[] mapEAwardInvoBanSumyStringToObject(String[] content) {
        Object[] objs = new Object[EAwardInvoBanSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
         try {
         objs[1]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//發票歸屬期別年月
         } catch (ParseException e) {
         throw new RuntimeException(e);
         }
        objs[2] = content[1];//賣方統一編號
        objs[3] = new BigDecimal(content[2]);//電子發票中獎張數
        objs[4] = new BigDecimal(content[3]);//電子發票中獎金額
        return objs;

    }

    /**
     * 營所稅營業人申報彙總
     * 更新檔方式匯入(先刪再新增)
     * 每年 / 1/4, 5/4, 10/5 AM 00:00
     */
    @Async
    public void runEIdanpfsBanSumy(Date runDate) {
        String filePattern="EIDW-DAN.%s.%s.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMMdd").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanpfsBanSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanpfsBanSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            List<String> deletedYears=new ArrayList<String>();
            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanpfsBanSumy_FIELD_COUNT - 1)
                            continue;
                        String dataYear=content[0].substring(0,4);
                        if ( ! deletedYears.contains(dataYear) ) {
                            String deletesql = "year(資料年度) = "+dataYear;
                            service.deleteByResourceId(resourceId, deletesql);
                            deletedYears.add(dataYear);
                        }
                        Object[] objs = mapEIdanpfsBanSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanpfsBanSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanpfsBanSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

    }

    /**
     * 營所稅營業人申報彙總
     * 整檔方式匯入(全刪再新增)
     * 每年 / 1/4, 5/4, 10/5 AM 00:00
     */
    @Async
    public void runEIdanpfsBanSumyAll(Date runDate) {
        String filePattern="EIDW-DAN.%s.%s.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMMdd").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanpfsBanSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanpfsBanSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                
                service.deleteAllByResourceId(resourceId);//全刪
                
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanpfsBanSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEIdanpfsBanSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanpfsBanSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanpfsBanSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

    }
    /**
     * 將取得的Content轉為物件
     * 營所稅營業人申報彙總
     * @param content
     * @return
     */
    private Object[] mapEIdanpfsBanSumyStringToObject(String[] content) {
        Object[] objs = new Object[EIdanpfsBanSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//資料年度
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//營利事業暨扣繳單位統一編號
        objs[3] = new BigDecimal(content[2]);//營業淨利
        objs[4] = new BigDecimal(content[3]);//營業收入淨額
        return objs;
    }

   /**
     * 營所稅營業人申報行政區彙總
     * 更新檔方式匯入(先刪再新增)
     * 每年 / 10/10, 2/10, 6/10 AM 00:00
     */
    @Async
    public void runEIdanpfsDistrictSumy(Date runDate) {
        log.info("Ods776xJob_EIdanpfsDistrictSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        String filePattern="EIDW-DAN.%s.%s10.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMM").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanpfsDistrictSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanpfsDistrictSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            List<String> deletedYears=new ArrayList<String>();
            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanpfsDistrictSumy_FIELD_COUNT - 1)
                            continue;
                        String dataYear=content[0].substring(0,4);
                        if ( ! deletedYears.contains(dataYear) ) {
                            String deletesql = "year(資料年度) = "+dataYear;
                            service.deleteByResourceId(resourceId, deletesql);
                            deletedYears.add(dataYear);
                        }
                        Object[] objs = mapEIdanpfsDistrictSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanpfsDistrictSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanpfsDistrictSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EIdanpfsDistrictSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
   /**
     * 營所稅營業人申報行政區彙總
     * 整檔方式匯入(全刪再新增)
     * 每年 / 10/10, 2/10, 6/10 AM 00:00
     */
    @Async
    public void runEIdanpfsDistrictSumyAll(Date runDate) {
        log.info("Ods776xJob_EIdanpfsDistrictSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        String filePattern="EIDW-DAN.%s.%s10.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMM").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanpfsDistrictSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanpfsDistrictSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                service.deleteAllByResourceId(resourceId);//全刪
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanpfsDistrictSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEIdanpfsDistrictSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanpfsDistrictSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanpfsDistrictSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EIdanpfsDistrictSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 將取得的Content轉為物件
     * 營所稅營業人申報行政區彙總
     * @param content
     * @return
     */
    private Object[] mapEIdanpfsDistrictSumyStringToObject(String[] content) {
        Object[] objs = new Object[EIdanpfsDistrictSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//資料年度
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//縣市稅務轄區代號
        objs[3] = content[2];//營利事業暨扣繳單位地址縣市代號
        objs[4] = content[3];//營利事業暨扣繳單位地址縣市名稱
        objs[5] = content[4];//營利事業暨扣繳單位地址鄉鎮代號
        objs[6] = content[5];//營利事業暨扣繳單位地址鄉鎮市區名稱
        objs[7] = content[6];//扣繳檔案編號機關代號
        objs[8] = content[7];//扣繳檔案編號機關總分局處所名稱
        objs[9] = content[8];//行業代號前2碼
        objs[10] = content[9];//行業代號前2碼中文名稱
        objs[11] = new BigDecimal(content[10]);//營業淨利
        objs[12] = new BigDecimal(content[11]);//營業收入淨額
        objs[13] = new BigDecimal(content[12]);//營業人家數
        return objs;
    }

    /**
     * 營業稅營業人申報彙總
     * 整檔
     * 每月 / 5日 AM 00:00
     */
    @Async
    public void runEIdanvatBanSumy(Date runDate) {
        log.info("Ods776xJob_EIdanvatBanSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        String filePattern="EIDW-DAN.%s.%s10.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMM").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanvatBanSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanvatBanSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            String deletesql = "所屬年月 is not null";
            service.deleteByResourceId(resourceId, deletesql);
            
            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanvatBanSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEIdanvatBanSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanvatBanSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanvatBanSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EIdanvatBanSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 將取得的Content轉為物件
     * 營業稅營業人申報彙總
     * @param content
     * @return
     */
    private Object[] mapEIdanvatBanSumyStringToObject(String[] content) {
        Object[] objs = new Object[EIdanvatBanSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//所屬年月 (每期)
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//營業人統編
        objs[3] = new BigDecimal(content[2]);//銷項總計金額
        objs[4] = new BigDecimal(content[3]);//發票申購總計張數
        return objs;
    }

    /**
     * 營業稅營業人申報行政區彙總
     * 整檔
     * 每月 / 5日 AM 00:00
     */
    @Async
    public void runEIdanvatDistrictSumy(Date runDate) {
        log.info("Ods776xJob_EIdanvatDistrictSumy Start Executing!");
        long startTime = System.currentTimeMillis();
        String filePattern="EIDW-DAN.%s.%s10.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMM").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,EIdanvatDistrictSumy_TABLENAME,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(EIdanvatDistrictSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            String deletesql = "所屬年月 is not null";
            service.deleteByResourceId(resourceId, deletesql);
            
            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != EIdanvatDistrictSumy_FIELD_COUNT - 1)
                            continue;
                        Object[] objs = mapEIdanvatDistrictSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    EIdanvatDistrictSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, EIdanvatDistrictSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods776xJob_EIdanvatDistrictSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 將取得的Content轉為物件
     * 營業稅營業人申報行政區彙總
     * @param content
     * @return
     */
    private Object[] mapEIdanvatDistrictSumyStringToObject(String[] content) {
        Object[] objs = new Object[EIdanvatDistrictSumy_FIELD_COUNT];
        objs[0] = new Integer(1);//固定寫1
        try {
            objs[1]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[0]);//所屬年月
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[2] = content[1];//總局代號
        objs[3] = content[2];//縣市代號
        objs[4] = content[3];//縣市名稱
        objs[5] = content[4];//鄉鎮代號
        objs[6] = content[5];//鄉鎮名稱
        objs[7] = content[6];//分局稽徵所代號
        objs[8] = content[7];//分局稽徵所名稱
        objs[9] = content[8];//營業項目代號 (2碼)
        objs[10] = content[9];//營業項目名稱 (2碼)
        objs[11] = new BigDecimal(content[10]);//銷項總計金額
        objs[12] = new BigDecimal(content[11]);//發票申購總計張數
        objs[13] = new BigDecimal(content[12]);//營業人家數

        return objs;
    }

}
