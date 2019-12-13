package com.xa.test.securitydemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 prePostEnabled :决定Spring Security的前注解是否可用 [@PreAuthorize,@PostAuthorize,..]
 secureEnabled : 决定是否Spring Security的保障注解 [@Secured] 是否可用
 jsr250Enabled ：决定 JSR-250 annotations 注解[@RolesAllowed..] 是否可用.
 */
@Configurable
@EnableWebSecurity
//开启 Spring Security 方法级安全注解 @EnableGlobalMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserService customUserService;
//    @Autowired
//    private CustomPostProcessor postProcessor;

    /**
     * 主要配置路径，也就是资源的访问权限（是否需要认证，需要什么角色等）
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
//                .successHandler()
                .loginPage("/")//2.设置Security的登录页面访问路径为/login
                .loginProcessingUrl("/login")  //登录POST请求路径
//                .usernameParameter("username") //登录用户名参数
//                .passwordParameter("password") //登录密码参数
                .defaultSuccessUrl("/home")//3.登录成功后转向/chat路径
//                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error")//无权限页面,也可以用下面的
//                .accessDeniedHandler(customAccessDeniedHandler) //无权限处理器
                .and()
//                .csrf().disable()//默认打开,此行关闭csrf
                .authorizeRequests()
                .antMatchers("/", "/login").permitAll()//1.设置Security对/和/login请求不拦截
                .anyRequest().authenticated()//任何请求都进行拦截
                .and()
                .logout().permitAll()
//                .withObjectPostProcessor(postProcessor)//设置后置处理程序对象
                ;
    }

    /*
     * 主要配置身份认证来源，也就是用户及其角色。
     *
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        //在内存中配置两个用户，角色是USER
//        auth.inMemoryAuthentication()
//                .withUser("zhangsan").password("1").roles("USER")
//                .and()
//                .withUser("lisi").password("2").roles("USER");

//        auth.jdbcAuthentication().

        auth.authenticationProvider(authenticationProvider());//设置身份认证提供者
    }

    //不拦截/resources/static路径下的静态资源
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/static/**");
        //不拦截静态资源,所有用户均可访问的资源
//        webSecurity.ignoring().antMatchers(
//                "/",
//                "/css/**",
//                "/js/**",
//                "/images/**",
//                "/layui/**"
//        );
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //创建密码加密对象
    }

    /**
     * @return 封装身份认证提供者
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserService);  //自定义的用户和角色数据提供者
        authenticationProvider.setPasswordEncoder(passwordEncoder()); //设置密码加密对象
        return authenticationProvider;
    }
}
