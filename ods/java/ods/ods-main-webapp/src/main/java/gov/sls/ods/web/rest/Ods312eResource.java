package gov.sls.ods.web.rest;

import gov.sls.ods.web.interceptor.MobileDeviceChecker;
import gov.sls.ods.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("ODS312E")
public class Ods312eResource {

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private WebUtil webUtil;

    /*
     * 主題列表API說明
     */
    @RequestMapping(value = "/detail/packages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ModelAndView findPackagesDetail(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView("ods312e/ods312e");
        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));

        String urlBase = webUtil.getUrlBase(request);
        String url = "ODS358I/packages";
        String url2 = "ODS358I/packages?packageName={packageName}&packageTags={packageTags}&resourceFileExts={resourceFileExts}";
        mav.addObject("url", urlBase + url);
        mav.addObject("url2", urlBase + url2);

        return mav;
    }


}
