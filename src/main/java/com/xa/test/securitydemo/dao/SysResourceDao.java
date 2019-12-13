package com.xa.test.securitydemo.dao;

import com.xa.test.securitydemo.entity.SysResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysResourceDao extends JpaRepository<SysResource,Integer> {

    SysResource findByUrl(String url);

    List<SysResource> findByIdIn(List<Integer> ids);
}
