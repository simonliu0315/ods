/**
 * 
 */
package gov.sls.ods.web.controller;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.dto.Ods302eDto;
import gov.sls.ods.service.Ods302eService;
import gov.sls.ods.web.dto.Ods302e2FormBean;
import gov.sls.ods.web.dto.Ods302eFormBean;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

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
@RequestMapping("ODS302E")
public class Ods302eController {

    @Autowired
    private Ods302eService service;

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;

    protected String CLEAR_PKG_NAME = "clearPackageName";

    protected String CLEAR_SELECTED_PKG_TAG = "clearSelectedOdsPackageTag";

    protected String CLEAR_SELECTED_RES_FILE_EXT = "clearSelectedOdsResourceFileExt";

    protected String MORE_RES_FILE_EXT = "btnMoreResFileExt";

    protected String MORE_PKG_TAG = "btnMorePkgTag";

    /**
     * 針對關鍵字、分類標籤、檔案格式、分眾推廣群進行主題群組的搜尋
     * 
     * @param ods302e
     *            ods302e
     * @return Ods302eFormBean
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods302eFormBean query(@RequestBody Ods302eFormBean ods302e) {
        String packageName = ods302e.getPackageName();
        int orderByType = ods302e.getOrderByType();
        List<String> selectedOdsPackageTag = ods302e.getSelectedOdsPackageTag();
        List<String> selectedOdsResourceFileExt = ods302e.getSelectedOdsResourceFileExt();
        List<Ods302eDto> odsPackage = findPackage(packageName, selectedOdsPackageTag,
                selectedOdsResourceFileExt, orderByType);
        ods302e.setOdsPackages(odsPackage);
        return ods302e;
    }

    /**
     * 依據條件查詢主題列表
     * 
     * @param packageName
     *            主題名稱(將會用like查詢)
     * @param tagList
     *            標籤清單
     * @param fileExtList
     *            檔案格式清單
     * @param orderByType
     *            0排序依據更新時間，1排序依據熱門排行
     * @return List<OdsPackage>主題群組清單
     */
    public List<Ods302eDto> findPackage(String packageName, List<String> tagList,
            List<String> fileExtList, int orderByType) {
        return service.findPackage(packageName, tagList, fileExtList, orderByType);
    }

    /**
     * 頁面載入，取得全部主題群組、分類標籤、檔案格式、分眾推廣群
     * 
     * @param ods302e
     *            ods302e
     * @return Ods302eFormBean
     */
    @RequestMapping(value = "/initPage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods302eFormBean initPage(@RequestBody Ods302eFormBean ods302e) {
        List<Ods302eDto> odsPackage = findPackage(null, null, null, 0);
        List<String> odsPackageTag = service.findAllPackageTag();
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        Ods302eFormBean formBean = new Ods302eFormBean();
        formBean.setOdsPackages(odsPackage);
        formBean.setOdsPackageTag(odsPackageTag);
        formBean.setOdsResourceFileExt(odsResourceFileExt);
        log.info("user barcode:" + ((SlsUser) UserHolder.getUser()).getBarCode());
        return formBean;
    }

    /**
     * 頁面載入，取得全部主題群組、分類標籤、檔案格式、分眾推廣群
     * 
     * @return the page
     */
    @RequestMapping(value = "/{orderByType}/", method = RequestMethod.GET)
    public ModelAndView initPageOrderByType(@PathVariable("orderByType") String orderByType,
            HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("ods302e/ods302e", "orderByType", orderByType);
        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }

    @RequestMapping(value = "/noscript/", method = RequestMethod.GET)
    public ModelAndView getOds302ePage(HttpServletRequest request) {
        int orderByType = 0;
        List<Ods302eDto> odsPackage = findPackage(null, null, null, orderByType);
        List<String> odsPackageTag = service.findAllPackageTag();
        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        Ods302e2FormBean ods302eback = new Ods302e2FormBean();
        ods302eback.setOdsPackages(odsPackage);
        ods302eback.setOdsPackageTag(odsPackageTag);
        ods302eback.setOdsResourceFileExt(odsResourceFileExt);

        ods302eback.setMoreResFileExt(0);
        ods302eback.setMorePkgTag(0);

        ModelAndView mav = new ModelAndView("ods302e/ods302e_noscript", "data", ods302eback);
        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }

    @RequestMapping(value = "/noscript/", method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public ModelAndView getOds302ePage(@ModelAttribute Ods302e2FormBean ods302e,
            HttpServletRequest request) {

        log.debug("***packageName:" + ods302e.getPackageName());

        log.debug("***orderByType:" + ods302e.getOrderByType());

        log.debug("***btnCommand:" + ods302e.getBtnCommand());

        Ods302e2FormBean ods302eback = new Ods302e2FormBean();

        int orderByType = ods302e.getOrderByType();
        ods302eback.setOrderByType(orderByType);

        String packageName = "";
        if (!CLEAR_PKG_NAME.equals(ods302e.getBtnCommand())) {
            packageName = ods302e.getPackageName();
            ods302eback.setPackageName(packageName);
        }

        List<String> selectedOdsPackageTag = null;
        if (!CLEAR_SELECTED_PKG_TAG.equals(ods302e.getBtnCommand())
                && !CollectionUtils.isEmpty(ods302e.getSelectedOdsPackageTag())) {
            selectedOdsPackageTag = ods302e.getSelectedOdsPackageTag();
            ods302eback.setSelectedOdsPackageTag(selectedOdsPackageTag); // 勾勾
        }

        List<String> selectedOdsResourceFileExt = null;
        if (!CLEAR_SELECTED_RES_FILE_EXT.equals(ods302e.getBtnCommand())
                && !CollectionUtils.isEmpty(ods302e.getSelectedOdsResourceFileExt())) {
            selectedOdsResourceFileExt = ods302e.getSelectedOdsResourceFileExt();
            ods302eback.setSelectedOdsResourceFileExt(ods302e.getSelectedOdsResourceFileExt()); // 勾勾
        }

        List<Ods302eDto> odsPackage = findPackage(packageName, selectedOdsPackageTag,
                selectedOdsResourceFileExt, orderByType);
        ods302eback.setOdsPackages(odsPackage);

        List<String> odsPackageTag = service.findAllPackageTag();
        ods302eback.setOdsPackageTag(odsPackageTag); // 框框

        List<String> odsResourceFileExt = service.findAllResourceFileExt();
        ods302eback.setOdsResourceFileExt(odsResourceFileExt); // 框框

        ods302eback.setMoreResFileExt(ods302e.getMoreResFileExt());
        if (MORE_RES_FILE_EXT.equals(ods302e.getBtnCommand())) {
            if (ods302e.getMoreResFileExt() == 0) {
                ods302eback.setMoreResFileExt(1);
            } else {
                ods302eback.setMoreResFileExt(0);
            }
        }

        ods302eback.setMorePkgTag(ods302e.getMorePkgTag());
        if (MORE_PKG_TAG.equals(ods302e.getBtnCommand())) {
            if (ods302e.getMorePkgTag() == 0) {
                ods302eback.setMorePkgTag(1);
            } else {
                ods302eback.setMorePkgTag(0);
            }
        }

        ModelAndView mav = new ModelAndView("ods302e/ods302e_noscript", "data", ods302eback);
        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        return mav;
    }
}
