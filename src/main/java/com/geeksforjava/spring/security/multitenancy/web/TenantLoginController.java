package com.geeksforjava.spring.security.multitenancy.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{tenantId}/login")
public class TenantLoginController {

    @GetMapping
    public String loginPage(@PathVariable String tenantId, Model model) {

        return String.format("login/%s", tenantId);
    }
}
