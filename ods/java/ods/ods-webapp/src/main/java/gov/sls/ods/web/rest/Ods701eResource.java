package gov.sls.ods.web.rest;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ads.Ads132fa;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsDanView;
import gov.sls.entity.ods.OdsDanWorkbook;
import gov.sls.entity.ods.OdsResource;
import gov.sls.ods.Messages;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods701e02Tab1FormBean;
import gov.sls.ods.dto.Ods701e05Tab1FormBean;
import gov.sls.ods.dto.Ods701eGrid1Dto;
import gov.sls.ods.dto.Ods701eGrid2Dto;
import gov.sls.ods.repository.Ads132faRepository;
import gov.sls.ods.service.Ods701eService;
import gov.sls.ods.web.dto.Ods701e05Dto;
import gov.sls.ods.web.dto.Ods701eDto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS701E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS701E')")
public class Ods701eResource {

    @Autowired
    private Ods701eService service;

    @Autowired
    private Ads132faRepository repository;
    
    @Autowired
    private FileStore store;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    protected String ODS_PACKAGE_TEMPLATE_HTML = "package" + File.separator;

/*    private List<Ads132fa> find(String ped, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);

        List<Ads132fa> asb103fas = service.findByPed(ped);
        if (asb103fas.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return asb103fas;
    }*/
    


    /*
     * 若沒有傳key值，會mapping到此網址，查詢所有資料
     */
/*    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ads132fa> find(Alerter alerter) {
        return find(Strings.emptyToNull(null), alerter);
    }*/
    


    /*
     * 若有傳key值，會mapping到此網址，查詢單一筆資料
     */
/*    @RequestMapping(value = "/{ped}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ads132fa get(@PathVariable String ped, Alerter alerter) {
        List<Ads132fa> asb103fas = find(ped, alerter);
        if (asb103fas.isEmpty()) {
            return null;
        }
        return asb103fas.get(0);
    }*/
    


    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
/*    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ads132fa> find(@RequestBody Ads132fa asb103fa, Alerter alerter) {
        return find(asb103fa.getPed(), alerter);
    }*/

    /*@RequestMapping(value = "/{ped}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods701eGrid1Dto get(@PathVariable String ped, Alerter alerter) {
        log.debug("GET!!!!!!!!!!!!!! rest/:ped}");
        List<Ods701eGrid1Dto> asb103fas = find(ped, alerter);
        if (asb103fas.isEmpty()) {
            return null;
        }
        return asb103fas.get(0);
    }*/
    @RequestMapping(value = "/find/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid1Dto> getRes(@RequestBody Ods701eGrid1Dto ods701eGrid1Dto, Alerter alerter) {
        log.debug("GET!!!!!!!!!!!!!! rest/find/all}");
        return find(ods701eGrid1Dto, alerter);
    }
    
    /*@RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid1Dto> findRes(Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/find/all}");
        return find(alerter);
    }*/
    
    @RequestMapping(value = "/find/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid1Dto> findRes(@RequestBody Ods701eGrid1Dto ods701eGrid1Dto, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/find/all}");
        return find(ods701eGrid1Dto, alerter);
    }

    private List<Ods701eGrid1Dto> find(Ods701eGrid1Dto ods701eGrid1Dto, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);

        List<Ods701eGrid1Dto> allRes = service.findAll(ods701eGrid1Dto.getName(),ods701eGrid1Dto.getDescription(),ods701eGrid1Dto.getWorkbookName(),ods701eGrid1Dto.getViewName());
        if (allRes.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return allRes;
    }

    
    
    /*
     * 若沒有傳key值，會mapping到此網址，查詢所有資料
     */
/*    @RequestMapping(value = "/resdetail/{ped}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsResourceVersion> getResDetail(@PathVariable String ped, Alerter alerter) {
        log.debug("GET!!!!!!!!!!!!!! {rest/resdetail/{ped}");
        return findResDetail(ped, alerter);
    }
    
    @RequestMapping(value = "/resdetail/find/res", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsResourceVersion> findResDetail(@RequestBody OdsResourceVersion odsResourceVersion, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! {rest/resdetail/find/res");
        return findResDetail(odsResourceVersion.getName(), alerter);
    }
    
    
    private List<OdsResourceVersion> findResDetail(String resId, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);

        List<OdsResourceVersion> asb103fas = service.findResDetailByResId(resId);
        if (asb103fas.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return asb103fas;
    }*/
    
    
    
    /*
     * 若沒有傳key值，會mapping到此網址，查詢所有資料
     */
    @RequestMapping(value = "/resdetailndel/{ped}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid2Dto> getResDetailNDel(@PathVariable String ped, Alerter alerter) {
        log.debug("GET!!!!!!!!!!!!!! rest/resdetail/{ped}");
        return findResDetailNDel(ped, "", "", alerter);
    }
    @RequestMapping(value = "/resdetailndel/find/res", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid2Dto> findResDetailNDel(@RequestBody OdsResource odsResource, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/resdetailndel/find/res");
        System.out.println("AAAAAAAAAAAAAAAAA"+propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH));
        
        
           // OK, 但可能只能看本機的檔案
          /*try {
            
            if ((new File("c:\\4.pdf")).exists()) {
     
                Process p = Runtime
                   .getRuntime()
                   .exec("rundll32 url.dll,FileProtocolHandler c:\\4.pdf");
                p.waitFor();
     
            } else {
     
                System.out.println("File is not exists");
     
            }
     
            System.out.println("Done");
     
            } catch (Exception ex) {
            ex.printStackTrace();
          }
          */
        
     
          

        
        //試試這一個：http://www.informit.com/guides/content.aspx?g=java&seqNum=377
        
        return findResDetailNDel(odsResource.getId(), odsResource.getName(), odsResource.getDescription(), alerter);
    }
    private List<Ods701eGrid2Dto> findResDetailNDel(String resId, String name, String description, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);

        List<Ods701eGrid2Dto> resDetailNDel = service.findResDetailNDelByResId(resId, name, description);
        if (resDetailNDel.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return resDetailNDel;
    }
    
    
    @RequestMapping(value = "/resdetailall/{ped}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid2Dto> getResDetailAll(@PathVariable String ped, Alerter alerter) {
        log.debug("GET!!!!!!!!!!!!!! rest/resdetail/{ped}");
        return findResDetailAll(ped, alerter);
    }
    @RequestMapping(value = "/resdetailall/find/res", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Ods701eGrid2Dto> findResDetailAll(@RequestBody OdsResource odsResource, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/resdetailall/find/res");
        return findResDetailAll(odsResource.getId(), alerter);
    }    
    private List<Ods701eGrid2Dto> findResDetailAll(String resId, Alerter alerter) {
        // 取得內網專用的國庫署使用者物件
        // 如果登入時使用支用機關、公用事業或其他憑證登入，則會收到 ClassCastException !!!
        // 詳細使用方式請參考 UserHolder.getUser() 的 javadoc，謝謝
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);

        List<Ods701eGrid2Dto> resDetailAll = service.findResDetailAllByResId(resId);
        if (resDetailAll.isEmpty()) {
            alerter.info(Messages.warning_notFound());
        } else {
            alerter.success(Messages.success_find());
        }
        return resDetailAll;
    }
    
    
    
    



    @RequestMapping(value = "/{ped}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@PathVariable String ped, @Valid @RequestBody Ads132fa asb103fa, Alerter alerter) {
        service.create(asb103fa);
        alerter.success(Messages.success_create());
    }
    
    /*
    @RequestMapping(value = "/res/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@RequestBody OdsResource odsResource, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/res/create");

        service.create(odsResource);
        alerter.success(Messages.success_create());
    }*/
    
    

    
    @RequestMapping(value = "/res/saveGrid1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    //void saveGrid1(@RequestBody List<Ods701eGrid1Dto> ods701eGrid1Dtos,
    void saveGrid1(@RequestBody Ods701eGrid1Dto ods701eGrid1Dto,
            Alerter alerter) {
        
        List<Ods701eGrid1Dto> updOds701eGrid1Dtos = new ArrayList<Ods701eGrid1Dto>();
        
        //for (Ods701eGrid1Dto ods701eGrid1Dto : ods701eGrid1Dtos) {
            if("Y".equals(ods701eGrid1Dto.getIsChange()))
            {
                updOds701eGrid1Dtos.add(ods701eGrid1Dto);
            }
        //}
        
        //LIST updGrid1Data
        /*for (Ods701eGrid1Dto updOds701eDto: updOds701eGrid1Dtos)
        {
            log.debug("updOds701eDtos:" + ToStringBuilder.reflectionToString(updOds701eDto));
        }*/
        
        

        if(!updOds701eGrid1Dtos.isEmpty())
        {
            boolean isDuplicateName = false;
            
            List<Ods701eGrid1Dto> allRes = service.findAll("", "", "", "");
            for (Ods701eGrid1Dto res: allRes)
            {
                log.debug("allRes:" + ToStringBuilder.reflectionToString(res));
            }
            dupCheck:
            for (int i = 0; i < allRes.size(); i++)
            {
                String resName = allRes.get(i).getName();
                String resId = allRes.get(i).getId();
                
                for (int j = 0; j < updOds701eGrid1Dtos.size(); j++)
                {
                    String updName = updOds701eGrid1Dtos.get(j).getName();
                    String updId = updOds701eGrid1Dtos.get(j).getId();
                    
                    if (resName.equals(updName) && !resId.equals(updId)){
                        isDuplicateName = true;
                        break dupCheck;
                    }
                    
                }
                
                
            }
            
            log.debug("isDuplicateName:" + isDuplicateName);
            if(isDuplicateName)
            {
                alerter.fatal("素材及案例名稱不可重複");
                return;
            } else {
                for (Ods701eGrid1Dto updOds701eGrid1Dto: updOds701eGrid1Dtos)
                {
                    log.debug("updOds701eGrid1Dto:" + ToStringBuilder.reflectionToString(updOds701eGrid1Dto));
                }
                service.saveGrid1(updOds701eGrid1Dtos);
                alerter.success(Messages.success_update());
            }
            
            
        } else {
            alerter.info("資料無異動");
        }
        
        
    }
    

        

    @RequestMapping(value = "/res/saveGrid2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void saveGrid2(@RequestBody Ods701eGrid2Dto ods701eGrid2Dto, 
            Alerter alerter) {
        

        List<Ods701eGrid2Dto> updOds701eGrid2Dtos = new ArrayList<Ods701eGrid2Dto>();
        
        //for (Ods701eGrid2Dto ods701eGrid2Dto : ods701eGrid2Dtos) {
            if("Y".equals(ods701eGrid2Dto.getIsChange()))
            {
                updOds701eGrid2Dtos.add(ods701eGrid2Dto);
            }
        //}
        

        /*for (Ods701eGrid2Dto updOds701eGrid2Dto: updOds701eGrid2Dtos)
        {
            log.debug("updOds701eGrid2Dto:" + ToStringBuilder.reflectionToString(updOds701eGrid2Dto));
        }*/
        
        //主檔跟版本邏輯不同，同樣的可修改多筆下，ver0不可以出現

        if(!updOds701eGrid2Dtos.isEmpty())
        {
            String resourceId = updOds701eGrid2Dtos.get(0).getResourceId();
            log.debug("resId:" + resourceId);
            
            boolean isDuplicateName = false;
            
            //目前素材及案例版本不需限定名稱是否重複，若有重複則開啟；且在create時也需多判斷
            /*List<Ods701eGrid2Dto> resDetailAll = service.findResDetailAllByResId(resourceId);
            dupCheck:
            for (int i = 0; i < resDetailAll.size(); i++)
            {
                String resName = resDetailAll.get(i).getName();
                String resVer = resDetailAll.get(i).getVer();
                if ("0".equals(resVer)){ //之後這段可拿掉，因為resdetailall不會再查出ver=0的資料
                    continue;
                }
                
                for (int j = 0; j < updOds701eGrid2Dtos.size(); j++)
                {
                    String updName = updOds701eGrid2Dtos.get(j).getName();
                    String updVer = updOds701eGrid2Dtos.get(j).getVer();
                    
                    if (resName.equals(updName) && !resVer.equals(updVer)){
                        isDuplicateName = true;
                        break dupCheck;
                    }
                    
                }
            }*/
            
            //log.debug("isDuplicateName:" + isDuplicateName);
            if(isDuplicateName)
            {
                alerter.fatal("素材及案例名稱不可重複");
                return;
            } else {
                /*for (Ods701eGrid2Dto updOds701eGrid2Dto: updOds701eGrid2Dtos)
                {
                    log.debug("updOds701eGrid2Dto:" + ToStringBuilder.reflectionToString(updOds701eGrid2Dto));
                }*/
                service.saveGrid2(updOds701eGrid2Dtos); 
                alerter.success(Messages.success_update());
            }
            
            
        } else {
            alerter.info("資料無異動");
        }
        
        
    }
    

    
    /*@RequestMapping(value = "/findunselcategory_resid/{resId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findUnSelCategoryByResId(@PathVariable String resId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> unSelCategory = service.findUnSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> unSelCategory = service.findUnSelCategoryByResId(resId);

        if (unSelCategory.isEmpty()) {
        } else {

            for (OdsCategory category: unSelCategory)
            {
                log.debug("findunselcategoryByResId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return unSelCategory;
    } */  
    @RequestMapping(value = "/findunselcategory_resid", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findUnSelCategoryByResId(@RequestBody OdsResource odsResource, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> unSelCategory = service.findUnSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> unSelCategory = service.findUnSelCategoryByResId(odsResource.getId());

        if (unSelCategory.isEmpty()) {
        } else {

            for (OdsCategory category: unSelCategory)
            {
                log.debug("findunselcategoryByResId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return unSelCategory;
    }   
    
    
    
    /*@RequestMapping(value = "/findselcategory_resid/{resId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findSelCategoryByResId(@PathVariable String resId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> selCategory = service.findSelCategoryByResId(resId);

        if (selCategory.isEmpty()) {

        } else {

            for (OdsCategory category: selCategory)
            {
                log.debug("findselcategoryByResId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return selCategory;
    } */
    @RequestMapping(value = "/findselcategory_resid", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findSelCategoryByResId(@RequestBody OdsResource odsResource, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> selCategory = service.findSelCategoryByResId(odsResource.getId());

        if (selCategory.isEmpty()) {

        } else {

            for (OdsCategory category: selCategory)
            {
                log.debug("findselcategoryByResId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return selCategory;

    }  
    
    
    @RequestMapping(value = "/findunselcategory_categoryid/{categoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findUnSelCategoryByCategoryId(@PathVariable String categoryId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> unSelCategory = service.findUnSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> unSelCategory = service.findUnSelCategoryByCategoryId(categoryId);

        if (unSelCategory.isEmpty()) {
        } else {

            for (OdsCategory category: unSelCategory)
            {
                log.debug("findunselcategoryByCategoryId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return unSelCategory;
    }   
    
    
    @RequestMapping(value = "/findselcategory_categoryid/{categoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsCategory> findSelCategoryByCategoryId(@PathVariable String categoryId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        List<OdsCategory> selCategory = service.findSelCategoryByCategoryId(categoryId);

        if (selCategory.isEmpty()) {
       } else {

            for (OdsCategory category: selCategory)
            {
                log.debug("findselcategoryByCategoryId:" + ToStringBuilder.reflectionToString(category));
            }
        }
        return selCategory;
    } 
    
    
    @RequestMapping(value = "/finddanwbk_all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsDanWorkbook> findDanWbkAll(Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        List<OdsDanWorkbook> danWbkAll = service.findDanWbkAll();

        if (danWbkAll.isEmpty()) {
       } else {

            for (OdsDanWorkbook danWbk: danWbkAll)
            {
                log.debug("wbk:" + ToStringBuilder.reflectionToString(danWbk));
            }
        }
        return danWbkAll;
    } 
    
    
    @RequestMapping(value = "/finddanview_danwbkid/{danWbkId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsDanView> findDanViewByDanWbkId(@PathVariable String danWbkId, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        
        
        log.debug("danWbkId:" + danWbkId);
        List<OdsDanView> danViews = service.findDanViewByDanWbkId(danWbkId);
        

        if (danViews.isEmpty()) {
       } else {

            for (OdsDanView danView: danViews)
            {
                log.debug("danView:" + danView);
            }
        }
        return danViews;
    } 
    
    
    @RequestMapping(value = "/get_wbk_res_file_size/{wbkId}/{viewId}/{resType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    double getWbkResFileSize(@PathVariable String wbkId, @PathVariable String viewId, @PathVariable String resType, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        
        
        log.debug("wbkId:" + wbkId);
        log.debug("viewId:" + viewId);
        log.debug("resType:" + resType);

        double fileSize = service.getWbkResFileSize(wbkId, viewId, resType);
        
        return fileSize;
    } 
    
    
    /*
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public @ResponseBody
    void fileUpload(@RequestParam("file") MultipartFile multipartFile, Alerter alerter)
            throws IOException {

        List<Nss701eBean> nss701eBeans = new ArrayList<Nss701eBean>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                multipartFile.getInputStream()));
        String read = bufferedReader.readLine();
        Integer count = 1;

        while (!Strings.isNullOrEmpty(read)) {
            Nss701eBean bean = new Nss701eBean(count, read);
            nss701eBeans.add(bean);

            read = bufferedReader.readLine();
            count++;
        }

        //service.doInsert(nss701eBeans);
        log.debug("findselcategoryByCategoryId:" + ToStringBuilder.reflectionToString(nss701eBeans));

        alerter.info("您上傳檔案名稱" + multipartFile.getOriginalFilename() + "上傳成功, 檔案size "
                + String.valueOf(multipartFile.getSize()) + " Bytes");
    }*/

    
    
    /*
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public @ResponseBody
    void fileUpload(@RequestParam("file") MultipartFile multipartFile,
            Alerter alerter) throws IOException {
        log.debug("AAAAAAAAAAAAAAAAAAA:" + Locations.Local.TEMP);
        log.debug("BBBBBBBBBBBBBBBBBBBBBBBB:" + multipartFile.getOriginalFilename());
        
//        File dest =                 
//                new File(propertiesAccessor
//                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
//                + ODS_PACKAGE_TEMPLATE_HTML
//                + "AAAAAAAAA"
//                + File.separator
//                + "html"
//                + File.separator
//                + "AAAAAAAAAAA"
//                + "-" + 1 + ".jpg");
//        
//        multipartFile.transferTo(dest);
        
        
        File file = store.getFile(
                Locations.Local.TEMP, 
                multipartFile.getOriginalFilename(), 
                true);
        
        Files.write(multipartFile.getBytes(), file);
        
        
        alerter.info("您上傳檔案名稱" + multipartFile.getOriginalFilename() + "上傳成功, 檔案size "
                + String.valueOf(multipartFile.getSize()) + " Bytes");
        //alerter.success(Messages.success_uploadFile());

    }*/
    
    
    @RequestMapping(value = "/res/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@RequestBody Ods701e02Tab1FormBean ods701e02Tab1FormBean, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/res/create");

        //log.debug("rest/res/create" + ods701e02Tab1FormBean.getName());
        service.create(ods701e02Tab1FormBean);
        alerter.success(Messages.success_create());
    }
    
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public @ResponseBody
    void fileUpload(@RequestParam("file") MultipartFile multipartFile,
            Alerter alerter) throws IOException {

        /*File file = store.getFile(Locations.Local.TEMP, multipartFile.getOriginalFilename(), true,
                "a", "b", "c");
        
        Files.write(multipartFile.getBytes(), file);*/
        
        InputStream in = null;
        try {
            in = multipartFile.getInputStream();
            service.generateTemplate2("XXXXXXXXXXXXXX", 1, in, multipartFile.getOriginalFilename());
        } finally {
            if (in != null) {
              safeClose(in);
            }
        }
        
        alerter.success(Messages.success_uploadFile());

    }
    
    @RequestMapping(value = "/createAndUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    //都可傳除了selcategory會傳空[]
    // void createAndUpload(@RequestParam("name") String name, @RequestParam("description") String description,  ArrayList<OdsCategory> selCategory, @RequestParam(value="myAry") String[] myParams, @RequestParam("file") MultipartFile multipartFile, Alerter alerter) throws IOException {
    //都可傳，但selCategory只能傳一筆資料
//     void createAndUpload(@RequestParam("name") String name, @RequestParam("description") String description,  @ModelAttribute(value="selCategory") OdsCategory selCategory, @RequestParam(value="myAry") String[] myParams, @RequestParam("file") MultipartFile multipartFile, Alerter alerter) throws IOException {
//        log.debug("rest/res/create" + selCategory);
//        log.debug("rest/res/create" + name);
//        log.debug("rest/res/create" + description);
//        log.debug("rest/res/create" + ToStringBuilder.reflectionToString(myParams));
//        service.generateTemplate2("XXXXXXXXXXXXXX", 1, multipartFile.getInputStream(), multipartFile.getOriginalFilename());
//        service.create(ods701e02Tab1FormBean);
    
    
//     void createAndUpload(@RequestParam("name") String name, @RequestParam("description") String description,
//             @RequestParam("toDatastoreSync") String toDatastoreSync, @RequestParam("toDatastoreDate") String toDatastoreDate,
//             @RequestParam("selCategoryIdList") String selCategoryIdList, @RequestParam("file") MultipartFile multipartFile, Alerter alerter) throws IOException {

    void createAndUpload(Ods701eDto ods701eDto, Alerter alerter)
            throws Exception {
    

        log.debug("rest/res/create" + encodeStr(ods701eDto.getName()));
        log.debug("rest/res/create" + ods701eDto.getDescription());
        log.debug("rest/res/create" + ods701eDto.getSelCategoryIdList());
        log.debug("rest/res/create" + ods701eDto.getToDatastoreSync());
        log.debug("rest/res/create" + ods701eDto.getToDatastoreDate());
        

        
        //service.generateTemplate2("XXXXXXXXXXXXXX", 1, multipartFile.getInputStream(), multipartFile.getOriginalFilename());
 
      //DataSourceThreadLocal.clearDataSourceEnum();//記得一定要clear, 還原為default
        Map<String, Object> toStoreMap = service.create(ods701eDto.getName(), ods701eDto.getDescription(),
                ods701eDto.getSelCategoryIdList()
//                , csvAddBom(ods701eDto.getFile().getOriginalFilename(),ods701eDto.getFile().getInputStream())
                ,ods701eDto.getFile().getInputStream()
                , ods701eDto.getFile().getOriginalFilename(),
                ods701eDto.getFile().getContentType(), ods701eDto
                        .getToDatastoreSync(), ods701eDto
                        .getToDatastoreDate());
        
        toDataStore(toStoreMap, alerter);
 
    }
    
    /*@RequestMapping(value = "/createAndImport", method = RequestMethod.POST)
    public @ResponseBody
    void createAndImport(@RequestParam("name") String name, @RequestParam("description") String description,
            @RequestParam("selCategoryIdList") String selCategoryIdList, 
            @RequestParam("toDatastoreSync") String toDatastoreSync, @RequestParam("toDatastoreDate") String toDatastoreDate,
            @RequestParam("selImportInfoList") String selImportInfoList, Alerter alerter) throws IOException {
   

       log.debug("rest/res/create" + encodeStr(name));
       log.debug("rest/res/create" + description);
       log.debug("rest/res/create" + selCategoryIdList);
       log.debug("rest/res/create" + selImportInfoList);*/

    
       
   @RequestMapping(value = "/createAndImport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody
   void createAndImport(@RequestBody Ods701eDto ods701eDto, Alerter alerter) throws Exception {
       log.debug("POST!!!!!!!!!!!!!! rest/createAndImport");

       log.debug("rest/res/create" + ods701eDto.getName());
       log.debug("rest/res/create" + ods701eDto.getDescription());
       log.debug("rest/res/create" + ods701eDto.getSelCategoryIdList());
       log.debug("rest/res/create" + ods701eDto.getSelImportInfoList());
       log.debug("rest/res/create" + ods701eDto.getToDatastoreSync());
       log.debug("rest/res/create" + ods701eDto.getToDatastoreDate());
       
       //service.generateTemplate2("XXXXXXXXXXXXXX", 1, multipartFile.getInputStream(), multipartFile.getOriginalFilename());

       log.debug("ZZZZZZZZZZZZZZZZZZZ");
       Map<String, Object> toStoreMap = service.create(ods701eDto.getName(), ods701eDto.getDescription(), ods701eDto.getSelCategoryIdList(), 
               ods701eDto.getSelImportInfoList(), ods701eDto.getToDatastoreSync(), ods701eDto.getToDatastoreDate());

       toDataStore(toStoreMap, alerter);

   }
    


    private static String encodeStr(String str) {
        try {
            return new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    @RequestMapping(value = "/resdetail/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void create(@RequestBody Ods701e05Tab1FormBean ods701e05Tab1FormBean, Alerter alerter) {
        log.debug("POST!!!!!!!!!!!!!! rest/resdetail/create");

        service.create(ods701e05Tab1FormBean);
        alerter.success(Messages.success_create());
    }
    
    
    
    

    
    void toDataStore(Map<String, Object> toStoreMap, Alerter alerter) throws Exception {
        String toDatastoreSuccess = null;

        try {
            boolean isToDatastore = service.isToDatastore(toStoreMap);
            if(isToDatastore){
                service.createDatasetToStore(toStoreMap);
                toDatastoreSuccess = "1";
            } else {
                toStoreMap.put("toDatastoreSync", (String) toStoreMap.get("toDatastoreSync"));
                toStoreMap.put("toDatastoreDate", (String) toStoreMap.get("toDatastoreDate"));
            }
            service.updateToStoreResult(toStoreMap, toDatastoreSuccess);
            alerter.success(Messages.success_create());
            
        } catch (Exception e) {
            log.error("轉檔發生錯誤:{}", e);
            
            toDatastoreSuccess = "0";
            service.updateToStoreResult(toStoreMap, toDatastoreSuccess);
            alerter.warn("執行成功，但轉檔失敗。請更正檔案後，進入版本管理頁面重新上傳");
        } 
    }
    
    
    @RequestMapping(value = "/resdetail/createAndUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    
/*     void resdetailCreateAndUpload(@RequestParam("resourceId") String resourceId, @RequestParam("name") String name, @RequestParam("description") String description,
             @RequestParam("toDatastoreSync") String toDatastoreSync, @RequestParam("toDatastoreDate") String toDatastoreDate, @RequestParam("file") MultipartFile multipartFile, Alerter alerter) throws IOException {
    
        log.debug("rest/res/create" + resourceId);
        log.debug("rest/res/create" + name);
        log.debug("rest/res/create" + description);
        log.debug("rest/res/create" + toDatastoreSync);
        log.debug("rest/res/create" + toDatastoreDate);*/
    
    
    void resdetailCreateAndUpload(Ods701e05Dto ods701e05Dto, Alerter alerter)
            throws Exception {
    
        log.debug("rest/res/create" + ods701e05Dto.getResourceId());
        log.debug("rest/res/create" + ods701e05Dto.getName());
        log.debug("rest/res/create" + ods701e05Dto.getDescription());
        log.debug("rest/res/create" + ods701e05Dto.getToDatastoreSync());
        log.debug("rest/res/create" + ods701e05Dto.getToDatastoreDate());
        
        Map<String, Object> toStoreMap = service.createResdetail(ods701e05Dto.getResourceId(), ods701e05Dto.getName(), ods701e05Dto.getDescription() 
//                ,csvAddBom(ods701e05Dto.getFile().getOriginalFilename(),ods701e05Dto.getFile().getInputStream())
                ,ods701e05Dto.getFile().getInputStream()
                , ods701e05Dto.getFile().getOriginalFilename(), ods701e05Dto.getFile().getContentType(), 
                ods701e05Dto.getToDatastoreSync(), ods701e05Dto.getToDatastoreDate());
        
        toDataStore(toStoreMap, alerter);
        

    }
    
    
    @RequestMapping(value = "/resdetail/fileRefreshUpload", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public @ResponseBody
    void resdetailFileRefreshUpload(Ods701e05Dto ods701e05Dto, Alerter alerter)
            throws Exception {
    
        log.debug("rest/resdetail/fileRefreshUpload" + ods701e05Dto.getResourceId());
        log.debug("rest/resdetail/fileRefreshUpload" + ods701e05Dto.getVer());
        log.debug("rest/resdetail/fileRefreshUpload" + ods701e05Dto.getToDatastoreSync());
        log.debug("rest/resdetail/fileRefreshUpload" + ods701e05Dto.getToDatastoreDate());
        
        Map<String, Object> toStoreMap = service.saveFileRefreshUpload(ods701e05Dto.getResourceId(), ods701e05Dto.getVer(),
                csvAddBom(ods701e05Dto.getFile().getOriginalFilename(),ods701e05Dto.getFile().getInputStream()), ods701e05Dto.getFile().getOriginalFilename(), ods701e05Dto.getFile().getContentType(), 
                ods701e05Dto.getToDatastoreSync(), ods701e05Dto.getToDatastoreDate());
        
        toDataStore(toStoreMap, alerter);
        

    }
    
    
    @RequestMapping(value = "/resdetail/fileRefreshImport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void resdetailFileRefreshImport(@RequestBody Ods701e05Dto ods701e05Dto, Alerter alerter) throws Exception {
        log.debug("POST!!!!!!!!!!!!!! rest/resdetail/fileRefreshImport");

        log.debug("rest/resdetail/fileRefreshImport" + ods701e05Dto.getResourceId());
        log.debug("rest/resdetail/fileRefreshImport" + ods701e05Dto.getVer());
        log.debug("rest/resdetail/fileRefreshImport" + ods701e05Dto.getToDatastoreSync());
        log.debug("rest/resdetail/fileRefreshImport" + ods701e05Dto.getToDatastoreDate());
        

        Map<String, Object> toStoreMap = service.saveFileRefreshImport(ods701e05Dto.getResourceId(), ods701e05Dto.getVer(), 
                ods701e05Dto.getToDatastoreSync(), ods701e05Dto.getToDatastoreDate());

        toDataStore(toStoreMap, alerter);

    }
    

    @RequestMapping(value = "/finddanwbk_by_name", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<OdsDanWorkbook> findDanWbkByName(@RequestBody OdsDanWorkbook odsDanWorkbook, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.trace("User \"{}\" performing query.", user);
        //List<OdsCategory> allCategory = service.findAllCategory();
        //List<OdsCategory> selCategory = service.findSelCategory(Strings.emptyToNull(resId));
        List<OdsDanWorkbook> danWbkByName = service.findDanWbkByName(odsDanWorkbook.getName());

        if (danWbkByName.isEmpty()) {
       } else {

            for (OdsDanWorkbook danWbk: danWbkByName)
            {
                log.debug("wbk:" + ToStringBuilder.reflectionToString(danWbk));
            }
        }
        return danWbkByName;
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
    
    
//    目前無使用
    public InputStream csvAddBom(String fileName,InputStream multipartFileStream) throws Exception{
        if(fileName.substring(fileName.length()-3, fileName.length()).equals("csv")) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    multipartFileStream,"utf-8"));
            String fileHeaderStr = bufferedReader.readLine();
            StringBuffer buffer = new StringBuffer();
            //不含BOM則增加BOM
            String UTF8_BOM = "\uFEFF";  
            if(!fileHeaderStr.startsWith(UTF8_BOM)){
                fileHeaderStr = UTF8_BOM+fileHeaderStr;
            }
            buffer.append(fileHeaderStr);
            while ((fileHeaderStr = bufferedReader.readLine()) != null){
              buffer.append("\n"+fileHeaderStr);
            }
            return new ByteArrayInputStream(buffer.toString().getBytes(StandardCharsets.UTF_8));
        }else {
            return multipartFileStream;
        }
    }
}
