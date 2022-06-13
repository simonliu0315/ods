package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.entity.ods.OdsDanResourceStus;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.entity.ods.OdsResourceVersionPK;
import gov.sls.entity.ods.OdsWorkbook;
import gov.sls.entity.ods.OdsWorkbookPK;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods711xViewDto;
import gov.sls.ods.dto.Ods711xWorkbookDto;
import gov.sls.ods.repository.OdsDanResourceStusRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;
import gov.sls.ods.repository.OdsWorkbookRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.Files;

@Service
@Slf4j
public class Ods771xService {

    @Autowired
    private OdsDanResourceStusRepository odsDanResourceStusRepository;

    @Autowired
    private OdsWorkbookRepository odsWorkbookRepository;

    @Autowired
    private OdsResourceVersionRepository odsResourceVersionRepository;
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    private Ods701eService ods701eService;
    
    @Autowired
    private FileStore fileStore;
    

    @Transactional
    public void importTwbs() throws IOException {
        List<Ods711xWorkbookDto> ods711xWorkbookDtos = odsDanResourceStusRepository
                .findNotImportOdsWorkbook();

        String destRootPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                + File.separator + "workbook";

        String sourceRootPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH);
        String destFilePath = null;
        String destFileDir = null;
        String sourceFilePath = null;
        OdsDanResourceStus odsDanResourceStus = null;
        OdsWorkbook odsWorkbook = null;
        OdsWorkbookPK odsWorkbookPK = null;
        Integer ver = null;
        for (Ods711xWorkbookDto ods711xWorkbookDto : ods711xWorkbookDtos) {
            ver = ods711xWorkbookDto.getMaxVer() == null ? 0
                    : ods711xWorkbookDto.getMaxVer() + 1;
            odsDanResourceStus = ods711xWorkbookDto.getOdsDanResourceStus();
            destFileDir = destRootPath + File.separator
                    + odsDanResourceStus.getResourceId() + File.separator
                    + "twb";

            destFilePath = destFileDir + File.separator
                    + odsDanResourceStus.getResourceId() + "-" + ver + ".twbx";

            sourceFilePath = sourceRootPath + File.separator
                    + odsDanResourceStus.getResourceId() + File.separator
                    + "twb" + File.separator
                    + odsDanResourceStus.getResourceId() + ".twbx";

            log.debug("importTwbs ver:{}", ver);
            log.debug("importTwbs destFilePath:{}", destFilePath);
            log.debug("importTwbs sourceFilePath:{}", sourceFilePath);

            copyTo(sourceFilePath, destFilePath);

            OdsDanResourceStus updateOdsDanResourceStus = odsDanResourceStusRepository
                    .findOne(odsDanResourceStus.getId());
            updateOdsDanResourceStus.setImportOdsStus("Y");
            updateOdsDanResourceStus = odsDanResourceStusRepository
                    .save(updateOdsDanResourceStus);
            log.debug("result of odsDanResourceStus:{}",
                    updateOdsDanResourceStus);

            odsWorkbook = new OdsWorkbook();
            odsWorkbookPK = new OdsWorkbookPK();
            odsWorkbookPK.setId(odsDanResourceStus.getResourceId());
            odsWorkbookPK.setVer(ver);
            odsWorkbook.setId(odsWorkbookPK);
            odsWorkbook.setName(ods711xWorkbookDto.getName());
            odsWorkbook.setCreated(new Date());
            odsWorkbook.setUpdated(new Date());
            odsWorkbook.setCreateUserId("sys");
            odsWorkbook.setUpdateUserId("sys");
            odsWorkbookRepository.create(odsWorkbook);
            log.debug("odsWorkbook:{}", odsWorkbook);
        }
    }

    public void copyTo(String sourceFilePath, String destFilePath)
            throws IOException {
        File destFile = new File(destFilePath);
        Files.createParentDirs(destFile);
        Files.asByteSource(new File(sourceFilePath)).copyTo(
                Files.asByteSink(destFile));
    }

    public void saveImportViews(){
        
        try {
            List<Ods711xViewDto> ods711xWorkbookDtos = odsDanResourceStusRepository
                    .findNotImportOdsView();

            String destRootPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                    + File.separator + "resource";

            String sourceRootPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_DAN_RESOURCE_PATH);
            String destFilePath = null;
            String sourceFilePath = null;
            OdsDanResourceStus odsDanResourceStus = null;
            OdsResourceVersion odsResourceVersion = null;
            OdsResourceVersionPK odsResourceVersionPK = null;
            Integer ver = null;
            String extFileName;
            log.debug("size of ods711xWorkbookDtos:{}", ods711xWorkbookDtos.size());
            for (Ods711xViewDto ods711xViewDto : ods711xWorkbookDtos) {
                odsDanResourceStus = ods711xViewDto.getOdsDanResourceStus();
                ver = ods711xViewDto.getMaxVer() + 1;
                extFileName = toExtFileName(ods711xViewDto.getFormat());
                destFilePath = destRootPath + File.separator
                        + ods711xViewDto.getOdsResourceId() + File.separator
                        + ods711xViewDto.getFormat() + File.separator
                        + ods711xViewDto.getOdsResourceId() + "-" + ver + "."
                        + extFileName;

                sourceFilePath = sourceRootPath + File.separator
                        + ods711xViewDto.getWorkbookId() + File.separator
                        + ods711xViewDto.getResourceId() + File.separator
                        + ods711xViewDto.getFormat() + File.separator
                        + ods711xViewDto.getWorkbookId() + "-"
                        + odsDanResourceStus.getResourceId() + "." + extFileName;

                log.debug("importViews ver:{}", ver);
                log.debug("importViews destFilePath:{}", destFilePath);
                log.debug("importViews sourceFilePath:{}", sourceFilePath);

                copyTo(sourceFilePath, destFilePath);

                OdsDanResourceStus updateOdsDanResourceStus = new OdsDanResourceStus();
                updateOdsDanResourceStus.setId(odsDanResourceStus.getId());
                updateOdsDanResourceStus.setExportStus(odsDanResourceStus
                        .getExportStus());
                updateOdsDanResourceStus.setRefreshTime(odsDanResourceStus
                        .getRefreshTime());
                updateOdsDanResourceStus.setResourceId(odsDanResourceStus
                        .getResourceId());
                updateOdsDanResourceStus.setResourceType(odsDanResourceStus
                        .getResourceType());
                updateOdsDanResourceStus.setImportOdsStus("Y");
                updateOdsDanResourceStus = odsDanResourceStusRepository
                        .save(updateOdsDanResourceStus);
                log.debug("result of odsDanResourceStus:{}",
                        updateOdsDanResourceStus);

                odsResourceVersion = new OdsResourceVersion();
                odsResourceVersionPK = new OdsResourceVersionPK();
                odsResourceVersionPK.setResourceId(ods711xViewDto
                        .getOdsResourceId());
                odsResourceVersionPK.setVer(ver);
                odsResourceVersion.setId(odsResourceVersionPK);
                odsResourceVersion.setName(ods711xViewDto.getName());
                odsResourceVersion.setUrl("abc");
                odsResourceVersion.setCreated(new Date());
                odsResourceVersion.setUpdated(new Date());
                odsResourceVersion.setCreateUserId("sys");
                odsResourceVersion.setUpdateUserId("sys");
                odsResourceVersionRepository.create(odsResourceVersion);
                
                exportToDataStore(ods711xViewDto);
            }

            log.debug("end of importViews");
        } catch(Exception e){
            log.error("error in importViews:{}", e);
            throw new RuntimeException(e);
        }
    }
    
    private void exportToDataStore(Ods711xViewDto ods711xViewDto) throws FileNotFoundException, Exception{
        log.debug("start Ods711eService#exportToDataStore:{}", ods711xViewDto);
        if(!"dataset".equals(ods711xViewDto.getFormat())){
            return;
        }
        
        String destRootPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                + File.separator + "resource";
        String destFilePath = destRootPath + File.separator
                + ods711xViewDto.getOdsResourceId() + File.separator
                + ods711xViewDto.getFormat() + File.separator
                + ods711xViewDto.getOdsResourceId() + "-" + (ods711xViewDto.getMaxVer() + 1) + ".csv";
        
        log.debug("Ods711eService#exportToDataStore destFilePath:{}", destFilePath);
        File csvFile = new File(destFilePath);
        
        FileInputStream in = null;
        try {
            in = new FileInputStream(csvFile);
            //ods701eService.datasetToDatastore(ods711xViewDto.getOdsResourceId(), ods711xViewDto.getMaxVer() + 1, in);
        } finally {
            if (in != null) {
              safeClose(in);
            }
        }
        
        
    }

    private String toExtFileName(String format) {
        switch (format) {
        case "dataset":
            return "csv";
        case "image":
            return "png";
        case "pdf":
            return "pdf";
        }

        return null;
    }

    public static void safeClose(FileInputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }
    
}
