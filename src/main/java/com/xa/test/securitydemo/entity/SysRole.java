package com.xa.test.securitydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "create_time")
    private Date createTime;

}
