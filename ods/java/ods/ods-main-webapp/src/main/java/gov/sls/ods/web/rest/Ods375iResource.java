package gov.sls.ods.web.rest;

import java.util.Date;

import gov.sls.commons.core.security.api.AccountType;
import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.commons.core.security.api.ValidateCode;
import gov.sls.ods.dto.Ods375iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods375iService;
import gov.sls.ods.service.OdsApiLogService;
import gov.sls.ods.web.util.AbnormalResourceAccessUtil;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

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
@RequestMapping("ODS375I")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS375I')")
public class Ods375iResource {

    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods375iService ods375iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS375_USERTYPE = "B";
    final static String ODS375_PACKAGE_CODE = "O1504@ALL";
    
    final static String DATE_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])*$";
    
    /** 營業人個別化主題-所有營業人電子發票統計查詢
     * 查詢地區所有營業人開立之電子發票統計資料。（為針對資料去識別化，僅提供該地區、營業項目所屬營業人家數大於5家的統計資料）
     * @param version       版本號碼(帶入範例值即可)
     * @param appID         APPID(APPID申請者統編需要等於營業人登入統一編號)
     * @param ban           營業人登入統一編號(僅可查詢APPID申請者自身營業人資料，故需與APPID申請者統編相同)
     * @param account       營業人登入帳號
     * @param password      營業人登入密碼
     * @param invoiceDateS  發票日期起
     * @param invoiceDateE  發票日期迄
     * @param hsnNm         發票種類(B2C、B2B、all)
     * @param townNm        縣市(用URLEncode)
     * @param invType       鄉鎮市區(用URLEncode)
     * @param bscd2Nm       主營業項目名稱(用URLEncode)
     * @param request
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, 
                    //consumes = "application/x-www-form-urlencoded;charset=UTF-8",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject query(
            @RequestParam(value ="version", required = false) String version,
            @RequestParam(value ="appID", required = false) String appID,
            @RequestParam(value ="ban", required = false) String ban,
            @RequestParam(value ="account", required = false) String account,
            @RequestParam(value ="password", required = false) String password,
            @RequestParam(value ="invoiceDateS", required = false) String invoiceDateS,
            @RequestParam(value ="invoiceDateE", required = false) String invoiceDateE,
            @RequestParam(value ="hsnNm", required = false) String hsnNm,
            @RequestParam(value ="townNm", required = false) String townNm,
            @RequestParam(value ="invType", required = false) String invType,
            @RequestParam(value ="bscd2Nm", required = false) String bscd2Nm,
            HttpServletRequest request) {
//        ban = BanMask.getInstance().process(ban); // ban 加密
        Ods375iQueryDto ods375iQueryDto = new Ods375iQueryDto();
        ods375iQueryDto.setV(version);
        ods375iQueryDto.setInvoiceDateS(invoiceDateS);
        ods375iQueryDto.setInvoiceDateE(invoiceDateE);
        ods375iQueryDto.setHsnNm(hsnNm);
        ods375iQueryDto.setTownNm(townNm);
        ods375iQueryDto.setInvType(invType);
        ods375iQueryDto.setBscd2Nm(bscd2Nm);
        if ( ! AbnormalResourceAccessUtil.isAbnormalAccess(request, appID) ) {
            ValidateCode validateAccount = userValidator.validateAccount(AccountType.B, ban, account, password);
            if(version == null || version.equals("") || appID == null || appID.equals("") 
                    || account == null || account.equals("") || password == null || password.equals("") 
                    || invoiceDateS == null || invoiceDateS.equals("") || invoiceDateE == null || invoiceDateE.equals("")
                    || invType == null || invType.equals("") || bscd2Nm == null || bscd2Nm.equals("")) {
                ods375iQueryDto.setCode("903");
                ods375iQueryDto.setMsg("參數錯誤");
            }else if(!invoiceDateS.matches(DATE_FORMATE_PATTERN) || !invoiceDateE.matches(DATE_FORMATE_PATTERN)
                    || !ods375iService.dateVerify(invoiceDateS, invoiceDateE)){
                ods375iQueryDto.setCode("903");
                ods375iQueryDto.setMsg("參數錯誤");
            }else if(!apiValidator.validationApiId(appID)) {
                ods375iQueryDto.setCode("998");
                ods375iQueryDto.setMsg("APPID錯誤");
            }else if(!apiValidator.validationAppIdAndBan(appID, ban)) {
                ods375iQueryDto.setCode("998");
                ods375iQueryDto.setMsg("APPID申請者統編與傳入BAN不符");
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else if(!ValidateCode.ACCOUNT_SUCCESS.equals(validateAccount)) {
                ods375iQueryDto.setCode("999");
                ods375iQueryDto.setMsg(validateAccount.getMsg());//直接抓validateCode
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else {
                ods375iQueryDto = ods375iService.query(ods375iQueryDto);
                if(ods375iQueryDto.getDetails().size()==0) {
                    ods375iQueryDto.setCode("901");
                    ods375iQueryDto.setMsg("查無資料");
                }else {
                    ods375iQueryDto.setCode("200");
                    ods375iQueryDto.setMsg("執行成功");
                }
            }
        } else {
            ods375iQueryDto.setCode("998");
            ods375iQueryDto.setMsg("異常登入次數超過5次，請24小時後再登入");
        }
        
        // 產生 API log
        String userId = ODS375_USERTYPE + "_" + ban  + "_" + account;
        String packageId = odsPackageRepository.findIdByCode(ODS375_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("008", request.getRemoteAddr(), new Date(), packageId, packageVer,
                        request.getQueryString(), ods375iQueryDto.getCode(), appID, userId);
        
        return (JSONObject) JSONSerializer.toJSON(ods375iQueryDto);
    }
    
}
