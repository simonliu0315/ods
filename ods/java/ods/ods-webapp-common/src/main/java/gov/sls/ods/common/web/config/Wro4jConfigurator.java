package gov.sls.ods.common.web.config;

import gov.sls.commons.web.providers.ThymeleafWro4jResourceProviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class Wro4jConfigurator implements ThymeleafWro4jResourceProviders {

    @Inject
    private ApplicationContext applicationContext;
    
    @Override
    public List<Resource> addResource() throws IOException {        
        Resource[] resources = applicationContext
                .getResources("classpath:META-INF/ods_wro.xml"); 
        List<Resource> resourceList = new ArrayList<Resource>();
        for (Resource resource: resources) {
            resourceList.add(resource);               
        }                       
        return resourceList;
    }

}
