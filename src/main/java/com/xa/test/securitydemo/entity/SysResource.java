package com.xa.test.securitydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SYS_RESOURCE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysResource {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //父节点id
    @Column(name = "pid")
    private Integer pid;

    //权限名称
    @Column(name = "name")
    private String name;

    //授权链接
    @Column(name = "url")
    private String url;

    //权限描述
    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Date createTime;
}
