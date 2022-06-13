package gov.sls.ods.web.rest;


import gov.sls.commons.core.security.api.ApiValidator;
import gov.sls.commons.core.security.api.UserValidator;
import gov.sls.ods.dto.Ods371iQueryDto;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.service.Ods371iService;
import gov.sls.ods.service.OdsApiLogService;

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
@RequestMapping("ODS371I")
public class Ods371iResource {
    
    @Autowired
    private ApiValidator apiValidator;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private Ods371iService ods371iService;
    
    @Autowired
    private OdsApiLogService odsApiLogService;
    
    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    final static String ODS371_PACKAGE_CODE = "O1501@ALL";
    
    final static String DATE_FORMATE_PATTERN = "^(19|20|21)[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])*$";
    
    /*
     * 已歸戶載具個別化主題。查詢已歸戶載具個別化主題統計資訊
     * 
     * @param version      版本號碼(帶入範例值即可)
     * @param appID        APPID
     * @param barcode      手機條碼
     * @param verifyCode   驗證碼
     * @param invoiceDateS 發票日期起yyyyMM
     * @param invoiceDateE 發票日期迄yyyyMM
     * @param hsnNm        縣市(用URLEncode)
     * @param townNm       鄉鎮市區(用URLEncode)
     * @param busiChiNm    商店種類(用URLEncode)
     * @param cardTypeNm   載具別(用URLEncode)
     * @param cardCodeNm   載具名稱(用URLEncode)
     * @param request
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject query(
            @RequestParam(value ="version", required = false) String version,
            @RequestParam(value ="appID", required = false) String appID,
            @RequestParam(value ="barcode", required = false) String barcode,
            @RequestParam(value ="verifyCode", required = false) String verifyCode,
            @RequestParam(value ="invoiceDateS", required = false) String invoiceDateS,
            @RequestParam(value ="invoiceDateE", required = false) String invoiceDateE,
            @RequestParam(value ="hsnNm", required = false) String hsnNm,
            @RequestParam(value ="townNm", required = false) String townNm,
            @RequestParam(value ="busiChiNm", required = false) String busiChiNm,
            @RequestParam(value ="cardTypeNm", required = false) String cardTypeNm,
            @RequestParam(value ="cardCodeNm", required = false) String cardCodeNm,
            HttpServletRequest request) {
        Ods371iQueryDto ods371iQueryDto = new Ods371iQueryDto();
        ods371iQueryDto.setBarcode(barcode);
        ods371iQueryDto.setV(version);
        ods371iQueryDto.setInvoiceDateS(invoiceDateS);
        ods371iQueryDto.setInvoiceDateE(invoiceDateE);
        ods371iQueryDto.setHsnNm(hsnNm);
        ods371iQueryDto.setTownNm(townNm);
        ods371iQueryDto.setCardTypeNm(cardTypeNm);
        ods371iQueryDto.setCardCodeNm(cardCodeNm);
        if(version == null || version.equals("") || appID == null || appID.equals("") 
                || barcode == null || barcode.equals("") || verifyCode == null || verifyCode.equals("") 
                || invoiceDateS == null || invoiceDateS.equals("") || invoiceDateE == null || invoiceDateE.equals("")) {
            ods371iQueryDto.setCode("903");
            ods371iQueryDto.setMsg("參數錯誤");
        }else if(!invoiceDateS.matches(DATE_FORMATE_PATTERN) || !invoiceDateE.matches(DATE_FORMATE_PATTERN)
                || !ods371iService.dateVerify(invoiceDateS, invoiceDateE)){
            ods371iQueryDto.setCode("903");
            ods371iQueryDto.setMsg("參數錯誤");
        }else if(!apiValidator.validationApiId(appID)) {
            ods371iQueryDto.setCode("998");
            ods371iQueryDto.setMsg("APPID錯誤");
        }else if(!userValidator.validateBarcode(barcode, verifyCode)) {
            ods371iQueryDto.setCode("919");
            ods371iQueryDto.setMsg("參數驗證碼錯誤");
        }else {
            ods371iQueryDto = ods371iService.query(ods371iQueryDto);
            if(ods371iQueryDto.getDetails().size()==0) {
                ods371iQueryDto.setCode("901");
                ods371iQueryDto.setMsg("查無資料");
            }else {
                ods371iQueryDto.setCode("200");
                ods371iQueryDto.setMsg("執行成功");
            }
        }
        
        // 產生 API log
        String userId = barcode;
        String packageId = odsPackageRepository.findIdByCode(ODS371_PACKAGE_CODE);
        Integer packageVer = 1;//個別化主題預設為1
        odsApiLogService
                .createLog("004", request.getRemoteAddr(), new Date(), packageId, packageVer, 
                        request.getQueryString(), ods371iQueryDto.getCode(), appID, userId);
        
        return (JSONObject) JSONSerializer.toJSON(ods371iQueryDto);
    }
}
