package gov.sls.ods.web.config;

import gov.sls.commons.web.interceptor.AddInterceptorProviders;
import gov.sls.commons.web.interceptor.ExternalTemplateModelValuesHandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorConfigurator implements AddInterceptorProviders {
    
    @Autowired
    private ExternalTemplateModelValuesHandlerInterceptor externalTemplateModelValuesHandlerInterceptor;

    @Override
    public List<HandlerInterceptor> addInterceptors() {
        List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>();
        interceptors.add(externalTemplateModelValuesHandlerInterceptor);        
        return interceptors;
    }
    

}
