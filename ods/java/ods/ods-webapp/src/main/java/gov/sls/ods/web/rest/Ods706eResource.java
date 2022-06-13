package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsResourceCriteria;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods706eGrid3Dto;
import gov.sls.ods.dto.Ods706eGrid4Dto;
import gov.sls.ods.dto.Ods706eTab1FormBean;
import gov.sls.ods.dto.Ods706eTab2Dto;
import gov.sls.ods.dto.Ods706eTab2FormBean;
import gov.sls.ods.service.Ods706eService;
import gov.sls.ods.web.dto.Ods706eDto;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS706E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS706E')")
public class Ods706eResource {

    @Autowired
    private Ods706eService service;

    //勿刪!!!用find會導致ie8抓cache的問題，導致在新增、修改後的查詢無法執行
    /*@RequestMapping(value = "/criterias/{resId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods706eGrid3Dto> findCriteriasByResId(@PathVariable String resId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        List<Ods706eGrid3Dto> resourceCriterias = service.findCriteriasByResId(resId);

        if (resourceCriterias.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            for (Ods706eGrid3Dto resourceCriteria: resourceCriterias)
            {
                log.debug("findCriteriasByResId:" + ToStringBuilder.reflectionToString(resourceCriteria));
            }
            alerter.success(Messages.success_find());
        }
        return resourceCriterias;
    }   */
    @RequestMapping(value = "/criterias", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods706eGrid3Dto> findCriteriasByResId(@RequestBody Ods706eDto dto, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        List<Ods706eGrid3Dto> resourceCriterias = service.findCriteriasByResId(dto.getResId(), dto.getName(), dto.getDescription());

        if (resourceCriterias.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            for (Ods706eGrid3Dto resourceCriteria: resourceCriterias)
            {
                log.debug("findCriteriasByResId:" + ToStringBuilder.reflectionToString(resourceCriteria));
            }
            alerter.success(Messages.success_find());
        }
        return resourceCriterias;
    }   
    
    
    
    /*@RequestMapping(value = "/criteria_detail/{resId}/{criId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods706eGrid4Dto> findCriteriaDetailByResIdCriId(@PathVariable String resId, @PathVariable String criId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        List<Ods706eGrid4Dto> criteriaDetails = service.findCriteriaDetailByResIdCriId(resId, criId);

        if (criteriaDetails.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            for (Ods706eGrid4Dto criteriaDetail: criteriaDetails)
            {
                log.debug("findCriteriaDetailByResIdCriId:" + ToStringBuilder.reflectionToString(criteriaDetail));
            }
            alerter.success(Messages.success_find());
        }
        return criteriaDetails;
    } */
    @RequestMapping(value = "/criteriaDetail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods706eGrid4Dto> findCriteriaDetailByResIdCriId(@RequestBody Ods706eDto dto, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        List<Ods706eGrid4Dto> criteriaDetails = service.findCriteriaDetailByResIdCriId(dto.getResId(), dto.getCriId());

        if (criteriaDetails.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {

            for (Ods706eGrid4Dto criteriaDetail: criteriaDetails)
            {
                log.debug("findCriteriaDetailByResIdCriId:" + ToStringBuilder.reflectionToString(criteriaDetail));
            }
            alerter.success(Messages.success_find());
        }
        return criteriaDetails;
    }   
    
    @RequestMapping(value = "/create_criteria", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void createCriteriaByResId(@RequestBody Ods706eDto dto, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! create_criteria");
        Ods706eTab1FormBean ods706eTab1FormBean = new Ods706eTab1FormBean();
        
        ods706eTab1FormBean.setResId(dto.getResId());
        ods706eTab1FormBean.setName(dto.getName());
        ods706eTab1FormBean.setDescription(dto.getDescription());
       
        OdsResourceCriteria orcRtn = service.createCriteria(ods706eTab1FormBean);
        if (orcRtn != null) {
            service.createDetail(orcRtn, dto.getOds706eTab2FormBeanList());
            alerter.success(Messages.success_create());
        } else {
            alerter.fatal("方案名稱不可重覆!");
        }
        
    }
       
    @RequestMapping(value = "/save_criteria", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void updateCriteriaByResIdCriId(@RequestBody Ods706eDto dto, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! save_criteria");

        service.updateCriteriaAndDetail(dto.getOds706eTab1FormBean(), dto.getOds706eTab2FormBeanList());
        
        alerter.success(Messages.success_update());
        
    }
    
    //使用pathvariable傳中文參數要注意編碼問題
    private static String encodeStr(String str) {
        try {
            return new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @RequestMapping(value = "/remove_criteria", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void deleteCriteria(@RequestBody Ods706eTab1FormBean ods706eTab1FormBean, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! remove_criteria");
       
        service.deleteCriteriaAndDetail(ods706eTab1FormBean);
        
        alerter.success(Messages.success_delete());
        
    }
    
    
    
    @RequestMapping(value = "/dataset_cols", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods706eTab2Dto> findDatasetColsByResId(@RequestBody OdsResource odsResource, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        List<Ods706eTab2Dto> datasetCols = service.findDatasetColsByResId(odsResource.getId().replaceAll("-", "_"));

        if (datasetCols.isEmpty()) {

        } else {

            for (Ods706eTab2Dto datasetCol: datasetCols)
            {
                log.debug("findDatasetColsByResId:" + ToStringBuilder.reflectionToString(datasetCol));
            }
            alerter.success(Messages.success_find());
        }
        return datasetCols;
    }   

    
    //@RequestMapping(value = "/create_criteria_detail/{resId}/{criId}/{dataField}/{aggregateFunc}/{operator}/{target}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/create_criteria_detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    /*void createCriteriaDetailByResIdCriId(@PathVariable String resId, @PathVariable String criId, @PathVariable String dataField, 
            @PathVariable String aggregateFunc, @PathVariable String operator, @PathVariable BigDecimal target, Alerter alerter) {*/
    void createCriteriaDetailByResIdCriId(@RequestBody Ods706eTab2FormBean ods706eTab2FormBean, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! create_criteria_detail");

        service.createCriteriaDetailByResIdCriId(ods706eTab2FormBean);
        
        alerter.success(Messages.success_create());
    }
    
    
    //@RequestMapping(value = "/create_criteria_detail/{resId}/{criId}/{dataField}/{aggregateFunc}/{operator}/{target}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/save_criteria_detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void updateCriteriaDetailByResIdCriIdCond(@RequestBody Ods706eTab2FormBean ods706eTab2FormBean, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! save_criteria_detail");

        service.updateCriteriaDetailByResIdCriIdCond(ods706eTab2FormBean);
        
        alerter.success(Messages.success_update());
    }
    
    //@RequestMapping(value = "/create_criteria_detail/{resId}/{criId}/{dataField}/{aggregateFunc}/{operator}/{target}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/remove_criteria_detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void deleteCriteriaDetailByResIdCriIdCond(@RequestBody Ods706eTab2FormBean ods706eTab2FormBean, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! remove_criteria_detail");

        service.deleteCriteriaDetailByResIdCriIdCond(ods706eTab2FormBean);
        
        alerter.success(Messages.success_delete());
    }
}
