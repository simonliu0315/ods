package gov.sls.ods.common.web.rest;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.entity.ods.OdsResourceVersionPK;
import gov.sls.entity.ods.OdsUserPackageVersionDownload;
import gov.sls.entity.ods.OdsUserResourceVersionDownload;
import gov.sls.ods.common.web.dto.Ods308ePreloadDto;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods303eAnalysisDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods308eService;
import gov.sls.ods.service.UaaAuthoriy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.security.Authority;

@Slf4j
@Controller
@RequestMapping("ODS308E")
// @PreAuthorize("hasAuthority('AUTHORITY_ODS308E')")
public class Ods308eResource {

    @Autowired
    private Ods308eService ods308eService;
    
    @Autowired
    private Ods303eService ods303eService;

    @Inject
    private Environment environment;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    FileStore fileStore;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private Ods307eService ods307eService;
    
    @RequestMapping(value = "/{packageId}/{packageVerStr}/{resourceId}/{resourceVerStr}/", method = RequestMethod.GET)
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("resourceId") String resourceId,
            @PathVariable("resourceVerStr") String resourceVerStr,
            HttpServletRequest request,
            @RequestParam(value = "preview", required = false) String preview) {
        try {
            int packageVer = Integer.parseInt(packageVerStr);
            int resourceVer = Integer.parseInt(resourceVerStr);
            return getPage(packageId, packageVerStr, resourceId, resourceVerStr, "0", "0", request, preview);
        } catch (Exception e) {
            log.error("error in getPage:{}", e);
            return new ModelAndView("404");
        }
    }

    @RequestMapping(value = "/{packageId}/{packageVerStr}/{resourceId}/{resourceVerStr}/{rowPosition}/{columnPosition}/", method = RequestMethod.GET) 
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("resourceId") String resourceId,
            @PathVariable("resourceVerStr") String resourceVerStr,
            @PathVariable("rowPosition") String rowPosition,
            @PathVariable("columnPosition") String columnPosition,
            HttpServletRequest request,
            @RequestParam(value = "preview", required = false) String preview) {
        try {
            //UAA權限控管
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(UserHolder.getUser().getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                if (packageId.equals(authority.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return new ModelAndView("404");
            }
            
            int packageVer = Integer.parseInt(packageVerStr);
            int resourceVer = Integer.parseInt(resourceVerStr);
            log.debug("get page model and view");
            OdsResourceVersion odsResourceVersion = new OdsResourceVersion();
            OdsResourceVersionPK odsResourceVersionPK = new OdsResourceVersionPK();
            odsResourceVersionPK.setResourceId(resourceId);
            odsResourceVersionPK.setVer(resourceVer);
            odsResourceVersion.setId(odsResourceVersionPK);
            OdsResourceVersion odsResourceVersionRet = ods308eService
                    .findResourceVer(odsResourceVersion);
            if (odsResourceVersionRet == null) {
                return new ModelAndView("404");
            }
            OdsResource odsResource = ods308eService.findResource(resourceId);
            if (odsResource == null) {
                return new ModelAndView("404");
            }
            ModelAndView mav = new ModelAndView();
            if ("pdf".equals(odsResource.getFormat().toLowerCase()) && preview == null) {
                mav = new ModelAndView("ods308e/ods308ePdf");
            } else if ("image".equals(odsResource.getFormat().toLowerCase())) {
                mav = new ModelAndView("ods308e/ods308eImage");
            } else if ("dataset".equals(odsResource.getFormat().toLowerCase())) {
                mav = new ModelAndView("ods308e/ods308e");
            } else if ("pdf".equals(odsResource.getFormat().toLowerCase()) && "Y".equals(preview)) {
                mav = new ModelAndView("ods308e/ods308ePdfIframe");
            } else if ("common".equals(odsResource.getFormat().toLowerCase())) {
                mav = new ModelAndView("ods308e/ods308eCommon");
            } else {
                mav = new ModelAndView("ods308e/ods308eCommon");
            }
            List<PackageAndResourceDto> parDtoList = ods308eService.findPackageAndResourceAndLayout(
                    packageId, packageVer);
            Ods308ePreloadDto preloadDto = new Ods308ePreloadDto();
            preloadDto.setId(odsResourceVersionRet.getId().getResourceId());
            preloadDto.setVer(odsResourceVersionRet.getId().getVer());
            preloadDto.setFormat(odsResource.getFormat().toLowerCase());
            preloadDto.setPackageId(packageId);
            preloadDto.setPackageVer(packageVer);
            preloadDto.setDatastore_active(true);
            preloadDto.setDescription(odsResourceVersionRet.getDescription().replace("\n", "<br>"));
//            odsResourceVersionRet.getDescription();
            preloadDto.setName(odsResourceVersionRet.getName());
            preloadDto.setParDtoList(parDtoList);
            preloadDto.setRowPosition(rowPosition);
            preloadDto.setColumnPosition(columnPosition);
            
            //判斷 develop or production
            if (propertiesAccessor.isInProductionProfile()) {
                preloadDto.setSiteUrl(request.getContextPath() + "/");
            } else {
                preloadDto.setSiteUrl(request.getServerPort() + request.getContextPath() + "/");
            }
            if ("dataset".equals(odsResource.getFormat().toLowerCase())) {
                preloadDto.setUrl(preloadDto.getSiteUrl() + "ODS308E/public/resource/"
                        + preloadDto.getId() + "/dataset/" + preloadDto.getId() + "-"
                        + preloadDto.getVer() + ".csv");
            } else if ("pdf".equals(odsResource.getFormat().toLowerCase())) {
                preloadDto.setUrl(preloadDto.getSiteUrl() + "ODS308E/public/resource/"
                        + preloadDto.getId() + "/pdf/" + preloadDto.getId() + "-" + preloadDto.getVer()
                        + ".pdf");
            } else if ("image".equals(odsResource.getFormat().toLowerCase())) {
                preloadDto.setUrl(preloadDto.getSiteUrl() + "ODS308E/public/resource/"
                        + preloadDto.getId() + "/image/" + preloadDto.getId() + "-"
                        + preloadDto.getVer() + ".png");
            } else {
                preloadDto.setUrl(preloadDto.getSiteUrl() + "ODS308E/public/resource/"
                        + preloadDto.getId() + "/dataset/" + preloadDto.getId() + "-"
                        + preloadDto.getVer());
            }

            mav.addObject("PreloadDto", preloadDto);
            mav.addObject("systemId", environment.getProperty("systemId"));

            return mav;
        } catch (Exception e) {
            log.error("error in getPage:{}", e);
            return new ModelAndView("404");
        }
    }

    //@RequestMapping(method = RequestMethod.GET, value = "/download/{id}/{verStr}/")
    @RequestMapping(method = RequestMethod.GET, value = "/download/{packageId}/{packageVerStr}/{id}/{verStr}/")
    public ResponseEntity<byte[]> displayUploadedFile(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @PathVariable("id") String id,
            @PathVariable("verStr") String verStr,
            @RequestParam(value = "fileType", required = false) String fileType,
            HttpServletRequest request) throws IOException {
        try {            
            int ver = Integer.parseInt(verStr);
            

            //Modification by Ropin13
            //增加版本0時 取 最新版本maxVer
            if (0 == ver) {
                Integer maxVer = ods308eService.queryMaxVer(packageId, id);
                if (maxVer != null) {
                    ver = maxVer;
                }
            }
            
            List<OdsPackageVersion> opvList = ods308eService.findPublishPackageVerByResourceVer(
                    id, ver);
            
            boolean isPublished = ods303eService.isPackageOrResourcePublished(id, ver, "", 0);
            String publicPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
            
            //String disposition = id + ver + fileType;
            String fileName = id + "-" + ver;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileName += "." + fileType;
            headers.add("Content-Disposition", "attachment;filename=" + fileName);

            if (opvList.size() == 0 && !isPublished) {
                log.info("not published");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            //UAA權限控管
            SlsUser user = UserHolder.getUser();
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                for (OdsPackageVersion opv : opvList) {
                    if (opv.getPackageId().equals(authority.getId())) {
                        found = true;
                        // 變更對應resource ver 的packageId version
                        packageVer = Integer.toString(opv.getId().getVer());
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            File f = null;
            log.info("FileType:" + fileType);
            if ("png".equals(fileType)) {
                f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + id + "/image/" + id + "-"
                      + ver + "." + fileType);
            } else if ("csv".equals(fileType)) {
                f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + id + "/dataset/" + id + "-"
                      + ver + "." + fileType);
            } else if ("pdf".equals(fileType)) {
                f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + id + "/pdf/" + id + "-"
                      + ver + "." + fileType);
            }
            //log.debug(f.getCanonicalPath());
            if (f == null || (f != null && !f.exists())) {
                log.info("file do not exist");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            } else {
                log.info("file exists and path of file:{}", f.getCanonicalPath());
            }
            
            InputStream in = null;
            try {
                if (f != null) {
                    //寫入ODS_USER_RESOURCE_VERSION_DOWNLOAD
                    OdsUserResourceVersionDownload odsUserResourceVersionDownload = new OdsUserResourceVersionDownload();
                    odsUserResourceVersionDownload.setUserId(user.getId());
                    odsUserResourceVersionDownload.setPackageId(packageId);
                    odsUserResourceVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                    odsUserResourceVersionDownload.setResourceId(id);
                    odsUserResourceVersionDownload.setResourceVer(Integer.parseInt(verStr));
                    odsUserResourceVersionDownload.setIpAddress(request.getRemoteAddr());
                    odsUserResourceVersionDownload.setCreated(new Date());
                    odsUserResourceVersionDownload.setCreateUserId(user.getId());
                    if ("png".equals(fileType)) {
                        odsUserResourceVersionDownload.setFormat("image");
                    } else if ("csv".equals(fileType)) {
                        odsUserResourceVersionDownload.setFormat("dataset");
                    } else if ("pdf".equals(fileType)) {
                        odsUserResourceVersionDownload.setFormat("pdf");
                    }                    
                    
                    if (!user.getRoles().isEmpty()) {
                        odsUserResourceVersionDownload.setUserRole(String.valueOf(user
                                .getRoles().get(0)));
                    }
                    ods308eService.createDownload(odsUserResourceVersionDownload);
                    
                    in = applicationContext.getResource(f.toURI().toString()).getInputStream();
                    return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
                } 
                
                return null;
            } finally {
                if (in != null) {
                  safeClose(in);
                }
            }
            
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
        
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/download/{packageId}/{packageVerStr}/twbx/")
    public ResponseEntity<byte[]> displayTwbxFile(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @RequestParam(value = "fileType", required = false) String fileType, 
            @RequestParam(value = "workbookId", required = false) String workbookId,
            @RequestParam(value = "workbookVer", required = false) String workbookVer,
            HttpServletRequest request) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            
            List<Ods303eAnalysisDto> opvList = ods308eService.findPackageByWorkbook(workbookId, workbookVer);
            log.info("opvList size:" + opvList.size());
            if (opvList.size() == 0) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            int i = 0;
            for (Ods303eAnalysisDto opv : opvList) {
                log.info("opvList getPackageId " + i + ":" + opv.getPackageId());
                log.info("opvList getPackageVer " + i + ":" + opv.getPackageVer());
                log.info("opvList getResourceId " + i + ":" + opv.getResourceId());
                log.info("opvList getResourceVer " + i++ + ":" + opv.getResourceVer());
            }
            boolean isPublished = ods303eService.isPackageOrResourcePublished(
                    opvList.get(0).getPackageId(), Integer.parseInt(opvList.get(0).getPackageVer()), 
                    opvList.get(0).getResourceId(), Integer.parseInt(opvList.get(0).getResourceVer()));
            log.info("isPublish:" + isPublished);
            String publicPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
            
            //String disposition = id + ver + fileType;
            String fileName = opvList.get(0).getResourceId() + "-" + opvList.get(0).getResourceVer();
            
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileName += "." + fileType;
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
            
            if (!isPublished) {
                log.info("not published");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            //UAA權限控管
            SlsUser user = UserHolder.getUser();
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                for (Ods303eAnalysisDto opv : opvList) {
                    if (opv.getPackageId().equals(authority.getId())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }

            File f = null;
            log.info("FileType:" + fileType);
            if ("twbx".equals(fileType)) {
                log.info("twbx path:{}", publicPath + "workbook/" + workbookId + "/twb/" + workbookId + "-"
                      + workbookVer + "." + fileType);
                f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "workbook/" + workbookId + "/twb/" + workbookId + "-"
                      + workbookVer + "." + fileType);
            }
            //log.debug(f.getCanonicalPath());
            if (f == null || (f != null && !f.exists())) {
                log.info("file do not exist");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            } else {
                log.info("file exists and path of file:{}", f.getCanonicalPath());
            }
            
            InputStream in = null;
            try {
                if (f != null) {
                    //寫入ODS_USER_PACKAGE_VERSION_DOWNLOAD
                    OdsUserPackageVersionDownload odsUserPackageVersionDownload = new OdsUserPackageVersionDownload();
                    odsUserPackageVersionDownload.setUserId(user.getId());
                    odsUserPackageVersionDownload.setPackageId(packageId);
                    odsUserPackageVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                    odsUserPackageVersionDownload.setIpAddress(request.getRemoteAddr());
                    odsUserPackageVersionDownload.setCreated(new Date());
                    odsUserPackageVersionDownload.setCreateUserId(user.getId());
                    odsUserPackageVersionDownload.setFormat("twbx");
                    odsUserPackageVersionDownload.setDownloadTarget(workbookId);
                    
                    if (!user.getRoles().isEmpty()) {
                        odsUserPackageVersionDownload.setUserRole(String.valueOf(user
                                .getRoles().get(0)));
                    }
                    ods307eService.createDownload(odsUserPackageVersionDownload, user);
                    
                    in = applicationContext.getResource(f.toURI().toString()).getInputStream();
                    return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
                } 
                
                return null;
            } finally {
                if (in != null) {
                  safeClose(in);
                }
            }
            
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
        
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/download/{packageId}/{packageVerStr}/zip/")
    public ResponseEntity<byte[]> displayZipFile(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @RequestParam(value = "fileType", required = false) String fileType,
            HttpServletRequest request) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            
            //UAA權限控管
            SlsUser user = UserHolder.getUser();
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                if (packageId.equals(authority.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            String publicPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
            //String disposition = id + ver + fileType;
            String fileName = packageId + "-" + packageVer;
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileName += "." + fileType;
            headers.add("Content-Disposition", "attachment;filename=" + fileName);

            File f = null;
            log.info("FileType:" + fileType);
            if ("zip".equals(fileType)) {
                log.info("zip path:{}", publicPath + "package/" + packageId + "/zip/" + packageId + "-" + packageVer + ".zip");
                f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "package/" + packageId + "/zip/" + packageId + "-" + packageVer + ".zip");
            }
            //log.debug(f.getCanonicalPath());
            if (f == null || (f != null && !f.exists())) {
                log.info("file do not exist");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            } else {
                log.info("file exists and path of file:{}", f.getCanonicalPath());
            }
            
            InputStream in = null;
            try {
                if (f != null) {
                    //寫入ODS_USER_PACKAGE_VERSION_DOWNLOAD
                    OdsUserPackageVersionDownload odsUserPackageVersionDownload = new OdsUserPackageVersionDownload();
                    odsUserPackageVersionDownload.setUserId(user.getId());
                    odsUserPackageVersionDownload.setPackageId(packageId);
                    odsUserPackageVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                    odsUserPackageVersionDownload.setIpAddress(request.getRemoteAddr());
                    odsUserPackageVersionDownload.setCreated(new Date());
                    odsUserPackageVersionDownload.setCreateUserId(user.getId());
                    odsUserPackageVersionDownload.setFormat("zip");
                    
                    if (!user.getRoles().isEmpty()) {
                        odsUserPackageVersionDownload.setUserRole(String.valueOf(user
                                .getRoles().get(0)));
                    }
                    ods307eService.createDownload(odsUserPackageVersionDownload, user);
                    
                    in = applicationContext.getResource(f.toURI().toString()).getInputStream();
                    return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
                } 
                
                return null;
            } finally {
                if (in != null) {
                  safeClose(in);
                }
            }
            
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }        
    }

    @ResponseBody
    @RequestMapping(value = "/public/{filterType}/{id}/{format}/{url}.{ext}", method = RequestMethod.GET, produces = {
            MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE,
            "text/csv", "application/pdf" })
    public byte[] getPublicImage(@PathVariable("filterType") String filterType,
            @PathVariable("id") String id, @PathVariable("format") String format,
            @PathVariable("url") String url, @PathVariable("ext") String ext) throws IOException {    
        try {
            String publicPath = propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
            List cntList = null;
            if ("group".equals(filterType)) {
                cntList = ods308eService.findGroupInPublishPackage(id);
            } else if ("package".equals(filterType)) {
                cntList = ods308eService.findPublishPackage(id);
            } else if ("resource".equals(filterType)) {
                if (url.split("-").length < 5 || !StringUtils.isNumeric(url.split("-")[5])) {
                    log.debug("image resourceIdVer not match spec." + url.split("-").length + ", "
                            + url.split("-")[5]);
                    return new ByteArrayOutputStream().toByteArray();
                }
                String resourceVer = url.split("-")[5];
                cntList = ods308eService.findPublishPackageVerByResourceVer(id,
                        Integer.parseInt(resourceVer));
            } else {
                cntList = new ArrayList();
            }
            if (cntList.size() == 0
                    && "oda-main".equals(environment.getProperty("systemId").toLowerCase())) {
                return new ByteArrayOutputStream().toByteArray();
            }
            
            String filePath = publicPath + "/" + filterType + "/" + id + "/" + format + "/" + url + "."
                    + ext;
            return ods308eService.getResourceByteArray(filePath, ext);
        } catch (Exception e) {
            log.error("error in getPublicImage:{}", e);
            return null;
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
    
}
