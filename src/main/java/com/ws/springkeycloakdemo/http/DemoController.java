package com.ws.springkeycloakdemo.http;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {
    @GetMapping("/authenticated")
    public String getAuthenticatedPage() {
        return "authenticated";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String getManagerPage() {
        return "manager";
    }
}
