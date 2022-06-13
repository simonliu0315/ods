package gov.sls.ods.web.interceptor;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
@Component
public class TemplateModelValuesHandlerInterceptor extends HandlerInterceptorAdapter {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        if (request.getRequestURI().split("/").length <= 2) {
            log.debug("Root path, direct to ODS302E");
            
            response.sendRedirect("/ods-main/ODS302E/");
        }
        return super.preHandle(request, response, handler);
    }
}
