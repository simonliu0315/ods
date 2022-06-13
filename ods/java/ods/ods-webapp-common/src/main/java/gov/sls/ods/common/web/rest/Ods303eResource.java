package gov.sls.ods.common.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsUserPackageVersionClick;
import gov.sls.entity.ods.OdsUserPackageVersionDownload;
import gov.sls.entity.ods.OdsUserPackageVersionShare;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.TemplateDynamicGeneratorDto;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods310eService;
//import gov.sls.ods.service.RedirectView;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.commons.web.controller.ExceptionHandlingControllerAdvice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;








import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;

@Slf4j
@Controller
@RequestMapping("ODS303E")
@PreAuthorize("hasAuthority('AUTHORITY_ODS303E')")
public class Ods303eResource {

    @Autowired
    private Ods303eService ods303eService;

    @Autowired
    private Ods307eService ods307eService;
    
    @Autowired
    private Ods310eService ods310eService;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private AnonymousUserService anonymousUserService;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @RequestMapping(value = "/{packageId}/{packageVerStr}", method = {
            RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonParseException,
            JsonMappingException, UnsupportedEncodingException, IOException {
        try {
            return getPage(packageId, packageVerStr, null, request, response);
        } catch (Exception e) {
            log.error("error:{}", e);
            return new ModelAndView("404");
        }
    }

    @RequestMapping(value = "/{packageId}/{packageVerStr}/{parentBreadLink64}", method = {
            RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getPage(@PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("parentBreadLink64") String parentBreadLink64,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonParseException,
            JsonMappingException, UnsupportedEncodingException, IOException {
        try {
            boolean packageCookie = false;

            //判斷是否為第一次登入,或超過30天沒登入
            Cookie cookies[] = request.getCookies() ;
            if (cookies != null) {
                for (Cookie cookie : cookies){
                    log.info( "cookie name : " + cookie.getName() + "    " ) ;
                    log.info( "cookie value :" + cookie.getValue() );
                    if (("visitedPackage").equals(cookie.getName())){
                        packageCookie = true;
                        break;
                    }
                }
            }
            
            if (!packageCookie) {
                //創建Cookie
                Cookie c = new Cookie("visitedPackage", packageId) ;
                
                c.setHttpOnly(true);
                c.setSecure(true);
                // 設定有效時間以s為單位
                c.setMaxAge(60*60*24*30); //30天
                //c.setMaxAge(60*5);
                //設置Cookie 路徑和域名
                c.setPath( "/" ) ;
                //c.setDomain( ".cht.tw" ) ; // 域名要以“.” 開頭
                //發送Cookie 文件
                response.addCookie(c) ;
            }
          
            int packageVer = Integer.parseInt(packageVerStr);
            //Modification by Often
            //增加版本0時 redir 至最新版本maxVer
            int packageVerIsZero = 0;
            if (packageVer == packageVerIsZero) {
                Integer maxVer = ods303eService.queryMaxVer(packageId);
                String systemId = propertiesAccessor.getProperty("systemId");
                log.info("*************** ODS303E queryMaxVer ****************");
                log.info("maxVer" + maxVer);
                ModelAndView mav = new ModelAndView();

                if (maxVer != null) {
                    RedirectView rv = new RedirectView("/" + systemId + "/ODS303E/" + packageId
                            + "/" + maxVer + "/" + parentBreadLink64);

                    rv.setHttp10Compatible(false);
                    mav.setView(rv);
                    return mav;
                } else {
                    mav.setViewName("404");// new ModelAndView("404");
                    return mav;
                }

            }
            //Modification by Often
            SlsUser user = UserHolder.getUser();
            log.debug("*************** ODS303E getPage  ****************");
            log.debug("packageId:" + packageId);
            log.debug("versionId:" + packageVer);
            log.debug("isPublish:"
                    + !ods303eService.isPackageOrResourcePublished(packageId,
                            packageVer, "", 0));
            log.debug("contextPath:" + request.getContextPath());
            log.debug("parentBreadLink64:" + parentBreadLink64);
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
            ModelAndView mav = new ModelAndView();
            TemplateDynamicGeneratorDto dto = ods303eService
                    .getDynamicTemplateObject(packageId, packageVer);
            String decodeParentBreadLink = null;
            try {
                decodeParentBreadLink = new String(
                        Base64.decodeBase64(parentBreadLink64), "UTF-8");
            } catch (Exception e) {
                // 發生錯誤只顯示目前位置，不拋錯
            }
            dto.setParentBreadLink(parentBreadLink64);
            dto = setBreadLink(dto, decodeParentBreadLink, packageId, packageVer);
            mav.addObject("dynamicContentObjs", dto);
            mav.setViewName("/ods/public/package/" + packageId + "/html/"
                    + packageId + "-" + packageVer);
            mav = setBreadcrumb(mav, decodeParentBreadLink);
            //check device
            mav.addObject("isMobileDevice", isMobileDevice(request));
            //check cookie
            mav.addObject("packageCookie", packageCookie);            
            log.info("mav packageCookie:" + packageCookie);            
            
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


    private ModelAndView setBreadcrumb(ModelAndView mav,
            String decodeParentBreadLink) {
        TemplateDynamicGeneratorDto dto = new TemplateDynamicGeneratorDto();

        List<String[]> funcInfo = new ArrayList<String[]>();

        try {
            String[] decodeParentBreadLinkAry = decodeParentBreadLink
                    .split(";");
            String[] levle1Ary = decodeParentBreadLinkAry[0].split(",");

            if ("1".equals(levle1Ary[0])) {
                funcInfo.add(new String[] { "ODS301E", "主題群組列表查詢" });
                funcInfo.add(new String[] { "ODS301E/rest/" + levle1Ary[1],
                        "主題清單" });
                dto.setFuncInfo(funcInfo);
                mav.addObject("breadData", dto);
            }
            if ("2".equals(levle1Ary[0])) {
                funcInfo.add(new String[] { "ODS302E", "主題列表查詢" });
                dto.setFuncInfo(funcInfo);
                mav.addObject("breadData", dto);
            }
        } catch (Exception e) {
            // 發生錯誤只顯示目前位置，不拋錯
        }

        return mav;
    }

    private TemplateDynamicGeneratorDto setBreadLink(
            TemplateDynamicGeneratorDto dto, String decodeParentBreadLink,
            String packageId, int packageVer) {
        String url = null;
        if (decodeParentBreadLink == null) {
            url = "3;" + packageId + ',' + packageVer + ';';
        } else {
            url = decodeParentBreadLink + packageId + ',' + packageVer + ';';
        }

        try {
            dto.setBreadLink(Base64.encodeBase64String(url.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

        return dto;
    }

    @PreAuthorize("hasAuthority('AUTHORITY_ODS303E')")
    /*
     * @RequestMapping(value = "/create_share_record", method =
     * RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces
     * = MediaType.APPLICATION_JSON_VALUE) public @ResponseBody void
     * createRate(@RequestBody Ods303eDto ods303eDto, Alerter alerter,
     * HttpServletRequest request) {
     */
    @RequestMapping(value = "/create_share_record/{packageId}/{packageVerStr}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void createShareRecord(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr,
            @PathVariable("type") String type, Alerter alerter,
            HttpServletRequest request) {
        try {
            int packageVer = Integer.parseInt(packageVerStr);
            log.debug("***ods303Service***");
            SlsUser user = UserHolder.getUser();

            OdsUserPackageVersionShare odsUserPackageVersionShare = new OdsUserPackageVersionShare();
            odsUserPackageVersionShare.setUserId(user.getId());
            odsUserPackageVersionShare.setPackageId(packageId);
            odsUserPackageVersionShare.setPackageVer(packageVer);
            odsUserPackageVersionShare.setIpAddress(request.getRemoteAddr());
            odsUserPackageVersionShare.setCreated(new Date());
            odsUserPackageVersionShare.setCreateUserId(user.getId());
            odsUserPackageVersionShare.setUpdated(new Date());
            odsUserPackageVersionShare.setUpdateUserId(user.getId());
            if (!user.getRoles().isEmpty()) {
                odsUserPackageVersionShare.setUserRole(String.valueOf(user
                        .getRoles().get(0)));
            }
            if ("facebook".equals(type)) {
                odsUserPackageVersionShare.setShareTarget("01");
            } else if ("google".equals(type)) {
                odsUserPackageVersionShare.setShareTarget("02");
            } else if ("twitter".equals(type)) {
                odsUserPackageVersionShare.setShareTarget("03");
            }

            ods307eService.createShare(odsUserPackageVersionShare, user);

            // log.debug("odsUserPackageVersionShare:" +
            // ToStringBuilder.reflectionToString(odsUserPackageVersionShare));
        } catch (Exception e) {
            log.error("error:{}", e);
        }
        
    }

    @PreAuthorize("hasAuthority('AUTHORITY_ODS303E')")
    /*
     * @RequestMapping(value = "/create_share_record", method =
     * RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces
     * = MediaType.APPLICATION_JSON_VALUE) public @ResponseBody void
     * createRate(@RequestBody Ods303eDto ods303eDto, Alerter alerter,
     * HttpServletRequest request) {
     */
    @RequestMapping(value = "/create_download_record/{packageId}/{packageVerStr}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void createDownloadRecord(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVerStr, Alerter alerter,
            HttpServletRequest request) {
        try {
            int packageVer = Integer.parseInt(packageVerStr);
            log.debug("***ods303Service***");
            SlsUser user = UserHolder.getUser();

            log.debug("odsUserPackageVersionShare:" + packageId);
            log.debug("odsUserPackageVersionShare:" + packageVer);

            OdsUserPackageVersionDownload odsUserPackageVersionDownload = new OdsUserPackageVersionDownload();
            odsUserPackageVersionDownload.setUserId(user.getId());
            odsUserPackageVersionDownload.setPackageId(packageId);
            odsUserPackageVersionDownload.setPackageVer(packageVer);
            odsUserPackageVersionDownload.setIpAddress(request.getRemoteAddr());
            odsUserPackageVersionDownload.setCreated(new Date());
            odsUserPackageVersionDownload.setCreateUserId(user.getId());
            odsUserPackageVersionDownload.setFormat("pdf");
            
            if (!user.getRoles().isEmpty()) {
                odsUserPackageVersionDownload.setUserRole(String.valueOf(user
                        .getRoles().get(0)));
            }
            ods307eService.createDownload(odsUserPackageVersionDownload, user);

            // log.debug("odsUserPackageVersionShare:" +
            // ToStringBuilder.reflectionToString(odsUserPackageVersionShare));
        } catch (Exception e) {
            log.error("error:{}", e);
        }
        
    }
    
    private boolean isMobileDevice(HttpServletRequest request) {
        if (!Strings.isNullOrEmpty(request.getHeader("User-Agent"))) {
            String ua = request.getHeader("User-Agent").toLowerCase();
            log.debug("User-Agent = " + ua);
            // 不偵測無障礙檢測軟體freego送來的User-Agent (java/1.5.xxx);
            if (ua.indexOf("java/") >= 0) {
                return false;
            }

            if (ua.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/" +
                "|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|" +
                "iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|" +
                "palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|" +
                "treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*")
                || ua.substring(0, 4).matches(
                    "(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|" +
                    "abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|" +
                    "aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|" +
                    "bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|" +
                    "cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|" +
                    "dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|" +
                    "er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|" +
                    "gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|" +
                    "hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|" +
                    "hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|" +
                    "im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|" +
                    "klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])" +
                    "|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)" +
                    "|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|" +
                    "t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|" +
                    "n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|" +
                    "nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|" +
                    "pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|" +
                    "prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|" +
                    "r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|" +
                    "sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|" +
                    "shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|" +
                    "sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|" +
                    "tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|" +
                    "tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|" +
                    "\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )" +
                    "|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
                return true;
            }
        }
        return false;
    }
}
