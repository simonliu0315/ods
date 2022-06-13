package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * dan to ods 受捐贈機關或團體轉檔 VIEW_ID
 * O1503#D01@ALL$01 資料集受捐贈機關或團體捐贈統計彙總表
 */
@Slf4j
@Service
public class Ods775xService {

    private final static String FILENAME_PREFIX = "EIDW-DAN.DAN_B2C_XCA_DNT_SUMY.%s";
    
    private final static String VIEW_ID="O1503#D01@ALL$01";
    
    private final static String DATE="15";
    
    private final static int FIELD_COUNT=11;
    
    private final static int MAX_BATCH=1000;
    
    private final static Charset ENCODING=StandardCharsets.UTF_8;

    private final static String END_MARK="#\\|";
    
    private final static String SPLIT_MARK="\\*\\|";
    
    private final static String SUFFIX=".TXT";
    
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
    
    private File getUploadFile(){
        String datetime = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime())
                +DATE +".";
        final String prefix=String.format(FILENAME_PREFIX,datetime);
        File[] fileList = new File(getFullPath()).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().startsWith(prefix) && pathname.getName().endsWith(SUFFIX));
            }
        });
        File result_file=null;
        
        if (fileList!=null) {
            for (File file:fileList){
                result_file=file;
            }        
        }
        return result_file;
    }

    @Async
    public void run() { 
        File uploadFile=getUploadFile();
        if(uploadFile!=null && uploadFile.exists()) {
            String resourceId=service.getResourceID(VIEW_ID);
            service.deleteAllByResourceId(resourceId);
            List<Object[]> datas=new ArrayList<Object[]>();
            Path path = Paths.get(uploadFile.getPath());
            try (Scanner scanner =  new Scanner(path, ENCODING.name())){
              while (scanner.hasNextLine()){
                String []groups=scanner.nextLine().split(END_MARK);
                for(String contents:groups) {
                    String[] content=contents.split(SPLIT_MARK);
                    if (content.length!=FIELD_COUNT-1) continue;
                    Object []objs=mapStringToObject(content);
                    datas.add(objs);
                    if(datas.size()==MAX_BATCH) {
                        service.createDataByResourceId(resourceId, FIELD_COUNT, datas);
                        datas.clear();
                    }
                }
              }
              if(datas.size()>0) {
                  service.createDataByResourceId(resourceId, FIELD_COUNT, datas);
                  datas.clear();
              }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            removeFile(path);
            updateOdsResourceIndivid();//更新訂閱更新紀錄
        }else{
            log.info("File is not Exist!!");
        }
    }

    /**
     * 更新訂閱更新紀錄
     */
    private void updateOdsResourceIndivid() {
        log.info("updateOdsResourceIndivid Start Executing!");
        service.createOdsResourceIndivid(VIEW_ID);
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
            throw new RuntimeException(e);
        }
    }
    
    private Object[] mapStringToObject(String[] content) {
        Object []objs=new Object[FIELD_COUNT];
        objs[0]=new Integer(1);
        objs[1]=content[0];
        objs[2]=content[1];
        objs[3]=content[2];
        objs[4]=content[3];
        objs[5]=content[4];
        objs[6]=content[5];
        objs[7]=content[6];
        objs[8]=Integer.parseInt(content[7]);
        objs[9]=Integer.parseInt(content[8]);
        objs[10]=Integer.parseInt(content[9]);
        return objs;
    }
    
}
