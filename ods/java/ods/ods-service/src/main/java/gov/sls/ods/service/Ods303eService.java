package gov.sls.ods.service;

import gov.sls.commons.config.ApplicationProperties;
import gov.sls.commons.config.ApplicationPropertiesAccessor;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.dto.Ods303eAnalysisDto;
import gov.sls.ods.dto.OdsOpenGraphDto;
import gov.sls.ods.dto.PackageAndResourceDto;
import gov.sls.ods.dto.TemplateDynamicGeneratorDto;
import gov.sls.ods.dto.TemplateGeneratorDto;
import gov.sls.ods.dto.UserPackageRateAggregateDto;
import gov.sls.ods.repository.OdsMetadataRepository;
import gov.sls.ods.repository.OdsPackageLayoutRepository;
import gov.sls.ods.repository.OdsPackageMetadataRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;
import gov.sls.ods.repository.OdsUserPackageRateRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.resourceresolver.FileResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;



import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods303eService {

    @Autowired
    private OdsPackageVersionRepository packageVersionRepos;//所有Repos的目的就是為了從資料庫撈東西對應後，進行CRUD

    @Autowired
    private OdsUserPackageRateRepository userPackageRateRepos;

    @Autowired
    private OdsPackageMetadataRepository packageMetadataRepos;

    @Autowired
    private OdsPackageTagRepository packageTagRepos;

    @Inject
    private Environment environment;//Environment是判斷系統目前是在哪個目錄下

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;//有時候有些東西是動態生成的，Repos因為沒有table，所以無法進行對應，因此我們必須透過直接查詢DB的方式幫我們執行
    
    @Autowired
    @Qualifier("odsJdbcTemplate")
    private NamedParameterJdbcTemplate odsJdbcTemplate;//?(Qualifier)

    @Autowired
    private OdsPackageLayoutRepository packageLayoutRepos;

    @Autowired
    private OdsPackageRepository packageRepos;

    @Autowired
    private OdsPackageResourceRepository packageResourceRepos;

    @Autowired
    private OdsResourceVersionRepository resourceVersionRepos;

    @Autowired
    private OdsMetadataRepository metadataRepos;

    @Autowired
    private OdsResourceRepository resourceRepos;
    
    @Autowired
    private SqlExecutor sqlExecutor;//對Jdbc下達複雜SQL執行
    
    @Inject
    private ApplicationPropertiesAccessor slsPropertiesAccessor;
    
    @Autowired
    private SqlEscapeService sqlEscapeService;
        

    protected String ODS_TEMPLATE_HTML = "package" + File.separator + "html" + File.separator;
    protected String ODS_PACKAGE_TEMPLATE_HTML = "package" + File.separator;
    //protected String ODS_PATH_ROOT = "/psrdata/";
    //protected String ODS_PATH_ROOT = slsPropertiesAccessor.getProperty(ApplicationProperties.ENVIRONMENT_PSRDATA);

    
    /**
     * <p>max
     * </p>
     * @param packageId desc packageId
     * @return Integer
     */
    public Integer queryMaxVer (String packageId)
    {
        List<OdsPackageVersion> maxVerList = packageVersionRepos.getPublishPackageVerLast(packageId);
        if (CollectionUtils.isEmpty(maxVerList)) {
            return null;
        } else {
            return maxVerList.get(0).getVer();
        }
            
    }//Modification By Often

    public boolean isPackageOrResourcePublished(String packageId, int packageVer,
            String resourceId, int resourceVer) {
        if (StringUtils.isNotBlank(packageId) && StringUtils.isNotBlank(resourceId)) {
            if (packageVersionRepos.getPublishPackageVerResourceVer(packageId, packageVer,
                    resourceId, resourceVer).size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            if (packageVersionRepos.getPublishPackageVer(packageId, packageVer).size() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void generateTemplate(String packageId, int ver) {

        log.debug("ENVIRONMENT_SHARED_PATH:"
                + propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH));
        log.debug("ENVIRONMENT_PUBLIC_PATH:"
                + propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH));
        TemplateGeneratorDto dto = new TemplateGeneratorDto();
        dto.setPublishPackageVerLast(packageVersionRepos.getPublishPackageVerLast(packageId));

        OdsPackageVersionPK packageVersionPk = new OdsPackageVersionPK();
        packageVersionPk.setPackageId(packageId);
        packageVersionPk.setVer(ver);

        OdsOpenGraphDto ogDto = null;
        OdsPackageVersion packageVersion = packageVersionRepos.findOne(packageVersionPk);
        if (packageVersion != null) {
            ogDto = new OdsOpenGraphDto();
            ogDto.setTitle(packageVersion.getName());
            ogDto.setDescription(packageVersion.getDescription());
            log.debug(packageVersion.getPattern());            
//            if (!Strings.isNullOrEmpty(packageVersion.getDescription())) {
//                packageVersion.setDescription(packageVersion.getDescription().replaceAll("\r\n", "<br>"));
//            }
//            log.info("PublishVersion Description:" + packageVersion.getDescription());
            dto.setPublishPackage(packageVersion);
            String row[] = packageVersion.getPattern().split(",");
            Integer[] intarray = new Integer[row.length];
            for (int i = 0; i < row.length; i++) {
                intarray[i] = Integer.parseInt(row[i]);
            }

            int cnt = 0;
            int datasetOrder = 0;
            List<List<PackageAndResourceDto>> rowList = new ArrayList<List<PackageAndResourceDto>>();
            for (int i : intarray) {

                List<PackageAndResourceDto> columnList = new ArrayList<PackageAndResourceDto>();
                for (int j = 0; j < i; j++) {

                    List<PackageAndResourceDto> list = packageVersionRepos.findPackageAndResource(
                            packageId, ver, cnt, j);
                    if (list.size() > 0) {
                        PackageAndResourceDto parDto = list.get(0);                        
                        if ("dataset".equals(parDto.getFormat())) {
                            //DataSourceThreadLocal.setDataSourceEnum(DataSourceEnum.ODS_DATASTORE);
                            List<Map<String, Object>> grid = null;
                            Set<String> currencyColsSet = toCurrencyColsSet(parDto.getCurrencyCols());
                            
                            //log.debug("DataSource: datasource1555" + DataSourceThreadLocal.getDataSourceEnum() );                            
                            //Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
                            //targetDataSources.put("ODS_DATASTORE", "");
                            //datasource.setTargetDataSources(datasourceMap1);
                            //DataSource datasource =(DataSource)SpringContextUtil.getBean("odsDataStoreDataSource");                            

                            //NamedParameterJdbcTemplate jdbcTemplate1 = new NamedParameterJdbcTemplate(datasource);
                            Map<String, Object> where = new HashMap<String, Object>();
                            where.put("odsResourceVer", parDto.getResourceVer());
//                            List<HashMap> grid = sqlExecutor.queryForList("SELECT TOP 10 * FROM ODS_"
//                                            + parDto.getResourceId().replaceAll("-", "_") + " WHERE ODS_RESOURCE_VER = :odsResourceVer", where, HashMap.class);
                            log.debug("sql:" + "SELECT TOP 10 * FROM ODS_" + parDto.getResourceId().replaceAll("-", "_") + " WHERE ODS_RESOURCE_VER = " + parDto.getResourceVer());
                            grid = odsJdbcTemplate.queryForList(
                                    "SELECT TOP 10 * FROM ODS_"
                                            + sqlEscapeService.escapeMsSql(parDto.getResourceId().replaceAll("-", "_")) + " WHERE ODS_RESOURCE_VER = :odsResourceVer",
                                            where);                            
                            //DataSourceThreadLocal.clearDataSourceEnum();
                            //log.debug("DataSourceAfter:" + DataSourceThreadLocal.getDataSourceEnum());
                            for (Map<String, Object> map : grid) {
                                map.remove("ODS_RESOURCE_VER");
                            }
                            if (grid.size() > 0) {
                                parDto.setGridTitle(grid.get(0).keySet());
                                
                              //Format number
                              for (Map<String, Object> gridDatas : grid) {
                                  for (String key : gridDatas.keySet()) {
                                      if (isNumeric(String.valueOf(gridDatas.get(key)))) {
                                          log.info("oriValue:" + String.valueOf(gridDatas.get(key)));
                                          //DecimalFormat df = new DecimalFormat("###.######");                        
                                          //String newValue = df.format(String.valueOf(gridDatas.get(key)));
                                          String newValue = String.valueOf(gridDatas.get(key)).replaceFirst("\\.0*$|(\\.\\d*?)0+$", "$1");
                                          log.info("newValue:" + newValue);
                                          
                                          //補上千分位
                                          if(currencyColsSet.contains(key)){
                                              double amount = Double.parseDouble(newValue);
                                              DecimalFormat formatter = new DecimalFormat("#,###.00");
                                              newValue = formatter.format(amount);
                                          }
                                          gridDatas.put(key, newValue);
                                      }
                                  }
                              }
                            }
                            parDto.setGridData((List)grid);
                            parDto.setDatasetOrder(++datasetOrder);
                        } else {
                            parDto.setGridData(new ArrayList<Map<String, Object>>());
                        }
                        parDto.setPattern("" + i);
                        columnList.add(parDto);
                    }
                }
                cnt++;                
                rowList.add(columnList);
            }

            dto.setMainData(rowList);

            dto.setPackageTags(packageTagRepos.findByPackageId(packageId));
            dto.setPackageMetadata(packageMetadataRepos.getUnionPackageExtra(packageId, ver));            
            dto.setPublishVersions(packageVersionRepos.getPublishPackage(packageId));

            TemplateDynamicGeneratorDto dynamicDto = getDynamicTemplateObject(packageId, ver);

            dto.setShowStars(dynamicDto.getShowStars());
            dto.setShowScore(dynamicDto.getShowScore());
            dto.setDatasetNums(datasetOrder);

            TemplateResolver resolver = new TemplateResolver();
            FileResourceResolver res = new FileResourceResolver();
            resolver.setCharacterEncoding("UTF-8");
            Set<ITemplateResolver> resolvers = new LinkedHashSet<ITemplateResolver>(1);

            final Context ctx = new Context(Locale.TAIWAN);
            ctx.setVariable("contentObjs", dto);
            ctx.setVariable("og", ogDto);
            ctx.setVariable("openBrace", "@{");
            ctx.setVariable("closeBrace", "}");
            ctx.setVariable("systemId", environment.getProperty("systemId"));
            ctx.setVariable("systemMainId", environment.getProperty("systemId") + "-main");
            resolver.setPrefix(slsPropertiesAccessor.getProperty(ApplicationProperties.ENVIRONMENT_PSRDATA) + propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_SHARED_PATH)
                    + ODS_TEMPLATE_HTML);

            resolver.setResourceResolver(res);
            resolvers.add(resolver);
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolvers(resolvers);
            String htmlContent = templateEngine.process("ODS003_05.html", ctx);

            log.debug("before htmlContent--->" + htmlContent);
            htmlContent = htmlContent
                    .replaceFirst(
                            Pattern.quote("[[#{cht.header}]]"),
                            "<meta th:substituteby='fragments/external_meta :: meta'></meta>\r\n"
                                    + "<meta th:substituteby='fragments/external_resources :: head_resources'></meta>\r\n"
                                    + "<meta th:substituteby='fragments/resources :: head_resources'></meta>\r\n");
            htmlContent = htmlContent
                    .replaceFirst(
                            Pattern.quote("[[#{cht.body_header}]]"),
                            "<div th:replace='fragments/external_mobile_layout :: header'></div>");
//            htmlContent = htmlContent
//                    .replaceFirst(
//                            Pattern.quote("[[#{cht.body_header}]]"),
//                            "<div th:if='\\${isMobileDevice} == false'><div th:substituteby='fragments/external_header :: header'></div></div>"
//                                    + "<!--div th:substituteby='fragments/body_header :: body_header'></div-->");
//            htmlContent = htmlContent
//                    .replaceFirst(
//                            Pattern.quote("[[#{cht.body_header.mobile}]]"),
//                            "<a th:href='@{~/sip-main/mobile/}' class='top'><div th:if='\\${isMobileDevice} == true' class='header-mobile' style=\"background-image:url('/sip-main/images/external/mobile_header.png'); max-width:100%\"></div></a>");
//            htmlContent = htmlContent
//                    .replaceFirst(
//                            Pattern.quote("[[#{cht.bread.mobile}]]"),
//                            "<div th:if='\\${isMobileDevice} == true' class='crumb-row'>"
//                            + "<span class='crumb'>"
//                            + "<a id='guiding-brick-content' href='#guiding-brick-content' class='accesskey' accesskey='C' title='主要內容區'>:::</a>"
//                            + "<a th:href='@{~/sip-main/}' class='crumb'>首頁</a>"
//                            + "</span>"
//                            + "<span th:replace='fragments/external_breadcrumb :: breadcrumb'></span></div>");
            htmlContent = htmlContent
                    .replaceFirst(
                            Pattern.quote("[[#{cht.footer}]]"),
                            "<div th:replace='fragments/external_mobile_layout :: footer'></div>");
//            htmlContent = htmlContent
//                    .replaceFirst(
//                            Pattern.quote("[[#{cht.footer}]]"),
//                            "<div th:if='\\${isMobileDevice} == false'><div th:substituteby='fragments/external_footer :: footer'></div>\r\n"
//                                    + "<div th:substituteby='fragments/external_resources :: body_resources'></div>\r\n"
//                                    + "<div th:substituteby='fragments/resources :: body_resources'></div></div>");
//            htmlContent = htmlContent
//                    .replaceFirst(
//                            Pattern.quote("[[#{cht.footer.mobile}]]"),
//                            "<div th:if='\\${isMobileDevice} == true' class='footer-mobile'><div class='footer-mobile-text'>客服專線：0800-521-988  電子發票智慧好生活平台 版權所有<br>copyrights © 2014 All Rights Reserved</div>\r\n"
//                                    + "<div th:substituteby='fragments/external_resources :: body_resources'></div>\r\n"
//                                    + "<div th:substituteby='fragments/resources :: body_resources'></div></div>");
            htmlContent = htmlContent.replaceFirst(
                    Pattern.quote("[[#{cht.checkCookie}]]"),
                    "<input type='hidden' id='packageCookie' th:value='\\${packageCookie}' />");
            
            htmlContent = htmlContent.replaceFirst(
                    Pattern.quote("[[#{cht.dynamicContents.order}]]"),
                    "<div th:substituteby='/ods/package/html/Dynamic_ODS003 :: order'></div>\r\n");
            
            htmlContent = htmlContent.replaceFirst(
                    Pattern.quote("[[#{cht.dynamicContents.score}]]"),
                    "<div th:substituteby='/ods/package/html/Dynamic_ODS003 :: score'></div>\r\n");
            
            htmlContent = htmlContent.replaceFirst(
                    Pattern.quote("[[#{cht.dynamicContents.publishVersions}]]"),
                    "<div th:substituteby='/ods/package/html/Dynamic_ODS003 :: publishVersions'></div>\r\n");

            htmlContent = htmlContent
                    .replaceFirst(Pattern.quote("[[#{cht.dynamicContents.analysis}]]"),
                            "<div th:substituteby='/ods/package/html/Dynamic_ODS003 :: analysis'></div>\r\n");

            htmlContent = htmlContent.replaceAll(Pattern.quote("[[#{cht.systemId}]]"),
                    "' +\\${systemId} + ' ");
            htmlContent = htmlContent.replaceAll(Pattern.quote("cht_src"), "th:src");
            htmlContent = htmlContent.replaceAll(Pattern.quote("cht_href"), "th:href");
            htmlContent = htmlContent.replaceAll(Pattern.quote("cht_value"), "th:value");
            htmlContent = htmlContent.replaceAll(Pattern.quote("cht_background"), "th:background");
            htmlContent = htmlContent.replaceAll(Pattern.quote("cht_class"), "th:class");
            log.debug("after htmlContent--->" + htmlContent);
            writeStringToFile(htmlContent, new File(slsPropertiesAccessor.getProperty(ApplicationProperties.ENVIRONMENT_PSRDATA) + propertiesAccessor
                    .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)
                    + ODS_PACKAGE_TEMPLATE_HTML
                    + packageId
                    + File.separator
                    + "html"
                    + File.separator + packageId + "-" + ver + ".html"));
        }
            
    }
    
    private Set<String> toCurrencyColsSet(String currencyCols){
        Set<String> set = new HashSet<String>();
        if(Strings.isNullOrEmpty(currencyCols)){
            return set;
        }
        
        String[] currencyColsArray = currencyCols.split(",");
        for(String currencyCol : currencyColsArray){
            set.add(currencyCol);
        }
        
        return set;
    }
    
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public TemplateDynamicGeneratorDto getDynamicTemplateObject(String packageId, int packageVer) {
        TemplateDynamicGeneratorDto dto = new TemplateDynamicGeneratorDto();
        List<UserPackageRateAggregateDto> aggregateList = userPackageRateRepos
                .getAggregateByPackageId(packageId);
        if (aggregateList.size() == 0) {
            dto.setShowStars(BigDecimal.ZERO);
        } else {
            dto.setShowStars(aggregateList.get(0).getRateAvg());
        }
        if (aggregateList.size() == 0) {
            dto.setShowScore(BigDecimal.ZERO);
        } else {
            dto.setShowScore(aggregateList.get(0).getRateCount());
        }
        dto.setSystemId(environment.getProperty("systemId"));

        List<Ods303eAnalysisDto> analysisList = packageResourceRepos.findWorkbookByIdAndVer(packageId, packageVer);
        for (Ods303eAnalysisDto ans : analysisList) {
            String aName = ans.getName();
            int idx = ans.getName().indexOf("_");
            if (idx >= 0) {
                aName = aName.substring(idx + 1);
                ans.setName(aName);
            }
        }
        dto.setAnalysisData(analysisList);
        dto.setPackageId(packageId);
        dto.setPackageVer(packageVer);
        dto.setPublishVersions(packageVersionRepos.getPublishPackage(packageId));
        return dto;
    }
    
    private void writeStringToFile(String content, File file) {
        BufferedWriter out = null;
        FileOutputStream fOut = null;
        try {
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
            fOut = new FileOutputStream(file);
            out = new BufferedWriter(new OutputStreamWriter(
                    fOut, "UTF-8"));
            out.write(content);
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error("", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();    
                }                        
            } catch (IOException e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
            if (fOut != null) {
                safeClose(fOut);
            }
        }          
    }
    
    public static void safeClose(FileOutputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

}
