package com.xa.test.securitydemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

//@Component("postProcessor")
public class CustomPostProcessor implements ObjectPostProcessor<FilterSecurityInterceptor> {

    @Autowired
    private CustomFilterSecurityMetadataSource filterSecurityMetadataSource;
    @Autowired
    private CustomAccessDecisionManager accessDecisionManager;

    @Override
    public <T extends FilterSecurityInterceptor> T postProcess(T fsi) {
        fsi.setAccessDecisionManager(accessDecisionManager); //权限决策处理类
        fsi.setSecurityMetadataSource(filterSecurityMetadataSource); //路径（资源）拦截处理
        return fsi;
    }
}