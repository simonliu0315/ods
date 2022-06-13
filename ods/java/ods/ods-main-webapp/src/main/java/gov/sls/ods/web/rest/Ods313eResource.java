package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.ods.service.Ods313eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;
import gov.sls.ods.web.util.WebUtil;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.security.Authority;

@Slf4j
@Controller
@RequestMapping("ODS313E")
public class Ods313eResource {

    @Autowired
    private Ods313eService ods313eService;

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private WebUtil webUtil;
    
    /*
     * 特定主題API說明
     */
    @RequestMapping(value = "/detail/packageInfo/{packageId}/{packageVer}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ModelAndView packageInfoDetail(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVer") String packageVer, HttpServletRequest request) {
        
        //UAA權限控管
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(UserHolder.getUser().getId());
        boolean found = false;
        for (Authority authority: findResultList) {
            if (packageId.equals(authority.getId())) {
                found = true;
                break;
            }
        }
        if (!found && "/ods-main".equals(request.getContextPath())) {
            return new ModelAndView("404");
        }

        ModelAndView mav = new ModelAndView("ods313e/ods313e");
        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));

        String urlBase = webUtil.getUrlBase(request);
        String url1 = "ODS359I/package/versions/" + packageId;
        String url2 = "ODS359I/resources/" + packageId + "/" + packageVer;
        String url3 = "ODS359I/resourceDownloadList/" + packageId + "/" + packageVer;
        String url1Intro = "ODS359I/package/versions/{packageId}";
        String url2Intro = "ODS359I/resources/{packageId}/{packageVer}";
        String url3Intro = "ODS359I/resourceDownloadList/{packageId}/{packageVer}";
        mav.addObject("url1", urlBase + url1);
        mav.addObject("url2", urlBase + url2);
        mav.addObject("url3", urlBase + url3);
        mav.addObject("url1Intro", urlBase + url1Intro);
        mav.addObject("url2Intro", urlBase + url2Intro);
        mav.addObject("url3Intro", urlBase + url3Intro);
        
        String packageName = "";
        List<OdsPackage> pkg = ods313eService.getPackageById(packageId);
        if (!pkg.isEmpty()) {
            packageName = pkg.get(0).getName();
        }
        mav.addObject("packageName", packageName);        

        return mav;
    }

}
