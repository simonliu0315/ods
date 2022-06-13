package gov.sls.ods.repository;

import gov.sls.ods.dto.EPaper;
import gov.sls.ods.dto.Ods302eDto;
import gov.sls.ods.dto.Ods703eGridDto;
import gov.sls.ods.dto.Ods707eGridDto;
import gov.sls.ods.dto.PackageInfo;
import gov.sls.ods.dto.PortalStat;
import gov.sls.ods.dto.RSSItem;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

public class OdsPackageRepositoryImpl implements OdsPackageRepositoryCustom{
    
    @Autowired
    private SqlExecutor executor;

    @Override
    public List<Ods703eGridDto> findPackageAndVersion(String name, String description) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("A.*,")
               .append("B.VER ver, B.VERSION_DATETIME versionDatetime")
               .append("FROM ODS_PACKAGE A")
               .append("LEFT OUTER JOIN ODS_PACKAGE_VERSION B ON A.ID = B.PACKAGE_ID")
               .append("AND B.VER IN")
               .append("(SELECT MAX(C.VER) FROM ODS_PACKAGE_VERSION C WHERE C.PACKAGE_ID = A.ID)")
               .appendWhen((!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(description)), "WHERE")
               .appendWhen(!Strings.isNullOrEmpty(name), "A.NAME LIKE :name", "%" + name + "%")
               .appendWhen((!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(description)), "AND")
               .appendWhen(!Strings.isNullOrEmpty(description), "A.DESCRIPTION LIKE :description", "%" + description + "%")
               .append("ORDER BY A.CREATED DESC");

        Query query = builder.build();
        return executor.queryForList(query, Ods703eGridDto.class);
    }
    @Override
    public List<Ods302eDto> findPackage(String packageName, List<String> tagList,
            List<String> fileExtList, int orderByType, List<String> idList) {

        Query.Builder builder = Query.builder();
        int firstConditionFlag = 0;// 用來判斷是否要加上AND
        builder.expandIterableParameters(true)
                .append("SELECT P.id, P.IMAGE_URL imageUrl, P.NAME name, P.DESCRIPTION, p.TYPE, P.CODE, ")
                .append("(SELECT MAX(VER) FROM ODS_PACKAGE_VERSION PV WHERE PV.PACKAGE_ID = P.ID AND PV.IS_PUBLISHED = 'true') latestVer")
                .append("FROM ODS_PACKAGE P");

        // 1.2 關鍵字為條件查詢主題檔。
        if (StringUtils.isNotEmpty(packageName)) {
            builder.append("WHERE ");
            builder.appendWhen(StringUtils.isNotEmpty(packageName), "P.name like :packageName", "%"
                    + packageName + "%");
            firstConditionFlag = 1;
        }

        // 1.3 分類標籤為條件查詢主題檔
        if (!CollectionUtils.isEmpty(tagList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.append("EXISTS(SELECT *")
                    .append("      FROM ODS_PACKAGE_TAGS T")
                    .append("      WHERE P.ID = T.PACKAGE_ID")
                    .appendWhen(!CollectionUtils.isEmpty(tagList), "AND T.TAG_NAME in (:tagList)",
                            tagList)
                    .append(")");
        }
        // 1.4 檔案格式為條件查詢主題檔。
        if (!CollectionUtils.isEmpty(fileExtList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.append("EXISTS(SELECT *")
                    .append("      FROM ODS_PACKAGE_RESOURCE PR")
                    .append("      WHERE EXISTS(SELECT *")
                    .append("                   FROM ODS_RESOURCE R")
                    .append("                   WHERE PR.RESOURCE_ID = R.ID")
                    .append("                   AND P.ID = PR.PACKAGE_ID")
                    .appendWhen(!CollectionUtils.isEmpty(fileExtList),
                            "AND R.FORMAT in (:fileExtList)", fileExtList)
                    .append("                   ))                                              ");
        }
        // 1.5 依使用者資料權查詢主題檔。
        if (!CollectionUtils.isEmpty(idList)) {
            if (1 == firstConditionFlag) {
                builder.append("AND");
            } else {
                builder.append("WHERE ");
                firstConditionFlag = 1;
            }
            builder.appendWhen(!CollectionUtils.isEmpty(idList), "P.ID in (:idList)",
                    idList);
        }
        // 主題版本檔至少需要有一筆為publish才可
        if (1 == firstConditionFlag) {
            builder.append("AND");
        } else {
            builder.append("WHERE ");
            firstConditionFlag = 1;
        }
        builder.append("EXISTS(SELECT *")
                .append("      FROM ODS_PACKAGE_VERSION PV")
                .append("      WHERE P.ID = PV.PACKAGE_ID")
                .append("      AND PV.IS_PUBLISHED = 'true'")
                .append(")");

        if (0 == orderByType) {
            // 依所查詢之主題結果各別的主題明細檔之 更新時間 進行排序
//            builder.append("ORDER BY (SELECT PV_MAX_CREATED.created")
//                    .append("         FROM (SELECT P.id,")
//                    .append("               Max(PV.created) CREATED")
//                    .append("               FROM   ods_package P, ods_package_version PV")
//                    .append("               WHERE P.id = PV.package_id")
//                    .append("               GROUP BY P.id) PV_MAX_CREATED")
//                    .append("         WHERE P.id = PV_MAX_CREATED.id) DESC");
        } else if (1 == orderByType) {
            // 依所查詢之主題結果各別的主題群組與主題關聯檔與使用者瀏覽主題版本計數檔的計數加總結果進行排序
//            builder.append("ORDER BY (SELECT PV_MAX_CLICK.CLICK")
//                    .append("         FROM (SELECT P.ID,")
//                    .append("                      COUNT(UPVC.PACKAGE_ID) CLICK")
//                    .append("               FROM ODS_PACKAGE P,")
//                    .append("                    ODS_PACKAGE_VERSION PV")
//                    .append("                    LEFT JOIN")
//                    .append("                    ODS_USER_PACKAGE_VERSION_CLICK UPVC")
//                    .append("                           ON ( PV.PACKAGE_ID = UPVC.PACKAGE_ID")
//                    .append("                                AND PV.VER = UPVC.PACKAGE_VER )")
//                    .append("               WHERE P.ID = PV.PACKAGE_ID")
//                    .append("               GROUP BY P.ID) PV_MAX_CLICK")
//                    .append("         WHERE P.ID = PV_MAX_CLICK.ID) DESC");
        }
        Log.info("UAASQL:" + builder.toString());
        Query query = builder.build();
        return executor.queryForList(query, Ods302eDto.class);
    }

    @Override
    public List<Ods707eGridDto> findPackages(String name, String desc, String sDate, String eDate) {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("A.ID as packageId, A.NAME as packageName, A.DESCRIPTION as packageDescription")
               .append("FROM ODS_PACKAGE A")
//               .appendWhen((!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(desc) || 
//                       !Strings.isNullOrEmpty(sDate) || !Strings.isNullOrEmpty(eDate)), "WHERE")
               .appendWhen((!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(desc)), "WHERE")
               .appendWhen(!Strings.isNullOrEmpty(name), "A.NAME LIKE :name", "%" + name + "%")
               .appendWhen((!Strings.isNullOrEmpty(name) && (!Strings.isNullOrEmpty(desc) || 
                       !Strings.isNullOrEmpty(sDate) || !Strings.isNullOrEmpty(eDate))), "AND")
               .appendWhen(!Strings.isNullOrEmpty(desc), "A.DESCRIPTION LIKE :description", "%" + desc + "%")
//               .appendWhen((!Strings.isNullOrEmpty(desc) &&
//                       (!Strings.isNullOrEmpty(sDate) || !Strings.isNullOrEmpty(eDate))), "AND")
//               .appendWhen(!Strings.isNullOrEmpty(sDate), "A.UPDATED >= :sDate", sDate)
//               .appendWhen((!Strings.isNullOrEmpty(sDate) && !Strings.isNullOrEmpty(eDate)), "AND")
//               .appendWhen(!Strings.isNullOrEmpty(eDate), "A.UPDATED <= :eDate", eDate + " 23:59:59")
               .append(" ORDER BY A.CREATED");

        Query query = builder.build();
        return executor.queryForList(query, Ods707eGridDto.class);
    }
    
    @Override
    public void updateImageUrl(String packageId) {
        Query.Builder builder = Query.builder();
        builder.append("UPDATE ODS_PACKAGE")
               .append("SET IMAGE_URL = :imageUrl", packageId + ".png")
               .append("WHERE ID = :packageId", packageId);

        Query query = builder.build();
        executor.update(query);
    }
    
    @Override
    public List<PortalStat> findPackageCount() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("COUNT(1) AS number")
               .append("FROM ODS_PACKAGE A")
               .append("WHERE A.UPDATED >= GETDATE()-6")
               .append("AND A.UPDATED <= GETDATE()");

        Query query = builder.build();
        return executor.queryForList(query, PortalStat.class);
    }
    @Override
    public List<PortalStat> findAllPackageCount() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("COUNT(1) AS number")
               .append("FROM ODS_PACKAGE P")
               .append("WHERE EXISTS(SELECT *")
               .append("      FROM ODS_PACKAGE_VERSION PV")
               .append("      WHERE P.ID = PV.PACKAGE_ID")
               .append("      AND PV.IS_PUBLISHED = 'true'")
               .append(")");
        Query query = builder.build();
        return executor.queryForList(query, PortalStat.class);
    }
    
    @Override
    public List<EPaper> findEPaper(Date preDate) {
        Query.Builder builder = Query.builder();
        /*builder.append("SELECT")
               .append("A.ID as packageId, B.NAME as name, B.DESCRIPTION as description, A.IMAGE_URL as imageUrl")
               .append("FROM ODS_PACKAGE A, ODS_PACKAGE_VERSION B")
               .append("WHERE A.ID=B.PACKAGE_ID")
               .append("AND B.UPDATED >= :preDate", preDate)
               .append("AND B.UPDATED <= GETDATE()");*/
        builder.append("SELECT")
               .append("A.ID as packageId, MAX_VER_PKG.ver as packageVer, B.NAME as name, B.DESCRIPTION as description, A.IMAGE_URL as imageUrl,")
               .append("A.CODE, A.TYPE")
               .append("FROM ODS_PACKAGE A, ODS_PACKAGE_VERSION B,")
               .append("    (SELECT")
               .append("     PACKAGE_ID, MAX(VER) as VER")
               .append("     FROM ODS_PACKAGE_VERSION C")
               .append("     WHERE C.UPDATED >= :preDate", preDate)
               .append("     AND C.IS_PUBLISHED = 'true'")
               .append("     AND C.UPDATED <= GETDATE()")
               .append("     GROUP BY PACKAGE_ID) MAX_VER_PKG")
               .append("WHERE B.PACKAGE_ID = MAX_VER_PKG.PACKAGE_ID")
               .append("AND B.VER = MAX_VER_PKG.VER")
               .append("AND A.ID = B.PACKAGE_ID");
        

        Query query = builder.build();
        return executor.queryForList(query, EPaper.class);
    }
    
    @Override
    public List<RSSItem> findRss() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("B.NAME as title, B.DESCRIPTION as description, B.PACKAGE_ID as packageId")
               .append("FROM ODS_PACKAGE A, ODS_PACKAGE_VERSION B")
               .append("WHERE A.ID=B.PACKAGE_ID")
               .append("AND B.IS_PUBLISHED = 1")
               .append("AND B.UPDATED >= GETDATE() - 6")
               .append("AND B.UPDATED <= GETDATE()");

        Query query = builder.build();
        return executor.queryForList(query, RSSItem.class);
    }
    
    @Override
    public List<PackageInfo> findPackageInfo() {
        Query.Builder builder = Query.builder();
        builder.append("SELECT")
               .append("A.ID as packageId, A.NAME as packageName")
               .append("FROM ODS_PACKAGE A");

        Query query = builder.build();
        return executor.queryForList(query, PackageInfo.class);
    }
    
}
