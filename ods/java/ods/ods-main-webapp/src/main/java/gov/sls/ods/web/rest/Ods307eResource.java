package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.ods.Messages;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods307eDto;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS307E/rest")
public class Ods307eResource {

    @Autowired
    private Ods307eService ods307Service;
    
    @Autowired
    private AnonymousUserService anonymousUserService;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    //@PreAuthorize("hasAuthority('AUTHORITY_ODS307E')")
    @RequestMapping(value = "/{packageId}/{packageVer}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void createRate(@PathVariable("packageId") String packageId,
            @PathVariable("packageVer") int packageVer, @RequestBody Ods307eDto ods307eDto,
            Alerter alerter, HttpServletRequest request) {
        log.debug("***ods307Service***");
        log.info("packageId:"+packageId);
        SlsUser user = UserHolder.getUser();
        String userRole = String.valueOf(user.getRoles().get(0));

        //?????????????????????????????????????????????
        //1. ?????????????????????????????????????????????????????????????????????????????????????????????????????????
        //2. ???????????????????????????????????????????????????????????????????????????
        if (null != ods307eDto 
                && 0 < ods307eDto.getRate() 
                && ods307eDto.getRate() <= 5){
            //UAA????????????
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                if (packageId.equals(authority.getId())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
                odsUserPackageRate.setPackageId(packageId);
                odsUserPackageRate.setUserId(user.getId());
                odsUserPackageRate.setRate(ods307eDto.getRate());
                odsUserPackageRate.setIpAddress(request.getRemoteAddr());
                ods307Service.createRate(odsUserPackageRate, user, userRole);
                alerter.success(Messages.success_update());        
            } else {
                //???????????????
                if (anonymousUserService.isAnonymousUser(user)) {
                    //?????????????????? ??????????????????
                    // OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
                    // odsUserPackageRate.setPackageId(packageId);
                    // odsUserPackageRate.setUserId("00000");
                    // odsUserPackageRate.setRate(ods307eDto.getRate());
                    // odsUserPackageRate.setIpAddress(request.getRemoteAddr());
                    // ods307Service.createRate(odsUserPackageRate, user, userRole);
                    // alerter.success(Messages.success_update());
                    alerter.fatal(Messages.fatal_unauthorized());
                } else {
                    alerter.fatal(Messages.fatal_unauthorized());
                }
            }
        } else {
            alerter.fatal("????????????????????????????????????1~5???????????????!");
        }
    }
    
    @RequestMapping(value = "/find/{packageId}/{packageVer}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody OdsUserPackageRate find(@PathVariable("packageId") String packageId,
            @PathVariable("packageVer") int packageVer, @RequestBody Ods307eDto ods307eDto,
            Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        //OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
        //odsUserPackageRate.setPackageId(packageId);
        //odsUserPackageRate.setUserId(user.getId());
        alerter.success(Messages.success_find());
        //OdsUserPackageRate returnOdsUserPackageRate = ods307Service.findRate(odsUserPackageRate, user);
        if (anonymousUserService.isAnonymousUser(user)) {
            return new OdsUserPackageRate();
        } else {
            OdsUserPackageRate returnOdsUserPackageRate = ods307Service.findRateByPkgIdAndUserId(packageId, user.getId());        
            return returnOdsUserPackageRate == null ? new OdsUserPackageRate () : returnOdsUserPackageRate; 
        }
    }
    
    @RequestMapping(value = "/chkAnonymousUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean chkAnonymousUser(Alerter alerter) {
        SlsUser user = UserHolder.getUser();
        log.info("user:" + user);
        log.info("isAnonymousUser:" + anonymousUserService.isAnonymousUser(user));
        return anonymousUserService.isAnonymousUser(user);
    }
}
