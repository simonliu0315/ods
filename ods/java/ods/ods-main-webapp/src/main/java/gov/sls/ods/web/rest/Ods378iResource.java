package gov.sls.ods.web.rest;

import java.util.Date;

import gov.sls.commons.core.security.api.AccountType;
import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.commons.core.security.api.ValidateCode;
import gov.sls.ods.dto.Ods378iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods378iService;
import gov.sls.ods.service.OdsApiLogService;
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
@RequestMapping("ODS378I")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS378I')")
public class Ods378iResource {

    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods378iService ods378iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS378_USERTYPE = "B";
    final static String ODS378_PACKAGE_CODE = "O1504@ALL";
    
    final static String YEAR_MONTH_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}(0[1-9]|1[012])$";
    
    /** 營業人個別化主題-營業人營業稅統計查詢。查詢營業人自己之營業稅發票申購張數、銷項總計金額統計資料。
     * @param version  版本號碼(帶入範例值即可)
     * @param appID    APPID(APPID申請者統編需要等於營業人登入統一編號)
     * @param ban      營業人登入統一編號(僅可查詢APPID申請者自身營業人資料，故需與APPID申請者統編相同)
     * @param account  營業人登入帳號
     * @param password 營業人登入密碼
     * @param blYmS    所屬年月起
     * @param blYmE    所屬年月迄
     * @param request
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject query(
            @RequestParam(value ="version", required = false) String version,
            @RequestParam(value ="appID", required = false) String appID,
            @RequestParam(value ="ban", required = false) String ban,
            @RequestParam(value ="account", required = false) String account,
            @RequestParam(value ="password", required = false) String password,
            @RequestParam(value ="blYmS", required = false) String blYmS,
            @RequestParam(value ="blYmE", required = false) String blYmE,
            HttpServletRequest request) {
//        ban = BanMask.getInstance().process(ban); // ban 加密
        Ods378iQueryDto ods378iQueryDto = new Ods378iQueryDto();
        ods378iQueryDto.setV(version);
        ods378iQueryDto.setBan(ban);
        ods378iQueryDto.setBlYmS(blYmS);
        ods378iQueryDto.setBlYmE(blYmE);
        if ( ! AbnormalResourceAccessUtil.isAbnormalAccess(request, appID) ) {
            ValidateCode validateAccount = userValidator.validateAccount(AccountType.B, ban, account, password);
            if(version == null || version.equals("") || appID == null || appID.equals("") 
                    || account == null || account.equals("") || password == null || password.equals("") 
                    || blYmS == null || blYmS.equals("") || blYmE == null || blYmE.equals("")) {
                ods378iQueryDto.setCode("903");
                ods378iQueryDto.setMsg("參數錯誤");
            }else if(!blYmS.matches(YEAR_MONTH_FORMATE_PATTERN) || !blYmE.matches(YEAR_MONTH_FORMATE_PATTERN)
                    || !ods378iService.yearMonthVerify(blYmS, blYmE)){
                ods378iQueryDto.setCode("903");
                ods378iQueryDto.setMsg("參數錯誤");
            }else if(!apiValidator.validationApiId(appID)) {
                ods378iQueryDto.setCode("998");
                ods378iQueryDto.setMsg("APPID錯誤");
            }else if(!apiValidator.validationAppIdAndBan(appID, ban)) {
                ods378iQueryDto.setCode("998");
                ods378iQueryDto.setMsg("APPID申請者統編與傳入BAN不符");
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else if(!ValidateCode.ACCOUNT_SUCCESS.equals(validateAccount)) {
                ods378iQueryDto.setCode("999");
                ods378iQueryDto.setMsg(validateAccount.getMsg());//直接抓validateCode
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else {
                ods378iQueryDto = ods378iService.query(ods378iQueryDto);
                if(ods378iQueryDto.getDetails().size()==0) {
                    ods378iQueryDto.setCode("901");
                    ods378iQueryDto.setMsg("查無資料");
                }else {
                    ods378iQueryDto.setCode("200");
                    ods378iQueryDto.setMsg("執行成功");
                }
            }
        } else {
            ods378iQueryDto.setCode("998");
            ods378iQueryDto.setMsg("異常登入次數超過5次，請24小時後再登入");
        }
        
        // 產生 API log
        String userId = ODS378_USERTYPE + "_" + ban  + "_" + account;
        String packageId = odsPackageRepository.findIdByCode(ODS378_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("011", request.getRemoteAddr(), new Date(), packageId, packageVer,
                        request.getQueryString(), ods378iQueryDto.getCode(), appID, userId);
        
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.setExcludes(new String[] {"ban"});
       return (JSONObject) JSONSerializer.toJSON(ods378iQueryDto,jsonConfig);
    }
    
}
