package gov.sls.ods.service;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsUserResourceVersionDownload;
import gov.sls.entity.ods.OdsXcaDonateGoal;
import gov.sls.entity.ods.OdsXcaDonateGoalPK;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsUserResourceVersionDownloadRepository;
import gov.sls.ods.repository.OdsXcaDonateGoalRepository;
import gov.sls.ods.util.BanMask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods314eService {
    
    @Autowired
    private OdsXcaDonateGoalRepository odsXcaDonateGoalRepository;
    
    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    
    @Autowired
    private OdsUserResourceVersionDownloadRepository odsUserResourceVersionDownloadRepository;
    
    @Autowired
    private OdsResourceRepository odsResourceRepository;


    /**查詢受捐贈機關或團體資料
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param sDate       sDate      
     * @param eDate       eDate      
     * @param ipAddress   ipAddress  
     * @param isDownload  isDownload 
     * @return List<Map<String, Object>> 發票年,發票年月,縣市,鄉鎮市區,行業別,載具類別名稱,載具名稱,電子發票捐贈張數,電子發票中獎張數,電子發票中獎金額
     */
    public List<Map<String, Object>> createPlotDataDownload(String packageId, String packageVer, String sDate, String eDate, String ipAddress, boolean isDownload) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                SlsUser user = UserHolder.getUser();
                String ban = user.getBan();
                String maskBan = BanMask.getInstance().process(ban);
                log.debug("BAN:" + ban + " maskBan:" + maskBan);
                
                String sql = "SELECT SUBSTRING(發票年月,1,4) AS 發票年,發票年月,"
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END \"縣市\", "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END \"鄉鎮市區\", "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END \"行業別\", "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END \"載具類別名稱\", "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END \"載具名稱\", "
                        
                        + "電子發票捐贈張數,電子發票中獎張數,電子發票中獎金額 "
                        + "FROM ODS_"
                        + sqlEscapeService.escapeMsSql(pkgRes.getResourceId().replaceAll("-", "_")) + " WHERE ODS_RESOURCE_VER = :odsResourceVer "
//                        + "AND 受捐贈機關或團體統編 = :ban ";
                        + "AND 社福團體統編 = :ban ";
                if (!Strings.isNullOrEmpty(sDate)) {
                    sql = sql + "AND 發票年月 >= :sDate ";
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    sql = sql + "AND 發票年月 <= :eDate ";
                }
                
                Map<String, Object> where = new HashMap<String, Object>();
                where.put("odsResourceVer", pkgRes.getResourceVer());
                where.put("ban", maskBan);
                if (!Strings.isNullOrEmpty(sDate)) {
                    where.put("sDate", sDate);
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    where.put("eDate", eDate);
                }
                
                log.info("sql:" + sql);
                log.info("where1:" + pkgRes.getResourceVer());
                log.info("where2:" + maskBan);
                log.info("where3:" + sDate);
                log.info("where4:" + eDate);
                
                resultList = odsJdbcTemplate.queryForList(sql,where);                            
                
                //寫入ODS_USER_RESOURCE_VERSION_DOWNLOAD
                log.info("isDownload " + isDownload);
                if (isDownload) {
                    OdsUserResourceVersionDownload odsUserResourceVersionDownload = new OdsUserResourceVersionDownload();
                    odsUserResourceVersionDownload.setUserId(user.getId());
                    odsUserResourceVersionDownload.setPackageId(packageId);
                    odsUserResourceVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                    odsUserResourceVersionDownload.setResourceId(pkgRes.getResourceId());
                    odsUserResourceVersionDownload.setResourceVer(Integer.parseInt(pkgRes.getResourceVer()));
                    odsUserResourceVersionDownload.setIpAddress(ipAddress);
                    odsUserResourceVersionDownload.setCreated(new Date());
                    odsUserResourceVersionDownload.setCreateUserId(user.getId());
                    odsUserResourceVersionDownload.setFormat("dataset");
                    
                    if (!user.getRoles().isEmpty()) {
                        odsUserResourceVersionDownload.setUserRole(String.valueOf(user
                                .getRoles().get(0)));
                    }
                    odsUserResourceVersionDownloadRepository.create(odsUserResourceVersionDownload);
                }

                break;
            } else {
                continue;
            }
        }

        return resultList;
    }

    /**查詢受捐贈機關或團體資料 outer join 整體受捐贈機關或團體
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param sDate       sDate      
     * @param eDate       eDate      
     * @param ipAddress   ipAddress 
     * @param isDownload   isDownload  
     * @return List<Map<String, Object>> 發票年,發票年月,縣市,鄉鎮市區,行業別,載具類別名稱,載具名稱,電子發票捐贈張數,電子發票中獎張數,電子發票中獎金額
     */
    public List<Map<String, Object>> createPlotData(String packageId, String packageVer, String sDate, String eDate, String ipAddress, boolean isDownload) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                SlsUser user = UserHolder.getUser();
                String ban = user.getBan();
                String maskBan = BanMask.getInstance().process(ban);
                log.debug("BAN:" + ban + " maskBan:" + maskBan);
                String sql = "";
                sql += "SELECT alldata.發票年, alldata.發票年月, alldata.縣市, alldata.鄉鎮市區, alldata.行業別,";
                sql += "       alldata.載具類別名稱, alldata.載具名稱, isnull(p.電子發票捐贈張數, 0) 電子發票捐贈張數,";
                sql += "       isnull(p.電子發票中獎張數, 0) 電子發票中獎張數, isnull(p.電子發票中獎金額, 0) 電子發票中獎金額 ";
                sql += " from ";
                sql += "(SELECT SUBSTRING(發票年月,1,4) AS 發票年,發票年月,"
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END \"縣市\", "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END \"鄉鎮市區\", "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END \"行業別\", "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END \"載具類別名稱\", "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END \"載具名稱\", "
                        
                        + "round(sum(電子發票捐贈張數)/count(1), 0) \"電子發票捐贈張數\","
                        + "round(sum(電子發票中獎張數)/count(1), 0) \"電子發票中獎張數\","
                        + "round(sum(電子發票中獎金額)/count(1), 0) \"電子發票中獎金額\""
                        + " FROM " + danB2cXcaDntSumy() 
                        + " WHERE ODS_RESOURCE_VER = :odsResourceVer ";
                if (!Strings.isNullOrEmpty(sDate)) {
                    sql = sql + "AND 發票年月 >= :sDate ";
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    sql = sql + "AND 發票年月 <= :eDate ";
                }
                
                sql = sql + "group by SUBSTRING(發票年月,1,4), 發票年月, "
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END , "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END , "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END , "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END , "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END) as alldata left outer join";
                
                Map<String, Object> where = new HashMap<String, Object>();
                where.put("odsResourceVer", pkgRes.getResourceVer());
                if (!Strings.isNullOrEmpty(sDate)) {
                    where.put("sDate", sDate);
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    where.put("eDate", eDate);
                }
                
                sql += "(SELECT SUBSTRING(發票年月,1,4) AS 發票年,發票年月,"
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END \"縣市\", "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END \"鄉鎮市區\", "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END \"行業別\", "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END \"載具類別名稱\", "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END \"載具名稱\", "
                        
                        + "電子發票捐贈張數,電子發票中獎張數,電子發票中獎金額 "
                        + " FROM " + danB2cXcaDntSumy()
                        + " WHERE ODS_RESOURCE_VER = :odsResourceVer "
                              //  + "AND 受捐贈機關或團體統編 = :ban ";
                        + "AND 社福團體統編= :ban ";
                if (!Strings.isNullOrEmpty(sDate)) {
                    sql = sql + "AND 發票年月 >= :sDate2 ";
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    sql = sql + "AND 發票年月 <= :eDate2 ";
                }
                sql += ") p on alldata.發票年 = p.發票年 and alldata.發票年月 = p.發票年月 and alldata.縣市 = p.縣市";
                sql += " and alldata.鄉鎮市區 = p.鄉鎮市區 and alldata.行業別 = p.行業別";
                sql += " and alldata.載具類別名稱  = p.載具類別名稱  and alldata.載具名稱 = p.載具名稱";
                
                where.put("odsResourceVer", pkgRes.getResourceVer());
                where.put("ban", maskBan);
                if (!Strings.isNullOrEmpty(sDate)) {
                    where.put("sDate2", sDate);
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    where.put("eDate2", eDate);
                }
                
                log.info("sql:" + sql);
                log.info("where1:" + pkgRes.getResourceVer());
                log.info("where2:" + maskBan);
                log.info("where3:" + sDate);
                log.info("where4:" + eDate);
                
                resultList = odsJdbcTemplate.queryForList(sql,where);

                
                //寫入ODS_USER_RESOURCE_VERSION_DOWNLOAD
                log.info("isDownload " + isDownload);
                if (isDownload) {
                    OdsUserResourceVersionDownload odsUserResourceVersionDownload = new OdsUserResourceVersionDownload();
                    odsUserResourceVersionDownload.setUserId(user.getId());
                    odsUserResourceVersionDownload.setPackageId(packageId);
                    odsUserResourceVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                    odsUserResourceVersionDownload.setResourceId(pkgRes.getResourceId());
                    odsUserResourceVersionDownload.setResourceVer(Integer.parseInt(pkgRes.getResourceVer()));
                    odsUserResourceVersionDownload.setIpAddress(ipAddress);
                    odsUserResourceVersionDownload.setCreated(new Date());
                    odsUserResourceVersionDownload.setCreateUserId(user.getId());
                    odsUserResourceVersionDownload.setFormat("dataset");
                    
                    if (!user.getRoles().isEmpty()) {
                        odsUserResourceVersionDownload.setUserRole(String.valueOf(user
                                .getRoles().get(0)));
                    }
                    odsUserResourceVersionDownloadRepository.create(odsUserResourceVersionDownload);
                }

                break;
            } else {
                continue;
            }
        }

        return resultList;
    }

    /**查詢整體受捐贈機關或團體資料
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param sDate       sDate      
     * @param eDate       eDate      
     * @param ipAddress   ipAddress  
     * @return List<Map<String, Object>> 發票年,發票年月,縣市,鄉鎮市區,行業別,載具類別名稱,載具名稱,電子發票捐贈張數,電子發票中獎張數,電子發票中獎金額
     */
    public List<Map<String, Object>> getAllSocialData(String packageId, String packageVer, String sDate, String eDate, String ipAddress) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                String sql = "SELECT SUBSTRING(發票年月,1,4) AS 發票年,發票年月,"
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END \"縣市\", "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END \"鄉鎮市區\", "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END \"行業別\", "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END \"載具類別名稱\", "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END \"載具名稱\", "
                        
                        + "round(sum(電子發票捐贈張數)/count(1), 0) \"電子發票捐贈張數\","
                        + "round(sum(電子發票中獎張數)/count(1), 0) \"電子發票中獎張數\","
                        + "round(sum(電子發票中獎金額)/count(1), 0) \"電子發票中獎金額\""
                        + " FROM " + danB2cXcaDntSumy()
                        + " WHERE ODS_RESOURCE_VER = :odsResourceVer ";
                if (!Strings.isNullOrEmpty(sDate)) {
                    sql = sql + "AND 發票年月 >= :sDate ";
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    sql = sql + "AND 發票年月 <= :eDate ";
                }
                
                sql = sql + "group by SUBSTRING(發票年月,1,4), 發票年月, "
                        + "CASE 縣市 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 縣市 "
                        + "END , "

                        + "CASE 鄉鎮市區 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 鄉鎮市區 "
                        + "END , "
                        
                        + "CASE 行業別 "
                        + "WHEN '' THEN '其他' "
                        + "WHEN null THEN '其他' "
                        + "ELSE 行業別 "
                        + "END , "

                        + "CASE 載具類別名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具類別名稱 "
                        + "END , "
                        
                        + "CASE 載具名稱 "
                        + "WHEN '' THEN '捐贈碼' "
                        + "WHEN null THEN '捐贈碼' "
                        + "ELSE 載具名稱 "
                        + "END ";
                
                Map<String, Object> where = new HashMap<String, Object>();
                where.put("odsResourceVer", pkgRes.getResourceVer());
                if (!Strings.isNullOrEmpty(sDate)) {
                    where.put("sDate", sDate);
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    where.put("eDate", eDate);
                }
                
                log.info("sql:" + sql);
                log.info("where1:" + pkgRes.getResourceVer());
                log.info("where2:" + sDate);
                log.info("where3:" + eDate);
                
                resultList = odsJdbcTemplate.queryForList(sql,where);
                break;
            } else {
                continue;
            }
        }

        return resultList;
    }
    
    

    /**查詢指定年月捐贈張數、中獎張數、中獎金額
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param hasBan 判斷條件是否加入登入者統編
     * @param startYyyyMm 年月
     * @param endYyyyMm 年月
     * @return String
     */
    public List<Map<String, Object>> getDonateCompare(String packageId, String packageVer, boolean hasBan, String startYyyyMm, String endYyyyMm) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                String sql = "SELECT 發票年月, "
                        + "SUM(電子發票捐贈張數) as 捐贈張數, "
                        + "SUM(電子發票中獎張數) as 中獎張數, "
                        + "SUM(電子發票中獎金額) as 中獎金額 "
                        + " FROM " + danB2cXcaDntSumy() 
                        + " WHERE ODS_RESOURCE_VER = :odsResourceVer ";
                Map<String, Object> where = new HashMap<String, Object>();
                where.put("odsResourceVer", pkgRes.getResourceVer());
                
                if (!Strings.isNullOrEmpty(startYyyyMm) && !Strings.isNullOrEmpty(endYyyyMm)) {
                    sql = sql + " AND :startYyyyMm <= 發票年月 ";
                    sql = sql + " AND 發票年月 <= :endYyyyMm ";
                    where.put("startYyyyMm", startYyyyMm);
                    where.put("endYyyyMm", endYyyyMm);
                }
                if (hasBan){
                    SlsUser user = UserHolder.getUser();
                    String ban = user.getBan();
                    String maskBan = BanMask.getInstance().process(ban);
                    log.debug("BAN:" + ban + " maskBan:" + maskBan);
                    //sql = sql + " AND 受捐贈機關或團體統編 = :ban ";
                    sql = sql + " AND 社福團體統編 = :ban ";
                    where.put("ban", maskBan);
                }
                sql = sql + " group by 發票年月 order by 發票年月";

                log.info("sql:" + sql);
                log.info("startYyyyMm:" + startYyyyMm);
                log.info("endYyyyMm:" + endYyyyMm);
                
                resultList = odsJdbcTemplate.queryForList(sql,where);                            
                break;
            } else {
                continue;
            }
        } 
        return resultList;
    }
    
    /**查詢年度捐贈目標張數
     * @param packageId  packageId  
     * @param packageVer packageVer 
     * @param yyyy     yyyy     
     * @return String
     */
    public BigDecimal getYearDonateCntGoal(String packageId, String packageVer, String yyyy) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban);
        log.debug("BAN:" + ban + " maskBan:" + maskBan + " yyyy:" + yyyy);

        OdsXcaDonateGoalPK id = new OdsXcaDonateGoalPK(maskBan, yyyy);
        OdsXcaDonateGoal findOne = odsXcaDonateGoalRepository.findOne(id);
        
        BigDecimal donateAwardCnt = BigDecimal.ZERO;
        if(null != findOne){
            donateAwardCnt = findOne.getDonateCount();
            if (null == donateAwardCnt){
                donateAwardCnt = BigDecimal.ZERO;
            }
        }
        return donateAwardCnt;
    }
    
    /**設定年度捐贈目標張數
     * @param packageId  packageId  
     * @param packageVer packageVer 
     * @param yyyy     yyyy     
     * @param donateCount     BigDecimal     
     * @return String
     */
    public OdsXcaDonateGoal saveYearDonateCntGoal(String packageId,
            String packageVer, String yyyy, BigDecimal donateCount) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban);
        log.debug("BAN:" + ban + " maskBan:" + maskBan);

        OdsXcaDonateGoal goal = new OdsXcaDonateGoal();
        goal.setAssociationBan(maskBan);
        goal.setYear(yyyy);
        goal.setDonateCount(donateCount);
        goal = odsXcaDonateGoalRepository.findOne(goal.getId());
        log.info("findOne:" + goal);
        if (null != goal) {
            log.debug("save=====");
            goal.setDonateCount(donateCount);
            goal = odsXcaDonateGoalRepository.save(goal);
        } else {
            goal = new OdsXcaDonateGoal();
            goal.setAssociationBan(maskBan);
            goal.setYear(yyyy);
            goal.setDonateCount(donateCount);
            log.debug("create====");
            goal = odsXcaDonateGoalRepository.create(goal);
        }
        return goal;
    }
    
    /** 依據viewId取得 resource ID
     * @param viewId viewId
     * @return String resource ID
     */
    public String getDbName(String viewId) {
        BooleanExpression criteria = QOdsResource.odsResource.viewId.eq(viewId);
        OdsResource odsResource = odsResourceRepository.findOne(criteria);
        String resourceId = "";
        if (odsResource != null) {
            resourceId = odsResource.getId();
        }
        return resourceId;
    }

    /** 依據viewId取得 OdsResource
     * @param viewId viewId
     * @return OdsResource
     */
    public OdsResource getOdsResource(String viewId) {
        BooleanExpression criteria = QOdsResource.odsResource.viewId.eq(viewId);
        return odsResourceRepository.findOne(criteria);
    }
    
    /**DAN送ODS受捐贈機關或團體捐贈彙總檔
     * @return String table name
     */
    private String danB2cXcaDntSumy(){
        return "ODS_" + sqlEscapeService.escapeMsSql(getDbName("O1503#D01@ALL$01").replaceAll("-", "_"));
    }
}
