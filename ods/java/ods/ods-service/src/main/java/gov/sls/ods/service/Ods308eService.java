package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.entity.ods.OdsUserResourceVersionDownload;
import gov.sls.ods.dto.Ods303eAnalysisDto;
import gov.sls.ods.dto.Ods308ePreviewFieldsDto;
import gov.sls.ods.dto.Ods308ePreviewResultDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.repository.OdsDataStroeRepository;
import gov.sls.ods.repository.OdsGroupRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;
import gov.sls.ods.repository.OdsUserResourceVersionDownloadRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class Ods308eService {

    @Autowired
    private OdsResourceVersionRepository resourceVersionRepos;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private OdsDataStroeRepository odsDataStroeRepository;

    @Autowired
    private OdsPackageVersionRepository packageVersionRepository;

    @Autowired
    private OdsResourceRepository resourceRepos;
    
    @Autowired
    private OdsPackageResourceRepository packageResourceRepos;

    @Autowired
    private OdsPackageRepository packageRepos;

    @Autowired
    private OdsGroupRepository groupRepos;
    
    @Autowired
    FileStore fileStore;
    
    @Inject
    private ApplicationContext applicationContext;
    
    @Autowired
    private OdsUserResourceVersionDownloadRepository odsUserResourceVersionDownloadRepository;

    public OdsResourceVersion findResourceVer(OdsResourceVersion odsResourceVersion) {
        return resourceVersionRepos.findOne(odsResourceVersion.getId());
    }

    private final static String[] columnNumericType = { "int", "float", "numeric" };
    private final static String[] columnStringType = { "varchar", "nvarchar" };

    public Ods308ePreviewResultDto getPreviewData(String resourceId, int resourceVer,
            Map<String, Object> filters, int offset, int limit, String sort, String q) {
        Ods308ePreviewResultDto result = new Ods308ePreviewResultDto();
        Map<String, String> tableColume = new HashMap<String, String>();
        List<Map<String, Object>> metaDataList = odsDataStroeRepository
                .findDataStoreMetaData(resourceId);
        // log.debug("****----" + metaDataList.size());
        for (Map<String, Object> metaMap : metaDataList) {
            tableColume.put(metaMap.get("COLUMN_NAME").toString(), metaMap.get("DATA_TYPE")
                    .toString());
        }
        List<Map<String, Object>> allList = odsDataStroeRepository.findOneDataStoreAllData(resourceId,
                resourceVer);
        Set<String> columnSet = null;
        if (allList.size() > 0) {
            columnSet = allList.get(0).keySet();
        }
        int total = odsDataStroeRepository.countDataStoreData(resourceId,
                resourceVer, filters, sort, q, columnSet);
        List<Map<String, Object>> grid = odsDataStroeRepository.findDataStoreDataPaging(resourceId,
                resourceVer, filters, offset, limit, sort, q, columnSet);
        log.debug("allData size:" + total);
        log.debug("grid size:" + grid.size());
        List<Ods308ePreviewFieldsDto> fieldsList = new ArrayList<Ods308ePreviewFieldsDto>();
        if (grid.size() > 0) {
            for (String field : grid.get(0).keySet()) {
                Ods308ePreviewFieldsDto fields = new Ods308ePreviewFieldsDto();
                if (field.equals("ODS_RESOURCE_VER") || field.equals("ODS_RESOURCE_ROW")) {
                    continue;
                }
                fields.setId(field);
                if (ArrayUtils.contains(columnNumericType, tableColume.get(field))) {
                    fields.setType("numeric");
                } else {
                    fields.setType("text");
                }
                fieldsList.add(fields);
            }
            
            //Format number
//            for (Map<String, Object> gridDatas : grid) {
//                for (String key : gridDatas.keySet()) {
//                    if (isNumeric(String.valueOf(gridDatas.get(key)))) {
//                        log.info("oriValue:" + String.valueOf(gridDatas.get(key)));
//                        //DecimalFormat df = new DecimalFormat("###.######");                        
//                        //String newValue = df.format(String.valueOf(gridDatas.get(key)));
//                        String newValue = String.valueOf(gridDatas.get(key)).replaceFirst("\\.0*$|(\\.\\d*?)0+$", "$1");
//                        log.info("newValue:" + newValue);
//                        gridDatas.put(key, new BigDecimal(newValue));
//                    }
//                }
//            }
        }

        result.setFields(fieldsList);
        result.setRecords(grid);
        result.setTotal(total);
        return result;
    }
    
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    
    @Cacheable(value={"odsResourceCache"})
    public byte[] getResourceByteArray(String filePath, String ext) throws IOException{
        File f = fileStore.getFile(Locations.Persistent.ROOT, filePath);     
  
        if (!f.exists()) {
            return new ByteArrayOutputStream().toByteArray();
        }
        InputStream in = null;
        OutputStream os = null;
        ByteArrayOutputStream bos = null;
        try {
            if("csv".equals(ext)){
                in = applicationContext.getResource(f.toURI().toString()).getInputStream();
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

//                byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
//                baos.write(UTF8_BOM);
//                baos.flush();
                
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > -1 ) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                InputStream streamToDatastore = new ByteArrayInputStream(baos.toByteArray());  
                
                return IOUtils.toByteArray(streamToDatastore);
            } else {
                in = applicationContext.getResource(f.toURI().toString()).getInputStream();
                return IOUtils.toByteArray(in);
            }
        } finally {
            if (in != null) {
              safeClose(in);
            }
            if (os != null) {
                os.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
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

    public List<OdsPackageVersion> findPublishPackageVerByResourceVer(String resourceId,
            int resourceVer) {
        return packageVersionRepository.getPublishPackageVerByResourceVer(resourceId, resourceVer);
    }
    
    public List<Ods303eAnalysisDto> findPackageByWorkbook(String workbookId, String workbookVer) {
        return packageResourceRepos.findPackageByWorkbook(workbookId, workbookVer);
    }

    public OdsResource findResource(String resourceId) {
        return resourceRepos.findOne(resourceId);
    }
    
    /**
     * <p>max
     * </p>
     * @param packageId packageId
     * @param resourceId resourceId
     * @return Integer
     */
    public Integer queryMaxVer (String packageId, String resourceId)
    {
        List<OdsPackageResource> maxVerList = resourceVersionRepos.findResourceVerLast(packageId, resourceId);
        if (CollectionUtils.isEmpty(maxVerList)) {
            return null;
        } else {
            return maxVerList.get(0).getId().getResourceVer();
        }
            
    }//Modification By Ropin13

    public List<PackageAndResourceDto> findPackageAndResourceAndLayout(String packageId,
            int packageVer) {
        return packageVersionRepository.findPackageAndResourceAndLayout(packageId, packageVer);
    }

    public List<OdsPackage> findPublishPackage(String packageId) {
        return packageRepos.getPublishPackage(packageId);
    }

    public List<OdsGroup> findGroupInPublishPackage(String groupId) {
        return groupRepos.getGroupInPublishPackage(groupId);
    }
    
    public int createDownload(OdsUserResourceVersionDownload odsUserResourceVersionDownload) {
        OdsUserResourceVersionDownload OdsUserResourceVersionDownloadRet = null;
        OdsUserResourceVersionDownloadRet = odsUserResourceVersionDownloadRepository.create(odsUserResourceVersionDownload);
        if (OdsUserResourceVersionDownloadRet == null) {
            return 0;
        } else {
            return 1;
        }
        
    }
}
