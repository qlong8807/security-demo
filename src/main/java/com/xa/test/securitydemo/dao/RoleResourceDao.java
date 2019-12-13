package com.xa.test.securitydemo.dao;

import com.xa.test.securitydemo.entity.RoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleResourceDao extends JpaRepository<RoleResource,Integer> {

    List<RoleResource> findByRoleIdIn(List<Integer> roleIds);
}
