package gov.sls.ods.web.util;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebUtil {

    public String getUrlBase(HttpServletRequest request) {
        String urlBase = request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + request.getContextPath() + "/";
        log.info("urlBase:" + urlBase);
        return urlBase;
    }

}
