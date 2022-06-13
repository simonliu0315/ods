package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.entity.ods.OdsIdentity;
import gov.sls.entity.ods.OdsIdentityGroup;
import gov.sls.entity.ods.QOdsIdentity;
import gov.sls.entity.ods.QOdsIdentityGroup;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods704eFormBean;
import gov.sls.ods.dto.Ods704eGrid1Dto;
import gov.sls.ods.repository.OdsGroupPackageRepository;
import gov.sls.ods.repository.OdsGroupRepository;
import gov.sls.ods.repository.OdsIdentityGroupRepository;
import gov.sls.ods.repository.OdsIdentityRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods704eService {

    
    @Autowired
    private OdsGroupRepository odsGroupRepository;
    
    @Autowired
    private OdsIdentityRepository odsIdentityRepository;
    
    @Autowired
    private OdsIdentityGroupRepository odsIdentityGroupRepository;
    
    @Autowired
    private OdsGroupPackageRepository odsGroupPackageRepository;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;
    
    protected String ODS_PACKAGE_TEMPLATE_HTML = "package" + File.separator;
    protected String ODS_GROUP_PATH = "group" + File.separator;
    
    public List<OdsIdentity> findIdentities() {
        return Lists
                .newArrayList(odsIdentityRepository.findAll(null, QOdsIdentity.odsIdentity.name.asc()));
    } 
    
    public List<OdsGroup> findGroupByName(String name, String description) {
        return odsGroupRepository.findGroupByName(name, description);
    }    
    
    public List<Ods704eGrid1Dto> findPackageByGroupId(String groupId) {
        return odsGroupRepository.findPackageByGroupId(groupId);
    }    
    
    public List<OdsIdentityGroup> findIndentityByGroupId(String groupId) {
        BooleanExpression criteria = QOdsIdentityGroup.odsIdentityGroup.id.groupId.eq(groupId);
        
        return Lists
                .newArrayList(odsIdentityGroupRepository.findAll(criteria));
    }   
    
    public List<Ods704eGrid1Dto> findUnGroupPackageByNameSelPkg(String name, String selectedPkgList) {
        return odsIdentityGroupRepository.findUnGroupPackageByNameSelPkg(name, selectedPkgList);
    }  
    

    private String delLastCommon(String beforeString) {
        
        String afterString = "";
        
        if (StringUtils.isNotEmpty(beforeString) && ",".equals(beforeString.substring(beforeString.length() - 1, beforeString.length()) ))
        {
            afterString = beforeString.substring(0, beforeString.length() - 1);
        }
        
        return afterString;
    }
    
    private String getFileExtension(String fileName){
        log.debug("input fileName{" + fileName + "}");
        
        int startIndex = fileName.lastIndexOf(46) + 1;
        int endIndex = fileName.length();
        String fileExtension = fileName.substring(startIndex, endIndex);
        log.debug("output fileExtension{" + fileExtension + "}");
        
        return fileExtension;
    }
    
    public void create(String name, String description, String chkIdentityIdList, String selPackageIdList, InputStream multipartFileStream, String fileName) throws Exception {
        
        String fileExtension = getFileExtension(fileName);
        
        String groupId = odsGroupRepository.createGroupByNameDesc(name, description, fileExtension);
        
        chkIdentityIdList = delLastCommon(chkIdentityIdList);
        if (!chkIdentityIdList.isEmpty())
        {
            odsIdentityGroupRepository.createIdentityGroupByGupIdIdtIdList(groupId, chkIdentityIdList);
        }
        
        selPackageIdList = delLastCommon(selPackageIdList);
        if (!selPackageIdList.isEmpty())
        {
            odsGroupPackageRepository.createGroupPackageByGupIdPkgIdList(groupId, selPackageIdList);
        }
        
        
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = multipartFileStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        
        multipartFileStream.close();
        multipartFileStream = null;*/
        String imagePath = getImagePath(groupId, fileExtension);
        
        //資料建入實體檔案(必擺最後，否則前段出錯的話檔案還是會被新增進去)
        //InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray());  
        
        try {
            generateFile(imagePath, multipartFileStream);
        } finally {
            multipartFileStream.close();
            multipartFileStream = null;
        }

        /*baos.close();
        baos = null;
        streamToDatastore.close();
        streamToDatastore = null;*/

        
    }
    
    private String getImagePath(String groupId, String fileExtension) {
        return 
        propertiesAccessor
        .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
        + ODS_GROUP_PATH
        + groupId
        + File.separator
        + "image"
        + File.separator
        + groupId + ".png";
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
        } /*finally {

            if (multipartFileStream != null) {
                log.debug("AAA");   multipartFileStream.close(); multipartFileStream=null;
            }
        }*/

     
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
    
    

    
    public void updateGroupByNameDescIdtIdLstPkgIdLst(Ods704eFormBean ods704eFormBean) throws Exception {
        odsGroupRepository.updateGroupByNameDescIdtIdLstPkgIdLst(ods704eFormBean);
    } 
    
    
    
    public void updateGroupByNameDescIdtIdLstPkgIdLstImage(Ods704eFormBean ods704eFormBean, InputStream multipartFileStream, String fileName) throws Exception {
        
        String fileExtension = getFileExtension(fileName);
        
        String imageUrl = ods704eFormBean.getId() + ".png";
        
        ods704eFormBean.setImageUrl(imageUrl);
        
        updateGroupByNameDescIdtIdLstPkgIdLst(ods704eFormBean);
        
        String imagePath = getImagePath(ods704eFormBean.getId(), fileExtension);
        
        try {
            generateFile(imagePath, multipartFileStream);
        } finally {
            multipartFileStream.close();
            multipartFileStream = null;
        }
    } 
    
    

    private String getImagePath2(String groupId) {
        return 
        propertiesAccessor
        .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
        + ODS_GROUP_PATH
        + groupId;
    }
    
    public void deleteGroupByGroupId(Ods704eFormBean ods704eFormBean) throws Exception {

        //System.gc();
        String filePath = getImagePath2(ods704eFormBean.getId());
        

        File toFile =                 
                new File(filePath);
        delete(toFile);

        
        odsGroupRepository.deleteGroupByGroupId(ods704eFormBean);
    } 
    
    
    public static void delete(File file){
        if (file.exists()){

            if (file.isDirectory()){
                File[] files = file.listFiles();
                for (File f : files){
                    log.debug("DDDDDDDDDDDDD" + f.getName());
                    log.debug("DDDDDDDDDDDDD" + f.getPath());
                    delete(f);
                }
            }

            file.delete();
        }
    }
}
