package gov.sls.ods.common.web.config;

import gov.sls.commons.config.ApplicationProperties;
import gov.sls.commons.config.ApplicationPropertiesAccessor;
import gov.sls.commons.core.Profiles;
import gov.sls.commons.web.providers.AddTemplateResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Component
public class TemplateResolverConfigurator implements AddTemplateResolver {

    //public static final String FILE_TEMPLATE_PREFIX = "/psrdata/";
    public static final String DEFAULT_TEMPLATE_MODE = "LEGACYHTML5";
    public static final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    public static final String DEFAULT_ENCODING = "UTF-8";

    @Inject
    private Environment environment;
    
    @Inject
    private ApplicationPropertiesAccessor slsPropertiesAccessor;
    
    //protected String FILE_TEMPLATE_PREFIX = slsPropertiesAccessor.getProperty(ApplicationProperties.ENVIRONMENT_PSRDATA);
    
    private List<String> getCacheablePatterns() {
        List<String> list = new ArrayList<String>();
        String basePath = "ods/";
        list.add(basePath + "*");
        return list;
    }

    @Override
    public ITemplateResolver addTemplateResolver() {
        return fileTemplateResolver();
    }

    public FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(slsPropertiesAccessor.getProperty(ApplicationProperties.ENVIRONMENT_PSRDATA));
        templateResolver.setSuffix(DEFAULT_TEMPLATE_SUFFIX);
        templateResolver.setTemplateMode(DEFAULT_TEMPLATE_MODE);
        templateResolver.setCharacterEncoding(DEFAULT_ENCODING);        
        if (Profiles.PRODUCTION.isProfileActivated(environment)) {
            //templateResolver.setCacheTTLMs(TemplateResolver.DEFAULT_CACHE_TTL_MS);
            templateResolver.setCacheTTLMs(Long.valueOf(0L));//暫時設定為0
            templateResolver.setCacheable(true);
        } else {
            templateResolver.setCacheTTLMs(Long.valueOf(0L));
            templateResolver.setCacheable(false);
        }
        templateResolver.setOrder(Integer.valueOf(3));
        

        templateResolver.setCacheable(true);
        Set<String> cacheablePatterns = new HashSet<String>(getCacheablePatterns().size());
        for (String pattern : getCacheablePatterns()) {
            cacheablePatterns.add(pattern);
        }
        templateResolver.setCacheablePatterns(cacheablePatterns);
        return templateResolver;
    }

}
