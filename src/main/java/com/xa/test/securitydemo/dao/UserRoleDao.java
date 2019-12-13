package com.xa.test.securitydemo.dao;

import com.xa.test.securitydemo.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleDao extends JpaRepository<UserRole,Integer> {

    List<UserRole> findByUserId(Integer userId);
}
