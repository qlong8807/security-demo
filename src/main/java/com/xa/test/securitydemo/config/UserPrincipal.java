package com.xa.test.securitydemo.config;

import com.xa.test.securitydemo.entity.SysResource;
import com.xa.test.securitydemo.entity.SysRole;
import com.xa.test.securitydemo.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserPrincipal implements UserDetails {

    private SysUser user;
    private List<SysRole> roles;
    private List<SysResource> resources;

    private Collection<? extends GrantedAuthority> authorities;

    UserPrincipal(SysUser user, List<SysRole> roles, List<SysResource> resources, Collection<? extends GrantedAuthority> authorities){
        this.user = user;
        this.roles = roles;
        this.resources = resources;
        this.authorities = authorities;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roleCodes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
