/**
 * 
 */
package gov.sls.ods.web.controller;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsGroup;
import gov.sls.ods.service.Ods301eService;
import gov.sls.ods.web.dto.Ods301e2FormBean;
//import gov.sls.ods.service.Ods772xService;
import gov.sls.ods.web.dto.Ods301eFormBean;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 */
@Slf4j
@Controller
@RequestMapping("ODS301E")
public class Ods301eController {

    @Autowired
    private Ods301eService service;
    
    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private ServletContext servletContext;

    protected String CLEAR_GUP_NAME = "clearGroupName";
    
    protected String CLEAR_SELECTED_PKG_TAG = "clearSelectedOdsPackageTag";
    
    protected String CLEAR_SELECTED_RES_FILE_EXT = "clearSelectedOdsResourceFileExt";
    
    protected String CLEAR_SELECTED_IDENTITY = "clearSelectedOdsIdentity";
    
    protected String MORE_IDENTITY_TAG = "btnMoreIdentityTag";
    
    protected String MORE_RES_FILE_EXT = "btnMoreResFileExt";
    
    protected String MORE_PKG_TAG = "btnMorePkgTag";
    
    
    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * 
     * @param ods301e
     *            ods301e
     * @return Ods301eFormBean
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods301eFormBean query(@RequestBody Ods301eFormBean ods301e) {
        String groupName = ods301e.getGroupName();
        int orderByType = ods301e.getOrderByType();
        List<String> selectedOdsIdentity = ods301e.getSelectedOdsIdentity();
        List<String> selectedOdsPackageTag = ods301e.getSelectedOdsPackageTag();
        List<String> selectedOdsResourceFileExt = ods301e.getSelectedOdsResourceFileExt();
        List<OdsGroup> odsGroup = findGroup(groupName, selectedOdsPackageTag,
                selectedOdsResourceFileExt, selectedOdsIdentity, orderByType);
        ods301e.setOdsGroups(odsGroup);
        return ods301e;
    }

    /**
     * ????????????????????????????????????
     * 
     * @param groupName
     *            ??????????????????(?????????like??????)
     * @param tagList
     *            ????????????
     * @param fileExtList
     *            ??????????????????
     * @param identityList
     *            ??????????????????
     * @param orderByType
     *            0???????????????????????????1????????????????????????
     * @return List<OdsGroup>??????????????????
     */
    public List<OdsGroup> findGroup(String groupName, List<String> tagList,
            List<String> fileExtList, List<String> identityList, int orderByType) {
        SlsUser user = UserHolder.getUser();
        String userType = user.getUserType();
        return service.findGroups(groupName, tagList, fileExtList, identityList, orderByType, userType);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     * 
     * @param ods301e
     *            ods301e
     * @return Ods301eFormBean
     */
    @RequestMapping(value = "/initPage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods301eFormBean initPage(@RequestBody Ods301eFormBean ods301e) { 
        int orderByType = ods301e.getOrderByType();
        List<OdsGroup> odsGroup = findGroup(null, null, null, null, orderByType);
        List<String> odsIdentity = service.findAllIdentity();
        List<String> odsPackageTag = service.findAllPackageTag();
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        Ods301eFormBean formBean = new Ods301eFormBean();
        formBean.setOdsGroups(odsGroup);
        formBean.setOdsIdentity(odsIdentity);
        formBean.setOdsPackageTag(odsPackageTag);
        formBean.setOdsResourceFileExt(odsResourceFileExt);
        return formBean;
    }
    
    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     * 
     * @return the page
     */
    @RequestMapping(value = "/{orderByType}/", method = RequestMethod.GET)
    public ModelAndView initPageOrderByType(@PathVariable("orderByType") String orderByType, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("ods301e/ods301e", "orderByType", orderByType);
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }

    /**
     * ????????????????????????
     * 
     * @return the page
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/rest/{groupId}/", method = RequestMethod.GET)
    public ModelAndView getOds301e_2Page(@PathVariable("groupId") String groupId, HttpServletRequest request) throws UnsupportedEncodingException {
        Ods301eFormBean ods301e = new Ods301eFormBean();
        ods301e.setGroupId(groupId);
        log.debug("ods301e_2 groupId:" + groupId);
        ods301e.setOdsPackage(service.findPackages(groupId));
        ods301e.setOdsGroup(service.findGroup(groupId));
        ods301e = setBreadLink(ods301e, groupId);
        ModelAndView mav = new ModelAndView("ods301e/ods301e_02", "data", ods301e);
        mav = setBreadcrumb(mav);
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }
    
    private Ods301eFormBean setBreadLink(Ods301eFormBean ods301e, String groupId)
    {
        String url = "1," + groupId + ';'; 
        
        try {
            ods301e.setBreadLink(Base64.encodeBase64String(url.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

        return ods301e;
    }
    
    private ModelAndView setBreadcrumb(ModelAndView mav)
    {
        Ods301eFormBean ods301e = new Ods301eFormBean();
        
        List<String[]> funcInfo = new ArrayList<String[]>();
        
        funcInfo.add(new String [] {"ODS301E","????????????????????????"});
        ods301e.setFuncInfo(funcInfo);

        mav.addObject("breadData", ods301e);

        return mav;
    }    
    
    @RequestMapping(value = "/noscript/", method = RequestMethod.GET)
    public ModelAndView getOds301ePage(HttpServletRequest request) {
       int orderByType = 0;
        List<OdsGroup> odsGroup = findGroup(null, null, null, null, orderByType);
        List<String> odsIdentity = service.findAllIdentity();
        List<String> odsPackageTag = service.findAllPackageTag();
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        Ods301e2FormBean ods301eback = new Ods301e2FormBean();
        ods301eback.setOdsGroups(odsGroup);
        ods301eback.setOdsIdentity(odsIdentity);
        ods301eback.setOdsPackageTag(odsPackageTag);
        ods301eback.setOdsResourceFileExt(odsResourceFileExt);
        
        ods301eback.setMoreIdentityTag(0);
        ods301eback.setMoreResFileExt(0);
        ods301eback.setMorePkgTag(0);
        
        ModelAndView mav = new ModelAndView("ods301e/ods301e_noscript", "data", ods301eback);
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }
    
    
    
    @RequestMapping(value = "/noscript/", method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public ModelAndView getOds301ePage(@ModelAttribute Ods301e2FormBean ods301e, HttpServletRequest request) {
        
        log.debug("ods301e_2 groupId:" + ods301e.getGroupName());
        
        log.debug("ods301e_2 groupId:" + ods301e.getOrderByType());
        
        log.debug("ods301e_2 groupId:" + ods301e.getSelectedOdsIdentity());
        
        log.debug("ods301e_2 groupId:" + ods301e.getBtnCommand());

        
        Ods301e2FormBean ods301eback = new Ods301e2FormBean();
                
        int orderByType = ods301e.getOrderByType();
        ods301eback.setOrderByType(orderByType);
        
        String groupName = "";
        if(!CLEAR_GUP_NAME.equals(ods301e.getBtnCommand()))
        {
            groupName = ods301e.getGroupName();
            ods301eback.setGroupName(groupName);
        }
        
        List<String> selectedOdsIdentity = null;
        if(!CLEAR_SELECTED_IDENTITY.equals(ods301e.getBtnCommand()) && !CollectionUtils.isEmpty(ods301e.getSelectedOdsIdentity()))
        {
            selectedOdsIdentity = ods301e.getSelectedOdsIdentity();
            ods301eback.setSelectedOdsIdentity(selectedOdsIdentity); //??????
        }
        
        List<String> selectedOdsPackageTag = null;
        if(!CLEAR_SELECTED_PKG_TAG.equals(ods301e.getBtnCommand()) && !CollectionUtils.isEmpty(ods301e.getSelectedOdsPackageTag()))
        {
            selectedOdsPackageTag = ods301e.getSelectedOdsPackageTag();
            ods301eback.setSelectedOdsPackageTag(selectedOdsPackageTag); //??????
        }
        
        List<String> selectedOdsResourceFileExt = null;
        if(!CLEAR_SELECTED_RES_FILE_EXT.equals(ods301e.getBtnCommand()) && !CollectionUtils.isEmpty(ods301e.getSelectedOdsResourceFileExt()))
        {
            selectedOdsResourceFileExt = ods301e.getSelectedOdsResourceFileExt();
            ods301eback.setSelectedOdsResourceFileExt(ods301e.getSelectedOdsResourceFileExt()); //??????
        }

        List<OdsGroup> odsGroup = findGroup(groupName, selectedOdsPackageTag,
                selectedOdsResourceFileExt, selectedOdsIdentity, orderByType);
        ods301eback.setOdsGroups(odsGroup);

        
        
        List<String> odsIdentity = service.findAllIdentity();
        ods301eback.setOdsIdentity(odsIdentity);	//??????

        
        List<String> odsPackageTag = service.findAllPackageTag();
        ods301eback.setOdsPackageTag(odsPackageTag);	//??????

        
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        ods301eback.setOdsResourceFileExt(odsResourceFileExt);	//??????

        
        ods301eback.setMoreIdentityTag(ods301e.getMoreIdentityTag());
        if(MORE_IDENTITY_TAG.equals(ods301e.getBtnCommand()))
        {
            if(ods301e.getMoreIdentityTag() == 0){
                ods301eback.setMoreIdentityTag(1);
            } else {
                ods301eback.setMoreIdentityTag(0);
            }
        }     
        
        
        ods301eback.setMoreResFileExt(ods301e.getMoreResFileExt());
        if(MORE_RES_FILE_EXT.equals(ods301e.getBtnCommand()))
        {
            if(ods301e.getMoreResFileExt() == 0){
                ods301eback.setMoreResFileExt(1);
            } else {
                ods301eback.setMoreResFileExt(0);
            }
        }  
        
        ods301eback.setMorePkgTag(ods301e.getMorePkgTag());
        if(MORE_PKG_TAG.equals(ods301e.getBtnCommand()))
        {
            if(ods301e.getMorePkgTag() == 0){
                ods301eback.setMorePkgTag(1);
            } else {
                ods301eback.setMorePkgTag(0);
            }
        }  
        
        ModelAndView mav = new ModelAndView("ods301e/ods301e_noscript", "data", ods301eback);
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }
    
    
    @RequestMapping(value = "/noscript/{orderByType}/sort", method = RequestMethod.GET)
    public ModelAndView getOds301ePageSort(@PathVariable("orderByType") int orderByType, HttpServletRequest request) {

        List<OdsGroup> odsGroup = findGroup(null, null, null, null, orderByType);
        List<String> odsIdentity = service.findAllIdentity();
        List<String> odsPackageTag = service.findAllPackageTag();
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        Ods301eFormBean ods301eback = new Ods301eFormBean();
        ods301eback.setOdsGroups(odsGroup);
        ods301eback.setOdsIdentity(odsIdentity);
        ods301eback.setOdsPackageTag(odsPackageTag);
        ods301eback.setOdsResourceFileExt(odsResourceFileExt);
        
        ModelAndView mav = new ModelAndView("ods301e/ods301e_noscript", "data", ods301eback);
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }
}
