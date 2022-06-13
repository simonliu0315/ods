package gov.sls.ods.web.rest;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.commons.service.AnonymousUserService;
import gov.sls.entity.ods.OdsPackageDocument;
import gov.sls.entity.ods.OdsUserPackageRate;
import gov.sls.ods.Messages;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods315eInputDto;
import gov.sls.ods.repository.OdsOrgCodeRepositoryCustom;
import gov.sls.ods.service.Ods303eService;
import gov.sls.ods.service.Ods307eService;
import gov.sls.ods.service.Ods310eService;
import gov.sls.ods.service.Ods315eService;
import gov.sls.ods.service.Ods703eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.util.BanMask;
import gov.sls.ods.web.dto.Ods315eDto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cht.commons.security.Role;
import com.cht.commons.web.Alerter;

@Slf4j
@Controller
@RequestMapping("ODS315E")
// @PreAuthorize("hasAuthority('AUTHORITY_ODS310E')")
public class Ods315eResource {

    @Autowired
    private Ods303eService ods303eService;

    @Autowired
    private Ods307eService ods307eService;

    @Autowired
    private Ods310eService ods310eService;
    
    @Autowired
    private Ods315eService ods315eService;

    @Autowired
    private Ods703eService ods703eService;
    
    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private OdsOrgCodeRepositoryCustom odsOrgCodeRepositoryCustom;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    @Autowired
    private FileStore store;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @RequestMapping(value = "/init", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Ods315eDto init(@RequestBody Ods315eDto dto, HttpServletRequest request,
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
        Ods315eDto ods315eDto = new Ods315eDto();
        
//        OdsUserPackageRate odsUserPackageRate = new OdsUserPackageRate();
//        OdsUserPackageRatePK odsUserPackageRatePK = new OdsUserPackageRatePK();
//        odsUserPackageRatePK.setPackageId(packageId);
//        odsUserPackageRatePK.setUserId(user.getId());
//        odsUserPackageRate.setId(odsUserPackageRatePK);
        alerter.success(Messages.success_find());
//        OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRate(odsUserPackageRate, user);
        OdsUserPackageRate returnOdsUserPackageRate = ods307eService.findRateByPkgIdAndUserId(packageId, user.getId());
        ods315eDto.setOdsUserPackageRate(returnOdsUserPackageRate == null ? new OdsUserPackageRate () : returnOdsUserPackageRate);
        
        if (!found && "/ods-main".equals(request.getContextPath())) {
            return ods315eDto;
        }
        return ods315eDto;
    }
    private boolean isRBRole(List<? extends Role> roles){
        for(Role role : roles){
            if("RB".equals(role.getId())){
                return true;
            }
        }
        return false;
    }

    /**取得縣市清單
     * @param request
     * @param alerter
     * @return
     */
    @RequestMapping(value = "/initFindCity", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray initFindCity( HttpServletRequest request,
            Alerter alerter) {
        Ods315eInputDto dto = new Ods315eInputDto();
        dto.setBan(getBan());
        return (JSONArray) JSONSerializer.toJSON(odsOrgCodeRepositoryCustom.findHsn());
    }
    
    /**查詢營業人縣市鄉鎮行業別
     * @param request
     * @param alerter
     * @return
     */
    @RequestMapping(value = "/initFindSelect", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray initFindSelect( HttpServletRequest request,
            Alerter alerter) {
        return (JSONArray) JSONSerializer.toJSON(ods315eService.findSellCity(getBan()));
    }
    
    /**取得鄉鎮清單
     * @param dto
     * @param request
     * @param alerter
     * @return
     */
    @RequestMapping(value = "/findTownCd", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray findTownCd(@RequestBody Ods315eInputDto dto, HttpServletRequest request,
            Alerter alerter) {
        return (JSONArray) JSONSerializer.toJSON(odsOrgCodeRepositoryCustom.findTown(dto.getHsnCd()));
    }
    
    /**取得營業人統計資料
     * @param dto
     * @param request
     * @param alerter
     * @return
     */
    @RequestMapping(value = "/plot", method = { RequestMethod.GET,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray getPlotDataSet(@RequestBody Ods315eInputDto dto, HttpServletRequest request,
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

            return (JSONArray) JSONSerializer.toJSON(new ArrayList<Map<String, Object>>());
        }

        dto.setBan(getBan());
        String yColumn = dto.getYcolumn();
        String aggreFun = dto.getAggreFun();
        String invoiceSDate = dto.getInvoiceSDate();
        String invoiceEDate = dto.getInvoiceEDate();
        //所屬縣市鄉鎮行業
        String selfHsnCd = dto.getSelfCounty();
        String selfTownCd = dto.getSelfArea();
        String selfIndustry = dto.getSelfIndustClass();
        //目標縣市鄉鎮行業
        String hsnCd = dto.getHsnCd();
        String townCd = dto.getTownCd();
        String industry = dto.getIndustry();
        String chartType = dto.getChartType();
        String ipAddress = request.getRemoteAddr();
        // 取得資料
        List<List<Map<String, Object>>> resultList = getPlotData(packageId, packageVer, yColumn,
                aggreFun, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd,
                selfIndustry, hsnCd, townCd, industry, chartType, false, ipAddress);

        return (JSONArray) JSONSerializer.toJSON(resultList);
    }

    /**取得資料
     * @param ycolumn      yColumn     
     * @param aggreFun     aggreFun    
     * @param invoiceSDate invoiceSDate
     * @param invoiceEDate invoiceEDate
     * @param selfHsnCd    selfHsnCd   
     * @param selfTownCd   selfTownCd  
     * @param selfIndustry selfIndustry
     * @param hsnCd        hsnCd       
     * @param townCd       townCd      
     * @param industry     industry    
     * @param chartType    chartType   
     * @param isDownload    isDownload
     * @return List<List<Map<String, Object>>>
     */
    private List<List<Map<String, Object>>> getPlotData(String packageId, String packageVer, String ycolumn, String aggreFun, String invoiceSDate,
            String invoiceEDate, String selfHsnCd, String selfTownCd,
            String selfIndustry, String hsnCd, String townCd, String industry,
            String chartType, boolean isDownload, String ipAddress) {

        long startTime;
        List<List<Map<String, Object>>> resultList = new ArrayList<List<Map<String, Object>>>();
        if("basic-table".equals(chartType)) {
            //基本資料表
            List<Map<String, Object>> basicInfo = new ArrayList<Map<String, Object>>();
            basicInfo.add(ods315eService.createForCorporation(packageId, packageVer, invoiceSDate, invoiceEDate, hsnCd, townCd, industry, selfHsnCd, selfTownCd, selfIndustry, isDownload, ipAddress));
            resultList.add(basicInfo);
        }else if("1".equals(ycolumn)){
            // 電子發票中獎張數、電子發票中獎金額
            startTime=System.currentTimeMillis();
            resultList.add(ods315eService.createGraphWinning(packageId, packageVer, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, false, isDownload, ipAddress));
            log.info("電子發票中獎張數、電子發票中獎金額 處理時間: "+(System.currentTimeMillis()-startTime));
        }else if("5".equals(ycolumn)){
            // 營所稅營業淨利、營所稅營業收入淨額
            startTime=System.currentTimeMillis();
            resultList.add(ods315eService.createPrcGraph(packageId, packageVer, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, isDownload, ipAddress));
            resultList.add(ods315eService.createPrcSecondYaxis(packageId, packageVer, invoiceSDate, invoiceEDate, townCd, hsnCd, industry, isDownload, ipAddress));
            log.info("營所稅營業淨利、營所稅營業收入淨額 處理時間: "+(System.currentTimeMillis()-startTime));
        }else if("6".equals(ycolumn)){
            // 營業稅發票申購張數、營業稅銷項總計金額
            startTime=System.currentTimeMillis();
            resultList.add(ods315eService.createBgmGraph(packageId, packageVer, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, isDownload, ipAddress));
            resultList.add(ods315eService.createBgmSecondYaxis(packageId, packageVer, invoiceSDate, invoiceEDate, townCd, hsnCd, industry, isDownload, ipAddress));
            log.info("營業稅發票申購張數、營業稅銷項總計金額 處理時間: "+(System.currentTimeMillis()-startTime));
        }else {
            //電子發票金額張數、客單價
            startTime=System.currentTimeMillis();
            resultList.add(ods315eService.createGraph(packageId, packageVer, aggreFun, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, false, isDownload, ipAddress));
            resultList.add(ods315eService.createSecondYaxis(packageId, packageVer, aggreFun, invoiceSDate, invoiceEDate, townCd, hsnCd, industry, isDownload, ipAddress));
            log.info("電子發票金額張數、客單價 處理時間: "+(System.currentTimeMillis()-startTime));
        }
        return resultList;
    }
    
    
    /**資料集下載
     * @param packageId
     * @param packageVer
     * @param sDate
     * @param eDate
     * @param chartType
     * @param aggreFun
     * @param ycolumn
     * @param hsnCd
     * @param townCd
     * @param industry
     * @param selfHsnCd
     * @param selfTownCd
     * @param selfIndustry
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/downloadDataset/{packageId}/{packageVerStr}/{sDate}/{eDate}/{chartType}/{ycolumn}/{aggreFun}/{hsnCd}/{townCd}/{industry}/{selfHsnCd}/{selfTownCd}/{selfIndustry}")
    public ResponseEntity<byte[]> downloadDataset(
            @PathVariable("packageId") String packageId,
            @PathVariable("packageVerStr") String packageVer,
            @PathVariable("sDate") String sDate,
            @PathVariable("eDate") String eDate,
            @PathVariable("chartType") String chartType,
            @PathVariable("aggreFun") String aggreFun,
            @PathVariable("ycolumn") String ycolumn,
            @PathVariable("hsnCd") String hsnCd,
            @PathVariable("townCd") String townCd,
            @PathVariable("industry") String industry,
            @PathVariable("selfHsnCd") String selfHsnCd, 
            @PathVariable("selfTownCd") String selfTownCd, 
            @PathVariable("selfIndustry") String selfIndustry,
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
            
            log.info("invoiceSDate:" + sDate);
            log.info("invoiceEDate:" + eDate);
            
            log.info("chartType:" + chartType);
            log.info("aggreFun:" + aggreFun);
            log.info("ycolumn:" + ycolumn);
            
            //目標
            log.info("hsnCd:" + hsnCd);
            log.info("townCd:" + townCd);
            log.info("industry:" + industry);
            
            //所屬
            log.info("selfHsnCd:" + selfHsnCd);
            log.info("selfTownCd:" + selfTownCd);
            log.info("selfIndustry:" + selfIndustry);
            
            //日期要補上/
            sDate = sDate.substring(0,4)+"/"+sDate.substring(4,6)+"/"+sDate.substring(6,8);
            eDate = eDate.substring(0,4)+"/"+eDate.substring(4,6)+"/"+eDate.substring(6,8);

            String ipAddress = request.getRemoteAddr();
            // 取得資料
            List<List<Map<String, Object>>> resultList = getPlotData(packageId, packageVer, ycolumn, aggreFun, sDate, eDate,
                    selfHsnCd, selfTownCd, selfIndustry, hsnCd, townCd, industry, chartType, true, ipAddress);

            if (CollectionUtils.isEmpty(resultList) || CollectionUtils.isEmpty(resultList.get(0))) {
                return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), headers,
                        HttpStatus.NOT_FOUND);
            }

            InputStream in = null;
            if(1==resultList.size()) {
                in = getDatain(chartType, aggreFun, ycolumn, hsnCd, townCd, industry,
                        selfHsnCd, selfTownCd, selfIndustry, headers, resultList.get(0), 1);
                return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
            }else {
                fileName = packageId + "-" + packageVer + ".zip";
                headers.remove("Content-Disposition");
                headers.add("Content-Disposition", "attachment;filename=" + fileName);
                
                ByteArrayOutputStream zipboutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(zipboutputStream);
                addFile("selfResult.csv", getDatain(chartType, aggreFun, ycolumn, hsnCd, townCd, industry,
                        selfHsnCd, selfTownCd, selfIndustry, headers, resultList.get(0), 1), zos);
                addFile("allResult.csv", getDatain(chartType, aggreFun, ycolumn, hsnCd, townCd, industry,
                        selfHsnCd, selfTownCd, selfIndustry, headers, resultList.get(1), 2), zos);
                zos.close();
                return new ResponseEntity<byte[]>(zipboutputStream.toByteArray(), headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 主題文件檔案
     * @param dto
     * @param request
     * @param alerter
     * @return
     */
    @RequestMapping(value = "/findPackageDocument", method = { RequestMethod.POST,
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONArray findPackageDocument(@RequestBody Ods315eInputDto dto, HttpServletRequest request,
            Alerter alerter) {
        String packageId=dto.getPackageId();
        log.debug("findPackageDocument: packageId="+packageId);
        return (JSONArray) JSONSerializer.toJSON(ods703eService.findPackageDocumentByPackageId(packageId));
    }
    
    /**
     * 下載單一主題文件檔案
     * @param packageId
     * @param documentId
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/downloadPackageDocument/{packageId}/{documentId}")
    public ResponseEntity<byte[]> downloadPackageDocument(@PathVariable("packageId") String packageId,@PathVariable("documentId") String documentId, HttpServletRequest request) throws IOException {
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
            OdsPackageDocument odsPackageDocument=ods703eService.getPackageDocumentById(documentId);
            String docPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)+ 
                    "package" + File.separator + "document" + File.separator + packageId + File.separator + odsPackageDocument.getId();
            String fileName = odsPackageDocument.getFilename();            
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            URLCodec codec=new org.apache.commons.codec.net.URLCodec("utf-8");
            headers.add("Content-Disposition", "attachment;filename=" + codec.encode(fileName));
            InputStream in=new FileInputStream(store.getFile(Locations.Persistent.ROOT, docPath));
            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 打包下載主題文件檔案
     * @param packageId
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/downloadPackageDocumentPack/{packageId}")
    public ResponseEntity<byte[]> downloadPackageDocumentPack(@PathVariable("packageId") String packageId, HttpServletRequest request) throws IOException {
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
            List<OdsPackageDocument> odsPackageDocuments=ods703eService.findPackageDocumentByPackageId(packageId);
            String fileName = packageId + ".zip";
            headers.remove("Content-Disposition");
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
            String docPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)+ 
                    "package" + File.separator + "document" + File.separator + packageId + File.separator;
            ByteArrayOutputStream zipboutputStream = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(zipboutputStream);
            for(OdsPackageDocument odsPackageDocument:odsPackageDocuments) {
                addFile(odsPackageDocument.getFilename(), new FileInputStream(store.getFile(Locations.Persistent.ROOT, docPath+odsPackageDocument.getId())), zos);
            }
            zos.close();
            return new ResponseEntity<byte[]>(zipboutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error in ResponseEntity:{}", e);
            return new ResponseEntity<byte[]>(new ByteArrayOutputStream().toByteArray(), new HttpHeaders(),
                    HttpStatus.NOT_FOUND);
        }
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
    
    /**資料集下載之輸出
     * @param chartType
     * @param aggreFun
     * @param ycolumn
     * @param hsnCd 目標縣市
     * @param townCd 目標鄉鎮
     * @param industry 目標主營業項目
     * @param selfHsnCd 所屬縣市
     * @param selfTownCd 所屬鄉鎮市區
     * @param selfIndustry 所屬主營業項目
     * @param headers
     * @param resultList
     * @param targetFlag 1所屬 / 2目標
     * @return InputStream
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private InputStream getDatain(String chartType, String aggreFun, String ycolumn, String hsnCd,
            String townCd, String industry, String selfHsnCd, String selfTownCd,
            String selfIndustry, HttpHeaders headers, List<Map<String, Object>> resultList, int targetFlag)
            throws UnsupportedEncodingException, IOException {
        JSONArray dataArray = (JSONArray) JSONSerializer.toJSON(resultList);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutStream = new BufferedOutputStream(outputStream);
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(bufferedOutStream, "UTF-8"), ',', '\"');
        outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF });
        
        //所屬鄉鎮縣市、行業別名稱
        if (!"all".equals(selfHsnCd)) {
            if (!"all".equals(selfTownCd)) {
                selfTownCd = selfTownCd+"-"+odsOrgCodeRepositoryCustom.findTownNameByCd(selfHsnCd, selfTownCd);
            }
            selfHsnCd = selfHsnCd+"-"+odsOrgCodeRepositoryCustom.findHsnNameByCd(selfHsnCd);
        }
        if (!"all".equals(selfIndustry)) {
            selfIndustry = selfIndustry+"-"+getIndustryMap().get(selfIndustry);
        }
        
        //目標鄉鎮縣市、行業別名稱
        if (StringUtils.isNotEmpty(hsnCd) && !"all".equals(hsnCd)) {
            if (StringUtils.isNotEmpty(townCd) && !"all".equals(townCd)) {
                townCd = townCd+"-"+odsOrgCodeRepositoryCustom.findTownNameByCd(hsnCd, townCd);
            }
            hsnCd = hsnCd+"-"+odsOrgCodeRepositoryCustom.findHsnNameByCd(hsnCd);
        }
        if (StringUtils.isNotEmpty(industry) && !"all".equals(industry)) {
            industry = industry+"-"+getIndustryMap().get(industry);
        }
        
        //資料集鄉鎮縣市、行業別欄位 內容
        String tmpHsnCd = selfHsnCd;
        String tmpTownCd = selfTownCd;
        String tmpIndustry = selfIndustry;
        String targetStr = "所屬";
        if (2==targetFlag) {
            tmpHsnCd = hsnCd;
            tmpTownCd = townCd;
            tmpIndustry = industry;
            targetStr = "目標";
        }
        
        //write title
        if(chartType.equals("basic-table")) {
            writer.writeNext(new String[] {
                    "營業人統編",
                    "資本額",
                    "所屬縣市",
                    "所屬鄉鎮市區",
                    "所屬主營業項目",
                    "所屬平均電子發票B2C金額",
                    "所屬平均電子發票B2B金額",
                    "所屬平均電子發票B2C張數",
                    "所屬平均電子發票B2B張數",
                    "所屬平均電子發票B2C客單價",
                    "所屬平均電子發票B2B客單價",
                    "所屬平均電子發票中獎張數",
                    "所屬平均電子發票中獎金額",
                    "所屬總機構營所稅營業淨利",
                    "所屬總機構營所稅營業收入淨額",
                    "所屬平均營業稅發票申購張數",
                    "所屬平均營業稅銷項總計金額"
            });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                writer.writeNext(new String[] {
                        object.getString("營業人統編"),
                        object.getString("資本額"),
                        selfHsnCd,
                        selfTownCd,
                        selfIndustry,
                        object.containsKey("電子發票B2C金額")?object.getString("電子發票B2C金額"):"查無資料",
                        object.containsKey("電子發票B2B金額")?object.getString("電子發票B2B金額"):"查無資料",
                        object.containsKey("電子發票B2C張數")?object.getString("電子發票B2C張數"):"查無資料",
                        object.containsKey("電子發票B2B張數")?object.getString("電子發票B2B張數"):"查無資料",
                        object.containsKey("電子發票B2C客單價")?object.getString("電子發票B2C客單價"):"查無資料",
                        object.containsKey("電子發票B2B客單價")?object.getString("電子發票B2B客單價"):"查無資料",
                        object.containsKey("電子發票中獎張數")?object.getString("電子發票中獎張數"):"查無資料",
                        object.containsKey("電子發票中獎金額")?object.getString("電子發票中獎金額"):"查無資料",
                        object.containsKey("營所稅營業淨利")?object.getString("營所稅營業淨利"):"查無資料",
                        object.containsKey("營所稅營業收入淨額")?object.getString("營所稅營業收入淨額"):"查無資料",
                        object.containsKey("營業稅發票申購張數")?object.getString("營業稅發票申購張數"):"查無資料",
                        object.containsKey("營業稅銷項總計金額")?object.getString("營業稅銷項總計金額"):"查無資料"
                                        
                });
            }
        }else if("7".equals(ycolumn)){//電子發票金額張數、客單價
            
            if(aggreFun.equals("all")) {
                //B2B,B2C
                aggreFun = "全部";
            }
            writer.writeNext(new String[] {
                    "發票日期",
                    targetStr + "縣市",
                    targetStr + "鄉鎮市區",
                    targetStr + "主營業項目",
                    targetStr + "平均電子發票" + aggreFun + "張數",
                    targetStr + "平均電子發票" + aggreFun + "金額",
                    targetStr + "平均電子發票" + aggreFun + "客單價"
                    //"營業人家數"
                });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                BigDecimal totalAmt=new BigDecimal(object.getString("電子發票金額"));
                BigDecimal totalCnt=new BigDecimal(object.getString("電子發票張數"));
                writer.writeNext(new String[] {
                        object.getString("發票日期"),
                        tmpHsnCd,
                        tmpTownCd,
                        tmpIndustry,
                        object.getString("電子發票張數"),
                        object.getString("電子發票金額"),
                        totalCnt.compareTo(BigDecimal.ZERO) > 0 ? totalAmt.divide(totalCnt, 2, BigDecimal.ROUND_HALF_UP).toString() : BigDecimal.ZERO.toString()
                        //object.getString("營業人家數")
                });
            }
        }else if("1".equals(ycolumn)){
            // 電子發票中獎張數、電子發票中獎金額
            writer.writeNext(new String[] {
                    "發票日期",
                    "所屬縣市",
                    "所屬鄉鎮市區",
                    "所屬主營業項目",
                    "所屬平均電子發票中獎金額",
                    //"營業人家數",
                    "所屬平均電子發票中獎張數"
                });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                writer.writeNext(new String[] {
                        object.getString("發票日期"),
                        selfHsnCd,
                        selfTownCd,
                        selfIndustry,
                        object.getString("電子發票中獎金額"),
                        //object.getString("營業人家數"),
                        object.getString("電子發票中獎張數")
                });
            }
        }else if("6".equals(ycolumn)){
            // 營業稅發票申購張數、營業稅銷項總計金額
            writer.writeNext(new String[] {
                    "所屬年月",
                    targetStr + "縣市",
                    targetStr + "鄉鎮市區",
                    targetStr + "主營業項目",
                    targetStr + "平均營業稅發票申購張數",
                    targetStr + "平均營業稅銷項總計金額"
                });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                writer.writeNext(new String[] {
                        object.getString("所屬年月"),
                        tmpHsnCd,
                        tmpTownCd,
                        tmpIndustry,
                        object.getString("營業稅發票申購張數"),
                        object.getString("營業稅銷項總計金額")
                });
            }
        }else if("5".equals(ycolumn)){
            // 營所稅營業淨利、營所稅營業收入淨額
            writer.writeNext(new String[] {
                    "資料年度",
                    targetStr + "縣市",
                    targetStr + "鄉鎮市區",
                    targetStr + "主營業項目",
                    targetStr + (2==targetFlag ? "平均" : "") + "總機構營所稅營業淨利",
                    targetStr + (2==targetFlag ? "平均" : "") + "總機構營所稅營業收入淨額"
                });
            //write data
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                writer.writeNext(new String[] {
                        object.getString("資料年度"),
                        tmpHsnCd,
                        tmpTownCd,
                        tmpIndustry,
                        object.getString("營所稅營業淨利"),
                        object.getString("營所稅營業收入淨額")
                });
            }
        }
        writer.flush();
        
        log.info("CSVWriter:" + outputStream.toByteArray().length);
        
        InputStream in = new ByteArrayInputStream(outputStream.toByteArray());
        
        writer.close();
        return in;
    }

    public String getBan() {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        ban = BanMask.getInstance().process(ban); // ban 加密
        return ban;
    }
    
    public static Map getIndustryMap() {
        HashMap<String,String> industryMap=new HashMap<String,String>();
        industryMap.put("01", "農、牧業");
        industryMap.put("02", "林業");
        industryMap.put("03", "漁業");
        industryMap.put("06", "砂、石及黏土採取業");
        industryMap.put("07", "其他礦業及土石採取業");
        industryMap.put("08", "食品製造業");
        industryMap.put("09", "飲料製造業");
        industryMap.put("11", "紡織業");
        industryMap.put("12", "成衣及服飾品製造業");
        industryMap.put("13", "皮革、毛皮及其製品製造業");
        industryMap.put("14", "木竹製品製造業");
        industryMap.put("15", "紙漿、紙及紙製品製造業");
        industryMap.put("16", "印刷及資料儲存媒體複製業");
        industryMap.put("17", "石油及煤製品製造業");
        industryMap.put("18", "化學材料製造業");
        industryMap.put("19", "化學製品製造業");
        industryMap.put("20", "藥品及醫用化學製品製造業");
        industryMap.put("21", "橡膠製品製造業");
        industryMap.put("22", "塑膠製品製造業");
        industryMap.put("23", "非金屬礦物製品製造業");
        industryMap.put("24", "基本金屬製造業");
        industryMap.put("25", "金屬製品製造業");
        industryMap.put("26", "電子零組件製造業");
        industryMap.put("27", "電腦、電子產品及光學製品製造業");
        industryMap.put("28", "電力設備製造業");
        industryMap.put("29", "機械設備製造業");
        industryMap.put("30", "汽車及其零件製造業");
        industryMap.put("31", "其他運輸工具及其零件製造業");
        industryMap.put("32", "家具製造業");
        industryMap.put("33", "其他製造業");
        industryMap.put("34", "產業用機械設備維修及安裝業");
        industryMap.put("35", "電力及燃氣供應業");
        industryMap.put("36", "用水供應業");
        industryMap.put("37", "廢（污）水處理業");
        industryMap.put("38", "廢棄物清除、處理及資源回收業");
        industryMap.put("39", "污染整治業");
        industryMap.put("41", "建築工程業");
        industryMap.put("42", "土木工程業");
        industryMap.put("43", "專門營造業");
        industryMap.put("45", "批發業");
        industryMap.put("46", "批發業");
        industryMap.put("47", "零售業");
        industryMap.put("48", "零售業");
        industryMap.put("49", "陸上運輸業");
        industryMap.put("50", "水上運輸業");
        industryMap.put("51", "航空運輸業");
        industryMap.put("52", "運輸輔助業");
        industryMap.put("53", "倉儲業");
        industryMap.put("54", "郵政及快遞業");
        industryMap.put("55", "住宿服務業");
        industryMap.put("56", "餐飲業");
        industryMap.put("58", "出版業");
        industryMap.put("59", "影片服務、聲音錄製及音樂出版業");
        industryMap.put("60", "傳播及節目播送業");
        industryMap.put("61", "電信業");
        industryMap.put("62", "電腦系統設計服務業");
        industryMap.put("63", "資料處理及資訊供應服務業");
        industryMap.put("64", "金融中介業");
        industryMap.put("65", "保險業");
        industryMap.put("66", "證券期貨及其他金融業");
        industryMap.put("67", "不動產開發業");
        industryMap.put("68", "不動產經營及相關服務業");
        industryMap.put("69", "法律及會計服務業");
        industryMap.put("70", "企業總管理機構及管理顧問業");
        industryMap.put("71", "建築、工程服務及技術檢測、分析服務業");
        industryMap.put("72", "研究發展服務業");
        industryMap.put("73", "廣告業及市場研究業");
        industryMap.put("74", "專門設計服務業");
        industryMap.put("76", "其他專業、科學及技術服務業");
        industryMap.put("77", "租賃業");
        industryMap.put("78", "人力仲介及供應業");
        industryMap.put("79", "旅行及相關代訂服務業");
        industryMap.put("80", "保全及私家偵探服務業");
        industryMap.put("81", "建築物及綠化服務業");
        industryMap.put("82", "業務及辦公室支援服務業");
        industryMap.put("85", "教育服務業");
        industryMap.put("86", "醫療保健服務業");
        industryMap.put("90", "創作及藝術表演業");
        industryMap.put("93", "運動、娛樂及休閒服務業");
        industryMap.put("94", "宗教、職業及類似組織");
        industryMap.put("95", "個人及家庭用品維修業");
        industryMap.put("96", "未分類其他服務業");
        return industryMap;
    }
}
