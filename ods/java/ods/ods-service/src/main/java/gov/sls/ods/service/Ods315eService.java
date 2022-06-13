package gov.sls.ods.service;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResource;
import gov.sls.entity.ods.OdsUserResourceVersionDownload;
import gov.sls.entity.ods.QOdsResource;
import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.io.OdsFmtDate;
import gov.sls.ods.repository.OdsOrgCodeRepositoryCustom;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsResourceIndividRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsUserResourceVersionDownloadRepository;
import gov.sls.ods.util.BanMask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ift.DateCellProcessor;

import com.mysema.query.types.expr.BooleanExpression;

@Slf4j
@Service
public class Ods315eService {

    @Autowired
    private OdsResourceRepository odsResourceRepository;

    @Autowired
    private OdsResourceIndividRepository odsResourceIndividRepository;

    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;

    @Autowired
    private OdsOrgCodeRepositoryCustom odsOrgCodeRepositoryCustom;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;

    @Autowired
    private SqlEscapeService sqlEscapeService;

    @Autowired
    private OdsUserResourceVersionDownloadRepository odsUserResourceVersionDownloadRepository;

    /**
     * 基本資料
     * 
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param hsnCd
     *            hsnCd
     * @param townCd
     *            townCd
     * @param industry
     *            industry
     * @return Map<String, Object>
     */
    public Map<String, Object> createForCorporation(String packageId, String packageVer, String invoiceSDate,
            String invoiceEDate, String hsnCd, String townCd, String industry,
            String selfHsnCd, String selfTownCd, String selfIndustry, boolean isDownload, String ipAddress) {
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("hsnCd:" + hsnCd);
        log.info("townCd:" + townCd);
        log.info("industry:" + industry);
        log.info("selfHsnCd:" + selfHsnCd);
        log.info("selfTownCd:" + selfTownCd);
        log.info("selfIndustry:" + selfIndustry);

        Map<String, Object> restMap = new HashMap<String, Object>();
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        restMap.put("營業人統編", ban);
        String maskBan = BanMask.getInstance().process(ban); // ban 加密

        // 資本額
        StringBuilder sql1 = new StringBuilder();
        sql1.append(" select 資本額  ");
        sql1.append(" from " + vatTaxRgstStus());
        sql1.append(" WHERE 營業人統編 = :sellerBan ");
        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("sellerBan", maskBan);
        log.info("資本額sql:" + sql1.toString());
        List<Map<String, Object>> list1 = odsJdbcTemplate.queryForList(
                sql1.toString(), params1);
        if (CollectionUtils.isNotEmpty(list1)) {
            restMap.putAll(list1.get(0));
        }
        createDownload(isDownload, "O1502#D01@ALL$01", packageId, packageVer, ipAddress);

        // 電子發票B2C張數、電子發票B2C金額、電子發票B2B張數 、電子發票B2B金額、營業人家數
        // 地區電子發票B2C張數、地區電子發票B2C金額、地區電子發票B2B張數 、地區電子發票B2B金額、地區營業人家數
        log.info("基本資料-電子發票");
        String aggreFun = "all";//FIX ME 預設為all
        List<Map<String, Object>> list3 = createGraphProportion(packageId, packageVer, aggreFun, hsnCd, townCd, industry, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, true, isDownload, ipAddress);
        if (CollectionUtils.isNotEmpty(list3)) {
            BigDecimal totalB2CInvCnt = (BigDecimal) list3.get(0).get("電子發票B2C張數");
            restMap.put("電子發票B2C張數", totalB2CInvCnt);
            if (null == totalB2CInvCnt) {
                totalB2CInvCnt = BigDecimal.ZERO;
            }
            BigDecimal totalB2CInvAmt = (BigDecimal) list3.get(0).get("電子發票B2C金額");
            restMap.put("電子發票B2C金額", totalB2CInvAmt);
            if (null == totalB2CInvAmt) {
                totalB2CInvAmt = BigDecimal.ZERO;
            }
            BigDecimal totalB2BInvCnt = (BigDecimal) list3.get(0).get("電子發票B2B張數");
            restMap.put("電子發票B2B張數", totalB2BInvCnt);
            if (null == totalB2BInvCnt) {
                totalB2BInvCnt = BigDecimal.ZERO;
            }
            BigDecimal totalB2BInvAmt = (BigDecimal) list3.get(0).get("電子發票B2B金額");
            restMap.put("電子發票B2B金額", totalB2BInvAmt);
            if (null == totalB2BInvAmt) {
                totalB2BInvAmt = BigDecimal.ZERO;
            }
            if ( totalB2CInvCnt.compareTo(BigDecimal.ZERO) > 0 ) {
                BigDecimal avgB2CInvAmt=totalB2CInvAmt.divide(totalB2CInvCnt,0,BigDecimal.ROUND_HALF_UP);
                restMap.put("電子發票B2C客單價", avgB2CInvAmt);
            }
            if ( totalB2BInvCnt.compareTo(BigDecimal.ZERO) > 0 ) {
                BigDecimal avgB2BInvAmt=totalB2BInvAmt.divide(totalB2BInvCnt,0,BigDecimal.ROUND_HALF_UP);
                restMap.put("電子發票B2B客單價", avgB2BInvAmt);
            }

            BigDecimal invCnt = totalB2CInvCnt.add(totalB2BInvCnt);
            BigDecimal invAmt = totalB2CInvAmt.add(totalB2BInvAmt);
            restMap.put("電子發票張數", invCnt);
            restMap.put("電子發票金額", invAmt);
            log.info("invCnt:" + invCnt);
            log.info("invAmt:" + invAmt);
        }
        
        //營所稅營業淨利占比、營所稅營業收入淨額占比、地區營所稅營業淨利占比、地區營所稅營業收入淨額占比
        log.info("基本資料-營所稅");
        List<Map<String, Object>> list4 = createGraphPrcProportion(packageId, packageVer, hsnCd, townCd, industry, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, true, isDownload, ipAddress);
        if (CollectionUtils.isNotEmpty(list4)) {
            BigDecimal prcBusiNetPf = (BigDecimal) list4.get(0).get("營業淨利");
            if (null == prcBusiNetPf) {
                prcBusiNetPf = BigDecimal.ZERO;
            }
            BigDecimal prcBusiRvnuNet = (BigDecimal) list4.get(0).get("營業收入淨額");
            if (null == prcBusiRvnuNet) {
                prcBusiRvnuNet = BigDecimal.ZERO;
            }

            restMap.put("營所稅營業淨利", prcBusiNetPf);
            restMap.put("營所稅營業收入淨額", prcBusiRvnuNet);

            log.info("prcBusiNetPf:" + prcBusiNetPf);
            log.info("prcBusiRvnuNet:" + prcBusiRvnuNet);
        }
        
        //營業稅銷項總計金額占比、營業稅發票申購總計張數占比、地區營業稅銷項總計金額占比、地區營業稅發票申購總計張數占比
        log.info("基本資料-營業稅");
        List<Map<String, Object>> list5 = createGraphBgmProportion(packageId, packageVer, hsnCd, townCd, industry, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, true, isDownload, ipAddress);
        if (CollectionUtils.isNotEmpty(list5)) {
            BigDecimal bgmSaleAmtTotal = (BigDecimal) list5.get(0).get("銷項總計金額");
            if (null == bgmSaleAmtTotal) {
                bgmSaleAmtTotal = BigDecimal.ZERO;
            }
            BigDecimal bgmTotalCount = (BigDecimal) list5.get(0).get("發票申購總計張數");
            if (null == bgmTotalCount) {
                bgmTotalCount = BigDecimal.ZERO;
            }

            restMap.put("營業稅銷項總計金額", bgmSaleAmtTotal);
            restMap.put("營業稅發票申購張數", bgmTotalCount);

            log.info("bgmSaleAmtTotal:" + bgmSaleAmtTotal);
            log.info("bgmTotalCount:" + bgmTotalCount);
        }

        // assign 中獎發票張數、金額 to restMap
        log.info("基本資料-中獎發票");
        List<Map<String, Object>> list6 = createGraphWinning(packageId, packageVer, invoiceSDate, invoiceEDate, selfHsnCd, selfTownCd, selfIndustry, true, isDownload, ipAddress);
        if (CollectionUtils.isNotEmpty(list6)) {
            BigDecimal awardCnt = (BigDecimal) list6.get(0).get("電子發票中獎張數");
            BigDecimal awardAmt = (BigDecimal) list6.get(0).get("電子發票中獎金額");
            if (null == awardCnt) {
                awardCnt = BigDecimal.ZERO;
            }
            if (null == awardAmt) {
                awardAmt = BigDecimal.ZERO;
            }
            restMap.put("電子發票中獎張數", awardCnt);
            restMap.put("電子發票中獎金額", awardAmt);
        }

        return restMap;
    }

    /**
     * 取得自己營業人開立發票資料
     * 
     * @param aggreFun
     *            aggreFun
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd
     *            selfHsnCd
     * @param selfTownCd
     *            selfTownCd
     * @param selfIndustry
     *            selfIndustry
     * @param isBasicInfo
     *            isBasicInfo若為查詢基本資料表，則需要加總所有日期的資料
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createGraph(String packageId, String packageVer, String aggreFun,
            String invoiceSDate, String invoiceEDate, String selfHsnCd,
            String selfTownCd, String selfIndustry, boolean isBasicInfo, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        log.info("aggreFun:" + aggreFun);
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("selfHsnCd:" + selfHsnCd);
        log.info("selfTownCd:" + selfTownCd);
        log.info("selfIndustry:" + selfIndustry);

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        sql.append(" select ");
        if(!isBasicInfo){//非基本資料報表 需要以日期group by
            sql.append(" baninv.發票日期 , DATEPART(year,  baninv.發票日期) as 年, substring(LEFT(CONVERT(VARCHAR, baninv.發票日期, 120), 10),6,2) as 年月, substring(LEFT(CONVERT(VARCHAR, baninv.發票日期, 120), 10),9,2) as 日期,");
        }
        if ("all".equals(aggreFun)) {
            sql.append(" round((sum(baninv.電子發票B2B開立張數) + sum(baninv.電子發票B2C開立張數)) / count(distinct baninv.賣方統一編號 ),0) as 電子發票張數 ");
            sql.append(" , round((sum(baninv.電子發票B2B銷售額) + sum(baninv.電子發票B2C銷售額 )) / count(distinct baninv.賣方統一編號 ),0) as 電子發票金額 ");
        } else {
            sql.append(" round((sum(baninv.電子發票" + aggreFun + "開立張數)) / count(distinct baninv.賣方統一編號 ),0) as 電子發票張數 ");
            sql.append(" , round((sum(baninv.電子發票" + aggreFun + "銷售額 )) / count(distinct baninv.賣方統一編號 ),0) as 電子發票金額 ");
        }
        sql.append(" , count(distinct baninv.賣方統一編號 )as 營業人家數 ");
        sql.append(" from " + einInvoBanSumy() + " as baninv");
        sql.append(" , " + vatTaxRgstStus() + " as company");
        sql.append(" WHERE baninv.發票日期 BETWEEN :invoiceSDate and :invoiceSEate ");
        sql.append(" AND (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan)");
        sql.append(" AND baninv.賣方統一編號 = company.營業人統編");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        if(!isBasicInfo){//非基本資料報表 需要以日期group by
            sql.append(" GROUP BY baninv.發票日期 ");
        }
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        params.put("sellerBan", maskBan);
        log.info("取得自己營業人開立發票資料sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        
        createDownload(isDownload, "O1502#D02@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**
     * 取得電子發票
     * 電子發票B2C張數、電子發票B2C金額、電子發票B2B張數 、電子發票B2B金額
     * 
     * 以下移除
     * 營業人家數
     * 地區電子發票B2C張數、地區電子發票B2C金額、地區電子發票B2B張數 、地區電子發票B2B金額、地區營業人家數
     * 
     * @param aggreFun
     *            aggreFun
     * @param hsnCd
     *            hsnCd
     * @param townCd
     *            townCd
     * @param industry
     *            industry
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd selfHsnCd
     * @param selfTownCd  selfTownCd
     * @param selfIndustry  selfIndustry
     * @param isBasicInfo
     *            isBasicInfo若為查詢基本資料表，則需要加總所有日期的資料
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createGraphProportion(String packageId, String packageVer, String aggreFun, 
            String hsnCd, String townCd, String industry, String invoiceSDate,
            String invoiceEDate, Object selfHsnCd, Object selfTownCd, String selfIndustry, boolean isBasicInfo, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        sql.append("SELECT SUM(電子發票B2B開立張數)  as 電子發票B2C張數 ");
        sql.append(" , SUM(電子發票B2C開立張數) as 電子發票B2C金額  ");
        sql.append(" , SUM(電子發票B2B銷售額) as 電子發票B2B張數 ");
        sql.append(" , SUM(電子發票B2C銷售額) as 電子發票B2B金額  ");
//        sql.append(" , round(b.地區電子發票B2C開立張數 / b.營業人家數,2) as 地區電子發票B2C張數  ");
//        sql.append(" , round(b.地區電子發票B2C銷售額 / b.營業人家數,2) as 地區電子發票B2C金額  ");
//        sql.append(" , round(b.地區電子發票B2B開立張數 / b.營業人家數,2) as 地區電子發票B2B張數  ");
//        sql.append(" , round(b.地區電子發票B2B銷售額 / b.營業人家數,2) as 地區電子發票B2B金額  ");
//        sql.append(" , b.營業人家數 as 地區營業人家數  ");
        sql.append(" from    ");

        sql.append(" ( select 發票日期 , ");
        sql.append("    round(SUM(電子發票B2B開立張數) / count(banSumy.賣方統一編號),0) AS 電子發票B2B開立張數,  ");
        sql.append("    round(SUM(電子發票B2C開立張數) / count(banSumy.賣方統一編號),0) AS 電子發票B2C開立張數,  ");
        sql.append("    round(SUM(電子發票B2B銷售額) / count(banSumy.賣方統一編號),0) AS 電子發票B2B銷售額,  ");
        sql.append("    round(SUM(電子發票B2C銷售額) / count(banSumy.賣方統一編號),0) AS 電子發票B2C銷售額 ");
        sql.append("    from " + einInvoBanSumy() + " banSumy , " + vatTaxRgstStus() + " as company ");
        sql.append("    where banSumy.發票日期 BETWEEN :invoiceSDate and :invoiceSEate");
        sql.append("    and (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan) ");
        sql.append("    and banSumy.賣方統一編號 = company.營業人統編  ");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        sql.append("    group by 發票日期  ");
        sql.append("   ) a ");
        
        params.put("sellerBan", maskBan);
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("取得基本資料電子發票sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D02@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**
     * 取得營業人營所稅佔比
     * 營所稅營業淨利占比、營所稅營業收入淨額占比、地區營所稅營業淨利占比、地區營所稅營業收入淨額占比
     * @param hsnCd
     *            hsnCd
     * @param townCd
     *            townCd
     * @param industry
     *            industry
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd selfHsnCd
     * @param selfTownCd selfTownCd
     * @param selfIndustry selfIndustry
     * @param isBasicInfo
     *            isBasicInfo若為查詢基本資料表，則需要加總所有日期的資料
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createGraphPrcProportion(String packageId, String packageVer, 
            String hsnCd, String townCd, String industry, String invoiceSDate,
            String invoiceEDate, Object selfHsnCd, Object selfTownCd, String selfIndustry, boolean isBasicInfo, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        //營所稅 為每年一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 4)+"/01/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 4)+"/01/01";
        }

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select ");
        sql.append(" SUM(營業淨利)  AS 營業淨利,  ");
        sql.append(" SUM(營業收入淨額) AS 營業收入淨額 ");
        sql.append(" from " + ePrcBanSumy() + " banSumy ");
        sql.append(" where banSumy.資料年度 BETWEEN :invoiceSDate and :invoiceSEate");
        sql.append(" and banSumy.營業人統編 = :sellerBan  ");
        params.put("sellerBan", maskBan);
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);

        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("取得營業人營所稅sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D05@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**
     * 取得營業人營業稅佔比
     * 營業稅銷項總計金額占比、營業稅發票申購總計張數占比、地區營業稅銷項總計金額占比、地區營業稅發票申購總計張數占比 
     * @param hsnCd
     *            hsnCd
     * @param townCd
     *            townCd
     * @param industry
     *            industry
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd selfHsnCd
     * @param selfTownCd selfTownCd
     * @param selfIndustry selfIndustry
     * @param isBasicInfo
     *            isBasicInfo若為查詢基本資料表，則需要加總所有日期的資料
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createGraphBgmProportion(String packageId, String packageVer, 
            String hsnCd, String townCd, String industry, String invoiceSDate,
            String invoiceEDate, Object selfHsnCd, Object selfTownCd, String selfIndustry, boolean isBasicInfo, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        //營業稅 為每期一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 7)+"/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 7)+"/01";
        }

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select ");
        sql.append("    round(SUM(銷項總計金額) / count(banSumy.營業人統編),0) AS 銷項總計金額,  ");
        sql.append("    round(SUM(發票申購總計張數) / count(banSumy.營業人統編),0) AS 發票申購總計張數  ");
        
        sql.append("    from " + eBgmBanSumy() + " banSumy , " + vatTaxRgstStus() + " as company ");
        sql.append("    where banSumy.所屬年月 BETWEEN :invoiceSDate and :invoiceSEate");
        sql.append("    and (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan) ");
        sql.append("    and banSumy.營業人統編 = company.營業人統編  ");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        params.put("sellerBan", maskBan);
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("selfHsnCd:" + selfHsnCd);
        log.info("selfTownCd:" + selfTownCd);
        log.info("selfIndustry:" + selfIndustry);
        log.info("取得營業人營業稅sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D07@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }
    
    /**
     * 營業人地區行業別開立電子發票彙總
     * 
     * @param aggreFun
     *            aggreFun
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param townCd
     *            townCd
     * @param hsnCd
     *            hsnCd
     * @param industry
     *            industry
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createSecondYaxis(String packageId, String packageVer, String aggreFun,
            String invoiceSDate, String invoiceEDate, String townCd,
            String hsnCd, String industry, boolean isDownload, String ipAddress) {
        log.info("aggreFun:" + aggreFun);
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("hsnCd:" + hsnCd);
        log.info("townCd:" + townCd);
        log.info("industry:" + industry);
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select 發票日期 , DATEPART(year,  發票日期) as 年, substring(LEFT(CONVERT(VARCHAR, 發票日期, 120), 10),6,2) as 年月, substring(LEFT(CONVERT(VARCHAR, 發票日期, 120), 10),9,2) as 日期");
        if("B2B".equals(aggreFun)){
            sql.append(" , round(sum(電子發票" + aggreFun + "開立張數) / sum(B2B營業人家數 ),0) as 電子發票張數 ");
            sql.append(" , round(sum(電子發票" + aggreFun + "銷售額 ) / sum(B2B營業人家數 ),0) as 電子發票金額 ");
            sql.append(" , sum(B2B營業人家數 )as 營業人家數 ");
        }else if("B2C".equals(aggreFun)){
            sql.append(" , round(sum(電子發票" + aggreFun + "開立張數) / sum(B2C營業人家數 ),0) as 電子發票張數 ");
            sql.append(" , round(sum(電子發票" + aggreFun + "銷售額 ) / sum(B2C營業人家數 ),0) as 電子發票金額 ");
            sql.append(" , sum(B2C營業人家數 )as 營業人家數 ");
        }else {
            sql.append(" , round((sum(電子發票B2B開立張數) + sum(電子發票B2C開立張數)) / sum(營業人家數 ),0) as 電子發票張數 ");
            sql.append(" , round((sum(電子發票B2B銷售額) + sum(電子發票B2C銷售額 ))  / sum(營業人家數 ),0) as 電子發票金額 ");
            sql.append(" , sum(營業人家數 )as 營業人家數 ");
        }
        sql.append(" from " + einInvoDistrictSumy());
        sql.append(" WHERE 發票日期 BETWEEN :invoiceSDate and :invoiceSEate ");
        if (!"all".equals(hsnCd)) {
            sql.append(" AND 縣市代號 = :hsnCd ");
            params.put("hsnCd", hsnCd);
            if (!"all".equals(townCd)) {
                sql.append(" AND 鄉鎮代號 = :townCd ");
                params.put("townCd", townCd);
            }
        }
        if (StringUtils.isNotEmpty(industry)) {
            sql.append(" AND 營業項目代號  = :industry ");
            params.put("industry", industry);
        }
        if("B2B".equals(aggreFun)){
            sql.append(" AND B2B營業人家數 > 5 "); 
        }else if("B2C".equals(aggreFun)){
            sql.append(" AND B2C營業人家數 > 5 ");
        }else {
            sql.append(" AND 營業人家數 > 5 ");
        }
        
        sql.append(" GROUP BY 發票日期 ");

        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("營業人地區行業別開立電子發票彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D04@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**
     * 取得中獎發票、金額
     * @param packageId   packageId  
     * @param packageVer  packageVer 
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd     selfHsnCd   
     * @param selfTownCd    selfTownCd  
     * @param selfIndustry  selfIndustry
     * @param isBasicInfo
     *            isBasicInfo若為查詢基本資料表，則需要加總所有日期的資料
     * @param isDownload  isDownload
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createGraphWinning(String packageId, String packageVer, String invoiceSDate,
            String invoiceEDate, Object selfHsnCd, Object selfTownCd, String selfIndustry, boolean isBasicInfo, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        if(isBasicInfo){
            sql.append(" select ");
        }else{
            sql.append(" select award.發票歸屬期別年月 as 發票日期, ");
            sql.append(" DATEPART(year, award.發票歸屬期別年月) as 年, ");
            sql.append(" substring(LEFT(CONVERT(VARCHAR, award.發票歸屬期別年月, 120), 10),6,2) as 年月, ");
            sql.append(" substring(LEFT(CONVERT(VARCHAR, award.發票歸屬期別年月, 120), 10),9,2) as 日期, ");
        }
        sql.append(" round(SUM(award.電子發票中獎金額) / count(distinct award.賣方統一編號),0) AS 電子發票中獎金額 , ");
        sql.append(" round(SUM(award.電子發票中獎張數) / count(distinct award.賣方統一編號),0) AS 電子發票中獎張數 , ");
        sql.append(" count(distinct award.賣方統一編號) AS 營業人家數  ");
        sql.append(" from " + awardInvoBanSumy() + " as award ");
        sql.append(" , " + vatTaxRgstStus() + " as company");

        sql.append(" WHERE award.發票歸屬期別年月 BETWEEN :invoiceSDate and :invoiceSEate ");
        sql.append(" and (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan)");
        sql.append(" and award.賣方統一編號 = company.營業人統編 ");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        if(isBasicInfo){
        }else{
            sql.append(" GROUP BY 發票歸屬期別年月  ");
        }
        params.put("sellerBan", maskBan);
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("取得中獎發票、金額彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D03@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**取得營業人縣市鄉鎮行業清單
     * @param ban maskBAN
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findSellCity(String ban) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select 縣市代號, 縣市名稱 , 鄉鎮代號 , 鄉鎮名稱, [營業項目代號 (2碼)], [營業項目名稱 (2碼)] ");
        sql.append(" ,CASE 總機構營業人統編  WHEN 營業人統編  THEN 1  ELSE 2  END headBan ");
        
        sql.append(" from " + vatTaxRgstStus());
        sql.append(" WHERE 營業人統編 = :sellerBan or 總機構營業人統編= :sellerBan ");
        sql.append(" order by headBan,縣市代號, 鄉鎮代號 , [營業項目代號 (2碼)] ");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sellerBan", ban);
        List<Map<String, Object>> reList = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        return reList;
    }
    

    /**
     * 取得營業人營所稅彙總
     * 
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd
     *            selfHsnCd
     * @param selfTownCd
     *            selfTownCd
     * @param selfIndustry
     *            selfIndustry
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createPrcGraph(String packageId, String packageVer, 
            String invoiceSDate, String invoiceEDate, String selfHsnCd,
            String selfTownCd, String selfIndustry, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        //營所稅 為每年一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 4)+"/01/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 4)+"/01/01";
        }
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("selfHsnCd:" + selfHsnCd);
        log.info("selfTownCd:" + selfTownCd);
        log.info("selfIndustry:" + selfIndustry);

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select baninv.資料年度 , DATEPART(year,  baninv.資料年度) as 年");
        sql.append(" , sum(baninv.營業淨利) as 營所稅營業淨利 ");
        sql.append(" , sum(baninv.營業收入淨額 )as 營所稅營業收入淨額 ");
        sql.append(" , count(baninv.營業人統編 )as 營業人家數 ");
        sql.append(" from " + ePrcBanSumy() + " as baninv");
        sql.append(" , " + vatTaxRgstStus() + " as company");
        sql.append(" WHERE baninv.資料年度  BETWEEN :invoiceSDate and :invoiceSEate ");
        sql.append(" AND (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan)");
        sql.append(" AND baninv.營業人統編 = company.營業人統編");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        sql.append(" GROUP BY baninv.資料年度 ");
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        params.put("sellerBan", maskBan);
        log.info("取得營業人營所稅彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D05@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }
    /**
     * 營業人地區行業別營所稅彙總
     * 
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param townCd
     *            townCd
     * @param hsnCd
     *            hsnCd
     * @param industry
     *            industry
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createPrcSecondYaxis(String packageId, String packageVer, 
            String invoiceSDate, String invoiceEDate, String townCd,
            String hsnCd, String industry, boolean isDownload, String ipAddress) {
        //營所稅 為每年一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 4)+"/01/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 4)+"/01/01";
        }
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("hsnCd:" + hsnCd);
        log.info("townCd:" + townCd);
        log.info("industry:" + industry);
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select 資料年度 , DATEPART(year,  資料年度) as 年");
        sql.append(" , round(sum(營業淨利) / sum(營業人家數), 0) as 營所稅營業淨利 ");
        sql.append(" , round(sum(營業收入淨額 ) / sum(營業人家數), 0) as 營所稅營業收入淨額  ");
        sql.append(" , sum(營業人家數)as 營業人家數 ");
        sql.append(" from " + ePrcHsnBscdSumy() );
        sql.append(" WHERE 資料年度  BETWEEN :invoiceSDate and :invoiceSEate ");
        if (!"all".equals(hsnCd)) {
            sql.append(" AND 縣市代號 = :hsnCd ");
            params.put("hsnCd", hsnCd);
            if (!"all".equals(townCd)) {
                sql.append(" AND 鄉鎮代號 = :townCd ");
                params.put("townCd", townCd);
            }
        }
        if (StringUtils.isNotEmpty(industry)) {
            sql.append(" AND 營業項目代號  = :industry ");
            params.put("industry", industry);
        }

        sql.append(" AND 營業人家數 > 5 ");
        sql.append(" GROUP BY 資料年度 ");

        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("營業人地區行業別營所稅彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D06@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }

    /**
     * 取得營業人營業稅彙總
     * 所屬年月
     * 營業人統編
     * 銷項總計金額
     * 發票申購總計張數
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param selfHsnCd
     *            selfHsnCd
     * @param selfTownCd
     *            selfTownCd
     * @param selfIndustry
     *            selfIndustry
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createBgmGraph(String packageId, String packageVer, 
            String invoiceSDate, String invoiceEDate, String selfHsnCd,
            String selfTownCd, String selfIndustry, boolean isDownload, String ipAddress) {
        SlsUser user = UserHolder.getUser();
        String ban = user.getBan();
        String maskBan = BanMask.getInstance().process(ban); // ban 加密
        //營業稅 為每期一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 7)+"/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 7)+"/01";
        }
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("selfHsnCd:" + selfHsnCd);
        log.info("selfTownCd:" + selfTownCd);
        log.info("selfIndustry:" + selfIndustry);

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select baninv.所屬年月 , DATEPART(year,  baninv.所屬年月) as 年, substring(LEFT(CONVERT(VARCHAR, baninv.所屬年月, 120), 10),6,2) as 年月");
        sql.append(" , round(sum(baninv.銷項總計金額) / count(baninv.營業人統編 ),0) as 營業稅銷項總計金額 ");
        sql.append(" , round(sum(baninv.發票申購總計張數 ) / count(baninv.營業人統編 ),0) as 營業稅發票申購張數 ");
        sql.append(" , count(baninv.營業人統編 )as 營業人家數 ");
        sql.append(" from " + eBgmBanSumy() + " as baninv");
        sql.append(" , " + vatTaxRgstStus() + " as company");
        sql.append(" WHERE baninv.所屬年月  BETWEEN :invoiceSDate and :invoiceSEate ");
        sql.append(" AND (company.營業人統編 = :sellerBan or company.總機構營業人統編= :sellerBan)");
        sql.append(" AND baninv.營業人統編 = company.營業人統編");
        if (!"all".equals(selfHsnCd)) {
            sql.append(" AND company.縣市代號 = :selfHsnCd ");
            params.put("selfHsnCd", selfHsnCd);
            if (!"all".equals(selfTownCd)) {
                sql.append(" AND company.鄉鎮代號 = :selfTownCd ");
                params.put("selfTownCd", selfTownCd);
            }
        }
        if (!"all".equals(selfIndustry)) {
            sql.append(" AND company.[營業項目代號 (2碼)]  = :selfIndustry ");
            params.put("selfIndustry", selfIndustry);
        }
        sql.append(" GROUP BY baninv.所屬年月 ");
        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        params.put("sellerBan", maskBan);
        log.info("取得營業人營業稅彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D07@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }
    /**
     * 地區行業別營業稅彙總
     * 
     * @param invoiceSDate
     *            invoiceSDate
     * @param invoiceEDate
     *            invoiceEDate
     * @param townCd
     *            townCd
     * @param hsnCd
     *            hsnCd
     * @param industry
     *            industry
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> createBgmSecondYaxis(String packageId, String packageVer, 
            String invoiceSDate, String invoiceEDate, String townCd,
            String hsnCd, String industry, boolean isDownload, String ipAddress) {
        //營業稅 為每期一筆
        if(StringUtils.isNotEmpty(invoiceSDate)){
            invoiceSDate = invoiceSDate.substring(0, 7)+"/01";
        }
        if(StringUtils.isNotEmpty(invoiceEDate)){
            invoiceEDate = invoiceEDate.substring(0, 7)+"/01";
        }
        log.info("invoiceSDate:" + invoiceSDate);
        log.info("invoiceEDate:" + invoiceEDate);
        log.info("hsnCd:" + hsnCd);
        log.info("townCd:" + townCd);
        log.info("industry:" + industry);
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" select 所屬年月 , DATEPART(year,  所屬年月) as 年, substring(LEFT(CONVERT(VARCHAR, 所屬年月, 120), 10),6,2) as 年月");
        sql.append(" , round(sum(銷項總計金額) / sum(營業人家數), 0) as 營業稅銷項總計金額 ");
        sql.append(" , round(sum(發票申購總計張數 ) / sum(營業人家數), 0) as 營業稅發票申購張數 ");
        sql.append(" , sum(營業人家數)as 營業人家數 ");
        sql.append(" from " + eBgmHsnBscdSumy() );
        sql.append(" WHERE 所屬年月 BETWEEN :invoiceSDate and :invoiceSEate ");
        if (!"all".equals(hsnCd)) {
            sql.append(" AND 縣市代號 = :hsnCd ");
            params.put("hsnCd", hsnCd);
            if (!"all".equals(townCd)) {
                sql.append(" AND 鄉鎮代號 = :townCd ");
                params.put("townCd", townCd);
            }
        }
        if (StringUtils.isNotEmpty(industry)) {
            sql.append(" AND 營業項目代號  = :industry ");
            params.put("industry", industry);
        }

        sql.append(" AND 營業人家數 > 5 ");
        sql.append(" GROUP BY 所屬年月 ");

        params.put("invoiceSDate", invoiceSDate);
        params.put("invoiceSEate", invoiceEDate);
        log.info("地區行業別營業稅彙總sql:" + sql.toString());
        List<Map<String, Object>> restlist = odsJdbcTemplate.queryForList(
                sql.toString(), params);
        createDownload(isDownload, "O1502#D08@ALL$01", packageId, packageVer, ipAddress);
        return restlist;
    }
    
    /**根據viewId建立download紀錄
     * @param isDownload isDownload 
     * @param viewId     viewId     
     * @param packageId  packageId  
     * @param packageVer packageVer 
     * @param ipAddress  ipAddress  
     */
    private void createDownload(boolean isDownload, String viewId, String packageId, String packageVer, String ipAddress){
        log.info("isDownload " + isDownload);
        if (isDownload) {
            List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository.findPackResInfoByIdAndVerAndViewId(packageId, Integer.parseInt(packageVer), viewId);
            log.info("pkgResList size:" + pkgResList.size());
            if(CollectionUtils.isNotEmpty(pkgResList)){
                Ods703eTab2DialogDto ods703eTab2DialogDto = pkgResList.get(0);
                SlsUser user = UserHolder.getUser();
                OdsUserResourceVersionDownload odsUserResourceVersionDownload = new OdsUserResourceVersionDownload();
                odsUserResourceVersionDownload.setUserId(user.getId());
                odsUserResourceVersionDownload.setPackageId(packageId);
                odsUserResourceVersionDownload.setPackageVer(Integer.parseInt(packageVer));
                odsUserResourceVersionDownload.setResourceId(ods703eTab2DialogDto.getResourceId());
                odsUserResourceVersionDownload.setResourceVer(Integer.parseInt(ods703eTab2DialogDto.getResourceVer()));
                odsUserResourceVersionDownload.setIpAddress(ipAddress);
                odsUserResourceVersionDownload.setCreated(new Date());
                odsUserResourceVersionDownload.setCreateUserId(user.getId());
                odsUserResourceVersionDownload.setFormat("dataset");
                if (!user.getRoles().isEmpty()) {
                    odsUserResourceVersionDownload.setUserRole(String.valueOf(user
                            .getRoles().get(0)));
                }
                log.info("odsUserResourceVersionDownloadRepository.create");
                odsUserResourceVersionDownloadRepository.create(odsUserResourceVersionDownload);
            }
        }
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
    
    /**DAN送ODS營業稅營業人稅籍檔狀態
     * @return String table name
     */
    private String vatTaxRgstStus(){
        return "ODS_" + getDbName("O1502#D01@ALL$01").replaceAll("-", "_");
    }
    
    /**DAN送ODS電子發票開立營業人彙總檔
     * @return String table name
     */
    private String einInvoBanSumy(){
        return "ODS_" + getDbName("O1502#D02@ALL$01").replaceAll("-", "_");
    }
    
    /**DAN送ODS電子發票中獎發票含營業人彙總
     * @return String table name
     */
    private String awardInvoBanSumy(){
        return "ODS_" + getDbName("O1502#D03@ALL$01").replaceAll("-", "_");
    }
    
    /**DAN送ODS電子發票開立地區彙總檔
     * @return String table name
     */
    private String einInvoDistrictSumy(){
        return "ODS_" + getDbName("O1502#D04@ALL$01").replaceAll("-", "_");
    }

    /** DAN送ODS營業人營所稅彙總E_PRC_BAN_SUMY
     * @return String table name 
     */
    private String ePrcBanSumy() {
        return "ODS_" + getDbName("O1502#D05@ALL$01").replaceAll("-", "_");
    }

    /** DAN送ODS地區行業別營所稅彙總E_PRC_HSN_BSCD_SUMY
     * @return String table name
     */
    private String ePrcHsnBscdSumy() {
        return "ODS_" + getDbName("O1502#D06@ALL$01").replaceAll("-", "_");
    }

    /** DAN送ODS營業人營業稅彙總E_BGM_BAN_SUMY
     * @return String table name 
     */
    private String eBgmBanSumy() {
        return "ODS_" + getDbName("O1502#D07@ALL$01").replaceAll("-", "_");
    }

    /** DAN送ODS地區行業別營業稅彙總E_BGM_HSN_BSCD_SUMY
     * @return String table name
     */
    private String eBgmHsnBscdSumy() {
        return "ODS_" + getDbName("O1502#D08@ALL$01").replaceAll("-", "_");
    }
}
