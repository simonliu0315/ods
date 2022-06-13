package gov.sls.ods.web.interceptor;

import gov.sls.commons.web.interceptor.AddInterceptorProviders;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorAddonProvider implements AddInterceptorProviders {

    @Autowired
    TemplateModelValuesHandlerInterceptor templateModelValuesHandlerInterceptor;
    
    @Override
    public List<HandlerInterceptor> addInterceptors() {
        List<HandlerInterceptor> ret = new ArrayList<HandlerInterceptor>();
        ret.add(templateModelValuesHandlerInterceptor);
        return ret;
    }

}
