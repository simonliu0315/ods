package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResourceCriteria;
import gov.sls.entity.ods.OdsUserFollowPackage;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.OdsResourceCriteriaEtxDto;
import gov.sls.ods.dto.TemplateDynamicGeneratorDto;
import gov.sls.ods.service.Ods305eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods305eDto;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS305E/rest")
@PreAuthorize("hasAuthority('AUTHORITY_ODS305E')")
public class Ods305eResource {

    @Autowired
    private Ods305eService ods305eService;
    
    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    @RequestMapping(value = "/{packageId}/{packageVer}", method = RequestMethod.GET)
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVer") int packageVer, HttpServletRequest request)
            throws UnsupportedEncodingException {
        return getPage(packageId, packageVer, null, null, request);
    }

    @RequestMapping(value = "/{packageId}/{packageVer}/{parentBreadLink64}", method = RequestMethod.GET)
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVer") int packageVer,
            @PathVariable("parentBreadLink64") String parentBreadLink64, HttpSession session, HttpServletRequest request)
            throws UnsupportedEncodingException {
        log.info("SessionId:" + session.getId());
        log.debug("****getPage****");
        SlsUser user = UserHolder.getUser();
        ModelAndView mav = new ModelAndView();
        
        //UAA權限控管
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
        boolean found = false;
        for (Authority authority: findResultList) {
            if (packageId.equals(authority.getId())) {
                found = true;
                break;
            }
        }
        if (!found) {
            return new ModelAndView("404");
        }
        
        Ods305eDto dto = new Ods305eDto();
        dto.setPackageId(packageId);
        dto.setPackageVer(packageVer);
        dto.setOdsResourceCriteriaList(ods305eService.getOdsResourceCriteria(
                packageId, packageVer));
        dto.setOdsUserFollowPackageList(ods305eService.getOdsUserFollowPackage(
                packageId, packageVer, user.getId()));
        for (OdsResourceCriteria odsResourceCriteria : dto
                .getOdsResourceCriteriaList()) {
            for (OdsUserFollowPackage odsUserFollowPackage : dto
                    .getOdsUserFollowPackageList()) {
                OdsResourceCriteriaEtxDto ext = new OdsResourceCriteriaEtxDto();
                if (odsResourceCriteria.getResourceId().equals(
                        odsUserFollowPackage.getResourceId())
                        && odsResourceCriteria.getId().equals(
                                odsUserFollowPackage.getResourceCriteriaId())) {
                    ext.setFollowUser(true);
                } else {
                    ext.setFollowUser(false);
                }
                ext.setOdsResourceCriteria(odsResourceCriteria);
            }
        }
        dto.setOdsUserFollowPackageList(ods305eService.getOdsUserFollowPackage(
                packageId, packageVer, user.getId()));
        log.debug("getOdsResourceCriteriaList:"
                + dto.getOdsResourceCriteriaList().size());
        log.debug("getOdsUserFollowPackageList:"
                + dto.getOdsUserFollowPackageList().size());
        log.debug("parentBreadLink64:" + parentBreadLink64);
        String decodeParentBreadLink = null;
        try {
            decodeParentBreadLink = new String(
                    Base64.decodeBase64(parentBreadLink64), "UTF-8");
        } catch (Exception e) {
            // 發生錯誤只顯示目前位置，不拋錯
        }
        mav.addObject("dynamicContentObjs", dto);
        mav.setViewName("ods305e/ods305e");
        //check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));
        setBreadcrumb(mav, decodeParentBreadLink);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String update(@RequestBody Ods305eDto ods305eDto,
            Alerter alerter, HttpServletRequest request) {
        log.info("Ods305eResource:" + ods305eDto.toString());
        SlsUser user = UserHolder.getUser();
        log.info("user:" + user);
        log.info("user:" + user);
        ods305eService.deleteUserFollowPackageByUserIdPackageId(user.getId(),
                ods305eDto.getPackageId());
        if (ods305eDto.getCriteriaSpecial() != null) {
            for (String special : ods305eDto.getCriteriaSpecial()) {
                OdsUserFollowPackage odsUserFollowPackage = new OdsUserFollowPackage();
                // odsUserFollowPackage.setId(UUID.randomUUID().toString());
                odsUserFollowPackage.setUserId(user.getId());
                odsUserFollowPackage.setPackageId(ods305eDto.getPackageId());
                odsUserFollowPackage.setCreated(new Date());
                odsUserFollowPackage.setCreateUserId(user.getId());
                odsUserFollowPackage.setUpdated(new Date());
                odsUserFollowPackage.setUpdateUserId(user.getId());
                odsUserFollowPackage.setUserRole(String.valueOf(user.getRoles().get(0)));
                odsUserFollowPackage.setIpAddress(request.getRemoteAddr());
                
                ods305eService.saveUserFollowPackage(odsUserFollowPackage);
            }
        }
        if (ods305eDto.getCriteriaNormal() != null) {
            for (String normal : ods305eDto.getCriteriaNormal()) {
                OdsUserFollowPackage odsUserFollowPackage = new OdsUserFollowPackage();
                // odsUserFollowPackage.setId(UUID.randomUUID().toString());
                odsUserFollowPackage.setUserId(user.getId());
                odsUserFollowPackage.setPackageId(ods305eDto.getPackageId());
                odsUserFollowPackage.setResourceId(normal.split(" ")[1]);
                odsUserFollowPackage
                        .setResourceCriteriaId(normal.split(" ")[2]);
                odsUserFollowPackage.setCreated(new Date());
                odsUserFollowPackage.setCreateUserId(user.getId());
                odsUserFollowPackage.setUpdated(new Date());
                odsUserFollowPackage.setUpdateUserId(user.getId());
                odsUserFollowPackage.setUserRole(String.valueOf(user.getRoles().get(0)));
                odsUserFollowPackage.setIpAddress(request.getRemoteAddr());

                ods305eService.saveUserFollowPackage(odsUserFollowPackage);
            }
        }

        alerter.success(Messages.success_update());
        return "AAAA";
    }

    @RequestMapping(value = "/findCheckBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<OdsUserFollowPackage> findCheckBox(
            @RequestBody Ods305eDto ods305eDto, Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        List<OdsUserFollowPackage> odsUserFollowPackageList = ods305eService
                .getOdsUserFollowPackage(ods305eDto.getPackageId(),
                        user.getId());
        if (odsUserFollowPackageList.size() > 0) {
            return odsUserFollowPackageList;
        } else {
            return ods305eService.getOdsUserFollowPackage(
                    ods305eDto.getPackageId(), ods305eDto.getPackageVer(),
                    user.getId());
        }

    }

    private ModelAndView setBreadcrumb(ModelAndView mav,
            String decodeParentBreadLink) {
        TemplateDynamicGeneratorDto dto = new TemplateDynamicGeneratorDto();

        List<String[]> funcInfo = new ArrayList<String[]>();
        log.debug("decodeParentBreadLink:" + decodeParentBreadLink);
        try {
            String[] decodeParentBreadLinkAry = decodeParentBreadLink.split(";");

            String[] levle1Ary = decodeParentBreadLinkAry[0].split(",");
            String[] levle2Ary = decodeParentBreadLinkAry[1].split(",");

            String encodeLevel1Str = decodeParentBreadLinkAry[0] + ";";
            encodeLevel1Str = Base64
                    .encodeBase64String(encodeLevel1Str
                            .getBytes("UTF-8"));
            log.debug("decodeParentBreadLink:" + levle1Ary[0]);
            if ("1".equals(levle1Ary[0])) {
                funcInfo.add(new String[] { "ODS301E", "主題群組列表查詢" });
                funcInfo.add(new String[] { "ODS301E/rest/" + levle1Ary[1], "主題清單" });
                funcInfo.add(new String[] {
                        "ODS303E/" + levle2Ary[0] + "/" + levle2Ary[1] + '/'
                                + encodeLevel1Str, "主題詳細資訊" });
                dto.setFuncInfo(funcInfo);
                mav.addObject("breadData", dto);
            }
            if ("2".equals(levle1Ary[0])) {
                funcInfo.add(new String[] { "ODS302E", "主題列表查詢" });
                funcInfo.add(new String[] {
                        "ODS303E/" + levle2Ary[0] + "/" + levle2Ary[1] + "/"
                                + encodeLevel1Str, "主題詳細資訊" });
                dto.setFuncInfo(funcInfo);
                mav.addObject("breadData", dto);
            }
            if ("3".equals(levle1Ary[0])) {
                funcInfo.add(new String[] {
                        "ODS303E/" + levle2Ary[0] + "/" + levle2Ary[1] , "主題詳細資訊" });
                dto.setFuncInfo(funcInfo);
                mav.addObject("breadData", dto);
            }

        } catch (Exception e) {
            // 發生錯誤只顯示目前位置，不拋錯
        }

        
        return mav;
    }

}
