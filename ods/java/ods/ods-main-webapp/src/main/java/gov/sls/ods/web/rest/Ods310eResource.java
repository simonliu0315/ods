package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.ods.Messages;
import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods310eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods310eDto;
import gov.sls.ods.web.dto.Ods310eInitDto;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import au.com.bytecode.opencsv.CSVWriter;

import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS310E")
// @PreAuthorize("hasAuthority('AUTHORITY_ODS310E')")
public class Ods310eResource {

    @Autowired
    private Ods303eService ods303eService;

    @Autowired
    private Ods307eService ods307eService;

    @Autowired
    private Ods310eService ods310eService;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Inject
    private ApplicationContext applicationContext;
    

    @RequestMapping(value = "/page/{packageCode}", method = RequestMethod.POST)
    public ModelAndView searchContents(@PathVariable("packageCode") String packageCode,
            Pageable pageable, HttpServletRequest request) {
        log.info("Pageable Begin");
        if (pageable != null) {
            log.info("Pageable: page=" + pageable.getPageNumber() + ", size="
                    + pageable.getPageSize());
        }

        // Get model and view with parameters and common session attributes
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/ods/package/html/individual/" + packageCode);
        // Put result data into model
        Page<Ods303eIndividualDto> page = ods310eService.getResourceDate(pageable);
        mav.addObject("odsContents", page);

        // check device
        mav.addObject("isMobileDevice", mobileDeviceChecker.isMobileDevice(request));

        return mav;
    }
    
    @RequestMapping(value = "/init", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods310eInitDto init(@RequestBody Ods310eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();
        SlsUser user = UserHolder.getUser();
//        user.setBarCode("00000");

        // UAA權限控管
        Collection<? extends Authority> findResultList = uaaAuthoriy
                .findDataAuthorityByUserId(user.getId());
        boolean found = false;
        for (Authority authority : findResultList) {
            if (packageId.equals(authority.getId())) {
                found = true;
                break;
            }
        }
        Ods310eInitDto ods310eInitDto = new Ods310eInitDto();
        ods310eInitDto.setAnonymousUser(anonymousUserService.isAnonymousUser(user));
        
        //OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
        //OdsUserPackageRatePK odsUserPackageRatePK = new OdsUserPackageRatePK();
        //odsUserPackageRatePK.setPackageId(packageId);
        //odsUserPackageRatePK.setUserId(user.getId());
        //odsUserPackageRate.setId(odsUserPackageRatePK);
        alerter.success(Messages.success_find());
        //OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRate(odsUserPackageRate, user);
        OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRateByPkgIdAndUserId(packageId, user.getId());
        ods310eInitDto.setOdsUserPackageRate(returnOdsUserPackageRate == null ? new OdsUserPackageRate () : returnOdsUserPackageRate);
        
        if (!found && "/ods-main".equals(request.getContextPath())) {

            return ods310eInitDto;
        }

        
        if(ods310eService.getDanExportStatus(packageId, packageVer)){
            Map<String, Object> data = ods310eService.queryDatasetInfo(packageId, packageVer);
            if(data == null || data.get("minInvoiceDate") == null || data.get("maxInvoiceDate") == null){
                ods310eInitDto.setTip("您目前尚未有任何已歸戶載具電子發票記錄。");    
            } else {
                ods310eInitDto.setTip("您目前有 "+data.get("minInvoiceDate")+" 至 " + data.get("maxInvoiceDate") + " 之電子發票可供查詢。");
            }
        }else{
            ods310eInitDto.setTip("很抱歉您訂閱的已歸戶載具電子發票統計資料尚在處理中。處理完成後，系統將發送電子郵件通知您，請稍待謝謝！");
        }

        return ods310eInitDto;
    }

    @RequestMapping(value = "/plot", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray getPlotDataSet(@RequestBody Ods310eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();
        String sDate = dto.getStartDate();
        String eDate = dto.getEndDate();

        // UAA權限控管
        Collection<? extends Authority> findResultList = uaaAuthoriy
                .findDataAuthorityByUserId(UserHolder.getUser().getId());
        boolean found = false;
        for (Authority authority : findResultList) {
            if (packageId.equals(authority.getId())) {
                found = true;
                break;
            }
        }
        if (!found && "/ods-main".equals(request.getContextPath())) {

            return (JSONArray) JSONSerializer.toJSON(new ArrayList<Map<String, Object>>());
        }

        List<Map<String, Object>> resultList = ods310eService.createPlotData(packageId, packageVer, sDate, eDate, request.getRemoteAddr(), false);

        return (JSONArray) JSONSerializer.toJSON(resultList);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/downloadDataset/{packageId}/{packageVerStr}/{sDate}/{eDate}/")
    public ResponseEntity<byte[]> downloadDataset(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @PathVariable("sDate") String sDate,
            @PathVariable("eDate") String eDate,
            HttpServletRequest request) throws IOException {
        
        HttpHeaders headers = new HttpHeaders();

        // UAA權限控管
        SlsUser user = UserHolder.getUser();
        Collection<? extends Authority> findResultList = uaaAuthoriy
                .findDataAuthorityByUserId(user.getId());
        boolean found = false;
        for (Authority authority : findResultList) {
            if (packageId.equals(authority.getId())) {
                found = true;
                break;
            }
        }
        if (!found && "/ods-main".equals(request.getContextPath())) {

            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                    HttpStatus.NOT_FOUND);
        }

        try {
            String fileName = packageId + "-" + packageVer + ".csv";            
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Content-Disposition", "attachment;filename=" + fileName);

            JSONArray dataArray = (JSONArray) JSONSerializer.toJSON(ods310eService.createPlotData(packageId, packageVer, sDate, eDate, request.getRemoteAddr(), true));                        

            if (dataArray.size() == 0) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutStream = new BufferedOutputStream(outputStream);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(bufferedOutStream, "UTF-8"), ',', '\"');
            outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
            //write title
            writer.writeNext(new String[] {
                    "載具名稱",
                    "發票日期",
                    "縣市",
                    "鄉鎮市區",
                    "商店種類",
                    "載具別",
                    "電子發票張數",
                    "電子發票金額",
                    "捐贈發票張數",
                    "捐贈發票金額",
                    "中獎發票張數",
                    "中獎獎項金額"
            });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                writer.writeNext(new String[] {
                        object.getString("載具名稱"),
                        object.getString("完整發票日期"),
                        object.getString("縣市"),
                        object.getString("鄉鎮市區"),
                        object.getString("商店種類"),
                        object.getString("載具別"),
                        object.getString("電子發票張數"),
                        object.getString("電子發票金額"),
                        object.getString("捐贈發票張數"),
                        object.getString("捐贈發票金額"),
                        object.getString("中獎發票張數"),
                        object.getString("中獎獎項金額")
                });
            }
            writer.flush();
            
            log.info("CSVWriter:" + outputStream.toByteArray().length);
            
            InputStream in = new ByteArrayInputStream(outputStream.toByteArray());
            
            writer.close();
            
            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
    }

}
