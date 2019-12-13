package com.xa.test.securitydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


/**
 * @GeneratedValue注解的strategy属性提供四种值：
 *
 * –AUTO： 主键由程序控制，是默认选项，不设置即此项。
 * –IDENTITY：主键由数据库自动生成，即采用数据库ID自增长的方式，Oracle不支持这种方式。
 * –SEQUENCE：通过数据库的序列产生主键，通过@SequenceGenerator 注解指定序列名，mysql不支持这种方式。
 * –TABLE：通过特定的数据库表产生主键，使用该策略可以使应用更易于数据库移植。
 */
@Entity
@Table(name = "sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @Column(name = "create_time")
    private Date createTime;
}
