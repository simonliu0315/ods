package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.entity.ods.OdsGroupPackagePK;
import gov.sls.entity.ods.OdsIdentity;
import gov.sls.entity.ods.OdsIdentityGroup;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods704eChosePkgFormBean;
import gov.sls.ods.dto.Ods704eFormBean;
import gov.sls.ods.dto.Ods704eGrid1Dto;
import gov.sls.ods.service.Ods704eService;
import gov.sls.ods.web.dto.Ods701eDto;
import gov.sls.ods.web.dto.Ods704eDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS704E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS704E')")
public class Ods704eResource {

    @Autowired
    private Ods704eService service;
    
    
    @RequestMapping(value = "/identities", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsIdentity> findIdentities(Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        List<OdsIdentity> identities = service.findIdentities();

        if (identities.isEmpty()) {

        } else {

            for (OdsIdentity identity: identities)
            {
                log.debug("identity:" + ToStringBuilder.reflectionToString(identity));
            }

        }
        return identities;
    }   
    
    
    @RequestMapping(value = "/group_name", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsGroup> findGroupByName(@RequestBody OdsGroup odsGroup, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        List<OdsGroup> groups = service.findGroupByName(odsGroup.getName(), odsGroup.getDescription());

        if (groups.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            for (OdsGroup group: groups)
            {
                log.debug("findGroupByName:" + ToStringBuilder.reflectionToString(group));
            }
            alerter.success(Messages.success_find());
        }
        return groups;
    }   

    
    @RequestMapping(value = "/package_by_group_id", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods704eGrid1Dto> findPackageByGroupId(@RequestBody OdsGroupPackagePK odsGroupPackagePk, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        List<Ods704eGrid1Dto> resources = service.findPackageByGroupId(odsGroupPackagePk.getGroupId());

        if (resources.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            /*for (Ods702eGrid1Dto resource: resources)
            {
                log.debug("findResourceByCategoryId:" + ToStringBuilder.reflectionToString(resource));
            }*/
            alerter.success(Messages.success_find());
        }
        return resources;
    }  
    
    
    @RequestMapping(value = "/identity_by_group_id", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsIdentityGroup> findIndentityByGroupId(@RequestBody OdsGroupPackagePK odsGroupPackagePk, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        log.debug("AAAAAAAAAAAAAAA");
        List<OdsIdentityGroup> identities = service.findIndentityByGroupId(odsGroupPackagePk.getGroupId());

        if (identities.isEmpty()) {
            log.debug("CCCCCCCCCCCC");
        } else {

            for (OdsIdentityGroup identity: identities)
            {
                log.debug("findIndentityByGroupId:" + ToStringBuilder.reflectionToString(identity));
            }

        }
        return identities;
    }  
    
    
    @RequestMapping(value = "/find/un_group_package", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods704eGrid1Dto> findUnGroupPackageByNameSelPkg(@RequestBody Ods704eChosePkgFormBean ods704eChosePkgFormBean, Alerter alerter) {
        /*log.debug("resName:"+ods702eChoseResFormBean.getName());
        log.debug("selectedResList:"+ods702eChoseResFormBean.getSelectedResList());*/
        
        List<Ods704eGrid1Dto> resources = service.findUnGroupPackageByNameSelPkg(ods704eChosePkgFormBean.getName(), ods704eChosePkgFormBean.getSelectedPkgList());
        
        if (resources.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            /*for (Ods702eGrid1Dto resource: resources)
            {
                log.debug("findUnCategoryResourceByNameSelRes:" + ToStringBuilder.reflectionToString(resource));
            }*/
            alerter.success(Messages.success_find());
        }
        return resources;
    }
    
    
    
    


            
            
    @RequestMapping(value = "/createAndUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    
     void createAndUpload(Ods704eDto ods704eDto, Alerter alerter) throws IOException {
    
        InputStream m = null;
        try {

            log.debug("rest/res/create" + ods704eDto.getName());
            log.debug("rest/res/create" + ods704eDto.getDescription());
            log.debug("rest/res/create" + ods704eDto.getChkIdentityIdList());
            log.debug("rest/res/create" + ods704eDto.getSelPackageIdList());
            
            
            m = ods704eDto.getFile().getInputStream();
            service.create(ods704eDto.getName(), ods704eDto.getDescription(), ods704eDto.getChkIdentityIdList(),
                    ods704eDto.getSelPackageIdList(), m, ods704eDto.getFile().getOriginalFilename());
            alerter.success(Messages.success_create());
        } catch (Exception e) {
            e.printStackTrace();
            alerter.fatal("系統發生錯誤");
        }  finally {
            if (m != null) try { log.debug("AAA");   m.close(); m=null;} catch (IOException logOrIgnore) {}
        } 

    }
    
    
    @RequestMapping(value = "/save_group", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String updateGroupByNameDescIdtIdLstPkgIdLst(@RequestBody Ods704eFormBean ods704eFormBean, Alerter alerter) throws Exception {
         /*log.debug("rest/res/create" + ods704eDto.getId());
        log.debug("rest/res/create" + ods704eDto.getName());
        log.debug("rest/res/create" + ods704eDto.getDescription());
        log.debug("rest/res/create" + ods704eDto.getChkIdentityIdList());
        log.debug("rest/res/create" + ods704eDto.getSelPackageIdList());*/
        
        service.updateGroupByNameDescIdtIdLstPkgIdLst(ods704eFormBean);
        
        alerter.success(Messages.success_update());

        return ods704eFormBean.getId();
    }
    
    
    /*@RequestMapping(value = "/saveGroupImage", method = RequestMethod.POST)
    public @ResponseBody
    
     void saveGroupImage(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("chkIdentityIdList") String chkIdentityIdList, 
             @RequestParam("selPackageIdList") String selPackageIdList, @RequestParam("file") MultipartFile multipartFile, Alerter alerter) throws IOException {
*/
        
    @RequestMapping(value = "/saveGroupImage", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    
     void saveGroupImage(Ods704eDto ods704eDto, Alerter alerter) throws IOException {
            

        /*
        log.debug("rest/res/create" + name);
        log.debug("rest/res/create" + description);
        log.debug("rest/res/create" + chkIdentityIdList);
        log.debug("rest/res/create" + selPackageIdList);
        log.debug("rest/res/create" + multipartFile.getOriginalFilename());*/
        Ods704eFormBean ods704eFormBean = new Ods704eFormBean();
        ods704eFormBean.setId(ods704eDto.getId());
        ods704eFormBean.setName(ods704eDto.getName());
        ods704eFormBean.setDescription(ods704eDto.getDescription());
        ods704eFormBean.setChkIdentityIdList(ods704eDto.getChkIdentityIdList());
        ods704eFormBean.setSelPackageIdList(ods704eDto.getSelPackageIdList());
        
        InputStream m = null;
        try {

            m = ods704eDto.getFile().getInputStream();
            service.updateGroupByNameDescIdtIdLstPkgIdLstImage(ods704eFormBean, m, ods704eDto.getFile().getOriginalFilename());
            
            alerter.success(Messages.success_update());
            


        } catch (Exception e) {
            e.printStackTrace();
            alerter.fatal("系統發生錯誤");
        }  finally {
            if (m != null) try { log.debug("BBB");   m.close(); m=null;} catch (IOException logOrIgnore) {}
        } 

    }
    
    
    @RequestMapping(value = "/delete_group", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void deleteGroupByGroupId(@RequestBody Ods704eFormBean ods704eFormBean, Alerter alerter) throws Exception {

        service.deleteGroupByGroupId(ods704eFormBean);
        
        alerter.success(Messages.success_delete());
    }
    
}
