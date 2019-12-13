package com.xa.test.securitydemo.config;

import com.xa.test.securitydemo.dao.*;
import com.xa.test.securitydemo.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component("customUserService")
public class CustomUserService implements UserDetailsService {

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private RoleResourceDao roleResourceDao;
    @Autowired
    private SysResourceDao sysResourceDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findByUsername(username); //根据用户名查询用户
        if (user == null) {
            throw new UsernameNotFoundException("can not found username " + username);
        }
        //根据用户id获取用户角色
        List<SysRole> roles = getRoles(user);
        // 填充权限
        Collection<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
        for (SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        //填充权限菜单
        List<SysResource> permissions = getPermissions(roles);
        return new UserPrincipal(user,roles,permissions,authorities);
    }

    private List<SysRole> getRoles(SysUser user) {
        List<UserRole> byUserId = userRoleDao.findByUserId(user.getId());
        List<Integer> roleIds = byUserId.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> byIdIn = sysRoleDao.findByIdIn(roleIds);
//        return byIdIn.stream().map(SysRole::getCode).collect(Collectors.toList());
        return byIdIn;
    }

    private List<SysResource> getPermissions(List<SysRole> roles) {
        List<Integer> roleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
        List<RoleResource> byRoleIdIn = roleResourceDao.findByRoleIdIn(roleIds);
        List<Integer> resIds = byRoleIdIn.stream().map(RoleResource::getResourceId).collect(Collectors.toList());
        return sysResourceDao.findByIdIn(resIds);
    }
}
