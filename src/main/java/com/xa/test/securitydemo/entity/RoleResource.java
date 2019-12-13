package com.xa.test.securitydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "role_resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResource {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "resource_id")
    private Integer resourceId;

    @Column(name = "create_time")
    private Date createTime;

}
