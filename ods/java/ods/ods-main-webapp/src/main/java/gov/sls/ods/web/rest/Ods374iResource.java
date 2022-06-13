package gov.sls.ods.web.rest;

import java.util.Date;

import gov.sls.commons.core.security.api.AccountType;
import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.commons.core.security.api.ValidateCode;
import gov.sls.ods.dto.Ods374iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods374iService;
import gov.sls.ods.service.OdsApiLogService;
import gov.sls.ods.util.BanMask;
import gov.sls.ods.web.util.AbnormalResourceAccessUtil;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("ODS374I")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS374I')")
public class Ods374iResource {

    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods374iService ods374iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS374_USERTYPE = "B";
    final static String ODS374_PACKAGE_CODE = "O1504@ALL";
    
    final static String DATE_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])*$";
    
    /**營業人個別化主題-營業人電子發票統計查詢。查詢營業人自己開立之電子發票統計資料。
     * @param version      版本號碼(帶入範例值即可)
     * @param appID        APPID(APPID申請者統編需要等於營業人登入統一編號)
     * @param ban          營業人登入統一編號(僅可查詢APPID申請者自身營業人資料，故需與APPID申請者統編相同)
     * @param account      營業人登入帳號
     * @param password     營業人登入密碼
     * @param invoiceDateS 發票年月起
     * @param invoiceDateE 發票年月迄
     * @param request
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject query(
            @RequestParam(value ="version", required = false) String version,
            @RequestParam(value ="appID", required = false) String appID,
            @RequestParam(value ="ban", required = false) String ban,
            @RequestParam(value ="account", required = false) String account,
            @RequestParam(value ="password", required = false) String password,
            @RequestParam(value ="invoiceDateS", required = false) String invoiceDateS,
            @RequestParam(value ="invoiceDateE", required = false) String invoiceDateE,
            HttpServletRequest request) {
        Ods374iQueryDto ods374iQueryDto = new Ods374iQueryDto();
        ods374iQueryDto.setV(version);
        ods374iQueryDto.setInvoiceDateS(invoiceDateS);
        ods374iQueryDto.setInvoiceDateE(invoiceDateE);
        ods374iQueryDto.setBan(ban);
        if ( ! AbnormalResourceAccessUtil.isAbnormalAccess(request, appID) ) {
            ValidateCode validateAccount = userValidator.validateAccount(AccountType.B, ban, account, password);
            if(version == null || version.equals("") || appID == null || appID.equals("") 
                    || account == null || account.equals("") || password == null || password.equals("") 
                    || invoiceDateS == null || invoiceDateS.equals("") || invoiceDateE == null || invoiceDateE.equals("")) {
                ods374iQueryDto.setCode("903");
                ods374iQueryDto.setMsg("參數錯誤");
            }else if(!invoiceDateS.matches(DATE_FORMATE_PATTERN) || !invoiceDateE.matches(DATE_FORMATE_PATTERN)
                    || !ods374iService.dateVerify(invoiceDateS, invoiceDateE)){
                ods374iQueryDto.setCode("903");
                ods374iQueryDto.setMsg("參數錯誤");
            }else if(!apiValidator.validationApiId(appID)) {
                ods374iQueryDto.setCode("998");
                ods374iQueryDto.setMsg("APPID錯誤");
            }else if(!apiValidator.validationAppIdAndBan(appID, ban)) {
                ods374iQueryDto.setCode("998");
                ods374iQueryDto.setMsg("APPID申請者統編與傳入BAN不符");
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else if(!ValidateCode.ACCOUNT_SUCCESS.equals(validateAccount)) {
                ods374iQueryDto.setCode("999");
                ods374iQueryDto.setMsg(validateAccount.getMsg());//直接抓validateCode
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else {
                ods374iQueryDto = ods374iService.query(ods374iQueryDto);
                if(ods374iQueryDto.getDetails().size()==0) {
                    ods374iQueryDto.setCode("901");
                    ods374iQueryDto.setMsg("查無資料");
                }else {
                    ods374iQueryDto.setCode("200");
                    ods374iQueryDto.setMsg("執行成功");
                }
            }
        } else {
            ods374iQueryDto.setCode("998");
            ods374iQueryDto.setMsg("異常登入次數超過5次，請24小時後再登入");
        }
        
        // 產生 API log
        String userId = ODS374_USERTYPE + "_" + ban  + "_" + account;
        String packageId = odsPackageRepository.findIdByCode(ODS374_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("007", request.getRemoteAddr(), new Date(), packageId, packageVer,
                        request.getQueryString(), ods374iQueryDto.getCode(), appID, userId);
        
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.setExcludes(new String[] {"ban"});
        return (JSONObject) JSONSerializer.toJSON(ods374iQueryDto, jsonConfig);
    }
    
}
