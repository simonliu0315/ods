package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;

import java.io.File;
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
import java.util.List;
import java.util.Scanner;
import java.util.Calendar;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Ods779xService {

    private final static int MAX_BATCH = 1000;

    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private final static String START_MARK = "\\|#";

    private final static String END_MARK = "#\\|";

    private final static String SPLIT_MARK = "\\*\\|";

    // 開立電子發票彙總
    private final static String b2cUserCarrierSumy_VIEW_ID = "O1501#D01@ALL$01";
    private final static String b2cUserCarrierSumy_FILENAME = "B2C_USER_CARRIER_SUMY";
    private final static int b2cUserCarrierSumy_FIELD_COUNT = 15;
    
    @Autowired
    private ImportService service;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;

    @Async
    public void runB2cUserCarrierSumy(Date runDate) {
        long startTime = System.currentTimeMillis();
        String filePattern="EIDW-DAN.DAN_%s.%s.000000.TXT";


        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, -1);
        runDate = c.getTime();


        String datetime=new SimpleDateFormat("yyyyMMdd").format(runDate);
        String filePath=getFullPath()+File.separator+String.format(filePattern,b2cUserCarrierSumy_FILENAME,datetime);
        log.info("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            String resourceId = service.getResourceID(b2cUserCarrierSumy_VIEW_ID);
            Path path = Paths.get(uploadFile.getPath());

            service.deleteAllByResourceId(resourceId);
            
            List<Object[]> datas = new ArrayList<Object[]>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != b2cUserCarrierSumy_FIELD_COUNT)
                            continue;
                        Object[] objs = mapB2cUserCarrierSumyStringToObject(content);
                        datas.add(objs);
                        if (datas.size() == MAX_BATCH) {
                            service.createDataByResourceId(resourceId,
                                    b2cUserCarrierSumy_FIELD_COUNT, datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    service.createDataByResourceId(resourceId, b2cUserCarrierSumy_FIELD_COUNT, datas);
                    datas.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
            updateOdsResourceIndivid(b2cUserCarrierSumy_VIEW_ID);//更新訂閱更新紀錄
        } else {
            log.info("File is not Exist!!");
        }

        log.info("Ods779xJob_runB2cUserCarrierSumy Finished!!Totle time:"
                + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    /**
     * 將取得的Content轉為物件
     * 
     * @param content
     * @return
     */
    private Object[] mapB2cUserCarrierSumyStringToObject(String[] content) {
        Object[] objs = new Object[b2cUserCarrierSumy_FIELD_COUNT];
        objs[0] = content[0];
        objs[1] = content[1];
        try {
            objs[2]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[2]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[3] = content[3];
        try {
            objs[4]=new SimpleDateFormat("yyyyMMddHHmmss").parseObject(content[4]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        objs[5] = content[5];
        objs[6] = content[6];
        objs[7] = content[7];
        objs[8] = content[8];
        objs[9] = new BigDecimal(content[9]);
        objs[10] = new BigDecimal(content[10]);
        objs[11] = new BigDecimal(content[11]);
        objs[12] = new BigDecimal(content[12]);
        objs[13] = new BigDecimal(content[13]);
        objs[14] = new BigDecimal(content[14]);

        return objs;
    }
    
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
     * 更新訂閱更新紀錄
     * @param viewId viewId
     */
    private void updateOdsResourceIndivid(String viewId) {
        log.info("updateOdsResourceIndivid Start Executing!");
        service.createOdsResourceIndivid(viewId);
        log.info("updateOdsResourceIndivid finished!");
    }
    
}
