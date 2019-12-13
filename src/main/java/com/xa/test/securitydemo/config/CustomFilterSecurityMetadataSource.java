package com.xa.test.securitydemo.config;

import com.xa.test.securitydemo.dao.SysResourceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * filterSecurityMetadataSource 需要实现 FilterInvocationSecurityMetadataSource接口，
 * Collection<ConfigAttribute> getAttributes(Object object)会返回改路径所需的角色集合到权限决策处理类中供其使用.
 */
//@Component("filterSecurityMetadataSource")
public class CustomFilterSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private SysResourceDao sysResourceDao;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object; //当前请求对象
        if (isMatcherAllowedRequest(fi)) return null ; //return null 表示允许访问，不做拦截
        List<ConfigAttribute> configAttributes = getMatcherConfigAttribute(fi.getRequestUrl());
        return configAttributes.size() > 0 ? configAttributes : deniedRequest(); //返回当前路径所需角色，如果没有则拒绝访问
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class< ? > aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }

    /**
     * 获取当前路径所需要的角色
     * @param url 当前路径
     * @return 所需角色集合
     */
    private List<ConfigAttribute> getMatcherConfigAttribute(String url){
//        return roleResourceRepository.findByResource_ResUrl(url).stream()
//                .map(roles -> new SecurityConfig(roles.getRole().getRoleCode()))
//                .collect(Collectors.toList());
        return null;
    }

    /**
     * 判断当前请求是否在允许请求的范围内
     * @param fi 当前请求
     * @return 是否在范围中
     */
    private boolean isMatcherAllowedRequest(FilterInvocation fi){
        return allowedRequest().stream().map(AntPathRequestMatcher::new)
                .filter(requestMatcher -> requestMatcher.matches(fi.getHttpRequest()))
                .toArray().length > 0;
    }

    /**
     * @return 定义允许请求的列表
     */
    private List<String> allowedRequest(){
        return Arrays.asList("/login","/css/**","/fonts/**","/js/**","/scss/**","/img/**");
    }

    /**
     * @return 默认拒绝访问配置
     */
    private List<ConfigAttribute> deniedRequest(){
        return Collections.singletonList(new SecurityConfig("ROLE_DENIED"));
    }
}