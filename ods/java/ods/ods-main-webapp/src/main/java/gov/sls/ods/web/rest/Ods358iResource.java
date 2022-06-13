package gov.sls.ods.web.rest;

import gov.sls.ods.dto.Ods302eDto;
import gov.sls.ods.service.Ods302eService;
import gov.sls.ods.service.Ods358iService;
import gov.sls.ods.service.OdsApiLogService;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;
import gov.sls.ods.web.util.WebUtil;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

@Slf4j
@Controller
@RequestMapping("ODS358I")
public class Ods358iResource {

    @Autowired
    private Ods302eService ods302eService;

    @Autowired
    private Ods358iService ods358iService;

    @Autowired
    private OdsApiLogService odsApiLogService;

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private WebUtil webUtil;

    /*
     * 主題列表API – 列出現行所有主題資訊 GET METHOD
     */
    @RequestMapping(value = "/packages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject findPackages(
            @RequestParam(value = "packageName", required = false) String packageName,
            @RequestParam(value = "orderByType", required = false) String orderByType,
            @RequestParam(value = "packageTags", required = false) String packageTags,
            @RequestParam(value = "resourceFileExts", required = false) String resourceFileExts, 
            HttpServletRequest request) {

        Map<String, Object> finalObjMap = new LinkedHashMap<String, Object>();
        try {
            List<String> selectedOdsPackageTag = new ArrayList<String>();
            List<String> selectedOdsResourceFileExt = new ArrayList<String>();

            if (!Strings.isNullOrEmpty(packageName)) {
                packageName = URLDecoder.decode(packageName, "UTF-8");
                log.info("packageName:" + packageName);
            }

            int order = 0;
            if (!Strings.isNullOrEmpty(orderByType)) {
                order = Integer.parseInt(orderByType);
                log.info("orderByType:" + orderByType);
            }
            if (!Strings.isNullOrEmpty(packageTags)) {
                selectedOdsPackageTag = Arrays.asList(packageTags.split(","));
                log.info("packageTags:" + packageTags);
            }
            if (!Strings.isNullOrEmpty(resourceFileExts)) {
                selectedOdsResourceFileExt = Arrays.asList(resourceFileExts.split(","));
                log.info("resourceFileExts:" + resourceFileExts);
            }

            List<Ods302eDto> odsPackages = ods302eService.findPackage(packageName,
                    selectedOdsPackageTag, selectedOdsResourceFileExt, order);

            List<Map<String, Object>> resultList = ods358iService.getPackageResultList(odsPackages, webUtil.getUrlBase(request));
            // JSONArray resultJsonArray = (JSONArray) JSONSerializer.toJSON(resultList);
            if (!resultList.isEmpty()) {
                finalObjMap.put("status", "00");
            } else {
                finalObjMap.put("status", "01");
            }
            finalObjMap.put("result", resultList);

        } catch (Exception e) {
            finalObjMap.put("status", "99");
            finalObjMap.put("result", "");
        }
        odsApiLogService
                .createLog("001", request.getRemoteAddr(), new Date(), null, null, request.getRequestURL()
                        + "?" + request.getQueryString(), finalObjMap.get("status"));
        // log.info("JSONArray!!:" + objMap.get("result"));

        return (JSONObject) JSONSerializer.toJSON(finalObjMap);
    }

    /*
     * 主題列表API – 列出現行所有主題資訊 POST METHOD
     */
    // @RequestMapping(value = "/packages", method = RequestMethod.POST, consumes =
    // MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // public @ResponseBody JSONObject findPackages(@RequestBody Ods358iDto dto) {
    // String packageName = "";
    // int orderByType = 0;
    // List<String> selectedOdsPackageTag = new ArrayList<String>();
    // List<String> selectedOdsResourceFileExt = new ArrayList<String>();
    // log.info("POST!!!");
    // if (!Strings.isNullOrEmpty(dto.getPackageName())) {
    // packageName = dto.getPackageName();
    // log.info("packageName:" + dto.getPackageName());
    // }
    // if (!Strings.isNullOrEmpty(dto.getOrderByType())) {
    // orderByType = Integer.parseInt(dto.getOrderByType());
    // log.info("orderByType:" + dto.getOrderByType());
    // }
    // if (!Strings.isNullOrEmpty(dto.getPackageTags())) {
    // selectedOdsPackageTag = Arrays.asList(dto.getPackageTags().split(","));
    // log.info("packageTags:" + dto.getPackageTags());
    // }
    // if (!Strings.isNullOrEmpty(dto.getResourceFileExts())) {
    // selectedOdsResourceFileExt = Arrays.asList(dto.getResourceFileExts().split(","));
    // log.info("resourceFileExts:" + dto.getResourceFileExts());
    // }
    //
    // List<Ods302eDto> odsPackages = ods302eService.findPackage(packageName,
    // selectedOdsPackageTag, selectedOdsResourceFileExt, orderByType);
    //
    // List<Map<String, Object>> resultList = ods358iService.getPackageResultList(odsPackages);
    //
    // Map<String, Object> objMap = new LinkedHashMap<String, Object>();
    // if (!resultList.isEmpty()) {
    // JSONArray resultJsonArray = (JSONArray) JSONSerializer.toJSON(resultList);
    // objMap.put("success", true);
    // objMap.put("result", resultJsonArray);
    // } else {
    // objMap.put("success", false);
    // objMap.put("result", "");
    // }
    //
    // // log.info("JSONArray!!:" + objMap.get("result"));
    //
    // return (JSONObject) JSONSerializer.toJSON(objMap);
    // }

}
