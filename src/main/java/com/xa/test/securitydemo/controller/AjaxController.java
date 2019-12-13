package com.xa.test.securitydemo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ajax")
public class AjaxController {


    @PostMapping("/public")
    public  Map<String, Object> doPublicHandler(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "请求成功" + id);
        return map;
    }

    @PostMapping("/vip")
    public  Map<String, Object> doVipHandler(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "请求成功" + id);
        return map;
    }
}
