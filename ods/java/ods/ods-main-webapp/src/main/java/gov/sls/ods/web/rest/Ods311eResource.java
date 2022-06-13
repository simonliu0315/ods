package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsUserPackageVersionClick;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.UserPackageRateAggregateDto;
import gov.sls.ods.repository.OdsUserPackageRateRepository;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods310eService;
import gov.sls.ods.service.Ods311eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods311eDto;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.security.Authority;
import com.cht.commons.security.Role;
import com.cht.commons.web.Alerter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Slf4j
@Controller
@RequestMapping("ODS311E")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS311E')")
public class Ods311eResource {

    @Autowired
    private Ods303eService ods303eService;

    @Autowired
    private Ods307eService ods307eService;
    
    @Autowired
    private Ods310eService ods310eService;
    
    @Autowired
    private Ods311eService ods311eService;
    
    @Autowired
    private AnonymousUserService anonymousUserService;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Autowired
    private OdsUserPackageRateRepository userPackageRateRepos;
    
    @RequestMapping(value = "/{packageId}/{packageVerStr}/{packageType}/{packageCode}", method = {
            RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("packageType") String packageType,
            @PathVariable("packageCode") String packageCode,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonParseException,
            JsonMappingException, UnsupportedEncodingException, IOException {
        try {
            return getPage(packageId, packageVerStr, null, packageType, packageCode, request, response);
        } catch (Exception e) {
            log.error("error:{}", e);
            return new ModelAndView("404");
        }
    }
    
    @RequestMapping(value = "/{packageId}/{packageVerStr}/{parentBreadLink64}/{packageType}/{packageCode}", method = {
            RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("parentBreadLink64") String parentBreadLink64,
            @PathVariable("packageType") String packageType,
            @PathVariable("packageCode") String packageCode,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonParseException,
            JsonMappingException, UnsupportedEncodingException, IOException {
        try {
            int packageVer = Integer.parseInt(packageVerStr);
            SlsUser user = UserHolder.getUser();
            log.debug("*************** ODS311E getPage  ****************");
            log.debug("packageId:" + packageId);
            log.debug("versionId:" + packageVer);
            log.debug("isPublish:"
                    + !ods303eService.isPackageOrResourcePublished(packageId,
                            packageVer, "", 0));
            log.debug("contextPath:" + request.getContextPath());
            log.debug("parentBreadLink64:" + parentBreadLink64);

            //test s
            //user.setUserType("B");
            //user.setUserId("B_00007102_00007102");//社福團體id,中間統編可以自行更換
            
            boolean isAnonymousUser = anonymousUserService.isAnonymousUser(user);
            log.info("isAnonymousUser:" + anonymousUserService.isAnonymousUser(user));

            //UAA權限控管
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
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
            
            if (!ods303eService.isPackageOrResourcePublished(packageId, packageVer,
                    "", 0) && "/ods-main".equals(request.getContextPath())) {
                return new ModelAndView("404");
            }
            boolean isSub = !ods311eService.getIndPackageSub(packageCode, user.getId()).isEmpty();//主題訂閱狀況
            
            log.info(user.getRoles().get(0) + ", is rn:" + "RN".equals(user.getRoles().get(0)) + ", role.id:" + user.getRoles().get(0).getId() + ",isRNRole:" + isRNRole(user.getRoles()));
            log.info(user.getRoles().get(0) + ", is rs:" + "RS".equals(user.getRoles().get(0)) + ", role.id:" + user.getRoles().get(0).getId() + ",isRSRole:" + isRSRole(user.getRoles()));
            log.info(user.getRoles().get(0) + ", is rb:" + "RB".equals(user.getRoles().get(0)) + ", role.id:" + user.getRoles().get(0).getId() + ",isRBRole:" + isRBRole(user.getRoles()));

            //訂閱、角色檢核 移到 各自的個別化主題 去檢核。
            ModelAndView mav = new ModelAndView();
//            if (isAnonymousUser //local測試用 未登入也可進入
            if (!isAnonymousUser) {//已登入
                mav.setViewName("/ods/package/html/individual/" + packageCode);
                List<UserPackageRateAggregateDto> aggregateList = userPackageRateRepos
                        .getAggregateByPackageId(packageId);
                if (aggregateList.size() == 0) {
                    mav.addObject("showStars", BigDecimal.ZERO);
                } else {
                    mav.addObject("showStars", aggregateList.get(0).getRateAvg());
                }
                if (aggregateList.size() == 0) {
                    mav.addObject("showScore", BigDecimal.ZERO);
                } else {
                    mav.addObject("showScore", aggregateList.get(0).getRateCount());
                }
                mav.addObject("isAnonymousUser", "N");
                mav.addObject("role", "");
                if(isRSRole(user.getRoles()) || "S".equals(user.getUserType())){
                    mav.addObject("role", "RS");
                } else if(isRNRole(user.getRoles())){
                    mav.addObject("role", "RN");
                } else if(isRBRole(user.getRoles()) || "B".equals(user.getUserType())){
                    mav.addObject("role", "RB");
                }
                mav.addObject("isSub", isSub);
            } else if (isAnonymousUser) {
                //您尚未登入系統
                mav.setViewName("ods311e/ods311e");
                mav.addObject("isAnonymousUser", "Y");
                mav.addObject("role", "");
                
            }
            
            mav.addObject("packageId", packageId);
            mav.addObject("packageVer", packageVer);
            mav.addObject("packageType", packageType);
            mav.addObject("packageCode", packageCode);
            mav.addObject("parentBreadLink64", parentBreadLink64);
            //check device
            mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));  
            
            OdsPackageVersion odsPackageVersion = ods311eService.getPackageVersion(packageId, String.valueOf(packageVer));
            mav.addObject("packageName", odsPackageVersion.getName());
            mav.addObject("packageDescription", odsPackageVersion.getDescription());
            log.info("mav packageId:" + packageId);
            log.info("mav packageVer:" + packageVer);
            log.info("mav packageType:" + packageType);
            log.info("mav packageCode:" + packageCode);
            log.info("mav parentBreadLink64:" + parentBreadLink64);
            log.info("isMobileDevice:" + mobileDeviceChecker.isMobileDevice(request));
            log.info("packageName:" + odsPackageVersion.getName());
            log.info("packageDescription:" + odsPackageVersion.getDescription());
            
            OdsUserPackageVersionClick odsUserPackageVersionClick = new OdsUserPackageVersionClick();
            // odsUserPackageVersionClick.setId(UUID.randomUUID().toString());
            odsUserPackageVersionClick.setUserId(user.getId());
            odsUserPackageVersionClick.setPackageId(packageId);
            odsUserPackageVersionClick.setPackageVer(packageVer);
            odsUserPackageVersionClick.setIpAddress(request.getRemoteAddr());
            odsUserPackageVersionClick.setCreated(new Date());
            odsUserPackageVersionClick.setCreateUserId(user.getId());
            odsUserPackageVersionClick.setUpdated(new Date());
            odsUserPackageVersionClick.setUpdateUserId(user.getId());
            if (!user.getRoles().isEmpty()) {
                odsUserPackageVersionClick.setUserRole(String.valueOf(user
                        .getRoles().get(0)));
            }
            ods307eService.createClick(odsUserPackageVersionClick, user);
            
            return mav;
        } catch (Exception e) {
            log.error("error:{}", e);
            return new ModelAndView("404");
        }
    }
    
    private boolean isRBRole(List<? extends Role> roles){
        for(Role role : roles){
            if("RB".equals(role.getId())){
                return true;
            }
        }
        return false;
    }
    
    private boolean isRSRole(List<? extends Role> roles){
        for(Role role : roles){
            if("RS".equals(role.getId())){
                return true;
            }
        }
        return false;
    }
    
    private boolean isRNRole(List<? extends Role> roles){
        for(Role role : roles){
            if("RN".equals(role.getId())){
                return true;
            }
        }
        return false;
    }
    
    @RequestMapping(value = "/initPage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods311eDto initPage(@RequestBody Ods311eDto dto) {
        log.info("ODS311E packageId:" + dto.getPackageId());
        log.info("ODS311E packageVer:" + dto.getPackageVer());
        dto.setOdsPackageVersion(ods311eService.getPackageVersion(dto.getPackageId(), dto.getPackageVer()));
        
        return dto;
    }

    @RequestMapping(value = "/createSub/{packageId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void createIndividePackageSub(@PathVariable("packageId") String packageId,
            Alerter alerter, HttpServletRequest request) {
        try {
            log.info("***ods311Service***");
            SlsUser user = UserHolder.getUser();
            
            //UAA權限控管
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                if (packageId.equals(authority.getId())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                String ipAddress = request.getRemoteAddr();
                ods311eService.createIndividePackageSub(packageId, ipAddress);
                alerter.success(Messages.success_create());        
            } else {
                alerter.fatal(Messages.fatal_unauthorized());
            }
        } catch(Exception e){
            log.error("error in createIndividePackageSub:{}", e);
        }
        
        
    }
    
    @RequestMapping(value = "/chkAnonymousUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean chkAnonymousUser(Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        boolean isAnonymousUser = anonymousUserService.isAnonymousUser(user);
        log.info("user:" + user);
        log.info("isAnonymousUser:" + isAnonymousUser);
        return isAnonymousUser;
    }

}
