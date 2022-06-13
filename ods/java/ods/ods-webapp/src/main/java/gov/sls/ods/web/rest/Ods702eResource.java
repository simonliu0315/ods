package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsResourceCategoryPK;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods702eChoseResFormBean;
import gov.sls.ods.dto.Ods702eDto;
import gov.sls.ods.dto.Ods702eGrid1Dto;
import gov.sls.ods.dto.Ods702eGrid2Dto;
import gov.sls.ods.service.Ods702eService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS702E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS702E')")
public class Ods702eResource {

    @Autowired
    private Ods702eService service;
    
    @RequestMapping(value = "/category_name", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods702eGrid2Dto> findCategoryByName(@RequestBody OdsCategory odsCategory, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        List<Ods702eGrid2Dto> categories = service.findCategoryByName(odsCategory.getName(), odsCategory.getDescription());

        if (categories.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            /*for (Ods702eGrid2Dto category: categories)
            {
                log.debug("findCriteriasByResId:" + ToStringBuilder.reflectionToString(category));
            }*/
            alerter.success(Messages.success_find());
        }
        return categories;
    }   
    
    
    @RequestMapping(value = "/resource_by_category_id", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods702eGrid1Dto> findResourceByCategoryId(@RequestBody OdsResourceCategoryPK odsCategoryPk, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        
        List<Ods702eGrid1Dto> resources = service.findResourceByCategoryId(odsCategoryPk.getCategoryId());

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
    
    
    @RequestMapping(value = "/find/un_category_resource", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods702eGrid1Dto> findUnCategoryResourceByNameSelRes(@RequestBody Ods702eChoseResFormBean ods702eChoseResFormBean, Alerter alerter) {
        /*log.debug("resName:"+ods702eChoseResFormBean.getName());
        log.debug("selectedResList:"+ods702eChoseResFormBean.getSelectedResList());*/
        
        List<Ods702eGrid1Dto> resources = service.findUnCategoryResourceByNameSelRes(ods702eChoseResFormBean.getName(), ods702eChoseResFormBean.getSelectedResList());
        
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
    
    
    
    @RequestMapping(value = "/create_category", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String createCategoryByNameDescGrid1(@RequestBody Ods702eDto ods702eDto, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! create_category");
        
        /*log.debug("POST!!!!!!!!!!!!!! create_category" + ods702eDto.getCategoryName());
        log.debug("POST!!!!!!!!!!!!!! create_category" + ods702eDto.getCategoryDescription());
        
        List<Ods702eGrid1Dto> ods702eGrid1Dtos = ods702eDto.getGrid1();
        
        for (Ods702eGrid1Dto ods702eGrid1Dto: ods702eGrid1Dtos)
        {
            log.debug("ods702eGrid1Dto:" + ods702eGrid1Dto);
        }*/
        String categoryId = service.createCategoryByNameDescGrid1(ods702eDto);
        
        alerter.success(Messages.success_create());
        
        return categoryId;
        
    }
    
    
    @RequestMapping(value = "/save_category", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void updateCategoryByNameDescGrid1(@RequestBody Ods702eDto ods702eDto, Alerter alerter) throws Exception {
        /*log.debug("POST!!!!!!!!!!!!!! save_category");
        
        log.debug("POST!!!!!!!!!!!!!! save_category" + ods702eDto.getCategoryName());
        log.debug("POST!!!!!!!!!!!!!! save_category" + ods702eDto.getCategoryDescription());
        
        List<Ods702eGrid1Dto> ods702eGrid1Dtos = ods702eDto.getGrid1();
        
        for (Ods702eGrid1Dto ods702eGrid1Dto: ods702eGrid1Dtos)
        {
            log.debug("ods702eGrid1Dto:" + ods702eGrid1Dto);
        }*/
        service.updateCategoryByNameDescGrid1(ods702eDto);
        
        alerter.success(Messages.success_update());
    }
    
    
    @RequestMapping(value = "/delete_category", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void deleteCategoryByCategoryId(@RequestBody Ods702eDto ods702eDto, Alerter alerter) throws Exception {

        service.deleteCategoryByCategoryId(ods702eDto);
        
        alerter.success(Messages.success_delete());
    }
    
}
