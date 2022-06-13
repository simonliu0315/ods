package gov.sls.ods.web.controller;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.ods.Messages;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.PackageMetadataDto;
import gov.sls.ods.dto.TemplateGeneratorDto;
import gov.sls.ods.service.Ods304eService;
import gov.sls.ods.service.UaaAuthoriy;
import gov.sls.ods.web.dto.Ods304eFormBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cht.commons.report.Report;
import com.cht.commons.report.Report.Builder;
import com.cht.commons.report.ReportException;
import com.cht.commons.report.springmvc.ReportMediaType;
import com.cht.commons.security.Authority;
import com.cht.commons.web.Alerter;
import com.google.common.base.Strings;

@Slf4j
@Controller
@RequestMapping("ODS304E")
public class Ods304eReportController {

    @Autowired
    private Ods304eService ods304eService;

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;

    @Inject
    private ApplicationContext applicationContext;
    
    @Autowired
    FileStore fileStore;
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;

    @RequestMapping(value = "/downloadPdf", method = RequestMethod.GET)
    public void downloadPdf(Ods304eFormBean formBean, HttpServletRequest request,
            HttpServletResponse response, Alerter alerter) throws IOException {
        log.debug("ODS304E" + formBean);
        //UAA權限控管
        Collection<? extends Authority> findResultList = uaaAuthoriy.findDataAuthorityByUserId(UserHolder.getUser().getId());
        boolean found = false;
        for (Authority authority: findResultList) {
            if (formBean.getPackageId().equals(authority.getId())) {
                found = true;
                break;
            }
        }
        if (found) {
        
            if (request.isSecure()) {
                formBean.setUrl("https://" + request.getServerName() + ":" + request.getServerPort()
                        + request.getContextPath() + "/ODS303E/" + formBean.getPackageId() + "/"
                        + formBean.getPackageVer() + "/");
            } else {
                formBean.setUrl("http://" + request.getServerName() + ":" + request.getServerPort()
                        + request.getContextPath() + "/ODS303E/" + formBean.getPackageId() + "/"
                        + formBean.getPackageVer() + "/");
            }
            Report report = generatePlainReport(formBean, "ods304p");
            response.setContentType(ReportMediaType.APPLICATION_PDF_VALUE);
            response.setHeader("Content-Disposition",
                    (new StringBuilder()).append("attachment;filename=\"").append("ods304p.pdf").toString());
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                JasperExportManager.exportReportToPdfStream(report.getJasperPrints().get(0),
                        outputStream);
                outputStream.flush();
                // parameters.put(JRExporterParameter.OUTPUT_STREAM, outputStream);
                // exporter.setParameters(parameters);
                log.debug("Start to export report.");
                // exporter.exportReport();
                log.debug("Exporting report was finished.");
                // outputStream.flush();
            } catch (JRException e) {
                throw new ReportException("Cannot render report.", e);
            } catch (IOException e) {
                throw new ReportException(
                        "Cannot render report while pre processing or post processing outputStream.", e);
            }
            
        } else {
            alerter.fatal(Messages.fatal_unauthorized());
        }
    }

    private Report generatePlainReport(Ods304eFormBean formBean, String reportName)
            throws IOException {
        Builder builder = Report.builder();
        String publicPath = propertiesAccessor
                .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
        TemplateGeneratorDto dto = ods304eService.generateTemplate(formBean.getPackageId(),
                formBean.getPackageVer());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("title", dto.getPublishPackage().getName());
        params.put("description", dto.getPublishPackage().getDescription());
        params.put("url_link", formBean.getUrl());

        List<Map<String, Object>> reportData = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> reportData1 = new ArrayList<Map<String, Object>>();
        for (PackageMetadataDto meta : dto.getPackageMetadata()) {
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
        for (int i = 0; i < dto.getPackageTags().size(); i++) {
            OdsPackageTag tag = dto.getPackageTags().get(i);
            if (i == dto.getPackageTags().size() - 1) {
                tagString += tag.getTagName() + " ";
            } else {
                tagString += tag.getTagName() + ", ";
            }
        }
        params.put("tags", tagString);
        reportData.add(map);
        for (List<PackageAndResourceDto> listDto : dto.getMainData()) {
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
                    pard.setGridData((List<Map<String, Object>>)grid);
                    
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
