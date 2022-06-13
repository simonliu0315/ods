package gov.sls.ods.web.rest;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.entity.ods.OdsXcaDonateGoal;
import gov.sls.ods.Messages;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods314eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods310eDto;
import gov.sls.ods.web.dto.Ods314eDto;
import gov.sls.ods.web.interceptor.MobileDeviceChecker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

import au.com.bytecode.opencsv.CSVWriter;

import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS314E")
// @PreAuthorize("hasAuthority('AUTHORITY_ODS310E')")
public class Ods314eResource {

    @Autowired
    private Ods303eService ods303eService;

    @Autowired
    private Ods307eService ods307eService;

    @Autowired
    private Ods314eService ods314eService;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    @Autowired
    private MobileDeviceChecker mobileDeviceChecker;
    
    @Inject
    private ApplicationContext applicationContext;
    
    @RequestMapping(value = "/init", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods314eDto init(@RequestBody Ods310eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();
        SlsUser user = UserHolder.getUser();
        
        

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
        boolean isAnonymousUser = anonymousUserService.isAnonymousUser(user);
        log.info("isAnonymousUser:" + isAnonymousUser);
        Ods314eDto ods314eDto = new Ods314eDto();
//        OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
//        OdsUserPackageRatePK odsUserPackageRatePK = new OdsUserPackageRatePK();
//        odsUserPackageRatePK.setPackageId(packageId);
//        odsUserPackageRatePK.setUserId(user.getId());
//        odsUserPackageRate.setId(odsUserPackageRatePK);
        alerter.success(Messages.success_find());
//        OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRate(odsUserPackageRate, user);
        OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRateByPkgIdAndUserId(packageId, user.getId());
        ods314eDto.setOdsUserPackageRate(returnOdsUserPackageRate == null ? new OdsUserPackageRate () : returnOdsUserPackageRate);
        
        if (!found && "/ods-main".equals(request.getContextPath())) {

            return ods314eDto;
        }

        return ods314eDto;
    }

    @RequestMapping(value = "/setDonateInfo", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject setYearDonateCntGoal(@RequestBody Ods314eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();

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

            return (JSONObject) JSONSerializer.toJSON(null);
        }
        String dateFormat = "yyyyMM";
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(dateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        //取得本月1號
        Date thisMonthOne = DateUtils.truncate(Calendar.getInstance().getTime(),
                Calendar.MONTH);
        log.debug("取得本月1號 " + thisMonthOne);

        //今年年月
        String thisMonth = formatter.format(thisMonthOne);
        log.debug("今年年月 " + thisMonth);
        
        BigDecimal goal = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(dto.getYearDonateCntGoal())) {
            goal = new BigDecimal(dto.getYearDonateCntGoal());
        }
        //年度目標
        OdsXcaDonateGoal yearDonateCntGoal = ods314eService.saveYearDonateCntGoal(packageId, packageVer, thisMonth.substring(0, 4), goal);
        log.debug("設定年度目標");

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("yearDonateCntGoal", yearDonateCntGoal.getDonateCount());
        log.debug("年度目標:"+yearDonateCntGoal.getDonateCount());
        
        return (JSONObject) JSONSerializer.toJSON(resultMap);
    }
    
    @RequestMapping(value = "/getDonateInfo", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject getDonateInfo(@RequestBody Ods310eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();

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

            return (JSONObject) JSONSerializer.toJSON(null);
        }
        //ods314eService.createPlotData(packageId, packageVer, sDate, eDate, request.getRemoteAddr(), false);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String dateFormat = "yyyyMM";
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(dateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        //取得本月1號
        Date thisMonthOne = DateUtils.truncate(Calendar.getInstance().getTime(),
                Calendar.MONTH);
        log.debug("取得本月1號 " + thisMonthOne);
        //上月1號
        Date lastMonthOne = DateUtils.addMonths(thisMonthOne, -1);
        log.debug("上月1號 " + lastMonthOne);

        //今年年月
        String thisMonth = formatter.format(thisMonthOne);
        log.debug("今年年月 " + thisMonth);
        //今年
        String thisyear = thisMonth.substring(0, 4);
        log.debug("今年 " + thisyear);

        
        // 上月年月
        String lastMonth = formatter.format(lastMonthOne);
        resultMap.put("lastMonth", lastMonth.substring(0, 4)+"年"+lastMonth.substring(4)+"月");
        log.debug("上月年月 " + lastMonth);
        
        //去年上月1號
        Date lastYearMonthOne = DateUtils.addYears(lastMonthOne, -1);

        //去年上月年月
        String lastYearMonth = formatter.format(lastYearMonthOne);
        resultMap.put("lastYearMonth", lastYearMonth.substring(0, 4)+"年"+lastYearMonth.substring(4)+"月");
        log.debug("去年上月年月 " + lastYearMonth);
        
        //年度目標
        BigDecimal yearDonateCntGoal = ods314eService.getYearDonateCntGoal(packageId, packageVer, thisMonth.substring(0, 4));
        resultMap.put("yearDonateCntGoal", yearDonateCntGoal);
        log.debug("年度目標:"+yearDonateCntGoal);
        
        //捐贈統計
        List<Map<String, Object>> selfStat = ods314eService.getDonateCompare(packageId, packageVer, true, lastYearMonth, lastMonth);
        List<Map<String, Object>> allStat = ods314eService.getDonateCompare(packageId, packageVer, false, lastYearMonth, lastMonth);

        //上月捐贈張數
        BigDecimal lastMonthBanDonate = BigDecimal.ZERO;
        //上月所有受捐贈機關或團體捐贈張數 計算佔比
        BigDecimal lastMonthDonate = BigDecimal.ZERO;
        //去年上月捐贈張數
        BigDecimal lastYearMonthBanDonate = BigDecimal.ZERO;
        //去年上月所有受捐贈機關或團體捐贈張數 計算佔比
        BigDecimal lastYearMonthDonate = BigDecimal.ZERO;
        
        //捐贈過去一年佔比
        List<Map<String, Object>> percStat = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : allStat) {
            String allyyyyMM = (String)map.get("發票年月");
            BigDecimal allawardAmt = (BigDecimal)map.get("中獎金額");
            BigDecimal alldonateCnt = (BigDecimal)map.get("捐贈張數");
            BigDecimal allawardCnt = (BigDecimal)map.get("中獎張數");

            for(Map<String, Object> selfmap : selfStat){
                String yyyyMM = (String)selfmap.get("發票年月");
                if(yyyyMM.equals(allyyyyMM)){
                    Map<String, Object> percMap = new HashMap<String, Object>();
                    BigDecimal awardAmt = (BigDecimal)selfmap.get("中獎金額");
                    BigDecimal donateCnt = (BigDecimal)selfmap.get("捐贈張數");
                    BigDecimal awardCnt = (BigDecimal)selfmap.get("中獎張數");
                    
                    percMap.put("發票年月", yyyyMM);
                    if (null!=awardAmt&&!BigDecimal.ZERO.equals(awardAmt) && null!=allawardAmt&&!BigDecimal.ZERO.equals(allawardAmt)) {
                        percMap.put("中獎金額", awardAmt.divide(allawardAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));//FIX ME
                    } else {
                        percMap.put("中獎金額", BigDecimal.ZERO);
                    }
                    if (null!=donateCnt&&!BigDecimal.ZERO.equals(donateCnt) && null!=alldonateCnt&&!BigDecimal.ZERO.equals(alldonateCnt)) {
                        percMap.put("捐贈張數", donateCnt.divide(alldonateCnt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));//FIX ME
                    } else {
                        percMap.put("捐贈張數", BigDecimal.ZERO);
                    }
                    if (null!=awardCnt&&!BigDecimal.ZERO.equals(awardCnt) && null!=allawardCnt&&!BigDecimal.ZERO.equals(allawardCnt)) {
                        percMap.put("中獎張數", awardCnt.divide(allawardCnt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));//FIX ME
                    } else {
                        percMap.put("中獎張數", BigDecimal.ZERO);
                    }
                    percStat.add(percMap);

                    //上月捐贈張數
                    if(yyyyMM.equals(lastMonth)) {
                        lastMonthBanDonate = donateCnt;
                        lastMonthDonate = (BigDecimal)percMap.get("捐贈張數");
                    }
                    //去年上月捐贈張數
                    if(yyyyMM.equals(lastYearMonth)) {
                        lastYearMonthBanDonate = donateCnt;
                        lastYearMonthDonate = (BigDecimal)percMap.get("捐贈張數");
                    }
                }
            }
        }
        resultMap.put("percStat", percStat);
        
        //上月捐贈張數
        resultMap.put("lastMonthDonateCnt", lastMonthBanDonate);
        log.debug("上月捐贈張數 "+lastMonthBanDonate);
        
        //上月所有受捐贈機關或團體捐贈張數 計算佔比
        resultMap.put("lastMonthDonatePercent", lastMonthDonate);
        log.debug("上月所有受捐贈機關或團體捐贈張數 "+lastMonthDonate);
        
        //去年上月捐贈張數
        resultMap.put("lastYearMonthDonateCnt", lastYearMonthBanDonate);
        log.debug("去年上月捐贈張數 "+lastYearMonthBanDonate);
        
        //去年上月所有受捐贈機關或團體捐贈張數 計算佔比
        resultMap.put("lastYearMonthDonatePercent", lastYearMonthDonate);
        log.debug("去年上月所有受捐贈機關或團體捐贈張數 "+lastYearMonthDonate);

        //今年度累積中獎金額
        BigDecimal thisYearAwardAmt = BigDecimal.ZERO;
        
        //今年度累積捐贈張數
        BigDecimal thisYearDonateCnt = BigDecimal.ZERO;
        
        //今年度中獎張數
        BigDecimal thisYearAwardCnt = BigDecimal.ZERO;
        
        for (Map<String, Object> map : selfStat) {
            if(thisyear.equals(((String)map.get("發票年月")).substring(0, 4))){
                thisYearDonateCnt = thisYearDonateCnt.add((BigDecimal)map.get("捐贈張數"));
                thisYearAwardCnt = thisYearAwardCnt.add((BigDecimal)map.get("中獎張數"));
                thisYearAwardAmt = thisYearAwardAmt.add((BigDecimal)map.get("中獎金額"));
            }
        }
        resultMap.put("thisYearAwardAmt", thisYearAwardAmt);
        log.debug("今年度累積中獎金額 "+thisYearAwardAmt);
        resultMap.put("thisYearDonateCnt", thisYearDonateCnt);
        log.debug("今年度累積捐贈張數 "+thisYearDonateCnt);
        resultMap.put("thisYearAwardCnt", thisYearAwardCnt);
        log.debug("今年度中獎張數  "+thisYearAwardCnt);
        
        
        //排除null的值
        Set<String> keySet = resultMap.keySet();
        List<String> rmKey = new ArrayList<String>();
        for (String key : keySet) {
            if(null==resultMap.get(key)){
                rmKey.add(key);
                log.debug("rmKey "+key);
            }
        }
        for (String key : rmKey) {
            resultMap.remove(key);
        }
        
        return (JSONObject) JSONSerializer.toJSON(resultMap);
    }

    @RequestMapping(value = "/plot", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray getPlotDataSet(@RequestBody Ods314eDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId = dto.getPackageId();
        String packageVer = dto.getPackageVer();
        String sDate = dto.getStartDate();
        String eDate = dto.getEndDate();
        String chartType = dto.getChartType();

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

        List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();
        //登錄受捐贈機關或團體捐贈統計
        List<Map<String, Object>> selfResultList = ods314eService.createPlotData(packageId, packageVer, sDate, eDate, request.getRemoteAddr(), false);
        result.add(selfResultList);
        
        //圓餅圖不做兩個axis
        if(!"pie".equals(chartType)){
            //所有受捐贈機關或團體捐贈統計
            List<Map<String, Object>> resultAllSocialList = ods314eService.getAllSocialData(packageId, packageVer, sDate, eDate, request.getRemoteAddr());
            result.add(resultAllSocialList);
        }
        return (JSONArray) JSONSerializer.toJSON(result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/downloadDataset/{packageId}/{packageVerStr}/{sDate}/{eDate}/")
    public ResponseEntity<byte[]> downloadDataset(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @PathVariable("sDate") String sDate,
            @PathVariable("eDate") String eDate,
            HttpServletRequest request) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            
            //UAA權限控管
            SlsUser user = UserHolder.getUser();
            Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(user.getId());
            boolean found = false;
            for (Authority authority: findResultList) {
                if (packageId.equals(authority.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error("UAA權限 isEmpty");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            String fileName = packageId + "-" + packageVer;
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileName += ".zip";
            headers.add("Content-Disposition", "attachment;filename=" + fileName);

            //登錄受捐贈機關或團體捐贈統計
            List<Map<String, Object>> selfResultList = ods314eService.createPlotData(packageId, packageVer, sDate, eDate, request.getRemoteAddr(), false);

            //所有受捐贈機關或團體捐贈統計
            List<Map<String, Object>> resultAllSocialList = ods314eService.getAllSocialData(packageId, packageVer, sDate, eDate, request.getRemoteAddr());
            

            if (CollectionUtils.isEmpty(selfResultList) || CollectionUtils.isEmpty(resultAllSocialList)) {
                log.error("ResponseEntity isEmpty");
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }
            
            ByteArrayOutputStream zipboutputStream = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipboutputStream);
            addFile("selfResult.csv", getDatain(selfResultList, 1), zos);
            addFile("allResult.csv", getDatain(resultAllSocialList, 2), zos);
            zos.close();
            
            try {
                return new ResponseEntity<byte[]>(zipboutputStream.toByteArray(), headers, HttpStatus.OK);
            } catch (Exception e) {
                log.error("error in ResponseEntity:{}", e);
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                        HttpStatus.NOT_FOUND);
            }            
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * @param resultList
     * @param targetFlag 1自己 / 2全部
     * @return InputStream
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private InputStream getDatain(List<Map<String, Object>> resultList, int targetFlag)
            throws UnsupportedEncodingException, IOException {
        JSONArray dataArray = (JSONArray) JSONSerializer.toJSON(resultList);                        

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutStream = new BufferedOutputStream(outputStream);
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(bufferedOutStream, "UTF-8"), ',', '\"');
        outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
        // write head
        JSONObject heads = dataArray.getJSONObject(0);
        Set<String> keySet = heads.keySet();
        String[] array = keySet.toArray(new String[] {});

        if (2==targetFlag) {
            writer.writeNext(new String[] {
                    "發票年",
                    "發票年月",
                    "縣市",
                    "鄉鎮市區",
                    "營業稅主行業別",
                    "載具類別名稱",
                    "載具名稱",
                    "平均電子發票捐贈張數",
                    "平均電子發票中獎張數",
                    "平均電子發票中獎金額"
                });
        } else {
            writer.writeNext(new String[] {
                    "發票年",
                    "發票年月",
                    "縣市",
                    "鄉鎮市區",
                    "營業稅主行業別",
                    "載具類別名稱",
                    "載具名稱",
                    "電子發票捐贈張數",
                    "電子發票中獎張數",
                    "電子發票中獎金額"
                });
        }

        // write data
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject object = dataArray.getJSONObject(i);
            Object[] objArr = object.values().toArray();
            String[] array2 = new String[objArr.length];
            for (int j = 0; j < objArr.length; j++) {
                array2[j] = objArr[j].toString();
            }
            writer.writeNext(array2);
        }
        writer.flush();
        
        log.info("CSVWriter:" + outputStream.toByteArray().length);

        InputStream datain = new ByteArrayInputStream(outputStream.toByteArray());
        
        writer.close();
        return datain;
    }

    /**
     * Recurses down a directory and its subdirectories to look for
     * files to add to the Zip. If the current file being looked at
     * is not a directory, the method adds it to the Zip file.
     */
    private static void addFile(String name, InputStream fin, ZipOutputStream zos) throws IOException,
            FileNotFoundException {
        byte[] buf = new byte[1024];
        int len;
        ZipEntry zipEntry = new ZipEntry(name);

        //Create a buffered input stream out of the file
        //we're trying to add into the Zip archive.
        BufferedInputStream in = new BufferedInputStream(fin);
        zos.putNextEntry(zipEntry);
        //Read bytes from the file and write into the Zip archive.

        while ((len = in.read(buf)) >= 0) {
            zos.write(buf, 0, len);
        }

        //Close the input stream.
        in.close();

        //Close this entry in the Zip stream.
        zos.closeEntry();
    }
}
