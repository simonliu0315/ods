package gov.sls.ods.service;

import gov.sls.commons.core.security.user.SlsUser;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsResourceIndivid;
import gov.sls.entity.ods.OdsUserResourceVersionDownload;
import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsResourceIndividRepository;
import gov.sls.ods.repository.OdsUserResourceVersionDownloadRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods310eService {
    
    @Autowired
    private OdsResourceIndividRepository odsResourceIndividRepository;
    
    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
    
    @Autowired
    private OdsUserResourceVersionDownloadRepository odsUserResourceVersionDownloadRepository;

    public Page<Ods303eIndividualDto> getResourceDate(Pageable pageable) {
        SlsUser user = UserHolder.getUser();
                
        return odsResourceIndividRepository.getResourceDate(user.getBarCode(), pageable);
    }
    
    public boolean getDanExportStatus(String packageId, String packageVer){
        SlsUser user = UserHolder.getUser();
        String userUnifyId = user.getBarCode();
        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                List<OdsResourceIndivid> list = odsResourceIndividRepository.queryDanExportStatus(userUnifyId, pkgRes.getResourceId());
                if(list.isEmpty()){
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Map<String, Object> queryDatasetInfo(String packageId, String packageVer) {
        SlsUser user = UserHolder.getUser();
        String userUnifyId = user.getBarCode();
        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                StringBuilder sql = new StringBuilder();
                sql.append(" select CONVERT(char(10), max(發票日期), 111) AS maxInvoiceDate, ");
                sql.append("        CONVERT(char(10), min(發票日期), 111) AS minInvoiceDate ");
                sql.append(" from ");
                sql.append(        "ODS_" + sqlEscapeService.escapeMsSql(pkgRes.getResourceId().replaceAll("-", "_")));
                sql.append(" WHERE ");
                sql.append("       ODS_RESOURCE_VER = :odsResourceVer ");
                sql.append("       AND USER_UNIFY_ID = :userUnifyId ");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("odsResourceVer", pkgRes.getResourceVer());
                params.put("userUnifyId", userUnifyId);
                List<Map<String, Object>> list = odsJdbcTemplate.queryForList(sql.toString(), params);
                if(list.isEmpty()){
                    return null;
                } else {
                    return list.get(0);
                }
            }
        }
        
        return null;
        
    }

    public List<Map<String, Object>> createPlotData(String packageId, String packageVer, String sDate, String eDate, String ipAddress, boolean isDownload) {

        List<Ods703eTab2DialogDto> pkgResList = odsPackageResourceRepository
                .findPackResInfoByIdAndVer(packageId, Integer.parseInt(packageVer));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Ods703eTab2DialogDto pkgRes : pkgResList) {
            if ("dataset".equals(pkgRes.getFormat())) {
                SlsUser user = UserHolder.getUser();
//                user.setBarCode("00000"); //for test
                String userUnifyId = user.getBarCode();
                
                String sql = "SELECT USER_UNIFY_ID, 載具名稱, 縣市, 鄉鎮市區, 商店種類, 載具別, 電子發票張數, 電子發票金額, "
                        + "捐贈發票張數, 捐贈發票金額, 中獎發票張數, 中獎獎項金額, "
                        + "RIGHT('0' + (CONVERT(VARCHAR(2), DATEPART(DAY, 發票日期))), 2) AS 發票日期, "
                        + "CONVERT(char(10), 發票日期, 111) AS 完整發票日期,"
                        + "DATENAME(YEAR, 發票日期) AS 年, "
                        + "RIGHT('0' + (CONVERT(VARCHAR(2), DATEPART(month, 發票日期))), 2) AS 年月, "
                        + "(CASE WHEN (DATEPART(WEEKDAY, 發票日期) = 1 OR DATEPART(WEEKDAY, 發票日期) = 7) THEN '週末' ELSE '工作日' END) AS 工作日類別, "
                        + "CEILING((DATEPART(DAY, 發票日期) + (DATEPART(WEEKDAY, CONVERT(char(7), 發票日期, 111)+'/01') - 1))/7.0) AS 當月週次 "
                        + "FROM ODS_"
                        + sqlEscapeService.escapeMsSql(pkgRes.getResourceId().replaceAll("-", "_")) + " WHERE ODS_RESOURCE_VER = :odsResourceVer "
                        + "AND USER_UNIFY_ID = :userUnifyId ";
                if (!Strings.isNullOrEmpty(sDate)) {
                    sql = sql + "AND 發票日期 >= :sDate ";
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    sql = sql + "AND 發票日期 <= :eDate ";
                }
                
                Map<String, Object> where = new HashMap<String, Object>();
                where.put("odsResourceVer", pkgRes.getResourceVer());
                where.put("userUnifyId", userUnifyId);
                if (!Strings.isNullOrEmpty(sDate)) {
                    where.put("sDate", sDate);
                }
                if (!Strings.isNullOrEmpty(eDate)) {
                    where.put("eDate", eDate);
                }
                
                log.info("sql:" + sql);
                log.info("where1:" + pkgRes.getResourceVer());
                log.info("where2:" + userUnifyId);
                log.info("where3:" + sDate);
                log.info("where4:" + eDate);
                
                resultList = odsJdbcTemplate.queryForList(sql,where);                            

                for (Map<String, Object> map : resultList) {
                    map.remove("ODS_RESOURCE_VER");
                }
                
                //寫入ODS_USER_RESOURCE_VERSION_DOWNLOAD
                log.info("isDownload" + isDownload);
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
}
