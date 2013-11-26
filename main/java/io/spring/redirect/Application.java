package io.spring.redirect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

@Configuration
@EnableAutoConfiguration
public class Application {

    public static final String REWRITE_FILTER_NAME = "rewriteFilter";
    public static final String REWRITE_FILTER_CONF_PATH = "urlrewrite.xml";

    @Bean
    public FilterRegistrationBean urlRewriteFilter() {
        FilterRegistrationBean reg = new FilterRegistrationBean(new UrlRewriteFilter());
        reg.setName(REWRITE_FILTER_NAME);
        reg.addInitParameter("confPath", REWRITE_FILTER_CONF_PATH);
        reg.addInitParameter("statusPath", "/rules");
        reg.addInitParameter("statusEnabledOnHosts", "*");
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
