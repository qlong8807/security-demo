package com.xa.test.securitydemo.dao;

import com.xa.test.securitydemo.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SysRoleDao extends JpaRepository<SysRole,Integer> {

    List<SysRole> findByIdIn(List<Integer> ids);
}
