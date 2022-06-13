package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.api.AccountType;
import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.commons.core.security.api.ValidateCode;
import gov.sls.ods.dto.Ods373iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods373iService;
import gov.sls.ods.service.OdsApiLogService;
import gov.sls.ods.web.util.AbnormalResourceAccessUtil;

import java.util.Date;

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
@RequestMapping("ODS373I")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS373I')")
public class Ods373iResource {


    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods373iService ods373iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS373_USERTYPE = "S";
    final static String ODS373_PACKAGE_CODE = "O1503@ALL";
    
    final static String DATE_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}(0[1-9]|1[012])*$";
    
    /**社福團體個別化主題-所有社福團體捐贈統計查詢。查詢所有社福團體加總之捐贈統計資訊。
     * @param version     版本號碼(帶入範例值即可)
     * @param appID       APPID(APPID申請者統編需要等於社福團體登入統一編號)
     * @param ban         社福團體登入統一編號(僅可查詢APPID申請者自身社福團體資料，故需與APPID申請者統編相同)
     * @param account     社福團體登入帳號
     * @param password    社福團體登入密碼
     * @param invoiceYmS  發票年月起yyyyMM
     * @param invoiceYmE  發票年月迄yyyyMM
     * @param hsnNm       縣市(縣市與營業稅主行業別不可同時當查詢條件)
     * @param busiChiNm   營業稅主行業別(縣市與營業稅主行業別不可同時當查詢條件)
     * @param cardTypeNm  載具類別名稱
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
            @RequestParam(value ="invoiceYmS", required = false) String invoiceYmS,
            @RequestParam(value ="invoiceYmE", required = false) String invoiceYmE,
            @RequestParam(value ="hsnNm", required = false) String hsnNm,
            @RequestParam(value ="busiChiNm", required = false) String busiChiNm,
            @RequestParam(value ="cardTypeNm", required = false) String cardTypeNm,
            HttpServletRequest request) {
        Ods373iQueryDto ods373iQueryDto = new Ods373iQueryDto();
        ods373iQueryDto.setV(version);
        ods373iQueryDto.setInvoiceYmS(invoiceYmS);
        ods373iQueryDto.setInvoiceYmE(invoiceYmE);
        ods373iQueryDto.setHsnNm(hsnNm);
        ods373iQueryDto.setBusiChiNm(busiChiNm);
        ods373iQueryDto.setCardTypeNm(cardTypeNm);
        if ( ! AbnormalResourceAccessUtil.isAbnormalAccess(request, appID) ) {
            ValidateCode validateAccount = userValidator.validateAccount(AccountType.S, ban, account, password);
            if(version == null || version.equals("") || appID == null || appID.equals("") 
                    || account == null || account.equals("") || password == null || password.equals("") 
                    || invoiceYmS == null || invoiceYmS.equals("") || invoiceYmE == null || invoiceYmE.equals("")) {
                ods373iQueryDto.setCode("903");
                ods373iQueryDto.setMsg("參數錯誤");
            }else if(((hsnNm == null || hsnNm.equals("")) && (busiChiNm == null || busiChiNm.equals(""))) 
                    || (!(hsnNm == null || hsnNm.equals("")) && !(busiChiNm == null || busiChiNm.equals(""))) ) {
                ods373iQueryDto.setCode("903");
                ods373iQueryDto.setMsg("參數錯誤");
            }else if(!invoiceYmS.matches(DATE_FORMATE_PATTERN) || !invoiceYmE.matches(DATE_FORMATE_PATTERN)
                    || !ods373iService.dateVerify(invoiceYmS, invoiceYmE)){
                ods373iQueryDto.setCode("903");
                ods373iQueryDto.setMsg("參數錯誤");
            }else if(!apiValidator.validationApiId(appID)) {
                ods373iQueryDto.setCode("998");
                ods373iQueryDto.setMsg("APPID錯誤");
            }else if(!apiValidator.validationAppIdAndBan(appID, ban)) {
                ods373iQueryDto.setCode("998");
                ods373iQueryDto.setMsg("APPID申請者統編與傳入BAN不符");
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else if(!ValidateCode.ACCOUNT_SUCCESS.equals(validateAccount)) {
                ods373iQueryDto.setCode("999");
                ods373iQueryDto.setMsg(validateAccount.getMsg());//直接抓validateCode
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else {
                ods373iQueryDto = ods373iService.query(ods373iQueryDto);
                if(ods373iQueryDto.getDetails().size()==0) {
                    ods373iQueryDto.setCode("901");;
                    ods373iQueryDto.setMsg("查無資料");
                }else {
                    ods373iQueryDto.setCode("200");;
                    ods373iQueryDto.setMsg("執行成功");
                }
            }
        } else {
            ods373iQueryDto.setCode("998");
            ods373iQueryDto.setMsg("異常登入次數超過5次，請24小時後再登入");
        }

        // 產生 API log
        String userId = ODS373_USERTYPE + "_" + ban  + "_" + account;
        String packageId = odsPackageRepository.findIdByCode(ODS373_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("006", request.getRemoteAddr(), new Date(), packageId, packageVer,
                        request.getQueryString(), ods373iQueryDto.getCode(), appID, userId);
        
        return (JSONObject) JSONSerializer.toJSON(ods373iQueryDto);
    }
    
    
}
