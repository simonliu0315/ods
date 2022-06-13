package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.repository.OdsPackageResourceRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PackResService {
    
    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;

    public void createCompressedFile(String packageId, String packageVer) throws Exception {
        List<Ods703eTab2DialogDto> resources = odsPackageResourceRepository.findPackResByIdAndVer(packageId, Integer.parseInt(packageVer));
        log.info("ResourceNums:" + resources.size());
        
        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "package/" + packageId + "/zip/").getAbsolutePath();
        log.info("Target Path:" + targetPath);
        File targetFile = new File(targetPath); // 目的
        // 如果目的路徑不存在，則新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));

        String targetName = packageId + "-" + packageVer + ".zip"; // 目的壓縮文件名
        FileOutputStream outputStream = new FileOutputStream(targetPath + "\\" + targetName);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));

        for (Ods703eTab2DialogDto res : resources) {
            File resourceFile = null;
            //只壓縮dataset
            log.info("Resource Format:" + res.getFormat());
            if ("dataset".equals(res.getFormat())) {
                resourceFile = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + res.getResourceId() + "/dataset/" + res.getResourceId() + "-"
                      + res.getVer() + ".csv");
            } else {
                continue;
            }
            
//            if ("image".equals(res.getFormat())) {
//                resourceFile = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + res.getResourceId() + "/image/" + res.getResourceId() + "-"
//                      + res.getVer() + "." + res.getFormat());
//            } else if ("dataset".equals(res.getFormat())) {
//                resourceFile = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + res.getResourceId() + "/dataset/" + res.getResourceId() + "-"
//                      + res.getVer() + "." + res.getFormat());
//            } else if ("pdf".equals(res.getFormat())) {
//                resourceFile = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + res.getResourceId() + "/pdf/" + res.getResourceId() + "-"
//                      + res.getVer() + "." + res.getFormat());
//            } else if ("twbx".equals(res.getFormat())) {
//                log.info("twbx path:{}", publicPath + "workbook/" + res.getResourceId() + "/twb/" + res.getResourceId() + "-"
//                      + res.getVer() + "." + res.getFormat());
//                resourceFile = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "workbook/" + res.getResourceId() + "/twb/" + res.getResourceId() + "-"
//                      + res.getVer() + "." + res.getFormat());
//            }
            
            FileInputStream fis = new FileInputStream(resourceFile);

            out.putNextEntry(new ZipEntry(resourceFile.getName()));
         // 進行寫操作
            int j = 0;
            byte[] buffer = new byte[1024];
            while ((j = fis.read(buffer)) > 0) {
                out.write(buffer, 0, j);
            }
            // 關閉輸入流
            fis.close();
        }
        
        out.close();
        
        log.info("CREATE ZIP COMPLETEED!");

    }
    
}
