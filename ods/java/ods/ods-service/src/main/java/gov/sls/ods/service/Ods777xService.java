package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ods.OdsOrgCode;
import gov.sls.entity.ods.OdsOrgCodePK;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.repository.OdsOrgCodeRepository;
import gov.sls.ods.repository.OdsOrgCodeRepositoryCustom;

import java.io.File;
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
 * 代碼檔轉檔
 * 縣市鄉鎮市區
 */
@Slf4j
@Service
public class Ods777xService {

    private final static int MAX_BATCH = 1000;

    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private final static String START_MARK = "\\|#";

    private final static String END_MARK = "#\\|";

    private final static String SPLIT_MARK = "\\*\\|";
    
    private final static int ODSORGCODE_FIELD_COUNT = 6;
    
    @Autowired
    private OdsOrgCodeRepository odsOrgCodeRepository;
    
    @Autowired
    private OdsOrgCodeRepositoryCustom odsOrgCodeRepositoryCustom;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;

    /**
     * DAN送ODS共用縣市鄉鎮狀態檔
     * 整檔
     * 每月 / 10日給檔
     */
    @Async
    public void runOdsOrgCode() {
        String filePattern="EIDW-DAN.DAN_SHR_HSN_TOWN_STUS.%s10.000000.TXT";
        String datetime=new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
        String filePath=getFullPath()+File.separator+String.format(filePattern,datetime);
        log.debug("filePath="+filePath);
        File uploadFile=new File(filePath);
        if (uploadFile!=null && uploadFile.exists()) {
            Path path = Paths.get(uploadFile.getPath());

           odsOrgCodeRepository.deleteAll();
            
            List<OdsOrgCode> datas = new ArrayList<OdsOrgCode>();
            try (Scanner scanner = new Scanner(path, ENCODING.name())) {
                while (scanner.hasNextLine()) {
                    String[] groups = scanner.nextLine().replaceAll(START_MARK, "").split(END_MARK);
                    for (String contents : groups) {
                        String[] content = contents.split(SPLIT_MARK);
                        if (content.length != ODSORGCODE_FIELD_COUNT)
                            continue;
                        OdsOrgCode obj = mapOdsOrgCodeEntity(content);
                        datas.add(obj);
                        if (datas.size() == MAX_BATCH) {
                            odsOrgCodeRepositoryCustom.saveBatch(datas);
                            //saveBatch(datas);
                            datas.clear();
                        }
                    }
                }
                if (datas.size() > 0) {
                    odsOrgCodeRepositoryCustom.saveBatch(datas);
                    //saveBatch(datas);
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
    
    private OdsOrgCode mapOdsOrgCodeEntity(String[] content) {
        OdsOrgCodePK pk=new OdsOrgCodePK();
        OdsOrgCode entity=new OdsOrgCode();
        pk.setHsnCd(content[0]);
        pk.setTownCd(content[2]);
        entity.setId(pk);
        entity.setHsnNm(content[1]);
        entity.setTownNm(content[3]);
        
        return entity;
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
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 批次儲存資料
     * @param entities
     */
    public void saveBatch(List<OdsOrgCode> entities) {
        for(OdsOrgCode entity:entities) {
            odsOrgCodeRepository.save(entity);
        }
    }
    
}
