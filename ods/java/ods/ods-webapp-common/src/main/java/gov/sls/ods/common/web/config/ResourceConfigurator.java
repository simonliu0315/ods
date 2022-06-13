package gov.sls.ods.common.web.config;

import gov.sls.commons.web.providers.AddResourceProviders;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;

import java.io.File;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Component
public class ResourceConfigurator implements AddResourceProviders {

    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;

    @Override
    public void addResource(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[] { "/public/**" }).addResourceLocations(
                new String[] { new File(propertiesAccessor
                        .getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH)).toURI()
                        .toString() });

    }

}
