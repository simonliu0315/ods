package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.ods.service.Ods359iService;
import gov.sls.ods.service.OdsApiLogService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;
import gov.sls.ods.web.util.WebUtil;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.security.Authority;

@Slf4j
@Controller
@RequestMapping("ODS359I")
public class Ods359iResource {

    @Autowired
    private Ods359iService ods359iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private WebUtil webUtil;

    
    /*
     * 特定主題發佈版本列表API – 列出特定主題的已發佈版本資訊
     */
    @RequestMapping(value = "/package/versions/{packageId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject findPackageVersions(@PathVariable("packageId") String packageId, 
            HttpServletRequest request) {
        
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
            Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
            odsApiLogService
            .createLog("002", request.getRemoteAddr(), new Date(),null , null, request.getRequestURL()
                    + "?" + request.getQueryString(), finalObjMap.get("status"));
            return (JSONObject) JSONSerializer.toJSON(finalObjMap);
        }

        Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
        try {
            List<Map<String, Object>> resultList = ods359iService
                    .getPackageVersionsResultList(packageId);

            Map<String, Object> objMap = new LinkedHashMap<String, Object>();
            objMap.put("packageId", packageId);
            objMap.put("publishedVersions", resultList);

            if (!resultList.isEmpty()) {
                finalObjMap.put("status", "00");
            } else {
                finalObjMap.put("status", "01");
            }
            finalObjMap.put("result", objMap);

            // log.info("JSONArray!!:" + objMap.get("result"));

        } catch (Exception e) {
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
        }
        odsApiLogService
        .createLog("002", request.getRemoteAddr(), new Date(),packageId , null, request.getRequestURL()
                + "?" + request.getQueryString(), finalObjMap.get("status"));
        return (JSONObject) JSONSerializer.toJSON(finalObjMap);
    }

    /*
     * 特定主題版本所有素材列表API – 列出特定主題下之個別版本之所有素材資訊
     */
    @RequestMapping(value = "/resources/{packageId}/{packageVer}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject findPackageResources(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVer") String packageVer, 
            HttpServletRequest request) {
        
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
            Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
            odsApiLogService
            .createLog("003", request.getRemoteAddr(), new Date(),null , null, request.getRequestURL()
                    + "?" + request.getQueryString(), finalObjMap.get("status"));
            return (JSONObject) JSONSerializer.toJSON(finalObjMap);
        }

        Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
        try {
            String urlBase = webUtil.getUrlBase(request);
            
            List<Map<String, Object>> resultList = ods359iService.getPackageResourcesResultList(
                    packageId, packageVer, urlBase);

            Map<String, Object> objMap = new LinkedHashMap<String, Object>();
            objMap.put("packageId", packageId);
            objMap.put("packageVer", packageVer);
            objMap.put("resources", resultList);

            if (!resultList.isEmpty()) {
                finalObjMap.put("status", "00");
            } else {
                finalObjMap.put("status", "01");
            }
            finalObjMap.put("result", objMap);

            // log.info("JSONArray!!:" + objMap.get("result"));

        } catch (Exception e) {
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
        }
        odsApiLogService
        .createLog("003", request.getRemoteAddr(), new Date(), packageId, Integer.valueOf(packageVer), request.getRequestURL()
                + "?" + request.getQueryString(), finalObjMap.get("status"));
        return (JSONObject) JSONSerializer.toJSON(finalObjMap);
    }

    /*
     * 特定主題版本素材下載路徑API – 列出特定主題下之個別版本之所有素材下載路徑資訊
     */
    @RequestMapping(value = "/resourceDownloadList/{packageId}/{packageVer}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject findResourceDownloadList(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVer") String packageVer, 
            HttpServletRequest request) {
        
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
            Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
            return (JSONObject) JSONSerializer.toJSON(finalObjMap);
        }

        Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
        try {
            String urlBase = webUtil.getUrlBase(request);
            
            List<Map<String, Object>> resultList = ods359iService.getResourcesDownloadResultList(
                    packageId, packageVer, urlBase);

            Map<String, Object> objMap = new LinkedHashMap<String, Object>();
            objMap.put("packageId", packageId);
            objMap.put("packageVer", packageVer);
            objMap.put("resourcesDownloadList", resultList);

            if (!resultList.isEmpty()) {
                finalObjMap.put("status", "00");
            } else {
                finalObjMap.put("status", "01");
            }
            finalObjMap.put("result", objMap);

            // log.info("JSONArray!!:" + objMap.get("result"));

        } catch (Exception e) {
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
        }

        return (JSONObject) JSONSerializer.toJSON(finalObjMap);
    }

}
