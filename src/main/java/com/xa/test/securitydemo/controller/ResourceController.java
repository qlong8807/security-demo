package com.xa.test.securitydemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("resource")
public class ResourceController {


    @GetMapping("/public")
    public String toPublicResource() {
        return "pages/public";
    }

    @GetMapping("/vip")
    public String toVipResource() {
        return "pages/vip";
    }
}
