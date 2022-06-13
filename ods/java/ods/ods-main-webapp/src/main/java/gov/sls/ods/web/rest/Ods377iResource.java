package gov.sls.ods.web.rest;

import java.util.Date;

import gov.sls.commons.core.security.api.AccountType;
import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.commons.core.security.api.ValidateCode;
import gov.sls.ods.dto.Ods377iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods377iService;
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
@RequestMapping("ODS377I")
//@PreAuthorize("hasAuthority('AUTHORITY_ODS377I')")
public class Ods377iResource {

    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods377iService ods377iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS377_USERTYPE = "B";
    final static String ODS377_PACKAGE_CODE = "O1504@ALL";

    final static String YEAR_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}$";
    
    /**營業人個別化主題-所有營業人營所稅統計查詢。
     * 查詢地區所有營業人之目標平均總機構營所稅營業淨利、目標平均總機構營所稅營業收入淨額。（為針對資料去識別化，僅提供該地區、營業項目所屬營業人家數大於5家的統計資料）
     * @param version  版本號碼(帶入範例值即可)
     * @param appID    APPID(APPID申請者統編需要等於營業人登入統一編號)
     * @param ban      營業人登入統一編號(僅可查詢APPID申請者自身營業人資料，故需與APPID申請者統編相同)
     * @param account  營業人登入帳號
     * @param password 營業人登入密碼
     * @param dataYrS  資料年度起
     * @param dataYrE  資料年度迄
     * @param hsnNm    縣市(用URLEncode)
     * @param townNm   鄉鎮市區(用URLEncode)
     * @param bscd2Nm  主營業項目名稱(用URLEncode)
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
            @RequestParam(value ="dataYrS", required = false) String dataYrS,
            @RequestParam(value ="dataYrE", required = false) String dataYrE,
            @RequestParam(value ="hsnNm", required = false) String hsnNm,
            @RequestParam(value ="townNm", required = false) String townNm,
            @RequestParam(value ="bscd2Nm", required = false) String bscd2Nm,
            HttpServletRequest request) {
//        ban = BanMask.getInstance().process(ban); // ban 加密
        Ods377iQueryDto ods377iQueryDto = new Ods377iQueryDto();
        ods377iQueryDto.setV(version);
        ods377iQueryDto.setDataYrS(dataYrS);
        ods377iQueryDto.setDataYrE(dataYrE);
        ods377iQueryDto.setHsnNm(hsnNm);
        ods377iQueryDto.setTownNm(townNm);
        ods377iQueryDto.setBscd2Nm(bscd2Nm);
        if ( ! AbnormalResourceAccessUtil.isAbnormalAccess(request, appID) ) {
            ValidateCode validateAccount = userValidator.validateAccount(AccountType.B, ban, account, password);
            if(version == null || version.equals("") || appID == null || appID.equals("") 
                    || account == null || account.equals("") || password == null || password.equals("") 
                    || dataYrS == null || dataYrS.equals("") || dataYrE == null || dataYrE.equals("")
                    || bscd2Nm == null || bscd2Nm.equals("")) {
                ods377iQueryDto.setCode("903");
                ods377iQueryDto.setMsg("參數錯誤");
            }else if(!dataYrS.matches(YEAR_FORMATE_PATTERN) || !dataYrE.matches(YEAR_FORMATE_PATTERN)
                    || !ods377iService.yearVerify(dataYrS, dataYrE)){
                ods377iQueryDto.setCode("903");
                ods377iQueryDto.setMsg("參數錯誤");
            }else if(!apiValidator.validationApiId(appID)) {
                ods377iQueryDto.setCode("998");
                ods377iQueryDto.setMsg("APPID錯誤");
            }else if(!apiValidator.validationAppIdAndBan(appID, ban)) {
                ods377iQueryDto.setCode("998");
                ods377iQueryDto.setMsg("APPID申請者統編與傳入BAN不符");
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else if(!ValidateCode.ACCOUNT_SUCCESS.equals(validateAccount)) {
                ods377iQueryDto.setCode("999");
                ods377iQueryDto.setMsg(validateAccount.getMsg());//直接抓validateCode
                AbnormalResourceAccessUtil.updateAbnormalAccess(request, appID);
            }else {
                ods377iQueryDto = ods377iService.query(ods377iQueryDto);
                if(ods377iQueryDto.getDetails().size()==0) {
                    ods377iQueryDto.setCode("901");
                    ods377iQueryDto.setMsg("查無資料");
                }else {
                    ods377iQueryDto.setCode("200");
                    ods377iQueryDto.setMsg("執行成功");
                }
            }
        } else {
            ods377iQueryDto.setCode("998");
            ods377iQueryDto.setMsg("異常登入次數超過5次，請24小時後再登入");
        }
        
        // 產生 API log
        String userId = ODS377_USERTYPE + "_" + ban  + "_" + account;
        String packageId = odsPackageRepository.findIdByCode(ODS377_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("010", request.getRemoteAddr(), new Date(), packageId, packageVer,
                        request.getQueryString(), ods377iQueryDto.getCode(), appID, userId);
        
        return (JSONObject) JSONSerializer.toJSON(ods377iQueryDto);
    }
    
}
