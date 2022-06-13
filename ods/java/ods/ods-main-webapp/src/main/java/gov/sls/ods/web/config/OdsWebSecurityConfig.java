/**
 * 
 */
package gov.sls.ods.web.config;

import gov.sls.commons.core.security.login.impl.SiteEnvironment;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * <pre>OdsWebSecurityConfig 這邊進行ODS-MAIN 之 spring security設定
 * 參考sls-sub-commons-web gov.sls.commons.web.config.WebSecurityConfig
 * 主要是針對 ODS37xI 的 api 設定 disable csrf
 * 原則上應該要在 gov.sls.commons.web.config.WebSecurityConfig 之前執行
 * </pre>
 */
@Configuration
@Order(1)
public class OdsWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    private SiteEnvironment siteEnvironment;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (siteEnvironment.isFrontSite()) {
            HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
            RequestMatcher requestMatcher = new RequestMatcher() {
                @Override
                public boolean matches(HttpServletRequest request) {
                    if (HttpMethod.GET == HttpMethod.valueOf(request
                            .getMethod())
                            || HttpMethod.POST == HttpMethod.valueOf(request
                                    .getMethod())) {
                        return true;
                    }
                    return false;
                }
            };
            requestCache.setRequestMatcher(requestMatcher);
            http.setSharedObject(RequestCache.class, requestCache);
            http.headers()
                    .addHeaderWriter(
                            new XFrameOptionsHeaderWriter(
                                    XFrameOptionsMode.SAMEORIGIN)).and()
                    .antMatcher("/**/ODS37*I/*").csrf().disable();
        }
    }
}
