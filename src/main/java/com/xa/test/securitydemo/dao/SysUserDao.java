package com.xa.test.securitydemo.dao;

import com.xa.test.securitydemo.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserDao extends JpaRepository<SysUser,Integer> {

    SysUser findByUsername(String username);
}
