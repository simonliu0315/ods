package gov.sls.ods.web.rest;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsLayout;
import gov.sls.entity.ods.OdsPackageDocument;
import gov.sls.entity.ods.OdsPackageExtra;
import gov.sls.entity.ods.OdsPackageLayout;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionExtra;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.ods.Messages;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods703eGridDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.dto.Ods703eTab2Dto;
import gov.sls.ods.dto.TemplateDynamicGeneratorDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods703eService;
import gov.sls.ods.service.Ods705eService;
import gov.sls.ods.web.dto.Ods703e02Dto;
import gov.sls.ods.web.dto.Ods703e03Dto;
import gov.sls.ods.web.dto.Ods703eDto;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.web.Alerter;
import com.cht.sac.uaa.m400.service.bean.UAA492Bean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.io.Files;

@Slf4j
@Controller
@RequestMapping("ODS703E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS703E')")
public class Ods703eResource {
    
    @Autowired
    private Ods703eService service;

    @Autowired
    private Ods303eService ods303eService;
    
    @Autowired
    private OdsPackageRepository repository;
    
    @Autowired
    private FileStore store;
    
    @Autowired
    private Ods705eService service705;
    
    @Autowired
    FileStore fileStore;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @RequestMapping(value = "/{packageId}/{versionId}/", method = {
            RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("versionId") int versionId)
            throws JsonParseException, JsonMappingException,
            UnsupportedEncodingException, IOException {
        log.debug("***************ODS703E getPage****************");
        log.debug("packageId:" + packageId);
        log.debug("versionId:" + versionId);
        ModelAndView mav = new ModelAndView();        
        TemplateDynamicGeneratorDto dto = ods303eService.getDynamicTemplateObject(packageId, versionId);
        mav.addObject("dynamicContentObjs", dto);
        mav.setViewName( "/ods/public/package/" +packageId+"/html/" +  packageId + "-" + versionId );
        return mav;        
    }
    
    private Ods703eDto find(String name, String description, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
//        NtaUser user = UserHolder.getUser();
//        log.trace("User \"{}\" performing query.", user);        

        //List<OdsPackage> odsPackage = service.findByName(name, description);
        List<Ods703eGridDto> packageAndVersionList = service.findPackageAndVersionByName(name, description);
        Ods703eDto dto = new Ods703eDto();
        //dto.setOdsPackageList(odsPackage);
        dto.setPackageAndVersionList(packageAndVersionList);
        if (packageAndVersionList.isEmpty()) {
            //alerter.info(Messages.warning_notFound());
            alerter.info(Messages.warning_notFound());
        } else {
            //alerter.success(Messages.success_find());
            alerter.success(Messages.success_find());
        }
        return dto;
    }

    /*
     * 若沒有傳key值，會mapping到此網址，查詢所有資料
     */
//    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody
//    List<OdsPackage> find(Alerter alerter) {
//        return find(Strings.emptyToNull(null), Strings.emptyToNull(null), alerter);
//    }

    /*
     * 若有傳key值，會mapping到此網址，查詢單一筆資料
     */
//    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody
//    OdsPackage get(@PathVariable String name, @PathVariable String description, Alerter alerter) {
//        List<OdsPackage> odsPackage = find(name, description, alerter);
//        if (odsPackage.isEmpty()) {
//            return null;
//        }
//        return odsPackage.get(0);
//    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        String status = service.create(ods703eDto.getOdsPackage(), ods703eDto.getOdsPackageTagList(), 
                ods703eDto.getPackageMetatemplateDto(), ods703eDto.getOdsPackageExtraList());
        if ("".equals(status)) {
            //alerter.success(Messages.success_create());
            alerter.success(Messages.success_create());
        } else {
            alerter.fatal("主題名稱不可重覆!");
        }
        

    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void save(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        service.save(ods703eDto.getOdsPackage(), ods703eDto.getOdsPackageTagList(), 
                ods703eDto.getPackageMetatemplateDto(), ods703eDto.getOdsPackageExtraList());
        //alerter.success(Messages.success_update());
        alerter.success(Messages.success_update());
    }

//    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
//    public @ResponseBody
//    void delete(@PathVariable String id, Alerter alerter) {
//        service.delete(id);
//        alerter.success(Messages.success_delete());
//    }
    
    @RequestMapping(value = "/uaaCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto uaaCheck(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        List<UAA492Bean> uaa492BeanList = service.uaaCheck(ods703eDto.getOdsPackage());
        String isUaaCheck = "true";
        if (uaa492BeanList.isEmpty()) {
            isUaaCheck = "false";
        }
        Ods703eDto dto = new Ods703eDto();
        dto.setIsUaaCheck(isUaaCheck);
        return dto;
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void delete(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        service.delete(ods703eDto.getOdsPackage());
        //alerter.success(Messages.success_delete());
        alerter.success(Messages.success_delete());
    }

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto find(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {        
        return find(ods703eDto.getOdsPackage().getName(), ods703eDto.getOdsPackage().getDescription(), alerter);
    }
    
    @RequestMapping(value = "/find/tags", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto findTags(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        List<OdsPackageTag> odsPackageTagList = service.findPackageTagsByPackageId(ods703eDto.getOdsPackage().getId());
        Ods703eDto dto = new Ods703eDto();
        dto.setOdsPackageTagList(odsPackageTagList);;
        return dto;
    }
    
    @RequestMapping(value = "/find/metadata", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto findMetadata(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        List<Ods703eTab2Dto> pmDto = service.findMetadataByPackageId(ods703eDto.getOdsPackage().getId());
        List<OdsPackageExtra> packageExtraList = service.findExtraMetadataByPackageId(ods703eDto.getOdsPackage().getId());
        Ods703eDto dto = new Ods703eDto();
        dto.setPackageMetatemplateDto(pmDto);
        dto.setOdsPackageExtraList(packageExtraList);
        return dto;
    }
    
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    void fileUpload(@RequestParam("file") MultipartFile multipartFile,
            String packageId, Alerter alerter) throws IOException {
        log.debug("uploadFile" + packageId);
        String imgOutPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)+ 
                "package" + File.separator + packageId + File.separator + "image" + File.separator + packageId + ".png";

        File file = fileStore.getFile(Locations.Persistent.ROOT, imgOutPath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        Files.write(multipartFile.getBytes(), file);

        //更新package imgageUrl
        service.updateImageUrl(packageId);
        //alerter.alert(StatusCodes.ODSWEB007S());

        //return multipartFile.getOriginalFilename();
        //return packageId + ".png";
    }
    
    @RequestMapping(value = "/delete/document", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto deleteDocument(@RequestBody Map<String,String>params, Alerter alerter) {
        String documentId=params.get("documentId");
        String packageId=params.get("packageId");
        String docPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)+ 
                "package" + File.separator + "document" + File.separator + packageId + File.separator + service.getPackageDocumentById(documentId).getId();
        File documentFile=fileStore.getFile(Locations.Persistent.ROOT, docPath);
        if ( documentFile.exists() ) {
            documentFile.delete();
        }
        service.deletePackageDocumentById(documentId);
        List<OdsPackageDocument> odsPackageDocumentList = service.findPackageDocumentByPackageId(packageId);
        Ods703eDto dto = new Ods703eDto();
        dto.setOdsPackageDocumentList(odsPackageDocumentList);
        return dto;
    }
    
    @RequestMapping(value = "/find/documents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703eDto findDocuments(@RequestBody Ods703eDto ods703eDto, Alerter alerter) {
        List<OdsPackageDocument> odsPackageDocumentList = service.findPackageDocumentByPackageId(ods703eDto.getOdsPackage().getId());
        Ods703eDto dto = new Ods703eDto();
        dto.setOdsPackageDocumentList(odsPackageDocumentList);
        return dto;
    }
    
    @RequestMapping(value = "/docUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    void docUpload(@RequestParam("file") MultipartFile multipartFile,
            String packageId, String description, Alerter alerter) throws IOException {
        log.debug("uploadFile" + packageId);
        String filename=multipartFile.getOriginalFilename();
        // 更新主題文件檔案 table
        OdsPackageDocument odsPackageDocument=service.createPackageDocument(packageId, filename, description);
        // 寫入主題文件檔案
        String docOutPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)+ 
                "package" + File.separator + "document" + File.separator + packageId + File.separator + odsPackageDocument.getId();

        log.debug("docOutPath="+docOutPath);
        File file = fileStore.getFile(Locations.Persistent.ROOT, docOutPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        Files.write(multipartFile.getBytes(), file);
    }
    
    //===============PackageVer Zone==============================
    
    @RequestMapping(value = "/find/ver", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e02Dto findVer(@RequestBody Ods703e02Dto ods703e02Dto, Alerter alerter) {
        log.debug("id:"+ods703e02Dto.getPackageId());
        List<OdsPackageVersion> odsPackageVersionList = service.findPackageVersionByPackageId(ods703e02Dto.getPackageId());
        Ods703e02Dto dto = new Ods703e02Dto();
        log.debug("List:"+odsPackageVersionList.size());
        dto.setOdsPackageVersionList(odsPackageVersionList);
        if (odsPackageVersionList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }
    
    @RequestMapping(value = "/find/verAll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e02Dto findVerAll(@RequestBody Ods703e02Dto ods703e02Dto, Alerter alerter) {
        log.debug("id:"+ods703e02Dto.getPackageId());
        List<OdsPackageVersion> odsPackageVersionList = service.findPackageVersionAllByPackageId(ods703e02Dto.getPackageId());
        Ods703e02Dto dto = new Ods703e02Dto();
        log.debug("List:"+odsPackageVersionList.size());
        dto.setOdsPackageVersionList(odsPackageVersionList);
        if (odsPackageVersionList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }
    
    @RequestMapping(value = "/createVer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void createVer(@RequestBody Ods703e02Dto ods703e02Dto, Alerter alerter) {
        OdsPackageVersion odsPackageVersion = ods703e02Dto.getOdsPackageVersion();
        odsPackageVersion.setId(ods703e02Dto.getOdsPackageVersionPK());
        service.createVer(ods703e02Dto.getOdsPackageVersion(), ods703e02Dto.getOds703eTab2DialogDtoList(), ods703e02Dto.getOdsPackageLayoutList(), 
                ods703e02Dto.getPackageMetatemplateDto(), ods703e02Dto.getOdsPackageVersionExtraList());
        alerter.success(Messages.success_create());
    }
    
    @RequestMapping(value = "/saveVer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void saveVer(@RequestBody Ods703e02Dto ods703e02Dto, Alerter alerter) {
        service.saveVer(ods703e02Dto.getOdsPackageVersion(), ods703e02Dto.getOds703eTab2DialogDtoList(), ods703e02Dto.getOdsPackageLayoutList(), 
                ods703e02Dto.getPackageMetatemplateDto(), ods703e02Dto.getOdsPackageVersionExtraList());
//        if (!Strings.isNullOrEmpty(ods703e02Dto.getOdsPackageVersion().getPattern())) {
//            ods303eService.generateTemplate(ods703e02Dto.getOdsPackageVersion().getId().getPackageId(), ods703e02Dto.getOdsPackageVersion().getId().getVer());
//        }
        alerter.success(Messages.success_update());
    }
    
    @RequestMapping(value = "/find/res", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e03Dto findRes(@RequestBody Ods703e03Dto ods703e03Dto, Alerter alerter) {
        log.debug("resName:"+ods703e03Dto.getResName());
        log.debug("catId:"+ods703e03Dto.getCatId());
        List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList = service.findResByNameAndCategory(ods703e03Dto.getResName(), ods703e03Dto.getCatId());
        Ods703e03Dto dto = new Ods703e03Dto();
        log.debug("List:"+ods703eTab2DialogDtoList.size());
        dto.setOds703eTab2DialogDtoList(ods703eTab2DialogDtoList);
        if (ods703eTab2DialogDtoList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }
    
    @RequestMapping(value = "/find/cat", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findCat(Alerter alerter) {
        List<OdsCategory> odsCategoryList = service.findCategory();
        log.debug("ListCategory:"+odsCategoryList.size());
        return odsCategoryList;
    }

    @RequestMapping(value = "/find/resVer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e03Dto findResVer(@RequestBody Ods703e03Dto ods703e03Dto, Alerter alerter) {
        log.debug("resId:"+ods703e03Dto.getResourceId());
        List<OdsResourceVersion> odsResourceVersionList = service.findResourceVer(ods703e03Dto.getResourceId());
        Ods703e03Dto dto = new Ods703e03Dto();
        log.debug("List:"+odsResourceVersionList.size());
        dto.setOdsResourceVersionList(odsResourceVersionList);
        return dto;
    }
    
    @RequestMapping(value = "/find/packRes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e03Dto findPackRes(@RequestBody Ods703e03Dto ods703e03Dto, Alerter alerter) {
        log.debug("packId:"+ods703e03Dto.getPackageId());
        log.debug("packVer:"+ods703e03Dto.getPackageVer());
        List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList = service.findPackResByIdAndVer(ods703e03Dto.getPackageId(), ods703e03Dto.getPackageVer());
        Ods703e03Dto dto = new Ods703e03Dto();
        log.debug("List:"+ods703eTab2DialogDtoList.size());
        dto.setOds703eTab2DialogDtoList(ods703eTab2DialogDtoList);
        return dto;
    }
    
    @RequestMapping(value = "/find/layout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsLayout> findLayout(@RequestBody OdsLayout odsLayout, Alerter alerter) {
        List<OdsLayout> odsLayoutList = service705.findByName(odsLayout.getName());
        log.debug("OdsLayout:"+odsLayoutList.size());
        return odsLayoutList;
    }
    
    @RequestMapping(value = "/find/packLayout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsPackageLayout> findPackLayout(@RequestBody Ods703e03Dto ods703e03Dto, Alerter alerter) {
        List<OdsPackageLayout> odsPackageLayoutList = service.findPackageLayout(ods703e03Dto.getPackageId(), ods703e03Dto.getPackageVer());
        log.debug("OdsPackageLayoutList:"+odsPackageLayoutList.size());
        return odsPackageLayoutList;
    }
    
    @RequestMapping(value = "/find/commonRes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e03Dto findCommonRes(Alerter alerter) {
        List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList = service.findCommonRes();
        Ods703e03Dto dto = new Ods703e03Dto();
        log.debug("List:"+ods703eTab2DialogDtoList.size());
        dto.setOds703eTab2DialogDtoList(ods703eTab2DialogDtoList);
        if (ods703eTab2DialogDtoList.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return dto;
    }
    
    @RequestMapping(value = "/find/versionMetadata", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods703e02Dto findVersionMetadata(@RequestBody Ods703e03Dto ods703e03Dto, Alerter alerter) {
        List<Ods703eTab2Dto> pmDto = service.findPackageVersionMetadata(ods703e03Dto.getPackageId(), ods703e03Dto.getPackageVer());
        List<OdsPackageVersionExtra> packageVersionExtraList = service.findPackageVersionExtra(ods703e03Dto.getPackageId(), ods703e03Dto.getPackageVer());
        Ods703e02Dto dto = new Ods703e02Dto();
        dto.setPackageMetatemplateDto(pmDto);
        dto.setOdsPackageVersionExtraList(packageVersionExtraList);
        return dto;
    }
}
