package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dao.OdsDWDao;
import gov.sls.ods.io.OdsCsvEncoder;
import gov.sls.ods.io.OdsCsvMapWriter;
import gov.sls.ods.io.OdsToCsv;
import gov.sls.ods.io.OdsToCsv.OdsCellProcessorType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

@Slf4j
@Service
public class Ods774xService {
    private String OUTPUT_FILENAME = "%s-DBO.%s.%s.TXT_TMP";
    private int PAGE_PER_SIZE = 100000;// 分頁每頁筆數
    private String SLSDB = "SLSDB";// 乙測是SLS_DB, 甲測、正式是SLSDB
    private String SLSUAAFRONT = "SLSUAAFRONT";
    String CREATED = "CREATED";
    String ODS_INDIVIDE_PACKAGE_SUB = "ODS_INDIVIDE_PACKAGE_SUB";

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;

    @Autowired
    FileStore fileStore;

    @Autowired
    private OdsDWDao odsDWDao;

    // 每月 2日 AM 00:00
    /**
     * select * from ODS_INDIVIDE_PACKAGE_SUB where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsIndividePackageSub(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = ODS_INDIVIDE_PACKAGE_SUB;
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_UNIFY_ID").addField("USER_ROLE")
                .addField("PACKAGE_ID").addField("IP_ADDRESS")
                .addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);

        // filename time
        String time = getDate() + ".000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsIndividePackageSub error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsIndividePackageSub finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from ODS_USER_FOLLOW_PACKAGE where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserFollowPackage(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_FOLLOW_PACKAGE";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("PACKAGE_ID")
                .addField("RESOURCE_ID").addField("RESOURCE_CRITERIA_ID").addField("IP_ADDRESS")
                .addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);

        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserFollowPackage error!" + e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("Ods774xJob findOdsUserFollowPackage finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from ODS_PACKAGE
     */
    @Async
    public void findOdsPackage() {
        Date jobStartTime = new Date();
        String tablename = "ODS_PACKAGE";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor ;
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID").addField("NAME")
                .addField("DESCRIPTION").addField("IMAGE_URL").addField("TYPE").addField("CODE")
                .addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, null, null, null, SLSDB);
        // filename time
        String time = getDate() + ".000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, null, null, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsPackage error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsPackage finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from ODS_USER_PACKAGE_RATE where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserPackageRate(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_PACKAGE_RATE";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("USER_ID")
                .addField("PACKAGE_ID").addField("USER_ROLE").addField("RATE")
                .addField("IP_ADDRESS").addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserPackageRate error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserPackageRate finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from ODS_USER_PACKAGE_VERSION_CLICK where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserPackageVersionClick(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_PACKAGE_VERSION_CLICK";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("PACKAGE_ID")
                .addField("PACKAGE_VER").addField("IP_ADDRESS")
                .addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserPackageVersionClick error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserPackageVersionClick finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }


    /**
     * select * from ODS_USER_PACKAGE_VERSION_SHARE where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserPackageVersionShare(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_PACKAGE_VERSION_SHARE";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("PACKAGE_ID")
                .addField("PACKAGE_VER").addField("SHARE_TARGET").addField("IP_ADDRESS")
                .addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("UPDATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("UPDATE_USER_ID").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserPackageVersionShare error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserPackageVersionShare finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from ODS_USER_PACKAGE_VERSION_DOWNLOAD where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserPackageVersionDownload(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_PACKAGE_VERSION_DOWNLOAD";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("PACKAGE_ID")
                .addField("PACKAGE_VER").addField("IP_ADDRESS")
                .addField("CREATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .addField("FORMAT").addField("DOWNLOAD_TARGET").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserPackageVersionDownload error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserPackageVersionDownload finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }


    /**
     * select * from ODS_USER_RESOURCE_VERSION_DOWNLOAD where CREATED >= :startdate AND CREATED <
     * :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserResourceVersionDownload(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_RESOURCE_VERSION_DOWNLOAD";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("PACKAGE_ID")
                .addField("PACKAGE_VER").addField("RESOURCE_ID").addField("RESOURCE_VER")
                .addField("IP_ADDRESS").addField("CREATED", OdsCellProcessorType.DATETIME)
                .addField("CREATE_USER_ID").addField("FORMAT").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserResourceVersionDownload error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserResourceVersionDownload finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from SIP_USER_SESSION where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findSipUserSession(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "SIP_USER_SESSION";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("SID")
                .addField("START_TIME", OdsCellProcessorType.DATETIME)
                .addField("END_TIME", OdsCellProcessorType.DATETIME).addField("SOURCE_IP")
                .addField("SESSION_ID").build();
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsvPage(tablename, SLSDB, odsToCsv, start, end, "START_TIME", "SID", time);
        } catch (IOException e) {
            log.error("Ods774xJob findSipUserSession error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findSipUserSession finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * select * from SIP_USER_SURF where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findSipUserSurf(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "SIP_USER_SURF";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("SID")
                .addField("SOURCE_IP").addField("FUNCTION_ID")
                .addField("CREATE_TIME", OdsCellProcessorType.DATETIME).addField("SESSION_ID")// 20151001增加欄位
                .build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, "CREATE_TIME",
                SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findSipUserSurf error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findSipUserSurf finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }


    /**
     * select * from UAARESOURCE
     */
    @Async
   public void findUaaresource() {
        Date jobStartTime = new Date();
        String tablename = "UAARESOURCE";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("TYPEID")
                .addField("RESOURCEID").addField("RESOURCENAME").addField("STATE")
                .addField("SECURITYLEVEL").addField("CREATETIME", OdsCellProcessorType.DATETIME)
                .addField("UPDATETIME", OdsCellProcessorType.DATETIME).addField("DATAROLE1")
                .addField("DATAROLE2").addField("UUIDNUMBER").build();
        List<Map<String, Object>> resultList = findWithDate(tablename, null, null, null,
                SLSUAAFRONT);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSUAAFRONT, odsToCsv, null, null, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findUaaresource error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findUaaresource finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }


    /**
     * select * from ODS_USER_PACKAGE_NOTIFY where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            start
     * @param end
     *            end
     */
    @Async
    public void findOdsUserPackageNotify(Date start, Date end) {
        Date jobStartTime = new Date();
        String tablename = "ODS_USER_PACKAGE_NOTIFY";
        // addField是有順序性的
        // processorType目前只有date、datetime，其他型態不用加processor
        OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(false).addField("ID")
                .addField("USER_ID").addField("USER_ROLE").addField("EMAIL").addField("PACKAGE_ID")
                .addField("RESOURCE_ID").addField("RESOURCE_CRITERA_ID")
                .addField("CREATED", OdsCellProcessorType.DATETIME).addField("CREATE_USER_ID")
                .build();
        List<Map<String, Object>> resultList = findWithDate(tablename, start, end, CREATED, SLSDB);
        // filename time
        String time = getMonth() + "02.000000";// 日期時間使用約定檔名
        try {
            outputToCsv(tablename, SLSDB, odsToCsv, start, end, resultList, time);
        } catch (IOException e) {
            log.error("Ods774xJob findOdsUserPackageNotify error!" + e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Ods774xJob findOdsUserPackageNotify finish use "
                + ((new Date()).getTime() - jobStartTime.getTime()) / 1000 + " sec.");
    }

    /**
     * 取得CSV路徑
     * 
     * @return String
     * @throws IOException
     */
    private String getResourcePath() throws IOException {
        // filename time
        String dateFormat = "yyyyMMdd";
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(dateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        String datestr = formatter.format(Calendar.getInstance().getTime());

        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "odsdan/")
                .getAbsolutePath();

        targetPath = targetPath + File.separator;
        String path = targetPath + datestr;
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        return path + File.separator + OUTPUT_FILENAME;
    }

    /**
     * List<Map> to csv
     * 
     * @param tablename   tablename  
     * @param dbname      dbname     
     * @param odsToCsv    odsToCsv   
     * @param start       start      
     * @param end         end        
     * @param resultList  resultList 
     * @param time        time       
     * @throws IOException
     */
    private void outputToCsv(String tablename, String dbname, OdsToCsv odsToCsv, Date start,
            Date end, List<Map<String, Object>> resultList, String time) throws IOException {

        String filePath = String.format(getResourcePath(), dbname, tablename, time);
        log.debug("Ods774xJob findOdsIndividePackageSub output file:" + filePath);
        File createFile = new File(filePath);
        if (!createFile.exists()) {
            log.debug("Ods774xJob create file:" + createFile.createNewFile());
        } else {
            log.debug("Ods774xJob file exists:" + filePath);
        }

        if (resultList.isEmpty()) {
            log.info("Ods774xJob resultList empty!");
        }
        log.info("Ods774xJob file rename to TXT!");
        odsToCsv.beanListToCsv(resultList, filePath);
        File rnfile = new File(filePath.substring(0,filePath.length()-4));//移除_tmp
        createFile.renameTo(rnfile);
    }

    /**
     * List<Map> to csv with paging
     * 
     * @param tablename
     *            tablename
     * @param dbname
     *            dbname
     * @param odsToCsv
     *            odsToCsv
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            dateFeildName
     * @param orderId
     *            orderId
     * @param time
     *            time
     * @throws IOException
     */
    private void outputToCsvPage(String tablename, String dbname, OdsToCsv odsToCsv, Date start,
            Date end, String dateFeildName, String orderId, String time) throws IOException {

        String filePath = String.format(getResourcePath(), SLSDB, tablename, time);
        log.debug("Ods774xJob findSipUserSession output file:" + filePath);

        File createFile = new File(filePath);
        if (!createFile.exists()) {
            log.debug("Ods774xJob create file:" + createFile.createNewFile());
        } else {
            log.debug("Ods774xJob file exists:" + filePath);
        }

        // 取得總筆數
        int total = countWithDate(tablename, start, end, dateFeildName, dbname);
        int offset = 0;
        if (total > 0) {
            // settting preference with OdsCsvEncode
            // line first is |#
            // line last is #/
            // delimiter is *|
            // no quote
            // no convert delimiter、quote、\r\n
            log.debug("beanListToCsv csvPreference building");
            final CsvPreference csvPreference = new CsvPreference.Builder('"', '|', "\n")
                    .useEncoder(new OdsCsvEncoder()).build();

            ICsvMapWriter mapWriter = null;
            try {
                File toFile = new File(filePath);
                OutputStream os = new FileOutputStream(toFile);
                /*
                 * byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF}; os.write(UTF8_BOM);
                 * os.flush();
                 */
                OutputStreamWriter out = new OutputStreamWriter(os, "UTF-8");
                mapWriter = new OdsCsvMapWriter(out, csvPreference);
                if (odsToCsv.isHasHeader()) {
                    log.debug("beanListToCsv writeHeader " + odsToCsv.isHasHeader());
                    mapWriter.writeHeader(odsToCsv.getHeader());
                }
                log.info("beanListToCsv writing... total size:" + total);

                // 分頁處理，每頁flush一次
                while (offset <= total) {
                    log.info("beanListToCsv page offset:" + offset);

                    // 查詢資料依據page
                    List<Map<String, Object>> resultList = findWithDatePage(tablename, start,
                            end, dateFeildName, dbname, offset, PAGE_PER_SIZE, orderId);

                    for (Map<String, Object> pojotest : resultList) {
                        mapWriter.write(pojotest, odsToCsv.getHeader(),
                                odsToCsv.getProcessors());
                    }
                    log.info("beanListToCsv flush");
                    mapWriter.flush();

                    offset = offset + PAGE_PER_SIZE;
                }
            } finally {
                if (mapWriter != null) {
                    mapWriter.close();
                    File rnfile = new File(filePath.substring(0,filePath.length()-4));//移除_tmp
                    createFile.renameTo(rnfile);
                }
                log.debug("beanListToCsv close.");
            }
        } else {
            log.info("Ods774xJob resultList empty!");
        }
    }

    /**
     * select * from dbname.tablename where CREATED >= :startdate AND CREATED < :enddate
     * 
     * @param start
     *            Date
     * @param end
     *            Date
     * @param dateFeildName
     *            dateFeildName
     * @param dbname
     *            dbname
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findWithDate(String tablename, Date start, Date end,
            String dateFeildName, String dbname) {
        log.info("select * from " + dbname + "." + tablename + " where " + dateFeildName + " >= '"
                + start + "' AND " + dateFeildName + " <'" + end + "'");
        return odsDWDao.findDWHStageData(tablename, start, end, dateFeildName, dbname);
    }

    /**
     * 分頁查詢
     * 
     * @param tablename
     *            tablename
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            dateFeildName
     * @param dbname
     *            dbname
     * @param offset
     *            offset
     * @param limit
     *            limit
     * @param orderId
     *            orderId
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findWithDatePage(String tablename, Date start, Date end,
            String dateFeildName, String dbname, int offset, int limit, String orderId) {
        log.info("select * from " + dbname + "." + tablename + " where " + dateFeildName + " >= '"
                + start + "' AND " + dateFeildName + " <'" + end + "'");
        log.info("offset:" + offset + " limit:" + limit + " orderId:" + orderId);
        return odsDWDao.findDWHStageDataPage(tablename, start, end, dateFeildName, dbname, offset,
                limit, orderId);
    }

    /**
     * 取得查詢結果總筆數
     * 
     * @param tablename
     *            tablename
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            dateFeildName
     * @param dbname
     *            dbname
     * @return int
     */
    public int countWithDate(String tablename, Date start, Date end, String dateFeildName,
            String dbname) {
        return odsDWDao.countDWHStageData(tablename, start, end, dateFeildName, dbname);
    }

    /**
     * @return yyyyMM
     */
    private String getMonth() {
        // String dateFormat = "yyyyMMdd.HHmmss";
        String dateFormat = "yyyyMM";
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(dateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        String time = formatter.format(Calendar.getInstance().getTime());
        return time;
    }

    /**
     * @return yyyyMMdd
     */
    private String getDate() {
        String dateFormat = "yyyyMMdd";
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(dateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        String time = formatter.format(Calendar.getInstance().getTime());
        return time;
    }
}
