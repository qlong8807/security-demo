package com.xa.test.securitydemo.controller;

import com.xa.test.securitydemo.dao.SysUserDao;
import com.xa.test.securitydemo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private SysUserDao sysUserDao;

    @GetMapping(value = {"/", "/login"})
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView mav = new ModelAndView();
//        if (error != null) {
//            System.out.println("----------------");
//            mav.addObject("error", "用户名或者密码不正确");
//        }
//        if (logout != null) {
//            mav.addObject("msg", "退出成功");
//        }
        List<SysUser> all = sysUserDao.findAll();
        mav.addObject("users", all);
        mav.setViewName("login");
        return mav;
    }

    @PostMapping("login")
    public void dologin(Model model) {
        log.info("执行登录");
//        return "home";
    }

    @PostMapping("error")
    public void error(Model model) {
        log.info("发生错误");
    }


}
