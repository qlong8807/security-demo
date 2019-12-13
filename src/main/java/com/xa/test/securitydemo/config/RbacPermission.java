package com.xa.test.securitydemo.config;


import com.xa.test.securitydemo.entity.SysResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 自定义实现 URL 权限控制
 * RBAC数据模型控制权限
 */
@Component("rbacPermission")
public class RbacPermission {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        boolean hasPermission = false;
        // 读取用户所拥有的权限菜单
        List<SysResource> menus = ((UserPrincipal) principal).getResources();
        System.out.println(menus.size());
        for (SysResource menu : menus) {
            if (antPathMatcher.match(menu.getUrl(), request.getRequestURI())) {
                hasPermission = true;
                break;
            }
        }
        return hasPermission;
    }
}
