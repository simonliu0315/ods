package gov.sls.ods.web.controller;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.PackageMetadataDto;
import gov.sls.ods.dto.TemplateGeneratorDto;
import gov.sls.ods.service.Ods304eService;
import gov.sls.ods.web.dto.Ods703e02Dto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.report.Report;
import com.cht.commons.report.Report.Builder;
import com.cht.commons.report.springmvc.ReportMediaType;
import com.google.common.base.Strings;

@Slf4j
@PreAuthorize("hasAuthority('AUTHORITY_ODS703E')")
//@PreAuthorize("isAuthenticated()")
@Controller
@RequestMapping("ODS703E")
public class Ods703eReportController {
    
    @Autowired
    private Ods304eService ods304eService;

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;

    @Inject
    private ApplicationContext applicationContext;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    FileStore fileStore;
    
    @RequestMapping(value = "/print", produces = ReportMediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody Report print(Ods703e02Dto dto) throws IOException {
        Builder builder = Report.builder();
        List<Map<String, Object>> reportData = new ArrayList<Map<String, Object>>();
        String basePath = dto.getCtx();
        String reportName = "ods703p";
        
        TemplateGeneratorDto templateGeneratorDto = ods304eService.generateTemplate(dto.getPackageId(),
                dto.getVer());
        
        String url = null;
        
        if("02".equals(templateGeneratorDto.getOdsPackage().getType())){
            url = basePath + "/ods-main/ODS311E/" + templateGeneratorDto.getOdsPackage().getId()+"/1/02/"+templateGeneratorDto.getOdsPackage().getCode()+"/";
        } else {
            url = basePath + "/ods-main/ODS303E/" + dto.getPackageId() + "/" + dto.getVer() + "/";
        }
        
        log.debug("title:" + dto.getName());
        log.debug("description:" + dto.getDescription());
        log.debug("url_link:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("title", dto.getName());
        params.put("description", dto.getDescription());
        params.put("url_link", url);
        builder.forReport(reportName).addParameters(params);
        builder.bindData(reportData);
        
        //bind ods703p2
        Report report = generatePlainReport(builder, dto, templateGeneratorDto);
        
        return report;
    }
    
    private Report generatePlainReport(Builder builder, Ods703e02Dto formBean, TemplateGeneratorDto templateGeneratorDto)
            throws IOException {
        String reportName = "ods304p";
        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
        
        String basePath = formBean.getCtx();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("title", templateGeneratorDto.getPublishPackage().getName());
        params.put("description", templateGeneratorDto.getPublishPackage().getDescription());
        params.put("showAtt", "yes");
        
        if("02".equals(templateGeneratorDto.getOdsPackage().getType())){
            params.put("url_link", basePath + "/ods-main/ODS311E/" + templateGeneratorDto.getOdsPackage().getId()+"/1/02/"+templateGeneratorDto.getOdsPackage().getCode()+"/");
        } else {
            params.put("url_link", basePath + "/ods-main/ODS303E/" + formBean.getPackageId() + "/" + formBean.getVer() + "/");
        }

        List<Map<String, Object>> reportData = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> reportData1 = new ArrayList<Map<String, Object>>();
        for (PackageMetadataDto meta : templateGeneratorDto.getPackageMetadata()) {
            map = new HashMap<String, Object>();
            map.put("dataKey", meta.getDataKey());
            if ("date".equals(meta.getDataType()) && !Strings.isNullOrEmpty(meta.getDataValue()) && meta.getDataValue().length() == 7) {
                map.put("dataValue", meta.getDataValue().substring(0, 3) + "年" + meta.getDataValue().substring(3, 5) + "月" + meta.getDataValue().substring(5) + "日");
            } else {
                map.put("dataValue", meta.getDataValue());
            }
            reportData1.add(map);
        }
        params.put("metadata", reportData1);

        String tagString = "";
        for (int i = 0; i < templateGeneratorDto.getPackageTags().size(); i++) {
            OdsPackageTag tag = templateGeneratorDto.getPackageTags().get(i);
            if (i == templateGeneratorDto.getPackageTags().size() - 1) {
                tagString += tag.getTagName() + " ";
            } else {
                tagString += tag.getTagName() + ", ";
            }
        }
        params.put("tags", tagString);
        reportData.add(map);
        for (List<PackageAndResourceDto> listDto : templateGeneratorDto.getMainData()) {
            int i = 0;
            map = new HashMap<String, Object>();
            for (PackageAndResourceDto pard : listDto) {
                log.debug("pard:" + pard);
                log.debug("pattern:" + listDto.size());
                map.put("pattern", ""+listDto.size());
                map.put("format" + i, pard.getFormat());
                File f = fileStore.getFile(Locations.Persistent.ROOT, publicPath + "resource/" + pard.getResourceId() + "/image/"
                        + pard.getResourceId() + "-" + pard.getResourceVer() + ".png");
                if (f.exists()) {
                    map.put("imagePath" + i, applicationContext
                            .getResource(f.toURI().toString()).getInputStream());
                }
                map.put("row" + i + "_name", pard.getName());
                if ("common".equals(pard.getFormat())) {
                    map.put("row" + i + "_description",
                            pard.getDescription());
                    map.put("row" + i + "_name", "");
                } else {
                    map.put("row" + i + "_description",
                            pard.getResourceDescription());    
                }
                
                //process dataset, list top 10 datas                
                if ("dataset".equals(pard.getFormat())) {
                    List<Map<String, Object>> grid = null;
                    Map<String, Object> where = new HashMap<String, Object>();
                    where.put("odsResourceVer", pard.getResourceVer());
                    log.debug("sql:" + "SELECT TOP 10 * FROM ODS_" + pard.getResourceId().replaceAll("-", "_") + " WHERE ODS_RESOURCE_VER = " + pard.getResourceVer());
                    grid = odsJdbcTemplate.queryForList(
                            "SELECT TOP 10 * FROM ODS_"
                                    + pard.getResourceId().replaceAll("-", "_") + " WHERE ODS_RESOURCE_VER = :odsResourceVer",
                                    where);
                    for (Map<String, Object> map2 : grid) {
                        map2.remove("ODS_RESOURCE_VER");
                    }
                    if (grid.size() > 0) {
                        pard.setGridTitle(grid.get(0).keySet());
                    }
                    if("02".equals(templateGeneratorDto.getOdsPackage().getType())){
                        pard.setGridData(new ArrayList<Map<String,Object>>());
                    } else {
                        pard.setGridData((List<Map<String, Object>>)grid);
                    }
                    
                    List<Map<String, Object>> gridDatas = new ArrayList<Map<String, Object>>();
                    Map<String, Object> gridTitle = new HashMap<String, Object>();
                    int j = 1;
                    for (String title : pard.getGridTitle()) {                        
                        gridTitle.put("field" + j++, title);
                        int n = j - 1;
                        log.debug("title field" + n + ":" + gridTitle.get("field" + n));
                    }
                    gridDatas.add(gridTitle);
                    j = 1;
                    for (Map<String, Object> map2 : pard.getGridData()) {
                        Map<String, Object> gridData = new HashMap<String, Object>();
                        for (Map.Entry<String, Object> m : map2.entrySet()) {
                            String fieldValue = String.valueOf(m.getValue());
                            if (m.getValue() == null || Strings.isNullOrEmpty(fieldValue) || "null".equalsIgnoreCase(fieldValue)) {
                                fieldValue = "";
                            }
                            gridData.put("field" + j++, fieldValue);
                            int n = j - 1;
                            log.debug("field" + n + ":" + m.getKey() + "/" + gridData.get("field" + n));
                        }
                        j = 1;
                        gridDatas.add(gridData);
                    }
                    map.put("gridDatas" + i, gridDatas);
                } else {
                    map.put("gridDatas" + i, "");
                }
                
                i++;
                
            }
            reportData.add(map);
        }
        builder.forReport(reportName).addParameters(params);
        builder.bindData(reportData);

        return builder.build();

    }
}
