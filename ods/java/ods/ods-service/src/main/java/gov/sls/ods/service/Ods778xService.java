/**
 * 
 */
package gov.sls.ods.service;

import gov.sls.commons.config.ApplicationProperties;
import gov.sls.commons.config.ApplicationPropertiesAccessor;
import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * <pre>
 * SFTP搬檔帳密設定路徑 
 * \psrapp\config\ods\sftppw.properties 
 * 內容： 
 * sever=sftpip
 * username=test 
 * password=test 
 * uploaddest=/TAX_EINVOICE/ODSDAN/recv
 * downloadsrc=/TAX_EINVOICE/DANODS/recv
 * fooduploaddest=/BEMF/import_ODStoFOOD
 * fooddownloadsrc=/BEMF/export_FOODtoODS
 * tofoodfile=SLSDB-DBO.ODS_INDIVIDE_PACKAGE_SUB.,SLSDB-DBO.ODS_PACKAGE.
 * </pre>
 */
@Slf4j
@Service
public class Ods778xService {

    private final static int SFTP_TIMEOUT = 5 * 60 * 1000;

    private final static String BACKUP = "backup";

    @Inject
    private ApplicationPropertiesAccessor propertiesAccessor;

    @Inject
    private OdsApplicationPropertiesAccessor odsPropertiesAccessor;

    @Autowired
    FileStore fileStore;

    private String REMOTE_SFTP_URI = "sftp://%s:%s@%s%s/%s";

    private String REMOTE_UPLOAD_PATH_PROP_NAME = "uploaddest";
    private String REMOTE_DOWNLOAD_PATH_PROP_NAME = "downloadsrc";

    // 食品雲平行測試使用之設定
    private String REMOTE_UPLOAD_FOOD_PATH_PROP_NAME = "fooduploaddest";
    private String REMOTE_DOWNLOAD_FOOD_PATH_PROP_NAME = "fooddownloadsrc";
    private String SEND_TO_FOOD_FILES = "tofoodfile";

    /**
     * SFTP for ODS TO DAN @Scheduled(cron = "0 1,30 * * * ?")
     * 
     */
    public void runUpload(Date uploadDate) {
        log.debug("runUpload start");
        String uploadsrc = getOdsDanPath() + File.separator
                + new SimpleDateFormat("yyyyMMdd").format(uploadDate);

        File localFolder = new File(uploadsrc);
        if (!localFolder.exists()) {
            log.info("runUpload finish! localfolder not exist. path is " + uploadsrc);
            return;
        }
        
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            FileSystemOptions opts = sftpInit(manager);

            log.debug("resolveFile");
            FileObject localFolderObject = manager.resolveFile(localFolder.getAbsolutePath());
            for (FileObject localFile : localFolderObject.getChildren()) {
                log.debug("resolveFiles:" + localFile.getName());

                String baseName = localFile.getName().getBaseName();
                String fileExtension = localFile.getName().getExtension();

                int sentFlag = 0;// 0未傳、1傳成功且改名完成


                // 食品雲並行傳檔處理
                if (StringUtils.equals(fileExtension, "TXT") && checkPropertyForFood(baseName)) {
                    log.debug(" food upload File:" + localFile.getName());
                    String remoteTempUrl = getRemoteUri(REMOTE_UPLOAD_FOOD_PATH_PROP_NAME, baseName + "_TMP");
                    log.info(" food upload uri:" + remoteTempUrl);
                    FileObject remoteTempObject = manager.resolveFile(remoteTempUrl, opts);
                    remoteTempObject.copyFrom(localFile, Selectors.SELECT_SELF);

                    log.debug(" food rename File:" + baseName);
                    String remoteUrl = getRemoteUri(REMOTE_UPLOAD_FOOD_PATH_PROP_NAME, baseName);
                    log.info(" food rename uri:" + remoteUrl);
                    FileObject remoteObject = manager.resolveFile(remoteUrl, opts);
                    remoteTempObject.moveTo(remoteObject);

                    sentFlag = 1;
                }
                
                if (StringUtils.equals(fileExtension, "TXT")) {

                    log.debug("  upload File:" + localFile.getName());
                    String remoteTempUrl = getRemoteUri(REMOTE_UPLOAD_PATH_PROP_NAME, baseName + "_TMP");
                    log.info("  upload uri:" + remoteTempUrl);
                    FileObject remoteTempObject = manager.resolveFile(remoteTempUrl, opts);
                    remoteTempObject.copyFrom(localFile, Selectors.SELECT_SELF);

                    log.debug("  rename File:" + baseName);
                    String remoteUrl = getRemoteUri(REMOTE_UPLOAD_PATH_PROP_NAME, baseName);
                    log.info("  rename uri:" + remoteUrl);
                    FileObject remoteObject = manager.resolveFile(remoteUrl, opts);
                    remoteTempObject.moveTo(remoteObject);

                    sentFlag = 1;
                }

                if (1 == sentFlag) {// 傳成功，備份檔案
                    // backup file
                    log.debug("  backup File:" + localFile.getName());
                    FileObject localBackupObject = manager.resolveFile(localFolder
                            .getAbsolutePath()
                            + File.separator
                            + BACKUP
                            + File.separator
                            + baseName);
                    localBackupObject.copyFrom(localFile, Selectors.SELECT_SELF);
                    localFile.delete();
                }
            }
        } catch (FileSystemException e) {
            log.error("runUpload sftp error!" + e.getMessage());
        } finally {
            manager.close();
        }
        log.debug("runUpload finish");
    }

    /**
     * SFTP for DAN TO ODS @Scheduled(cron = "0 15,45 * * * ?")
     * 
     */
    public void runDownload() {
        log.debug("runDownload start");
        String downloaddest = getDanOdsPath();

        File localFolder = new File(downloaddest);
        if (!localFolder.exists()) {
            log.info("downloaddest not exist. create it. path is " + downloaddest);
            localFolder.mkdirs();
        }
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            FileSystemOptions opts = sftpInit(manager);

            String remoteUrl = getRemoteUri(REMOTE_DOWNLOAD_PATH_PROP_NAME, "");

            log.debug("resolveFile");
            FileObject remoteFolder = manager.resolveFile(remoteUrl, opts);
            for (FileObject remoteFile : remoteFolder.getChildren()) {
                log.debug("resolveFiles:" + remoteFile.getName());
                if (StringUtils.equals(remoteFile.getName().getExtension(), "TXT")) {
                    log.debug("  copy File:" + remoteFile.getName());
                    FileObject localTempFile = manager.resolveFile(localFolder.getAbsolutePath() + File.separator + remoteFile.getName().getBaseName() + "_TMP");
                    localTempFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

                    FileObject localFile = manager.resolveFile(localFolder.getAbsolutePath() + File.separator + remoteFile.getName().getBaseName());
                    log.debug("  rename File:" + localFile.getName());
                    localTempFile.moveTo(localFile);

                    // remote file delete
                    remoteFile.delete();
                }
            }
        } catch (FileSystemException e) {
            log.error("runDownload sftp error!" + e.getMessage());
        } finally {
            manager.close();
        }
        log.debug("runDownload finish");
    }

    /**
     * SFTP for DAN Tableau TO ODS 
     */
    public void runDownloadTableau() {
        log.debug("runDownloadTableau start");
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            FileSystemOptions opts = sftpInit(manager);

            String remoteUrl = getRemoteUri(REMOTE_DOWNLOAD_PATH_PROP_NAME, "");

            log.debug("resolveFile");
            FileObject remoteFolder = manager.resolveFile(remoteUrl, opts);
            for (FileObject remoteFile : remoteFolder.getChildren()) {
                log.debug("resolveFiles:" + remoteFile.getName());
                if (StringUtils.equals(remoteFile.getName().getBaseName(), "comdat") ) {
                    
                    //M:/psrdata/comdat/ods/dan_resurce/
                    String targetPath = fileStore.getFile(Locations.Persistent.ROOT, "comdat/ods/dan_resource")
                            .getAbsolutePath();
                    File localFolder = new File(targetPath);
                    if (!localFolder.exists()) {
                        log.info("downloaddest not exist. create it. path is " + targetPath);
                        localFolder.mkdirs();
                    }
                    
                    log.debug("  copy File:" + remoteFile.getName());
                    for (FileObject comdatChildren : remoteFile.getChildren()) {
                        FileObject localFile = manager.resolveFile(localFolder.getAbsolutePath() + File.separator + comdatChildren.getName().getBaseName());
                        localFile.copyFrom(comdatChildren, Selectors.SELECT_ALL);//連子目錄檔案都下載
                    }
                    
                }
                if (StringUtils.equals(remoteFile.getName().getBaseName(), "config")) {
                    
                    //M:/psrapp/config/ods/
                    String targetPath = fileStore.getFile(Locations.Local.ROOT, "config/ods")
                            .getAbsolutePath();
                    File localFolder = new File(targetPath);
                    if (!localFolder.exists()) {
                        log.info("downloaddest not exist. create it. path is " + targetPath);
                        localFolder.mkdirs();
                    }
                    
                    log.debug("  copy File:" + remoteFile.getName());
                    for (FileObject configChildren : remoteFile.getChildren()) {
                        FileObject localFile = manager.resolveFile(localFolder.getAbsolutePath() + File.separator + configChildren.getName().getBaseName());
                        localFile.copyFrom(configChildren, Selectors.SELECT_ALL);//連子目錄檔案都下載
                    }
                }
            }
        } catch (FileSystemException e) {
            log.error("runDownloadTableau sftp error!" + e.getMessage());
        } finally {
            manager.close();
        }
        log.debug("runDownloadTableau finish");
    }
    
    /**
     * 取得SFTP remote URI字串
     * 
     * @param pathPropertyName
     *            設定檔路徑property name
     * @param fileName
     *            檔名
     * @return sft uri
     */
    public String getRemoteUri(String pathPropertyName, String fileName) {
        String server = null;
        String username = null;
        String password = null;
        String path = null;
        String uriString = null;
        try {
            Properties props = getConfigProperties();
            server = props.getProperty("sever");
            username = props.getProperty("username");
            password = props.getProperty("password");
            path = props.getProperty(pathPropertyName);
            
            String userInfo = username + ":" + password;
            URI uri = new URI("sftp", userInfo, server, -1, path + File.separator + fileName, null, null);
            uriString = uri.toString();
        } catch (FileNotFoundException e1) {
            log.error(e1.getMessage());
            throw new RuntimeException(e1);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IOException e1) {
            log.error(e1.getMessage());
            throw new RuntimeException(e1);
        }
        return uriString;
    }

    /**
     * 確認是否有食品雲設定 且 檔名符合tofoodfile設定
     * 
     * @param fileName
     *            本次上傳檔案檔名
     * @return boolean 有食品雲設定 且 檔名符合tofoodfile設定則回傳true,否則為false
     */
    public boolean checkPropertyForFood(String fileName) {
        try {
            Properties props = getConfigProperties();
            log.debug("check food settings");
            if (null == props.getProperty(REMOTE_UPLOAD_FOOD_PATH_PROP_NAME)) {
                log.debug("no food settings");
                return false;
            }
            String toFoodFiles = props.getProperty(SEND_TO_FOOD_FILES);
            log.debug("get food file list" + toFoodFiles);
            if (StringUtils.isEmpty(toFoodFiles)) {
                log.debug("no food settings");
                return false;
            }
            for (String foodFile : toFoodFiles.split(",")) {
                log.debug("food file compare: " + fileName + " contains " + foodFile + " =>"+fileName.contains(foodFile));
                if (fileName.contains(foodFile)) {
                    return true;// 有食品雲設定 且 檔名符合tofoodfile設定
                }
            }
        } catch (FileNotFoundException e1) {
            log.error(e1.getMessage());
            throw new RuntimeException(e1);
        } catch (IOException e1) {
            log.error(e1.getMessage());
            throw new RuntimeException(e1);
        }
        return false;
    }

    /**
     * SFTP 初始化與設定options，回傳options
     * 
     * @param manager
     *            StandardFileSystemManager
     * @return FileSystemOptions
     * @throws FileSystemException
     * 
     */
    private FileSystemOptions sftpInit(StandardFileSystemManager manager)
            throws FileSystemException, org.apache.commons.vfs2.FileSystemException {
        log.debug("StandardFileSystemManager init");
        manager.init();
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, SFTP_TIMEOUT);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        return opts;
    }

    /**
     * 讀取 property 檔設定
     * 
     * @return Properties
     * @throws IOException
     * @throws FileNotFoundException
     */
    private Properties getConfigProperties() throws FileNotFoundException, IOException {
        String psrappPath = propertiesAccessor
                .getProperty(ApplicationProperties.ENVIRONMENT_PSRAPP);
        String propertiesPath = psrappPath + File.separator + "config" + File.separator + "ods"
                + File.separator + "sftppw.properties";

        Properties props = new Properties();
        props.load(new FileReader(propertiesPath));

        return props;
    }

    /**
     * 取得 danods path
     * 
     * @return
     */
    private String getDanOdsPath() {
        String publicPath = odsPropertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "danods")
                .getAbsolutePath();
        return targetPath;
    }

    /**
     * 取得 odsdan path
     * 
     * @return
     */
    private String getOdsDanPath() {
        String publicPath = odsPropertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH);
        String targetPath = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "odsdan")
                .getAbsolutePath();
        return targetPath;
    }
}
